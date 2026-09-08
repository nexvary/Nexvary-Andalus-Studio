from __future__ import annotations

from collections.abc import Iterable
from dataclasses import dataclass
from html import escape
from math import cos, pi, sin


Point = tuple[float, float]


def _fmt(value: float) -> str:
    return f"{value:.3f}"


def _points_attr(points: Iterable[Point]) -> str:
    return " ".join(f"{_fmt(x)},{_fmt(y)}" for x, y in points)


def _svg(size: float, body: str, label: str) -> str:
    safe_label = escape(label, quote=True)
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {_fmt(size)} {_fmt(size)}" '
        f'role="img" aria-label="{safe_label}">'
        f"{body}</svg>"
    )


@dataclass(frozen=True)
class StarPattern:
    points: int = 8
    outer_radius: float = 100.0
    inner_radius: float = 45.0

    def validate(self) -> None:
        if not 5 <= self.points <= 32:
            raise ValueError("points must be between 5 and 32")
        if self.outer_radius <= 0 or self.inner_radius <= 0:
            raise ValueError("radii must be positive")
        if self.inner_radius >= self.outer_radius:
            raise ValueError("inner_radius must be smaller than outer_radius")

    def vertices(self) -> list[Point]:
        self.validate()
        cx = cy = self.outer_radius
        vertices: list[Point] = []
        total = self.points * 2
        for i in range(total):
            angle = -pi / 2 + i * pi / self.points
            radius = self.outer_radius if i % 2 == 0 else self.inner_radius
            vertices.append((cx + cos(angle) * radius, cy + sin(angle) * radius))
        return vertices


@dataclass(frozen=True)
class RosettePattern:
    petals: int = 8
    radius: float = 100.0
    petal_depth: float = 0.38

    def validate(self) -> None:
        if not 6 <= self.petals <= 32:
            raise ValueError("petals must be between 6 and 32")
        if self.radius <= 0:
            raise ValueError("radius must be positive")
        if not 0.1 <= self.petal_depth <= 0.8:
            raise ValueError("petal_depth must be between 0.1 and 0.8")

    def rings(self) -> tuple[list[Point], list[Point]]:
        self.validate()
        cx = cy = self.radius
        outer: list[Point] = []
        inner: list[Point] = []
        inner_radius = self.radius * (1 - self.petal_depth)
        for i in range(self.petals):
            angle = -pi / 2 + i * 2 * pi / self.petals
            outer.append((cx + cos(angle) * self.radius, cy + sin(angle) * self.radius))
            inner_angle = angle + pi / self.petals
            inner.append(
                (
                    cx + cos(inner_angle) * inner_radius,
                    cy + sin(inner_angle) * inner_radius,
                )
            )
        return outer, inner


@dataclass(frozen=True)
class ZellijGrid:
    cells: int = 4
    tile_size: float = 80.0
    star_points: int = 8
    inset_ratio: float = 0.45

    def validate(self) -> None:
        if not 1 <= self.cells <= 12:
            raise ValueError("cells must be between 1 and 12")
        if self.tile_size <= 0:
            raise ValueError("tile_size must be positive")
        if not 5 <= self.star_points <= 24:
            raise ValueError("star_points must be between 5 and 24")
        if not 0.2 <= self.inset_ratio <= 0.75:
            raise ValueError("inset_ratio must be between 0.2 and 0.75")


@dataclass(frozen=True)
class BorderPattern:
    repeats: int = 8
    cell_size: float = 60.0
    motif_points: int = 8

    def validate(self) -> None:
        if not 2 <= self.repeats <= 40:
            raise ValueError("repeats must be between 2 and 40")
        if self.cell_size <= 0:
            raise ValueError("cell_size must be positive")
        if not 5 <= self.motif_points <= 24:
            raise ValueError("motif_points must be between 5 and 24")


def generate_star_svg(pattern: StarPattern) -> str:
    pattern.validate()
    size = pattern.outer_radius * 2
    polygon = (
        f'<polygon points="{_points_attr(pattern.vertices())}" fill="none" '
        'stroke="currentColor" stroke-width="2" vector-effect="non-scaling-stroke"/>'
    )
    return _svg(size, polygon, f"{pattern.points}-point geometric star")


def generate_rosette_svg(pattern: RosettePattern) -> str:
    outer, inner = pattern.rings()
    size = pattern.radius * 2
    interlaced: list[Point] = []
    for i in range(pattern.petals):
        interlaced.extend([outer[i], inner[i]])
    body = (
        f'<polygon points="{_points_attr(interlaced)}" fill="none" '
        'stroke="currentColor" stroke-width="2" vector-effect="non-scaling-stroke"/>'
        f'<circle cx="{_fmt(pattern.radius)}" cy="{_fmt(pattern.radius)}" '
        f'r="{_fmt(pattern.radius * 0.22)}" fill="none" stroke="currentColor" '
        'stroke-width="1.5" vector-effect="non-scaling-stroke"/>'
    )
    return _svg(size, body, f"{pattern.petals}-petal Islamic rosette")


def _translated_star(
    cx: float,
    cy: float,
    outer_radius: float,
    inner_radius: float,
    points: int,
) -> list[Point]:
    result: list[Point] = []
    for i in range(points * 2):
        angle = -pi / 2 + i * pi / points
        radius = outer_radius if i % 2 == 0 else inner_radius
        result.append((cx + cos(angle) * radius, cy + sin(angle) * radius))
    return result


def generate_zellij_svg(pattern: ZellijGrid) -> str:
    pattern.validate()
    size = pattern.cells * pattern.tile_size
    pieces: list[str] = [
        (
            '<rect width="100%" height="100%" fill="none" stroke="currentColor" '
            'stroke-width="1" vector-effect="non-scaling-stroke"/>'
        )
    ]
    outer = pattern.tile_size * 0.42
    inner = outer * pattern.inset_ratio
    for row in range(pattern.cells):
        for col in range(pattern.cells):
            cx = col * pattern.tile_size + pattern.tile_size / 2
            cy = row * pattern.tile_size + pattern.tile_size / 2
            vertices = _translated_star(cx, cy, outer, inner, pattern.star_points)
            pieces.append(
                f'<polygon points="{_points_attr(vertices)}" fill="none" '
                'stroke="currentColor" stroke-width="1.4" '
                'vector-effect="non-scaling-stroke"/>'
            )
            pieces.append(
                f'<rect x="{_fmt(col * pattern.tile_size)}" '
                f'y="{_fmt(row * pattern.tile_size)}" '
                f'width="{_fmt(pattern.tile_size)}" height="{_fmt(pattern.tile_size)}" '
                'fill="none" stroke="currentColor" stroke-opacity="0.3" '
                'stroke-width="0.7" vector-effect="non-scaling-stroke"/>'
            )
    return _svg(size, "".join(pieces), "parametric zellij grid")


def generate_border_svg(pattern: BorderPattern) -> str:
    pattern.validate()
    width = pattern.repeats * pattern.cell_size
    height = pattern.cell_size
    pieces: list[str] = []
    for index in range(pattern.repeats):
        cx = index * pattern.cell_size + pattern.cell_size / 2
        cy = height / 2
        vertices = _translated_star(
            cx,
            cy,
            pattern.cell_size * 0.42,
            pattern.cell_size * 0.19,
            pattern.motif_points,
        )
        pieces.append(
            f'<polygon points="{_points_attr(vertices)}" fill="none" '
            'stroke="currentColor" stroke-width="1.2" vector-effect="non-scaling-stroke"/>'
        )
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {_fmt(width)} {_fmt(height)}" '
        'role="img" aria-label="repeating Islamic geometric border">'
        + "".join(pieces)
        + "</svg>"
    )


def available_patterns() -> list[dict[str, object]]:
    return [
        {"id": "star", "min_points": 5, "max_points": 32, "export": ["svg"]},
        {"id": "rosette", "min_petals": 6, "max_petals": 32, "export": ["svg"]},
        {"id": "zellij-grid", "max_cells": 12, "export": ["svg"]},
        {"id": "border", "max_repeats": 40, "export": ["svg"]},
    ]
