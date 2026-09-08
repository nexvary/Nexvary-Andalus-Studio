from __future__ import annotations

from html import escape

from .geometry import FloorPlan


def floorplan_to_svg(plan: FloorPlan, scale: float = 80.0, padding: float = 24.0) -> str:
    if scale <= 0:
        raise ValueError("scale must be positive")
    plan.validate()
    min_x, min_y, max_x, max_y = plan.bounding_box()
    width = max((max_x - min_x) * scale + padding * 2, 100.0)
    height = max((max_y - min_y) * scale + padding * 2, 100.0)

    def tx(x: float) -> float:
        return (x - min_x) * scale + padding

    def ty(y: float) -> float:
        return (y - min_y) * scale + padding

    parts = [
        (
            f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {width:.2f} {height:.2f}" '
            'role="img" aria-label="Nexvary Andalus floor plan">'
        )
    ]

    for room in plan.rooms:
        points = " ".join(f"{tx(p.x):.2f},{ty(p.y):.2f}" for p in room.polygon)
        parts.append(
            f'<polygon points="{points}" fill="currentColor" fill-opacity="0.04" '
            'stroke="none"/>'
        )
        if room.polygon:
            cx = sum(p.x for p in room.polygon) / len(room.polygon)
            cy = sum(p.y for p in room.polygon) / len(room.polygon)
            parts.append(
                f'<text x="{tx(cx):.2f}" y="{ty(cy):.2f}" text-anchor="middle" '
                'font-size="13" fill="currentColor">'
                f"{escape(room.name)}</text>"
            )

    for wall in plan.walls:
        parts.append(
            f'<line x1="{tx(wall.start.x):.2f}" y1="{ty(wall.start.y):.2f}" '
            f'x2="{tx(wall.end.x):.2f}" y2="{ty(wall.end.y):.2f}" '
            f'stroke="currentColor" stroke-width="{max(wall.thickness * scale, 2):.2f}" '
            'stroke-linecap="square"/>'
        )

    parts.append("</svg>")
    return "".join(parts)
