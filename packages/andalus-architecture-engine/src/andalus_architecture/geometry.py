from __future__ import annotations

from dataclasses import dataclass
from html import escape
from math import pi


@dataclass(frozen=True)
class HorseshoeArch:
    width: float = 2.0
    clear_height: float = 3.0
    spring_height: float = 1.8
    wall_thickness: float = 0.25
    horseshoe_drop: float = 0.18

    def validate(self) -> None:
        if self.width <= 0 or self.clear_height <= 0:
            raise ValueError("arch width and clear_height must be positive")
        if not 0 < self.spring_height < self.clear_height:
            raise ValueError("spring_height must be between 0 and clear_height")
        if self.wall_thickness <= 0:
            raise ValueError("wall_thickness must be positive")
        if not 0.0 <= self.horseshoe_drop <= 0.45:
            raise ValueError("horseshoe_drop must be between 0 and 0.45")

    @property
    def radius(self) -> float:
        self.validate()
        return self.width / 2.0

    @property
    def approximate_clear_area(self) -> float:
        self.validate()
        rectangular = self.width * self.spring_height
        semicircle = pi * self.radius**2 / 2.0
        drop_adjustment = self.width * self.radius * self.horseshoe_drop * 0.35
        return rectangular + semicircle + drop_adjustment


@dataclass(frozen=True)
class Courtyard:
    width: float
    length: float
    arcade_depth: float = 1.8
    fountain_diameter: float = 1.2
    walkway_clearance: float = 1.2

    def validate(self) -> None:
        if self.width <= 0 or self.length <= 0:
            raise ValueError("courtyard width and length must be positive")
        if self.arcade_depth < 0:
            raise ValueError("arcade_depth cannot be negative")
        if self.arcade_depth * 2 >= min(self.width, self.length):
            raise ValueError("arcade_depth leaves no usable inner court")
        if self.fountain_diameter <= 0:
            raise ValueError("fountain_diameter must be positive")
        if self.walkway_clearance < 0:
            raise ValueError("walkway_clearance cannot be negative")
        inner_w = self.width - 2 * self.arcade_depth
        inner_l = self.length - 2 * self.arcade_depth
        if self.fountain_diameter + 2 * self.walkway_clearance > min(inner_w, inner_l):
            raise ValueError("fountain and clearance do not fit the inner court")

    @property
    def total_area(self) -> float:
        self.validate()
        return self.width * self.length

    @property
    def inner_court_area(self) -> float:
        self.validate()
        return (self.width - 2 * self.arcade_depth) * (self.length - 2 * self.arcade_depth)

    @property
    def arcade_area(self) -> float:
        return self.total_area - self.inner_court_area

    def summary(self) -> dict[str, float]:
        self.validate()
        return {
            "width": round(self.width, 3),
            "length": round(self.length, 3),
            "totalArea": round(self.total_area, 3),
            "arcadeArea": round(self.arcade_area, 3),
            "innerCourtArea": round(self.inner_court_area, 3),
            "fountainDiameter": round(self.fountain_diameter, 3),
        }


def horseshoe_arch_svg(arch: HorseshoeArch) -> str:
    arch.validate()
    scale = 100.0
    padding = 24.0
    width_px = arch.width * scale
    height_px = arch.clear_height * scale
    spring_y = height_px - arch.spring_height * scale
    center_x = padding + width_px / 2
    left_x = padding
    right_x = padding + width_px
    bottom_y = padding + height_px
    radius_px = arch.radius * scale
    crown_y = spring_y + padding - radius_px
    drop = arch.horseshoe_drop * radius_px

    path = (
        f"M {left_x:.2f} {bottom_y:.2f} "
        f"L {left_x:.2f} {spring_y + padding + drop:.2f} "
        f"C {left_x:.2f} {spring_y + padding - radius_px * 0.35:.2f}, "
        f"{center_x - radius_px * 0.58:.2f} {crown_y:.2f}, "
        f"{center_x:.2f} {crown_y:.2f} "
        f"C {center_x + radius_px * 0.58:.2f} {crown_y:.2f}, "
        f"{right_x:.2f} {spring_y + padding - radius_px * 0.35:.2f}, "
        f"{right_x:.2f} {spring_y + padding + drop:.2f} "
        f"L {right_x:.2f} {bottom_y:.2f}"
    )
    view_w = width_px + padding * 2
    view_h = height_px + padding * 2
    label = escape("parametric Andalusian horseshoe arch", quote=True)
    return (
        f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {view_w:.2f} {view_h:.2f}" '
        f'role="img" aria-label="{label}">'
        f'<path d="{path}" fill="none" stroke="currentColor" stroke-width="3" '
        'vector-effect="non-scaling-stroke"/>'
        "</svg>"
    )
