from __future__ import annotations

import json
import sqlite3
from dataclasses import dataclass
from datetime import UTC, datetime
from pathlib import Path

from .models import ProjectSnapshot, canonical_json


class ProjectConflictError(RuntimeError):
    """Raised when optimistic-concurrency expectations are not satisfied."""


@dataclass(frozen=True)
class ProjectHead:
    project_id: str
    revision: int
    fingerprint: str
    updated_at: str


class SQLiteProjectRepository:
    """Transactional, append-only project revision repository.

    The database stores immutable revision records. Saving a revision requires the
    caller to provide the expected parent fingerprint once a project already has a
    head, preventing silent last-write-wins data loss.
    """

    def __init__(self, path: str | Path) -> None:
        self.path = Path(path)
        if str(self.path) != ":memory:":
            self.path.parent.mkdir(parents=True, exist_ok=True)
        self._initialize()

    def _connect(self) -> sqlite3.Connection:
        connection = sqlite3.connect(str(self.path), timeout=10, isolation_level=None)
        connection.row_factory = sqlite3.Row
        connection.execute("PRAGMA foreign_keys=ON")
        connection.execute("PRAGMA journal_mode=WAL")
        return connection

    def _initialize(self) -> None:
        with self._connect() as db:
            db.executescript(
                """
                CREATE TABLE IF NOT EXISTS projects (
                    project_id TEXT PRIMARY KEY,
                    head_revision INTEGER NOT NULL,
                    head_fingerprint TEXT NOT NULL,
                    updated_at TEXT NOT NULL
                );
                CREATE TABLE IF NOT EXISTS project_revisions (
                    project_id TEXT NOT NULL,
                    revision INTEGER NOT NULL,
                    fingerprint TEXT NOT NULL UNIQUE,
                    parent_fingerprint TEXT,
                    payload_json TEXT NOT NULL,
                    created_at TEXT NOT NULL,
                    PRIMARY KEY (project_id, revision),
                    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE
                );
                CREATE INDEX IF NOT EXISTS idx_project_revisions_fingerprint
                    ON project_revisions(fingerprint);
                """
            )

    def save(self, snapshot: ProjectSnapshot, *, expected_parent: str | None = None) -> ProjectHead:
        snapshot.validate()
        now = snapshot.created_at or datetime.now(UTC).isoformat()
        payload_json = canonical_json(snapshot.payload)
        with self._connect() as db:
            db.execute("BEGIN IMMEDIATE")
            head = db.execute(
                "SELECT head_revision, head_fingerprint FROM projects WHERE project_id=?",
                (snapshot.project_id,),
            ).fetchone()
            if head is None:
                if snapshot.revision != 1:
                    db.execute("ROLLBACK")
                    raise ProjectConflictError("first revision must be 1")
                if expected_parent is not None or snapshot.parent_fingerprint is not None:
                    db.execute("ROLLBACK")
                    raise ProjectConflictError("first revision cannot have a parent")
                db.execute(
                    "INSERT INTO projects(project_id, head_revision, head_fingerprint, updated_at) VALUES(?,?,?,?)",
                    (snapshot.project_id, 0, "", now),
                )
            else:
                current_revision = int(head["head_revision"])
                current_fingerprint = str(head["head_fingerprint"])
                if snapshot.revision != current_revision + 1:
                    db.execute("ROLLBACK")
                    raise ProjectConflictError(
                        f"revision must advance from {current_revision} to {current_revision + 1}"
                    )
                if snapshot.parent_fingerprint != current_fingerprint:
                    db.execute("ROLLBACK")
                    raise ProjectConflictError("snapshot parent fingerprint does not match project head")
                if expected_parent is not None and expected_parent != current_fingerprint:
                    db.execute("ROLLBACK")
                    raise ProjectConflictError("expected parent fingerprint does not match project head")

            db.execute(
                """INSERT INTO project_revisions
                   (project_id, revision, fingerprint, parent_fingerprint, payload_json, created_at)
                   VALUES(?,?,?,?,?,?)""",
                (
                    snapshot.project_id,
                    snapshot.revision,
                    snapshot.fingerprint,
                    snapshot.parent_fingerprint,
                    payload_json,
                    now,
                ),
            )
            db.execute(
                "UPDATE projects SET head_revision=?, head_fingerprint=?, updated_at=? WHERE project_id=?",
                (snapshot.revision, snapshot.fingerprint, now, snapshot.project_id),
            )
            db.execute("COMMIT")
        return ProjectHead(snapshot.project_id, snapshot.revision, snapshot.fingerprint, now)

    def load(self, project_id: str, revision: int | None = None) -> ProjectSnapshot:
        ProjectSnapshot(project_id=project_id, revision=1, payload={}).validate()
        with self._connect() as db:
            if revision is None:
                row = db.execute(
                    """SELECT r.* FROM project_revisions r
                       JOIN projects p ON p.project_id=r.project_id AND p.head_revision=r.revision
                       WHERE r.project_id=?""",
                    (project_id,),
                ).fetchone()
            else:
                if revision < 1:
                    raise ValueError("revision must be >= 1")
                row = db.execute(
                    "SELECT * FROM project_revisions WHERE project_id=? AND revision=?",
                    (project_id, revision),
                ).fetchone()
        if row is None:
            raise KeyError("project revision not found")
        snapshot = ProjectSnapshot(
            project_id=str(row["project_id"]),
            revision=int(row["revision"]),
            payload=json.loads(str(row["payload_json"])),
            parent_fingerprint=row["parent_fingerprint"],
            created_at=str(row["created_at"]),
        )
        if snapshot.fingerprint != row["fingerprint"]:
            raise ValueError("stored project fingerprint mismatch")
        return snapshot

    def history(self, project_id: str, *, limit: int = 100) -> list[ProjectHead]:
        if not 1 <= limit <= 1000:
            raise ValueError("limit must be between 1 and 1000")
        ProjectSnapshot(project_id=project_id, revision=1, payload={}).validate()
        with self._connect() as db:
            rows = db.execute(
                """SELECT project_id, revision, fingerprint, created_at
                   FROM project_revisions WHERE project_id=?
                   ORDER BY revision DESC LIMIT ?""",
                (project_id, limit),
            ).fetchall()
        return [ProjectHead(str(r["project_id"]), int(r["revision"]), str(r["fingerprint"]), str(r["created_at"])) for r in rows]

    def list_projects(self, *, limit: int = 100) -> list[ProjectHead]:
        if not 1 <= limit <= 1000:
            raise ValueError("limit must be between 1 and 1000")
        with self._connect() as db:
            rows = db.execute(
                "SELECT project_id, head_revision, head_fingerprint, updated_at FROM projects ORDER BY updated_at DESC LIMIT ?",
                (limit,),
            ).fetchall()
        return [ProjectHead(str(r["project_id"]), int(r["head_revision"]), str(r["head_fingerprint"]), str(r["updated_at"])) for r in rows]
