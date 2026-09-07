from fastapi.testclient import TestClient

from nexvary_andalus_api import app

client = TestClient(app)


def test_health() -> None:
    response = client.get("/health")
    assert response.status_code == 200
    payload = response.json()
    assert payload["status"] == "ok"
    assert payload["service"] == "nexvary-andalus-ai-api"


def test_star_pattern_endpoint() -> None:
    response = client.get("/v1/patterns/star?points=8&outer_radius=100&inner_radius=45")
    assert response.status_code == 200
    payload = response.json()
    assert payload["pattern_type"] == "star"
    assert payload["points"] == 8
    assert payload["svg"].startswith("<svg")


def test_invalid_star_geometry_is_rejected() -> None:
    response = client.get("/v1/patterns/star?outer_radius=50&inner_radius=60")
    assert response.status_code == 422
