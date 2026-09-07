from __future__ import annotations

from dataclasses import dataclass
from math import cos, pi, sin


@dataclass(frozen=True)
class StarPattern:
    points: int = 8
    outer_radius: float = 100.0
    inner_radius: float = 45.0

    def validate(self) -> None:
        if self.points < 5:
            raise ValueError("points must be >= 5")
        if self.outer_radius <= 0 or self.inner_radius <= 0:
            raise ValueError("radii must be positive")
        if self.inner_radius >= self.outer_radius:
            raise ValueError("inner_radius must be smaller than outer_radius")

    def vertices(self) -> list[tuple[float, float]]:
        self.validate()
        cx = cy = self.outer_radius
        vertices: list[tuple[float, float]] = []
        total = self.points * 2
        for i in range(total):
            angle = -pi / 2 + i * pi / self.points
            radius = self.outer_radius if i % 2 == 0 else self.inner_radius
            vertices.append((cx + cos(angle) * radius, cy + sin(angle) * radius))
        return vertices


def generate_star_svg(pattern: StarPattern) -> str:
    pattern.validate()
    size = pattern.outer_radius * 2
    coords = " ".join(f"{x:.3f},{y:.3f}" for x, y in pattern.vertices())
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {size:.3f} {size:.3f}" '
        f'role="img" aria-label="{pattern.points}-point geometric star">'
        '<rect width="100%" height="100%" fill="none"/>'
        f'<polygon points="{coords}" fill="none" stroke="currentColor" '
        'stroke-width="2" vector-effect="non-scaling-stroke"/>'
        '</svg>'
    )
