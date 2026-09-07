import pytest

from andalus_materials import SurfaceTakeoff, estimate_bom, estimate_surface, list_materials


def test_catalog_has_andalusian_materials() -> None:
    ids = {material.id for material in list_materials()}
    assert {"zellij-handmade", "carved-gypsum", "cedar-screen"} <= ids


def test_zellij_takeoff_includes_default_waste() -> None:
    line = estimate_surface(SurfaceTakeoff("zellij-handmade", 10.0))
    assert line.base_quantity == 10.0
    assert line.purchase_quantity == 11.2


def test_lime_paint_uses_coverage() -> None:
    line = estimate_surface(SurfaceTakeoff("lime-paint", 80.0, 0.0))
    assert line.base_quantity == 10.0
    assert line.purchase_unit == "liter"


def test_rejects_negative_area() -> None:
    with pytest.raises(ValueError):
        estimate_surface(SurfaceTakeoff("zellij-handmade", -1.0))


def test_bom_aggregates_same_material() -> None:
    lines, total = estimate_bom(
        [SurfaceTakeoff("zellij-handmade", 5), SurfaceTakeoff("zellij-handmade", 5)]
    )
    assert len(lines) == 1
    assert lines[0].purchase_quantity == 11.2
    assert total is None


def test_bom_uses_only_user_supplied_prices() -> None:
    lines, total = estimate_bom(
        [SurfaceTakeoff("zellij-handmade", 10)],
        prices={"zellij-handmade": 100.0},
        currency="EGP",
    )
    assert total == 1120.0
    assert lines[0].currency == "EGP"


def test_prices_require_currency() -> None:
    with pytest.raises(ValueError, match="currency"):
        estimate_bom([SurfaceTakeoff("zellij-handmade", 1)], prices={"zellij-handmade": 1.0})


def test_unknown_material_is_rejected() -> None:
    with pytest.raises(KeyError):
        estimate_surface(SurfaceTakeoff("unknown", 2))
