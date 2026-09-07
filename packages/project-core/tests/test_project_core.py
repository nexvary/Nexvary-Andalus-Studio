from pathlib import Path

import pytest

from andalus_project_core import (
    ProjectSnapshot,
    RevisionStore,
    canonical_json,
    diff_paths,
    project_fingerprint,
)


def test_canonical_json_is_order_independent() -> None:
    assert canonical_json({"b": 2, "a": 1}) == canonical_json({"a": 1, "b": 2})
    assert project_fingerprint({"b": 2, "a": 1}) == project_fingerprint({"a": 1, "b": 2})


def test_rejects_non_finite_values() -> None:
    with pytest.raises(ValueError):
        canonical_json({"bad": float("nan")})


def test_snapshot_fingerprint_changes_with_payload() -> None:
    a = ProjectSnapshot("villa-1", 1, {"name": "A"})
    b = ProjectSnapshot("villa-1", 1, {"name": "B"})
    assert a.fingerprint != b.fingerprint


def test_project_id_blocks_path_traversal() -> None:
    with pytest.raises(ValueError):
        ProjectSnapshot("../outside", 1, {}).validate()


def test_diff_paths_nested_changes() -> None:
    before = {"locks": {"massing": True}, "rooms": [1, 2]}
    after = {"locks": {"massing": False}, "rooms": [1, 3, 4]}
    changed = diff_paths(before, after)
    assert "$.locks.massing" in changed
    assert "$.rooms[1]" in changed
    assert "$.rooms.length" in changed


def test_revision_store_round_trip(tmp_path: Path) -> None:
    store = RevisionStore(tmp_path)
    first = ProjectSnapshot("house-01", 1, {"name": "بيت"})
    path = store.save(first)
    assert path.exists()
    loaded = store.load("house-01", 1)
    assert loaded.payload == {"name": "بيت"}
    assert loaded.fingerprint == first.fingerprint
    assert store.history("house-01") == [1]


def test_revision_store_refuses_overwrite(tmp_path: Path) -> None:
    store = RevisionStore(tmp_path)
    snapshot = ProjectSnapshot("house-01", 1, {})
    store.save(snapshot)
    with pytest.raises(FileExistsError):
        store.save(snapshot)


def test_revision_store_detects_tamper(tmp_path: Path) -> None:
    store = RevisionStore(tmp_path)
    snapshot = ProjectSnapshot("house-01", 1, {"x": 1})
    path = store.save(snapshot)
    text = path.read_text(encoding="utf-8").replace('"x": 1', '"x": 2')
    path.write_text(text, encoding="utf-8")
    with pytest.raises(ValueError, match="fingerprint"):
        store.load("house-01", 1)
