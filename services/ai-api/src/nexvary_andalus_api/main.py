from fastapi import FastAPI, HTTPException, Query
from pydantic import BaseModel

from andalus_pattern_engine import StarPattern, generate_star_svg

app = FastAPI(
    title="Nexvary Andalus Studio API",
    version="0.1.0",
    description="Geometry and AI orchestration API for Andalusian and Islamic design workflows.",
)


class HealthResponse(BaseModel):
    status: str
    service: str
    version: str


class PatternResponse(BaseModel):
    pattern_type: str
    points: int
    svg: str


@app.get("/health", response_model=HealthResponse)
def health() -> HealthResponse:
    return HealthResponse(status="ok", service="nexvary-andalus-ai-api", version="0.1.0")


@app.get("/v1/patterns/star", response_model=PatternResponse)
def star_pattern(
    points: int = Query(default=8, ge=5, le=24),
    outer_radius: float = Query(default=100.0, gt=0, le=2000),
    inner_radius: float = Query(default=45.0, gt=0, le=1999),
) -> PatternResponse:
    try:
        pattern = StarPattern(
            points=points,
            outer_radius=outer_radius,
            inner_radius=inner_radius,
        )
        svg = generate_star_svg(pattern)
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc

    return PatternResponse(pattern_type="star", points=points, svg=svg)
