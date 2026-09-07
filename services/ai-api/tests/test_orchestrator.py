import pytest

from nexvary_andalus_api.orchestrator import (
    compile_generation_plan,
    generation_provenance,
    validate_locked_candidate,
)


def test_photo_plan_requests_depth_and_edges_for_locked_massing() -> None:
    plan = compile_generation_plan(
        style_school="nasrid-granada",
        space_type="facade",
        source_kind="photo",
        locks={"massing": True, "openings": True},
    )
    assert "depth" in plan.controls
    assert "edges" in plan.controls
    assert "opening-mask" in plan.controls
    assert plan.concept_only is True


def test_floorplan_plan_requests_geometry_control() -> None:
    plan = compile_generation_plan(
        style_school="cordoban",
        space_type="floorplan",
        source_kind="floorplan",
        locks={"roomBoundaries": True},
    )
    assert "floorplan-geometry" in plan.controls
    assert "segmentation" in plan.controls


def test_unknown_lock_is_rejected() -> None:
    with pytest.raises(ValueError, match="unknown"):
        compile_generation_plan(
            style_school="cordoban",
            space_type="facade",
            source_kind="photo",
            locks={"magic": True},
        )


def test_candidate_accepts_same_locked_geometry() -> None:
    baseline = {"geometry": {"massingDigest": "abc", "floorCount": 2}}
    candidate = {"geometry": {"massingDigest": "abc", "floorCount": 3}}
    result = validate_locked_candidate(baseline, candidate, {"massing": True, "floorCount": False})
    assert result.accepted is True


def test_candidate_rejects_locked_geometry_change() -> None:
    baseline = {"geometry": {"massingDigest": "abc"}}
    candidate = {"geometry": {"massingDigest": "xyz"}}
    result = validate_locked_candidate(baseline, candidate, {"massing": True})
    assert result.accepted is False
    assert result.violations == ("geometry.massingDigest",)


def test_missing_locked_field_is_violation() -> None:
    baseline = {"geometry": {"rooflineDigest": "a"}}
    candidate = {"geometry": {}}
    result = validate_locked_candidate(baseline, candidate, {"roofline": True})
    assert result.accepted is False


def test_generation_provenance_has_digest() -> None:
    plan = compile_generation_plan(
        style_school="cordoban",
        space_type="interior",
        source_kind="blank",
        locks={},
    )
    record = generation_provenance(
        provider="local",
        model_id="example-model",
        model_license="reviewed-license",
        source_digest="a" * 64,
        plan=plan,
    )
    assert len(record["recordDigest"]) == 64
