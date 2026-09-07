import pytest

from andalus_pattern_engine import StarPattern, generate_star_svg


def test_eight_point_star_has_sixteen_vertices() -> None:
    pattern = StarPattern()
    assert len(pattern.vertices()) == 16


def test_svg_is_deterministic_and_accessible() -> None:
    pattern = StarPattern(points=8, outer_radius=100, inner_radius=45)
    first = generate_star_svg(pattern)
    second = generate_star_svg(pattern)
    assert first == second
    assert 'aria-label="8-point geometric star"' in first
    assert first.startswith("<svg")


def test_invalid_radius_is_rejected() -> None:
    with pytest.raises(ValueError):
        generate_star_svg(StarPattern(outer_radius=50, inner_radius=50))
