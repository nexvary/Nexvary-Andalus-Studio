import pytest

from andalus_architecture import Courtyard, HorseshoeArch, horseshoe_arch_svg


def test_horseshoe_arch_area_positive():
    arch = HorseshoeArch(width=2.4, clear_height=3.2, spring_height=1.9)
    assert arch.approximate_clear_area > 0


def test_horseshoe_arch_svg():
    svg = horseshoe_arch_svg(HorseshoeArch())
    assert svg.startswith("<svg")
    assert "<path" in svg


def test_horseshoe_arch_bad_spring_rejected():
    with pytest.raises(ValueError):
        HorseshoeArch(clear_height=3, spring_height=3.2).validate()


def test_courtyard_areas_balance():
    courtyard = Courtyard(width=10, length=12, arcade_depth=1.5, fountain_diameter=1.2)
    assert courtyard.total_area == pytest.approx(courtyard.arcade_area + courtyard.inner_court_area)


def test_courtyard_rejects_oversized_fountain():
    with pytest.raises(ValueError):
        Courtyard(
            width=6,
            length=6,
            arcade_depth=1.5,
            fountain_diameter=3.0,
            walkway_clearance=1.0,
        ).validate()
