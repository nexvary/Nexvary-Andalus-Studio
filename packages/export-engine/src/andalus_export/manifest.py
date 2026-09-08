from __future__ import annotations

import json
from collections.abc import Iterable
from dataclasses import dataclass
from hashlib import sha256


@dataclass(frozen=True)
class ExportManifest:
    project_id: str
    project_fingerprint: str
    generator_version: str
    outputs: tuple[str, ...]
    disclaimer: str
    manifest_fingerprint: str


def build_manifest(
    project_id: str,
    project_fingerprint: str,
    outputs: Iterable[str],
    generator_version: str = "0.6.25",
) -> ExportManifest:
    if len(project_fingerprint) != 64:
        raise ValueError("project_fingerprint must be SHA-256 hex")
    normalized = tuple(sorted(set(outputs)))
    if not normalized:
        raise ValueError("at least one output is required")
    payload = {
        "projectId": project_id,
        "projectFingerprint": project_fingerprint,
        "generatorVersion": generator_version,
        "outputs": normalized,
        "disclaimer": "Concept/export aid; verify dimensions and construction details professionally.",
    }
    digest = sha256(
        json.dumps(payload, sort_keys=True, separators=(",", ":")).encode("utf-8")
    ).hexdigest()
    return ExportManifest(
        project_id=project_id,
        project_fingerprint=project_fingerprint,
        generator_version=generator_version,
        outputs=normalized,
        disclaimer=payload["disclaimer"],
        manifest_fingerprint=digest,
    )
