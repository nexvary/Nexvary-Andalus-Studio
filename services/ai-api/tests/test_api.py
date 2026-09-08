from fastapi.testclient import TestClient

from nexvary_andalus_api import app

client = TestClient(app)


def test_health():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json()["version"] == "0.8.25"


def test_styles_are_bilingual():
    response = client.get("/v1/styles")
    assert response.status_code == 200
    styles = response.json()
    assert len(styles) >= 7
    assert all(style["name_ar"] and style["name_en"] for style in styles)


def test_pattern_catalog():
    response = client.get("/v1/patterns")
    assert response.status_code == 200
    ids = {item["id"] for item in response.json()["patterns"]}
    assert "zellij-grid" in ids


def test_zellij_endpoint():
    response = client.get("/v1/patterns/zellij?cells=2&star_points=8")
    assert response.status_code == 200
    assert response.json()["svg"].count("<polygon") == 4


def test_prompt_respects_architectural_locks():
    response = client.post(
        "/v1/design/prompt",
        json={
            "style_school": "nasrid-granada",
            "space_type": "facade",
            "ornament_intensity": "luxury",
            "user_request": "Add a refined entrance arch",
            "locks": {"massing": True, "openings": True, "entrance": False},
        },
    )
    assert response.status_code == 200
    payload = response.json()
    assert "overall building massing" in payload["preserved"]
    assert "main entrance geometry and position" in payload["unlocked"]


def test_unknown_style_rejected():
    response = client.post(
        "/v1/design/prompt",
        json={"style_school": "made-up", "space_type": "facade"},
    )
    assert response.status_code == 422


def test_floorplan_analyze():
    response = client.post(
        "/v1/floorplans/analyze",
        json={
            "units": "metric",
            "walls": [
                {
                    "start": {"x": 0, "y": 0},
                    "end": {"x": 5, "y": 0},
                    "thickness": 0.2,
                }
            ],
            "rooms": [
                {
                    "name": "Courtyard",
                    "polygon": [
                        {"x": 0, "y": 0},
                        {"x": 5, "y": 0},
                        {"x": 5, "y": 4},
                        {"x": 0, "y": 4},
                    ],
                }
            ],
        },
    )
    assert response.status_code == 200
    assert response.json()["summary"]["totalRoomArea"] == 20


def test_horseshoe_arch_endpoint():
    response = client.get(
        "/v1/elements/horseshoe-arch?width=2.4&clear_height=3.2&spring_height=1.9"
    )
    assert response.status_code == 200
    payload = response.json()
    assert payload["element_type"] == "horseshoe-arch"
    assert "<path" in payload["svg"]
    assert payload["metrics"]["approximateClearArea"] > 0


def test_courtyard_endpoint():
    response = client.post(
        "/v1/elements/courtyard",
        json={
            "width": 10,
            "length": 12,
            "arcade_depth": 1.5,
            "fountain_diameter": 1.2,
            "walkway_clearance": 1.0,
        },
    )
    assert response.status_code == 200
    assert response.json()["metrics"]["totalArea"] == 120


def test_stage625_material_takeoff_endpoint():
    response = client.post(
        "/v1/quantities/surface",
        json={"material_id": "zellij-handmade", "area_m2": 10},
    )
    assert response.status_code == 200
    assert response.json()["purchase_quantity"] == 11.2


def test_stage625_ai_candidate_lock_rejection():
    response = client.post(
        "/v1/ai/validate-candidate",
        json={
            "baseline": {"geometry": {"massingDigest": "abc"}},
            "candidate": {"geometry": {"massingDigest": "xyz"}},
            "locks": {"massing": True},
        },
    )
    assert response.status_code == 200
    assert response.json()["accepted"] is False
    assert "geometry.massingDigest" in response.json()["violations"]


def test_stage625_dxf_export_endpoint():
    response = client.post(
        "/v1/exports/plan.dxf",
        json={
            "units": "metric",
            "walls": [
                {"x1": 0, "y1": 0, "x2": 5, "y2": 0},
                {"x1": 5, "y1": 0, "x2": 5, "y2": 4},
            ],
        },
    )
    assert response.status_code == 200
    assert "LINE" in response.text
    assert response.text.endswith("EOF\n")
