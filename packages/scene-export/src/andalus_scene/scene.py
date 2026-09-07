from __future__ import annotations

from dataclasses import dataclass, asdict
from hashlib import sha256
import json
import math
from typing import Any


@dataclass(frozen=True)
class Transform:
    translation: tuple[float, float, float] = (0.0, 0.0, 0.0)
    rotation_y_deg: float = 0.0
    scale: tuple[float, float, float] = (1.0, 1.0, 1.0)

    def validate(self) -> None:
        values = (*self.translation, self.rotation_y_deg, *self.scale)
        if not all(math.isfinite(value) for value in values):
            raise ValueError("transform values must be finite")
        if any(value <= 0 for value in self.scale):
            raise ValueError("scale values must be positive")


@dataclass(frozen=True)
class SceneNode:
    node_id: str
    kind: str
    transform: Transform = Transform()
    asset_id: str | None = None
    material_id: str | None = None
    source_fingerprint: str | None = None

    def validate(self) -> None:
        if not self.node_id or len(self.node_id) > 120:
            raise ValueError("node_id is required and must be <= 120 chars")
        if not self.kind:
            raise ValueError("kind is required")
        self.transform.validate()


@dataclass(frozen=True)
class SceneManifest:
    project_id: str
    project_fingerprint: str
    units: str
    nodes: tuple[SceneNode, ...]
    stage: int = 825

    def validate(self) -> None:
        if self.units not in {"metric", "imperial"}:
            raise ValueError("units must be metric or imperial")
        if len(self.project_fingerprint) != 64 or any(
            ch not in "0123456789abcdef" for ch in self.project_fingerprint
        ):
            raise ValueError("project_fingerprint must be lowercase SHA-256 hex")
        ids: set[str] = set()
        for node in self.nodes:
            node.validate()
            if node.node_id in ids:
                raise ValueError("duplicate scene node id")
            ids.add(node.node_id)

    def as_dict(self) -> dict[str, Any]:
        self.validate()
        return {
            "version": "0.8.25",
            "stage": self.stage,
            "projectId": self.project_id,
            "projectFingerprint": self.project_fingerprint,
            "units": self.units,
            "nodes": [
                {
                    "nodeId": node.node_id,
                    "kind": node.kind,
                    "transform": asdict(node.transform),
                    "assetId": node.asset_id,
                    "materialId": node.material_id,
                    "sourceFingerprint": node.source_fingerprint,
                }
                for node in self.nodes
            ],
        }

    @property
    def fingerprint(self) -> str:
        encoded = json.dumps(self.as_dict(), ensure_ascii=False, sort_keys=True, separators=(",", ":"))
        return sha256(encoded.encode("utf-8")).hexdigest()
