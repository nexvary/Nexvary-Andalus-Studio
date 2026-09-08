from __future__ import annotations

import json
import os
from collections.abc import Callable
from dataclasses import asdict, dataclass
from datetime import UTC, datetime
from enum import Enum
from threading import RLock
from typing import Any
from urllib.parse import urlparse
from urllib.request import Request, urlopen


@dataclass(frozen=True)
class AdapterCapabilities:
    text_to_image: bool = False
    image_to_image: bool = True
    control_images: bool = True
    inpainting: bool = True
    cancellable: bool = False


@dataclass(frozen=True)
class AdapterInfo:
    adapter_id: str
    display_name: str
    configured: bool
    capabilities: AdapterCapabilities
    software_license: str
    weight_license_review_required: bool = True


class JobStatus(str, Enum):
    QUEUED = "queued"
    RUNNING = "running"
    SUCCEEDED = "succeeded"
    FAILED = "failed"
    CANCELLED = "cancelled"


@dataclass
class JobRecord:
    job_id: str
    adapter_id: str
    status: JobStatus
    created_at: str
    idempotency_key: str | None = None
    provider_job_id: str | None = None
    request_digest: str | None = None
    result: dict[str, Any] | None = None
    error: str | None = None


class JobManager:
    def __init__(self) -> None:
        self._lock = RLock()
        self._jobs: dict[str, JobRecord] = {}
        self._idempotency: dict[str, str] = {}
        self._counter = 0

    def create(
        self,
        adapter_id: str,
        *,
        idempotency_key: str | None = None,
        request_digest: str | None = None,
    ) -> JobRecord:
        with self._lock:
            if idempotency_key and idempotency_key in self._idempotency:
                existing = self._jobs[self._idempotency[idempotency_key]]
                if existing.adapter_id != adapter_id or existing.request_digest != request_digest:
                    raise ValueError("idempotency key was already used for a different request")
                return existing
            self._counter += 1
            job_id = f"job-{self._counter:08d}"
            record = JobRecord(
                job_id,
                adapter_id,
                JobStatus.QUEUED,
                datetime.now(UTC).isoformat(),
                idempotency_key,
                request_digest=request_digest,
            )
            self._jobs[job_id] = record
            if idempotency_key:
                self._idempotency[idempotency_key] = job_id
            return record

    def transition(
        self,
        job_id: str,
        status: JobStatus,
        *,
        provider_job_id: str | None = None,
        result: dict[str, Any] | None = None,
        error: str | None = None,
    ) -> JobRecord:
        with self._lock:
            try:
                record = self._jobs[job_id]
            except KeyError as exc:
                raise KeyError("job not found") from exc
            allowed = {
                JobStatus.QUEUED: {JobStatus.RUNNING, JobStatus.CANCELLED, JobStatus.FAILED},
                JobStatus.RUNNING: {JobStatus.SUCCEEDED, JobStatus.FAILED, JobStatus.CANCELLED},
                JobStatus.SUCCEEDED: set(),
                JobStatus.FAILED: set(),
                JobStatus.CANCELLED: set(),
            }
            if status == record.status:
                return record
            if status not in allowed[record.status]:
                raise ValueError(f"invalid job transition: {record.status} -> {status}")
            record.status = status
            if provider_job_id is not None:
                record.provider_job_id = provider_job_id
            if result is not None:
                record.result = result
            if error is not None:
                record.error = error[:2000]
            return record

    def get(self, job_id: str) -> JobRecord:
        with self._lock:
            if job_id not in self._jobs:
                raise KeyError("job not found")
            return self._jobs[job_id]


Transport = Callable[[str, str, dict[str, Any] | None], dict[str, Any]]


def _default_transport(
    method: str,
    url: str,
    payload: dict[str, Any] | None,
) -> dict[str, Any]:
    body = None if payload is None else json.dumps(payload).encode("utf-8")
    request = Request(
        url,
        data=body,
        method=method,
        headers={"Content-Type": "application/json"},
    )
    with urlopen(request, timeout=30) as response:  # nosec - server-configured URL
        return json.loads(response.read().decode("utf-8"))


def validate_configured_base_url(url: str) -> str:
    parsed = urlparse(url)
    if parsed.scheme not in {"http", "https"} or not parsed.hostname:
        raise ValueError("adapter base URL must use http(s) and include a host")
    if parsed.username or parsed.password or parsed.query or parsed.fragment:
        raise ValueError("adapter base URL cannot contain credentials, query, or fragment")
    return url.rstrip("/")


class DryRunAdapter:
    info = AdapterInfo(
        "dry-run",
        "Dry Run / Contract Test",
        True,
        AdapterCapabilities(
            text_to_image=True,
            image_to_image=True,
            control_images=True,
            inpainting=True,
        ),
        "Nexvary internal",
        False,
    )

    def submit(self, plan: dict[str, Any]) -> dict[str, Any]:
        return {"providerJobId": "dry-run", "accepted": True, "planEcho": plan}


class ComfyUIAdapter:
    def __init__(self, base_url: str, transport: Transport = _default_transport) -> None:
        self.base_url = validate_configured_base_url(base_url)
        self.transport = transport
        self.info = AdapterInfo(
            "comfyui",
            "ComfyUI external service",
            True,
            AdapterCapabilities(
                text_to_image=True,
                image_to_image=True,
                control_images=True,
                inpainting=True,
            ),
            "GPL-3.0 service boundary; no ComfyUI code copied into core",
            True,
        )

    def submit_workflow(self, workflow: dict[str, Any], *, client_id: str) -> str:
        if not workflow or len(workflow) > 5000:
            raise ValueError("workflow must contain between 1 and 5000 nodes")
        result = self.transport(
            "POST",
            f"{self.base_url}/prompt",
            {"prompt": workflow, "client_id": client_id},
        )
        prompt_id = result.get("prompt_id")
        if not isinstance(prompt_id, str) or not prompt_id:
            raise ValueError("ComfyUI response did not include prompt_id")
        return prompt_id

    def history(self, prompt_id: str) -> dict[str, Any]:
        if not prompt_id or len(prompt_id) > 200:
            raise ValueError("invalid prompt_id")
        return self.transport("GET", f"{self.base_url}/history/{prompt_id}", None)


def configured_adapters() -> list[AdapterInfo]:
    items = [DryRunAdapter.info]
    url = os.getenv("ANDALUS_COMFYUI_URL", "").strip()
    if url:
        try:
            adapter = ComfyUIAdapter(url)
            items.append(adapter.info)
        except ValueError:
            items.append(
                AdapterInfo(
                    "comfyui",
                    "ComfyUI external service",
                    False,
                    AdapterCapabilities(),
                    "GPL-3.0 service boundary",
                )
            )
    else:
        items.append(
            AdapterInfo(
                "comfyui",
                "ComfyUI external service",
                False,
                AdapterCapabilities(),
                "GPL-3.0 service boundary",
            )
        )
    return items


def job_to_dict(record: JobRecord) -> dict[str, Any]:
    data = asdict(record)
    data["status"] = record.status.value
    return data
