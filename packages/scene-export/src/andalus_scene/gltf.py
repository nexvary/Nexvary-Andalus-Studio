from __future__ import annotations

from dataclasses import dataclass
import base64
import json
import math
import struct
from typing import Iterable


@dataclass(frozen=True)
class GltfWall:
    x1: float
    z1: float
    x2: float
    z2: float
    thickness: float = 0.20
    height: float = 3.0

    def validate(self) -> None:
        values = (self.x1, self.z1, self.x2, self.z2, self.thickness, self.height)
        if not all(math.isfinite(value) for value in values):
            raise ValueError("wall values must be finite")
        if self.thickness <= 0 or self.height <= 0:
            raise ValueError("wall thickness and height must be positive")
        if math.hypot(self.x2 - self.x1, self.z2 - self.z1) <= 1e-9:
            raise ValueError("wall endpoints must differ")


def _wall_vertices(wall: GltfWall) -> list[tuple[float, float, float]]:
    wall.validate()
    dx, dz = wall.x2 - wall.x1, wall.z2 - wall.z1
    length = math.hypot(dx, dz)
    nx, nz = -dz / length * wall.thickness / 2, dx / length * wall.thickness / 2
    a = (wall.x1 + nx, 0.0, wall.z1 + nz)
    b = (wall.x2 + nx, 0.0, wall.z2 + nz)
    c = (wall.x2 - nx, 0.0, wall.z2 - nz)
    d = (wall.x1 - nx, 0.0, wall.z1 - nz)
    return [a, b, c, d] + [(x, wall.height, z) for x, _, z in (a, b, c, d)]


def walls_to_gltf(walls: Iterable[GltfWall], *, project_fingerprint: str | None = None) -> str:
    if project_fingerprint is not None and (
        len(project_fingerprint) != 64
        or any(ch not in "0123456789abcdef" for ch in project_fingerprint)
    ):
        raise ValueError("project_fingerprint must be lowercase SHA-256 hex")
    wall_list = list(walls)
    if not wall_list:
        raise ValueError("at least one wall is required")
    if len(wall_list) > 5000:
        raise ValueError("too many walls")

    positions: list[float] = []
    indices: list[int] = []
    faces = (
        0,1,2, 0,2,3, 4,6,5, 4,7,6,
        0,4,5, 0,5,1, 1,5,6, 1,6,2,
        2,6,7, 2,7,3, 3,7,4, 3,4,0,
    )
    for wall in wall_list:
        offset = len(positions) // 3
        vertices = _wall_vertices(wall)
        for vertex in vertices:
            positions.extend(vertex)
        indices.extend(offset + i for i in faces)
    if len(positions) // 3 > 65535:
        raise ValueError("scene exceeds uint16 glTF index limit")

    pos_bytes = b"".join(struct.pack("<f", value) for value in positions)
    while len(pos_bytes) % 4:
        pos_bytes += b"\x00"
    index_offset = len(pos_bytes)
    idx_bytes = b"".join(struct.pack("<H", value) for value in indices)
    while len(idx_bytes) % 4:
        idx_bytes += b"\x00"
    binary = pos_bytes + idx_bytes

    xs, ys, zs = positions[0::3], positions[1::3], positions[2::3]
    gltf = {
        "asset": {"version": "2.0", "generator": "Nexvary Andalus Studio 0.8.25"},
        "scene": 0,
        "scenes": [{"nodes": [0]}],
        "nodes": [{"name": "Andalus Walls", "mesh": 0}],
        "meshes": [{"name": "Walls", "primitives": [{"attributes": {"POSITION": 0}, "indices": 1, "material": 0}]}],
        "materials": [{"name": "Lime plaster", "pbrMetallicRoughness": {"baseColorFactor": [0.82,0.78,0.68,1.0], "metallicFactor": 0.0, "roughnessFactor": 0.85}}],
        "buffers": [{"byteLength": len(binary), "uri": "data:application/octet-stream;base64," + base64.b64encode(binary).decode("ascii")}],
        "bufferViews": [
            {"buffer": 0, "byteOffset": 0, "byteLength": len(pos_bytes), "target": 34962},
            {"buffer": 0, "byteOffset": index_offset, "byteLength": len(idx_bytes), "target": 34963},
        ],
        "accessors": [
            {"bufferView": 0, "componentType": 5126, "count": len(positions)//3, "type": "VEC3", "min": [min(xs),min(ys),min(zs)], "max": [max(xs),max(ys),max(zs)]},
            {"bufferView": 1, "componentType": 5123, "count": len(indices), "type": "SCALAR", "min": [min(indices)], "max": [max(indices)]},
        ],
        "extras": {"stage": 825, "projectFingerprint": project_fingerprint},
    }
    return json.dumps(gltf, ensure_ascii=False, sort_keys=True, separators=(",", ":"))
