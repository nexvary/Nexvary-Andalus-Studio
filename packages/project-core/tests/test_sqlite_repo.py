from pathlib import Path

import pytest

from andalus_project_core import ProjectConflictError, ProjectSnapshot, SQLiteProjectRepository


def test_transactional_revision_chain(tmp_path: Path):
    repo = SQLiteProjectRepository(tmp_path / "projects.sqlite3")
    first = ProjectSnapshot("villa-1", 1, {"name": "أندلس", "locks": {"massing": True}})
    head1 = repo.save(first)
    second = ProjectSnapshot(
        "villa-1",
        2,
        {"name": "أندلس 2"},
        parent_fingerprint=head1.fingerprint,
    )
    head2 = repo.save(second, expected_parent=head1.fingerprint)
    assert head2.revision == 2
    assert repo.load("villa-1").payload["name"] == "أندلس 2"
    assert [item.revision for item in repo.history("villa-1")] == [2, 1]


def test_stale_writer_is_rejected(tmp_path: Path):
    repo = SQLiteProjectRepository(tmp_path / "projects.sqlite3")
    head = repo.save(ProjectSnapshot("p", 1, {"v": 1}))
    repo.save(
        ProjectSnapshot("p", 2, {"v": 2}, parent_fingerprint=head.fingerprint),
        expected_parent=head.fingerprint,
    )
    stale = ProjectSnapshot("p", 3, {"v": 3}, parent_fingerprint=head.fingerprint)
    with pytest.raises(ProjectConflictError):
        repo.save(stale, expected_parent=head.fingerprint)


def test_first_revision_must_be_one(tmp_path: Path):
    repo = SQLiteProjectRepository(tmp_path / "projects.sqlite3")
    with pytest.raises(ProjectConflictError):
        repo.save(ProjectSnapshot("p", 2, {"v": 2}))


def test_list_projects(tmp_path: Path):
    repo = SQLiteProjectRepository(tmp_path / "projects.sqlite3")
    repo.save(ProjectSnapshot("a", 1, {}))
    repo.save(ProjectSnapshot("b", 1, {}))
    assert {p.project_id for p in repo.list_projects()} == {"a", "b"}
