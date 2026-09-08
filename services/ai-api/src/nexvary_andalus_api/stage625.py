from __future__ import annotations

from dataclasses import asdict
from typing import Any, Literal

from andalus_export import DxfWall, bom_to_csv, walls_to_dxf
from andalus_materials import SurfaceTakeoff, estimate_bom, estimate_surface, list_materials
from andalus_project_core import ProjectSnapshot, project_fingerprint
from fastapi import APIRouter, HTTPException
from fastapi.responses import PlainTextResponse
from pydantic import BaseModel, Field

from .orchestrator import compile_generation_plan, validate_locked_candidate

router = APIRouter(prefix="/v1", tags=["stage-625"])


class FingerprintRequest(BaseModel):
    project_id: str = Field(min_length=1, max_length=120)
    revision: int = Field(ge=1)
    payload: dict[str, Any]
    parent_fingerprint: str | None = None


class SurfaceRequest(BaseModel):
    material_id: str
    area_m2: float = Field(ge=0, le=1_000_000)
    waste_override: float | None = Field(default=None, ge=0, le=0.5)


class BomTakeoffModel(BaseModel):
    material_id: str
    area_m2: float = Field(ge=0, le=1_000_000)
    waste_override: float | None = Field(default=None, ge=0, le=0.5)


class BomRequest(BaseModel):
    takeoffs: list[BomTakeoffModel] = Field(max_length=5000)
    prices: dict[str, float] = Field(default_factory=dict)
    currency: str | None = Field(default=None, max_length=8)


class DxfWallModel(BaseModel):
    x1: float
    y1: float
    x2: float
    y2: float
    layer: str = Field(default="WALLS", max_length=31)


class DxfRequest(BaseModel):
    units: Literal["metric", "imperial"] = "metric"
    walls: list[DxfWallModel] = Field(min_length=1, max_length=5000)


class GenerationPlanRequest(BaseModel):
    style_school: str = Field(min_length=1, max_length=80)
    space_type: str = Field(min_length=1, max_length=80)
    source_kind: Literal["photo", "floorplan", "scan", "blank"]
    locks: dict[str, bool] = Field(default_factory=dict)
    user_request: str = Field(default="", max_length=4000)


class CandidateValidationRequest(BaseModel):
    baseline: dict[str, Any]
    candidate: dict[str, Any]
    locks: dict[str, bool]


@router.get("/materials")
def materials_catalog() -> dict[str, Any]:
    return {"materials": [asdict(material) for material in list_materials()]}


@router.post("/projects/fingerprint")
def fingerprint_project(request: FingerprintRequest) -> dict[str, Any]:
    try:
        snapshot = ProjectSnapshot(
            project_id=request.project_id,
            revision=request.revision,
            payload=request.payload,
            parent_fingerprint=request.parent_fingerprint,
        )
        return {
            "projectFingerprint": project_fingerprint(request.payload),
            "revisionFingerprint": snapshot.fingerprint,
        }
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc


@router.post("/quantities/surface")
def surface_quantity(request: SurfaceRequest) -> dict[str, Any]:
    try:
        return asdict(
            estimate_surface(
                SurfaceTakeoff(request.material_id, request.area_m2, request.waste_override)
            )
        )
    except (KeyError, ValueError) as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc


@router.post("/bom/estimate")
def bom_estimate(request: BomRequest) -> dict[str, Any]:
    try:
        lines, total = estimate_bom(
            [SurfaceTakeoff(item.material_id, item.area_m2, item.waste_override) for item in request.takeoffs],
            prices=request.prices,
            currency=request.currency,
        )
    except (KeyError, ValueError) as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return {"lines": [asdict(line) for line in lines], "total": total, "currency": request.currency}


@router.post("/exports/bom.csv", response_class=PlainTextResponse)
def export_bom_csv(request: BomRequest) -> str:
    try:
        lines, _ = estimate_bom(
            [SurfaceTakeoff(item.material_id, item.area_m2, item.waste_override) for item in request.takeoffs],
            prices=request.prices,
            currency=request.currency,
        )
    except (KeyError, ValueError) as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return bom_to_csv([asdict(line) for line in lines])


@router.post("/exports/plan.dxf", response_class=PlainTextResponse)
def export_plan_dxf(request: DxfRequest) -> str:
    try:
        return walls_to_dxf(
            [DxfWall(wall.x1, wall.y1, wall.x2, wall.y2, wall.layer) for wall in request.walls],
            units=request.units,
        )
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc


@router.post("/ai/plan")
def ai_plan(request: GenerationPlanRequest) -> dict[str, Any]:
    try:
        return asdict(
            compile_generation_plan(
                style_school=request.style_school,
                space_type=request.space_type,
                source_kind=request.source_kind,
                locks=request.locks,
                user_request=request.user_request,
            )
        )
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc


@router.post("/ai/validate-candidate")
def validate_candidate(request: CandidateValidationRequest) -> dict[str, Any]:
    try:
        return asdict(validate_locked_candidate(request.baseline, request.candidate, request.locks))
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
