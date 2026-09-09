from fastapi.testclient import TestClient

from nexvary_andalus_api.app import app

client = TestClient(app)


def test_secure_import_accepts_valid_png_and_returns_digest():
    payload = b"\x89PNG\r\n\x1a\n" + b"andalus"
    response = client.post(
        "/v1/security/imports/inspect?filename=facade.png",
        content=payload,
        headers={"content-type": "application/octet-stream"},
    )
    assert response.status_code == 200
    body = response.json()
    assert body["accepted"] is True
    assert body["stored_name"].endswith(".png")
    assert len(body["sha256"]) == 64


def test_secure_import_rejects_spoofed_extension():
    response = client.post(
        "/v1/security/imports/inspect?filename=plan.pdf",
        content=b"not-a-pdf",
        headers={"content-type": "application/octet-stream"},
    )
    assert response.status_code == 422
    assert response.json()["detail"]["reason"] == "signature_mismatch"


def test_secure_import_rejects_path_like_filename():
    payload = b"\x89PNG\r\n\x1a\n" + b"andalus"
    response = client.post(
        "/v1/security/imports/inspect?filename=..%2Fevil.png",
        content=payload,
        headers={"content-type": "application/octet-stream"},
    )
    assert response.status_code == 422
    assert response.json()["detail"]["reason"] == "unsafe_filename"


def test_remote_import_preflight_blocks_local_targets():
    response = client.post(
        "/v1/security/imports/check-remote",
        json={"url": "https://127.0.0.1/model.glb"},
    )
    assert response.status_code == 200
    assert response.json()["accepted"] is False


def test_remote_import_preflight_allows_public_https_hostname():
    response = client.post(
        "/v1/security/imports/check-remote",
        json={"url": "https://example.com/model.glb"},
    )
    assert response.status_code == 200
    assert response.json()["accepted"] is True
