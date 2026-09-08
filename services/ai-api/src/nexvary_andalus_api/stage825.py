from __future__ import annotations

import os
from dataclasses import asdict
from functools import lru_cache
from pathlib import Path
from typing import Any, Literal

from andalus_assets import DEFAULT_ASSETS
from andalus_project_core import ProjectConflictError, ProjectSnapshot, SQLiteProjectRepository
from andalus_scene import GltfWall, walls_to_gltf
from fastapi import APIRouter, HTTPException, Query
from fastapi.responses import PlainTextResponse
from pydantic import BaseModel, Field

from .model_runtime import DryRunAdapter, JobManager, JobStatus, configured_adapters, job_to_dict
from .orchestrator import compile_generation_plan

router = APIRouter(prefix="/v1", tags=["stage-825"])
_jobs = JobManager()


@lru_cache(maxsize=1)
def _repo() -> SQLiteProjectRepository:
    configured = os.getenv("ANDALUS_PROJECT_DB", ".andalus-data/projects.sqlite3")
    return SQLiteProjectRepository(Path(configured))


class ProjectRevisionRequest(BaseModel):
    project_id: str = Field(min_length=1, max_length=120)
    revision: int = Field(ge=1)
    payload: dict[str, Any]
    parent_fingerprint: str | None = Field(default=None, min_length=64, max_length=64)
    expected_parent: str | None = Field(default=None, min_length=64, max_length=64)


class GltfWallModel(BaseModel):
    x1: float
    z1: float
    x2: float
    z2: float
    thickness: float = Field(default=0.20, gt=0, le=5)
    height: float = Field(default=3.0, gt=0, le=50)


class GltfRequest(BaseModel):
    walls: list[GltfWallModel] = Field(min_length=1, max_length=5000)
    project_fingerprint: str | None = Field(default=None, min_length=64, max_length=64)


class DryRunJobRequest(BaseModel):
    style_school: str = Field(min_length=1, max_length=80)
    space_type: str = Field(min_length=1, max_length=80)
    source_kind: Literal["photo", "floorplan", "scan", "blank"]
    locks: dict[str, bool] = Field(default_factory=dict)
    user_request: str = Field(default="", max_length=4000)
    idempotency_key: str | None = Field(default=None, min_length=1, max_length=200)


@router.get("/projects")
def list_projects(limit: int = Query(default=100, ge=1, le=1000)) -> dict[str, Any]:
    return {"projects": [asdict(item) for item in _repo().list_projects(limit=limit)]}


@router.post("/projects/revisions")
def save_project_revision(request: ProjectRevisionRequest) -> dict[str, Any]:
    snapshot = ProjectSnapshot(
        project_id=request.project_id,
        revision=request.revision,
        payload=request.payload,
        parent_fingerprint=request.parent_fingerprint,
    )
    try:
        head = _repo().save(snapshot, expected_parent=request.expected_parent)
    except ProjectConflictError as exc:
        raise HTTPException(status_code=409, detail=str(exc)) from exc
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    return asdict(head)


@router.get("/projects/{project_id}")
def load_project(
    project_id: str,
    revision: int | None = Query(default=None, ge=1),
) -> dict[str, Any]:
    try:
        return _repo().load(project_id, revision).as_record()
    except KeyError as exc:
        raise HTTPException(status_code=404, detail=str(exc)) from exc
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc


@router.get("/projects/{project_id}/history")
def project_history(
    project_id: str,
    limit: int = Query(default=100, ge=1, le=1000),
) -> dict[str, Any]:
    try:
        return {"history": [asdict(item) for item in _repo().history(project_id, limit=limit)]}
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc


@router.get("/assets")
def asset_catalog(category: str | None = Query(default=None, max_length=80)) -> dict[str, Any]:
    return {"assets": [item.public_record() for item in DEFAULT_ASSETS.search(category=category)]}


@router.get("/ai/adapters")
def ai_adapters() -> dict[str, Any]:
    return {"adapters": [asdict(item) for item in configured_adapters()]}


@router.post("/ai/jobs/dry-run")
def submit_dry_run(request: DryRunJobRequest) -> dict[str, Any]:
    try:
        plan = compile_generation_plan(
            style_school=request.style_school,
            space_type=request.space_type,
            source_kind=request.source_kind,
            locks=request.locks,
            user_request=request.user_request,
        )
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
    try:
        record = _jobs.create(
            "dry-run",
            idempotency_key=request.idempotency_key,
            request_digest=plan.request_digest,
        )
    except ValueError as exc:
        raise HTTPException(status_code=409, detail=str(exc)) from exc
    if record.status == JobStatus.QUEUED:
        _jobs.transition(record.job_id, JobStatus.RUNNING)
        result = DryRunAdapter().submit(asdict(plan))
        _jobs.transition(
            record.job_id,
            JobStatus.SUCCEEDED,
            provider_job_id="dry-run",
            result=result,
        )
    return job_to_dict(_jobs.get(record.job_id))


@router.get("/ai/jobs/{job_id}")
def get_ai_job(job_id: str) -> dict[str, Any]:
    try:
        return job_to_dict(_jobs.get(job_id))
    except KeyError as exc:
        raise HTTPException(status_code=404, detail=str(exc)) from exc


@router.post("/exports/scene.gltf", response_class=PlainTextResponse)
def export_scene_gltf(request: GltfRequest) -> str:
    try:
        return walls_to_gltf(
            [
                GltfWall(w.x1, w.z1, w.x2, w.z2, w.thickness, w.height)
                for w in request.walls
            ],
            project_fingerprint=request.project_fingerprint,
        )
    except ValueError as exc:
        raise HTTPException(status_code=422, detail=str(exc)) from exc
