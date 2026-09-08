from __future__ import annotations

import json
from dataclasses import asdict, dataclass
from hashlib import sha256
from typing import Any, Literal

LockName = Literal[
    "massing", "floorCount", "openings", "entrance", "roofline", "roomBoundaries"
]

_LOCK_PATHS: dict[str, str] = {
    "massing": "geometry.massingDigest",
    "floorCount": "geometry.floorCount",
    "openings": "geometry.openingsDigest",
    "entrance": "geometry.entranceDigest",
    "roofline": "geometry.rooflineDigest",
    "roomBoundaries": "geometry.roomBoundariesDigest",
}


@dataclass(frozen=True)
class GenerationPlan:
    style_school: str
    space_type: str
    source_kind: str
    controls: tuple[str, ...]
    immutable_paths: tuple[str, ...]
    concept_only: bool
    request_digest: str


@dataclass(frozen=True)
class CandidateValidation:
    accepted: bool
    violations: tuple[str, ...]
    checked_paths: tuple[str, ...]


def _digest(payload: Any) -> str:
    text = json.dumps(payload, sort_keys=True, separators=(",", ":"), ensure_ascii=False)
    return sha256(text.encode("utf-8")).hexdigest()


def compile_generation_plan(
    *,
    style_school: str,
    space_type: str,
    source_kind: str,
    locks: dict[str, bool],
    user_request: str = "",
) -> GenerationPlan:
    if source_kind not in {"photo", "floorplan", "scan", "blank"}:
        raise ValueError("unsupported source_kind")
    unknown = sorted(set(locks) - set(_LOCK_PATHS))
    if unknown:
        raise ValueError(f"unknown architectural locks: {', '.join(unknown)}")
    if len(user_request) > 4000:
        raise ValueError("user_request exceeds 4000 characters")

    immutable = tuple(sorted(_LOCK_PATHS[name] for name, locked in locks.items() if locked))
    controls: set[str] = set()
    if source_kind in {"photo", "scan"}:
        controls.add("depth")
    if any(locks.get(name, False) for name in ("massing", "roofline", "openings", "entrance")):
        controls.add("edges")
    if locks.get("roomBoundaries", False):
        controls.add("segmentation")
    if locks.get("openings", False):
        controls.add("opening-mask")
    if source_kind == "floorplan":
        controls.add("floorplan-geometry")

    request_digest = _digest(
        {
            "styleSchool": style_school,
            "spaceType": space_type,
            "sourceKind": source_kind,
            "locks": locks,
            "userRequest": user_request,
        }
    )
    return GenerationPlan(
        style_school=style_school,
        space_type=space_type,
        source_kind=source_kind,
        controls=tuple(sorted(controls)),
        immutable_paths=immutable,
        concept_only=True,
        request_digest=request_digest,
    )


def _resolve_path(document: dict[str, Any], dotted_path: str) -> tuple[bool, Any]:
    current: Any = document
    for part in dotted_path.split("."):
        if not isinstance(current, dict) or part not in current:
            return False, None
        current = current[part]
    return True, current


def validate_locked_candidate(
    baseline: dict[str, Any],
    candidate: dict[str, Any],
    locks: dict[str, bool],
) -> CandidateValidation:
    unknown = sorted(set(locks) - set(_LOCK_PATHS))
    if unknown:
        raise ValueError(f"unknown architectural locks: {', '.join(unknown)}")
    checked: list[str] = []
    violations: list[str] = []
    for name, path in _LOCK_PATHS.items():
        if not locks.get(name, False):
            continue
        checked.append(path)
        baseline_ok, baseline_value = _resolve_path(baseline, path)
        candidate_ok, candidate_value = _resolve_path(candidate, path)
        if not baseline_ok or not candidate_ok or baseline_value != candidate_value:
            violations.append(path)
    return CandidateValidation(
        accepted=not violations,
        violations=tuple(violations),
        checked_paths=tuple(checked),
    )


def generation_provenance(
    *,
    provider: str,
    model_id: str,
    model_license: str,
    source_digest: str,
    plan: GenerationPlan,
) -> dict[str, Any]:
    if not provider or not model_id or not model_license:
        raise ValueError("provider, model_id and model_license are required")
    record = {
        "provider": provider,
        "modelId": model_id,
        "modelLicense": model_license,
        "sourceDigest": source_digest,
        "plan": asdict(plan),
    }
    return {**record, "recordDigest": _digest(record)}
