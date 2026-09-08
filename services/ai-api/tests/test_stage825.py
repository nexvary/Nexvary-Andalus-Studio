import json
import os
from pathlib import Path

from fastapi import FastAPI
from fastapi.testclient import TestClient

from nexvary_andalus_api import stage825


def client_for(tmp_path: Path) -> TestClient:
    os.environ["ANDALUS_PROJECT_DB"] = str(tmp_path / "projects.sqlite3")
    stage825._repo.cache_clear()
    app = FastAPI()
    app.include_router(stage825.router)
    return TestClient(app)


def test_project_revision_api_enforces_optimistic_concurrency(tmp_path):
    client = client_for(tmp_path)
    first = client.post(
        "/v1/projects/revisions",
        json={"project_id": "p", "revision": 1, "payload": {"name": "A"}},
    )
    assert first.status_code == 200
    fingerprint = first.json()["fingerprint"]
    second = client.post(
        "/v1/projects/revisions",
        json={
            "project_id": "p",
            "revision": 2,
            "payload": {"name": "B"},
            "parent_fingerprint": fingerprint,
            "expected_parent": fingerprint,
        },
    )
    assert second.status_code == 200
    stale = client.post(
        "/v1/projects/revisions",
        json={
            "project_id": "p",
            "revision": 3,
            "payload": {"name": "C"},
            "parent_fingerprint": fingerprint,
            "expected_parent": fingerprint,
        },
    )
    assert stale.status_code == 409


def test_assets_only_surface_reviewed_items(tmp_path):
    client = client_for(tmp_path)
    response = client.get("/v1/assets")
    assert response.status_code == 200
    assert all(asset["usableInProduct"] for asset in response.json()["assets"])


def test_dry_run_job_is_idempotent(tmp_path):
    client = client_for(tmp_path)
    payload = {
        "style_school": "nasrid-granada",
        "space_type": "facade",
        "source_kind": "photo",
        "locks": {"massing": True},
        "idempotency_key": "same",
    }
    first = client.post("/v1/ai/jobs/dry-run", json=payload)
    second = client.post("/v1/ai/jobs/dry-run", json=payload)
    assert first.status_code == 200
    assert first.json()["status"] == "succeeded"
    assert first.json()["job_id"] == second.json()["job_id"]


def test_gltf_endpoint_returns_valid_json(tmp_path):
    client = client_for(tmp_path)
    response = client.post(
        "/v1/exports/scene.gltf",
        json={"walls": [{"x1": 0, "z1": 0, "x2": 4, "z2": 0}]},
    )
    assert response.status_code == 200
    assert json.loads(response.text)["asset"]["version"] == "2.0"


def test_project_history_and_listing(tmp_path):
    client = client_for(tmp_path)
    first = client.post(
        "/v1/projects/revisions",
        json={"project_id": "hist", "revision": 1, "payload": {"v": 1}},
    ).json()
    client.post(
        "/v1/projects/revisions",
        json={
            "project_id": "hist",
            "revision": 2,
            "payload": {"v": 2},
            "parent_fingerprint": first["fingerprint"],
            "expected_parent": first["fingerprint"],
        },
    )
    history = client.get("/v1/projects/hist/history").json()["history"]
    projects = client.get("/v1/projects").json()["projects"]
    assert [row["revision"] for row in history] == [2, 1]
    assert any(row["project_id"] == "hist" and row["revision"] == 2 for row in projects)


def test_missing_project_returns_404(tmp_path):
    client = client_for(tmp_path)
    assert client.get("/v1/projects/no-such-project").status_code == 404
