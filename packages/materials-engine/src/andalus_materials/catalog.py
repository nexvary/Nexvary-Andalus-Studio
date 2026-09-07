from __future__ import annotations

from dataclasses import dataclass
from typing import Literal

Category = Literal["tile", "plaster", "wood", "stone", "metal", "paint", "glass"]
Unit = Literal["m2", "m", "piece", "kg", "liter"]


@dataclass(frozen=True)
class Material:
    id: str
    name_en: str
    name_ar: str
    category: Category
    purchase_unit: Unit
    coverage_per_unit: float
    default_waste: float
    notes: str = ""

    def validate(self) -> None:
        if not self.id or any(ch not in "abcdefghijklmnopqrstuvwxyz0123456789-_" for ch in self.id):
            raise ValueError("material id must be lowercase slug text")
        if self.coverage_per_unit <= 0:
            raise ValueError("coverage_per_unit must be positive")
        if not 0 <= self.default_waste <= 0.5:
            raise ValueError("default_waste must be between 0 and 0.5")


_MATERIALS = (
    Material("zellij-handmade", "Handmade zellij", "زليج يدوي", "tile", "m2", 1.0, 0.12),
    Material("encaustic-tile", "Geometric encaustic tile", "بلاط هندسي", "tile", "m2", 1.0, 0.10),
    Material("carved-gypsum", "Carved gypsum", "جبس محفور", "plaster", "m2", 1.0, 0.15),
    Material("cedar-screen", "Cedar lattice screen", "مشربية أرز", "wood", "m2", 1.0, 0.18),
    Material("cedar-beam", "Decorative cedar beam", "عارضة أرز زخرفية", "wood", "m", 1.0, 0.10),
    Material("marble-cladding", "Marble cladding", "كسوة رخام", "stone", "m2", 1.0, 0.10),
    Material("brass-inlay", "Brass inlay", "تطعيم نحاس", "metal", "m", 1.0, 0.15),
    Material("lime-paint", "Mineral lime paint", "دهان جيري معدني", "paint", "liter", 8.0, 0.08),
    Material("colored-glass", "Colored architectural glass", "زجاج معماري ملون", "glass", "m2", 1.0, 0.10),
)

for _material in _MATERIALS:
    _material.validate()

catalog: dict[str, Material] = {material.id: material for material in _MATERIALS}


def get_material(material_id: str) -> Material:
    try:
        return catalog[material_id]
    except KeyError as exc:
        raise KeyError(f"unknown material: {material_id}") from exc


def list_materials() -> tuple[Material, ...]:
    return _MATERIALS
