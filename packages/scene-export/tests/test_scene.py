import base64
import json

import pytest

from andalus_scene import GltfWall, SceneManifest, SceneNode, Transform, walls_to_gltf


def test_scene_manifest_is_deterministic():
    manifest = SceneManifest(
        "p",
        "a" * 64,
        "metric",
        (SceneNode("wall-1", "wall", Transform((1, 0, 2))),),
    )
    assert manifest.fingerprint == manifest.fingerprint
    assert manifest.as_dict()["stage"] == 825


def test_duplicate_node_id_rejected():
    manifest = SceneManifest(
        "p",
        "a" * 64,
        "metric",
        (SceneNode("x", "wall"), SceneNode("x", "wall")),
    )
    with pytest.raises(ValueError):
        manifest.validate()


def test_gltf_contains_real_geometry_buffer():
    text = walls_to_gltf([GltfWall(0, 0, 5, 0, 0.2, 3)], project_fingerprint="b" * 64)
    data = json.loads(text)
    assert data["asset"]["version"] == "2.0"
    assert data["accessors"][0]["count"] == 8
    uri = data["buffers"][0]["uri"]
    assert len(base64.b64decode(uri.split(",", 1)[1])) == data["buffers"][0]["byteLength"]


def test_zero_length_wall_rejected():
    with pytest.raises(ValueError):
        walls_to_gltf([GltfWall(1, 1, 1, 1)])


def test_gltf_is_deterministic_for_same_walls():
    walls = [GltfWall(0, 0, 4, 0), GltfWall(4, 0, 4, 3)]
    first = walls_to_gltf(walls, project_fingerprint="c" * 64)
    second = walls_to_gltf(walls, project_fingerprint="c" * 64)
    assert first == second


def test_invalid_project_fingerprint_rejected():
    with pytest.raises(ValueError):
        walls_to_gltf([GltfWall(0, 0, 1, 0)], project_fingerprint="Z" * 64)
