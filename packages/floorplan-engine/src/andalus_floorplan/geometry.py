from __future__ import annotations

from dataclasses import dataclass, field
from math import hypot
from typing import Iterable


@dataclass(frozen=True)
class Point:
    x: float
    y: float


@dataclass(frozen=True)
class Wall:
    start: Point
    end: Point
    thickness: float = 0.20

    def validate(self) -> None:
        if self.thickness <= 0:
            raise ValueError("wall thickness must be positive")
        if self.start == self.end:
            raise ValueError("wall must have non-zero length")

    @property
    def length(self) -> float:
        self.validate()
        return hypot(self.end.x - self.start.x, self.end.y - self.start.y)


@dataclass(frozen=True)
class Opening:
    wall_index: int
    offset: float
    width: float
    kind: str = "door"

    def validate(self, wall: Wall) -> None:
        if self.wall_index < 0:
            raise ValueError("wall_index must be non-negative")
        if self.width <= 0:
            raise ValueError("opening width must be positive")
        if self.offset < 0:
            raise ValueError("opening offset must be non-negative")
        if self.offset + self.width > wall.length + 1e-9:
            raise ValueError("opening exceeds wall length")
        if self.kind not in {"door", "window", "arch"}:
            raise ValueError("opening kind must be door, window, or arch")


@dataclass(frozen=True)
class Room:
    name: str
    polygon: tuple[Point, ...]
    kind: str = "room"

    def validate(self) -> None:
        if not self.name.strip():
            raise ValueError("room name is required")
        if len(self.polygon) < 3:
            raise ValueError("room polygon needs at least 3 points")
        if self.area <= 0:
            raise ValueError("room polygon area must be positive")

    @property
    def area(self) -> float:
        points = self.polygon
        if len(points) < 3:
            return 0.0
        total = 0.0
        for index, point in enumerate(points):
            nxt = points[(index + 1) % len(points)]
            total += point.x * nxt.y - nxt.x * point.y
        return abs(total) / 2.0

    @property
    def perimeter(self) -> float:
        if len(self.polygon) < 2:
            return 0.0
        return sum(
            hypot(
                self.polygon[(i + 1) % len(self.polygon)].x - point.x,
                self.polygon[(i + 1) % len(self.polygon)].y - point.y,
            )
            for i, point in enumerate(self.polygon)
        )


@dataclass
class FloorPlan:
    units: str = "metric"
    walls: list[Wall] = field(default_factory=list)
    openings: list[Opening] = field(default_factory=list)
    rooms: list[Room] = field(default_factory=list)

    def validate(self) -> None:
        if self.units not in {"metric", "imperial"}:
            raise ValueError("units must be metric or imperial")
        for wall in self.walls:
            wall.validate()
        for opening in self.openings:
            if opening.wall_index >= len(self.walls):
                raise ValueError("opening wall_index is out of range")
            opening.validate(self.walls[opening.wall_index])
        for room in self.rooms:
            room.validate()

    @property
    def total_room_area(self) -> float:
        return sum(room.area for room in self.rooms)

    @property
    def total_wall_length(self) -> float:
        return sum(wall.length for wall in self.walls)

    def bounding_box(self) -> tuple[float, float, float, float]:
        points: list[Point] = []
        for wall in self.walls:
            points.extend((wall.start, wall.end))
        for room in self.rooms:
            points.extend(room.polygon)
        if not points:
            return (0.0, 0.0, 0.0, 0.0)
        xs = [point.x for point in points]
        ys = [point.y for point in points]
        return (min(xs), min(ys), max(xs), max(ys))

    def summary(self) -> dict[str, object]:
        self.validate()
        min_x, min_y, max_x, max_y = self.bounding_box()
        return {
            "units": self.units,
            "wallCount": len(self.walls),
            "openingCount": len(self.openings),
            "roomCount": len(self.rooms),
            "totalRoomArea": round(self.total_room_area, 3),
            "totalWallLength": round(self.total_wall_length, 3),
            "bounds": {
                "minX": min_x,
                "minY": min_y,
                "maxX": max_x,
                "maxY": max_y,
                "width": max_x - min_x,
                "height": max_y - min_y,
            },
        }


def room_from_xy(name: str, xy: Iterable[tuple[float, float]], kind: str = "room") -> Room:
    return Room(name=name, polygon=tuple(Point(x, y) for x, y in xy), kind=kind)
