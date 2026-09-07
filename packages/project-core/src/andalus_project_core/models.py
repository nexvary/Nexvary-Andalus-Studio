from __future__ import annotations

from copy import deepcopy
from dataclasses import dataclass
from hashlib import sha256
import json
import math
import re
from typing import Any

_PROJECT_ID = re.compile(r"^[A-Za-z0-9][A-Za-z0-9_.-]{0,119}$")


def _validate_json_value(value: Any, path: str = "$") -> None:
    if value is None or isinstance(value, (str, bool, int)):
        return
    if isinstance(value, float):
        if not math.isfinite(value):
            raise ValueError(f"non-finite number at {path}")
        return
    if isinstance(value, list):
        for index, item in enumerate(value):
            _validate_json_value(item, f"{path}[{index}]")
        return
    if isinstance(value, dict):
        for key, item in value.items():
            if not isinstance(key, str):
                raise ValueError(f"non-string object key at {path}")
            _validate_json_value(item, f"{path}.{key}")
        return
    raise ValueError(f"unsupported value at {path}: {type(value).__name__}")


def canonical_json(payload: dict[str, Any]) -> str:
    _validate_json_value(payload)
    return json.dumps(
        payload,
        ensure_ascii=False,
        sort_keys=True,
        separators=(",", ":"),
        allow_nan=False,
    )


def project_fingerprint(payload: dict[str, Any]) -> str:
    return sha256(canonical_json(payload).encode("utf-8")).hexdigest()


@dataclass(frozen=True)
class ProjectSnapshot:
    project_id: str
    revision: int
    payload: dict[str, Any]
    parent_fingerprint: str | None = None
    created_at: str | None = None

    def validate(self) -> None:
        if not _PROJECT_ID.fullmatch(self.project_id):
            raise ValueError("project_id contains unsupported characters")
        if self.revision < 1:
            raise ValueError("revision must be >= 1")
        _validate_json_value(self.payload)
        if self.parent_fingerprint is not None:
            if len(self.parent_fingerprint) != 64 or any(
                ch not in "0123456789abcdef" for ch in self.parent_fingerprint
            ):
                raise ValueError("parent_fingerprint must be a lowercase SHA-256 hex digest")

    @property
    def fingerprint(self) -> str:
        self.validate()
        envelope = {
            "projectId": self.project_id,
            "revision": self.revision,
            "payload": self.payload,
            "parentFingerprint": self.parent_fingerprint,
        }
        return project_fingerprint(envelope)

    def as_record(self) -> dict[str, Any]:
        self.validate()
        return {
            "projectId": self.project_id,
            "revision": self.revision,
            "payload": deepcopy(self.payload),
            "parentFingerprint": self.parent_fingerprint,
            "createdAt": self.created_at,
            "fingerprint": self.fingerprint,
        }


def diff_paths(before: Any, after: Any, path: str = "$") -> list[str]:
    if type(before) is not type(after):
        return [path]
    if isinstance(before, dict):
        changed: list[str] = []
        keys = sorted(set(before) | set(after))
        for key in keys:
            child = f"{path}.{key}"
            if key not in before or key not in after:
                changed.append(child)
            else:
                changed.extend(diff_paths(before[key], after[key], child))
        return changed
    if isinstance(before, list):
        changed = []
        common = min(len(before), len(after))
        for index in range(common):
            changed.extend(diff_paths(before[index], after[index], f"{path}[{index}]"))
        if len(before) != len(after):
            changed.append(f"{path}.length")
        return changed
    return [] if before == after else [path]
