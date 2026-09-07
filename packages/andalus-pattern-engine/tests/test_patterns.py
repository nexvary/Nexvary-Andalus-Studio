import pytest

from andalus_pattern_engine import (
    BorderPattern,
    RosettePattern,
    StarPattern,
    ZellijGrid,
    available_patterns,
    generate_border_svg,
    generate_rosette_svg,
    generate_star_svg,
    generate_zellij_svg,
)


def test_star_svg_is_deterministic():
    pattern = StarPattern(points=8, outer_radius=100, inner_radius=45)
    assert generate_star_svg(pattern) == generate_star_svg(pattern)


def test_star_has_expected_vertex_count():
    assert len(StarPattern(points=10).vertices()) == 20


@pytest.mark.parametrize("points", [5, 8, 12, 24, 32])
def test_star_supported_points(points):
    assert "<svg" in generate_star_svg(StarPattern(points=points))


def test_star_rejects_bad_ratio():
    with pytest.raises(ValueError):
        generate_star_svg(StarPattern(outer_radius=50, inner_radius=50))


def test_rosette_svg_contains_label():
    svg = generate_rosette_svg(RosettePattern(petals=12))
    assert "12-petal Islamic rosette" in svg


def test_rosette_rejects_bad_depth():
    with pytest.raises(ValueError):
        RosettePattern(petal_depth=0.95).validate()


def test_zellij_generates_one_star_per_cell():
    svg = generate_zellij_svg(ZellijGrid(cells=3))
    assert svg.count("<polygon") == 9


def test_border_width_tracks_repeats():
    svg = generate_border_svg(BorderPattern(repeats=5, cell_size=40))
    assert 'viewBox="0 0 200.000 40.000"' in svg


def test_pattern_catalog_has_all_foundation_patterns():
    ids = {entry["id"] for entry in available_patterns()}
    assert {"star", "rosette", "zellij-grid", "border"} <= ids
