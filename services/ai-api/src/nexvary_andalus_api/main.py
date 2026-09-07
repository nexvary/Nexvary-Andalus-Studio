from __future__ import annotations

from dataclasses import asdict
from typing import Literal

from fastapi import FastAPI, HTTPException, Query
from pydantic import BaseModel, Field

from andalus_architecture import Courtyard, HorseshoeArch, horseshoe_arch_svg
from andalus_floorplan import FloorPlan, Opening, Point, Room, Wall, floorplan_to_svg
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

from .planner import DesignIntent, compose_design_prompt
from .styles import list_styles

app = FastAPI(
    title="Nexvary Andalus Studio API",
    version="0.4.25",
    description=(
        "Geometry, style intelligence and AI orchestration API for Andalusian "
        "and Islamic design workflows."
    ),
)


class HealthResponse(BaseModel):
    status: str
    service: str
    version: str


class PatternResponse(BaseModel):
    pattern_type: str
    parameters: dict[str, int | float | str]
    svg: str


class StyleResponse(BaseModel):
    id: str
    name_en: str
    name_ar: str
    palette: list[str]
    motifs: list[str]
    architectural_elements: list[str]


class DesignPromptRequest(BaseModel):
    style_school: str
    space_type: Literal["facade", "interior", "courtyard", "garden", "floorplan", "full-home"]
    ornament_intensity: Literal["calm", "balanced", "luxury", "royal", "historic"] = "balanced"
    user_request: str = Field(default="", max_length=2000)
    locks: dict[str, bool] = Field(default_factory=dict)


class DesignPromptResponse(BaseModel):
    style: str
    prompt: str
    negativePrompt: str
    preserved: list[str]
    unlocked: list[str]
    palette: list[str]
    motifs: list[str]
    architecturalElements: list[str]


class ArchitecturalElementResponse(BaseModel):
    element_type: str
    parameters: dict[str, int | float | str]
    svg: str | None = None
    metrics: dict[str, float] = Field(default_factory=dict)


class CourtyardRequest(BaseModel):
    width: float = Field(gt=0, le=500)
    length: float = Field(gt=0, le=500)
    arcade_depth: float = Field(default=1.8, ge=0, le=100)
    fountain_diameter: float = Field(default=1.2, gt=0, le=100)
    walkway_clearance: float = Field(default=1.2, ge=0, le=100)


class PointModel(BaseModel):
    x: float
    y: float


class WallModel(BaseModel):
    start: PointModel
    end: PointModel
    thickness: float = Field(default=0.20, gt=0, le=5)


class OpeningModel(BaseModel):
    wall_index: int = Field(ge=0)
    offset: float = Field(ge=0)
    width: float = Field(gt=0)
    kind: Literal["door", "window", "arch"] = "door"


class RoomModel(BaseModel):
    name: str = Field(min_length=1, max_length=120)
    polygon: list[PointModel] = Field(min_length=3)
    kind: str = Field(default="room", max_length=60)


class FloorPlanRequest(BaseModel):
    units: Literal["metric", "imperial"] = "metric"
    walls: list[WallModel] = Field(default_factory=list, max_length=2000)
    openings: list[OpeningModel] = Field(default_factory=list, max_length=2000)
    rooms: list[RoomModel] = Field(default_factory=list, max_length=1000)


class FloorPlanResponse(BaseModel):
    summary: dict[str, object]
    svg: str


@app.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    return HealthResponse(status="ok", service="nexvary-andalus-ai-api", version="0.4.25")


@app.get("/v1/styles", response_model=list[StyleResponse])
def styles() -> list[StyleResponse]:
    return [
        StyleResponse(
            id=style.id,
            name_en=style.name_en,
            name_ar=style.name_ar,
            palette=list(style.palette),
            motifs=list(style.motifs),
            architectural_elements=list(style.architectural_elements),
        )
        for style in list_styles()
    ]


@app.get("/v1/patterns")
def patterns_catalog() -> dict[str, object]:
    return {"patterns": available_patterns()}


@app.get("/v1/patterns/star", response_model=PatternResponse)
def star_pattern(
    points: int = Query(default=8, ge=5, le=32),
    outer_radius: float = Query(default=100.0, gt=0, le=2000),
    inner_radius: float = Query(default=45.0, gt=0, le=1999),
) -> PatternResponse:
    pattern = StarPattern(points=points, outer_radius=outer_radius, inner_radius=inner_radius)
    try:
        svg = generate_star_svg(pattern)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return PatternResponse(pattern_type="star", parameters=asdict(pattern), svg=svg)


@app.get("/v1/patterns/rosette", response_model=PatternResponse)
def rosette_pattern(
    petals: int = Query(default=8, ge=6, le=32),
    radius: float = Query(default=100.0, gt=0, le=2000),
    petal_depth: float = Query(default=0.38, ge=0.1, le=0.8),
) -> PatternResponse:
    pattern = RosettePattern(petals=petals, radius=radius, petal_depth=petal_depth)
    try:
        svg = generate_rosette_svg(pattern)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return PatternResponse(pattern_type="rosette", parameters=asdict(pattern), svg=svg)


@app.get("/v1/patterns/zellij", response_model=PatternResponse)
def zellij_pattern(
    cells: int = Query(default=4, ge=1, le=12),
    tile_size: float = Query(default=80.0, gt=0, le=500),
    star_points: int = Query(default=8, ge=5, le=24),
    inset_ratio: float = Query(default=0.45, ge=0.2, le=0.75),
) -> PatternResponse:
    pattern = ZellijGrid(
        cells=cells,
        tile_size=tile_size,
        star_points=star_points,
        inset_ratio=inset_ratio,
    )
    try:
        svg = generate_zellij_svg(pattern)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return PatternResponse(pattern_type="zellij-grid", parameters=asdict(pattern), svg=svg)


@app.get("/v1/patterns/border", response_model=PatternResponse)
def border_pattern(
    repeats: int = Query(default=8, ge=2, le=40),
    cell_size: float = Query(default=60.0, gt=0, le=500),
    motif_points: int = Query(default=8, ge=5, le=24),
) -> PatternResponse:
    pattern = BorderPattern(
        repeats=repeats,
        cell_size=cell_size,
        motif_points=motif_points,
    )
    try:
        svg = generate_border_svg(pattern)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return PatternResponse(pattern_type="border", parameters=asdict(pattern), svg=svg)


@app.post("/v1/design/prompt", response_model=DesignPromptResponse)
def design_prompt(request: DesignPromptRequest) -> DesignPromptResponse:
    try:
        result = compose_design_prompt(
            DesignIntent(
                style_school=request.style_school,
                space_type=request.space_type,
                ornament_intensity=request.ornament_intensity,
                user_request=request.user_request,
                locks=request.locks,
            )
        )
    except (KeyError, ValueError) as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return DesignPromptResponse(**result)


@app.get("/v1/elements/horseshoe-arch", response_model=ArchitecturalElementResponse)
def horseshoe_arch(
    width: float = Query(default=2.0, gt=0, le=50),
    clear_height: float = Query(default=3.0, gt=0, le=50),
    spring_height: float = Query(default=1.8, gt=0, le=49),
    wall_thickness: float = Query(default=0.25, gt=0, le=5),
    horseshoe_drop: float = Query(default=0.18, ge=0, le=0.45),
) -> ArchitecturalElementResponse:
    arch = HorseshoeArch(
        width=width,
        clear_height=clear_height,
        spring_height=spring_height,
        wall_thickness=wall_thickness,
        horseshoe_drop=horseshoe_drop,
    )
    try:
        svg = horseshoe_arch_svg(arch)
        area = arch.approximate_clear_area
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return ArchitecturalElementResponse(
        element_type="horseshoe-arch",
        parameters=asdict(arch),
        svg=svg,
        metrics={"approximateClearArea": round(area, 3)},
    )


@app.post("/v1/elements/courtyard", response_model=ArchitecturalElementResponse)
def courtyard_metrics(request: CourtyardRequest) -> ArchitecturalElementResponse:
    courtyard = Courtyard(
        width=request.width,
        length=request.length,
        arcade_depth=request.arcade_depth,
        fountain_diameter=request.fountain_diameter,
        walkway_clearance=request.walkway_clearance,
    )
    try:
        summary = courtyard.summary()
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return ArchitecturalElementResponse(
        element_type="courtyard",
        parameters={
            "width": request.width,
            "length": request.length,
            "arcade_depth": request.arcade_depth,
            "fountain_diameter": request.fountain_diameter,
            "walkway_clearance": request.walkway_clearance,
        },
        metrics=summary,
    )


def _to_floorplan(request: FloorPlanRequest) -> FloorPlan:
    return FloorPlan(
        units=request.units,
        walls=[
            Wall(
                start=Point(wall.start.x, wall.start.y),
                end=Point(wall.end.x, wall.end.y),
                thickness=wall.thickness,
            )
            for wall in request.walls
        ],
        openings=[
            Opening(
                wall_index=opening.wall_index,
                offset=opening.offset,
                width=opening.width,
                kind=opening.kind,
            )
            for opening in request.openings
        ],
        rooms=[
            Room(
                name=room.name,
                polygon=tuple(Point(point.x, point.y) for point in room.polygon),
                kind=room.kind,
            )
            for room in request.rooms
        ],
    )


@app.post("/v1/floorplans/analyze", response_model=FloorPlanResponse)
def analyze_floorplan(request: FloorPlanRequest) -> FloorPlanResponse:
    plan = _to_floorplan(request)
    try:
        summary = plan.summary()
        svg = floorplan_to_svg(plan)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return FloorPlanResponse(summary=summary, svg=svg)
