from __future__ import annotations

from dataclasses import dataclass
from math import ceil, isfinite
from typing import Iterable

from .catalog import get_material


@dataclass(frozen=True)
class SurfaceTakeoff:
    material_id: str
    area_m2: float
    waste_override: float | None = None

    def validate(self) -> None:
        if not isfinite(self.area_m2) or self.area_m2 < 0:
            raise ValueError("area_m2 must be a finite non-negative number")
        if self.waste_override is not None and not 0 <= self.waste_override <= 0.5:
            raise ValueError("waste_override must be between 0 and 0.5")


@dataclass(frozen=True)
class BomLine:
    material_id: str
    name_en: str
    name_ar: str
    purchase_unit: str
    base_quantity: float
    waste_fraction: float
    purchase_quantity: float
    unit_price: float | None = None
    currency: str | None = None
    line_total: float | None = None


def estimate_surface(takeoff: SurfaceTakeoff, round_purchase: bool = False) -> BomLine:
    takeoff.validate()
    material = get_material(takeoff.material_id)
    waste = material.default_waste if takeoff.waste_override is None else takeoff.waste_override
    base = takeoff.area_m2 / material.coverage_per_unit
    purchase = base * (1 + waste)
    if round_purchase and material.purchase_unit in {"piece", "liter", "kg"}:
        purchase = float(ceil(purchase))
    return BomLine(
        material_id=material.id,
        name_en=material.name_en,
        name_ar=material.name_ar,
        purchase_unit=material.purchase_unit,
        base_quantity=round(base, 4),
        waste_fraction=waste,
        purchase_quantity=round(purchase, 4),
    )


def estimate_bom(
    takeoffs: Iterable[SurfaceTakeoff],
    prices: dict[str, float] | None = None,
    currency: str | None = None,
) -> tuple[list[BomLine], float | None]:
    prices = prices or {}
    if prices and (not currency or len(currency) > 8):
        raise ValueError("currency is required when prices are supplied")
    aggregate: dict[str, tuple[float, float | None]] = {}
    for takeoff in takeoffs:
        takeoff.validate()
        area, override = aggregate.get(takeoff.material_id, (0.0, takeoff.waste_override))
        if override is not None and takeoff.waste_override is not None and override != takeoff.waste_override:
            raise ValueError("mixed waste overrides for one material are ambiguous")
        aggregate[takeoff.material_id] = (area + takeoff.area_m2, override or takeoff.waste_override)

    lines: list[BomLine] = []
    total = 0.0
    has_price = False
    for material_id in sorted(aggregate):
        area, override = aggregate[material_id]
        line = estimate_surface(SurfaceTakeoff(material_id, area, override))
        unit_price = prices.get(material_id)
        if unit_price is not None:
            if not isfinite(unit_price) or unit_price < 0:
                raise ValueError(f"invalid price for {material_id}")
            has_price = True
            line_total = round(line.purchase_quantity * unit_price, 2)
            total += line_total
            line = BomLine(
                **{**line.__dict__, "unit_price": unit_price, "currency": currency, "line_total": line_total}
            )
        lines.append(line)
    return lines, round(total, 2) if has_price else None
