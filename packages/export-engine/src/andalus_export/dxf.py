from __future__ import annotations

from collections.abc import Iterable
from dataclasses import dataclass
from math import isfinite
from typing import Literal

Unit = Literal["metric", "imperial"]


@dataclass(frozen=True)
class DxfWall:
    x1: float
    y1: float
    x2: float
    y2: float
    layer: str = "WALLS"

    def validate(self) -> None:
        if not all(isfinite(value) for value in (self.x1, self.y1, self.x2, self.y2)):
            raise ValueError("DXF coordinates must be finite")
        if self.x1 == self.x2 and self.y1 == self.y2:
            raise ValueError("DXF wall cannot have zero length")
        if not self.layer or len(self.layer) > 31 or any(ch in '<>/\\":;?*|=' for ch in self.layer):
            raise ValueError("invalid DXF layer name")


def _pair(code: int, value: object) -> str:
    return f"{code}\n{value}\n"


def walls_to_dxf(walls: Iterable[DxfWall], units: Unit = "metric") -> str:
    unit_code = 6 if units == "metric" else 1
    entities = []
    count = 0
    for wall in walls:
        wall.validate()
        count += 1
        entities.append(
            "".join(
                [
                    _pair(0, "LINE"),
                    _pair(8, wall.layer),
                    _pair(10, f"{wall.x1:.6f}"),
                    _pair(20, f"{wall.y1:.6f}"),
                    _pair(30, "0.0"),
                    _pair(11, f"{wall.x2:.6f}"),
                    _pair(21, f"{wall.y2:.6f}"),
                    _pair(31, "0.0"),
                ]
            )
        )
    if count == 0:
        raise ValueError("at least one wall is required for DXF export")
    return "".join(
        [
            _pair(0, "SECTION"),
            _pair(2, "HEADER"),
            _pair(9, "$ACADVER"),
            _pair(1, "AC1009"),
            _pair(9, "$INSUNITS"),
            _pair(70, unit_code),
            _pair(0, "ENDSEC"),
            _pair(0, "SECTION"),
            _pair(2, "ENTITIES"),
            *entities,
            _pair(0, "ENDSEC"),
            _pair(0, "EOF"),
        ]
    )
