from __future__ import annotations

import json
import os
import tempfile
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from .models import ProjectSnapshot


@dataclass
class RevisionStore:
    root: Path

    def __post_init__(self) -> None:
        self.root = Path(self.root)

    def _project_dir(self, project_id: str) -> Path:
        ProjectSnapshot(project_id=project_id, revision=1, payload={}).validate()
        return self.root / project_id

    def save(self, snapshot: ProjectSnapshot) -> Path:
        snapshot.validate()
        directory = self._project_dir(snapshot.project_id)
        directory.mkdir(parents=True, exist_ok=True)
        target = directory / f"{snapshot.revision:06d}.json"
        if target.exists():
            raise FileExistsError(f"revision {snapshot.revision} already exists")

        record = snapshot.as_record()
        encoded = json.dumps(record, ensure_ascii=False, sort_keys=True, indent=2) + "\n"
        fd, temporary = tempfile.mkstemp(prefix=".revision-", suffix=".tmp", dir=directory)
        try:
            with os.fdopen(fd, "w", encoding="utf-8", newline="\n") as handle:
                handle.write(encoded)
                handle.flush()
                os.fsync(handle.fileno())
            os.replace(temporary, target)
        finally:
            if os.path.exists(temporary):
                os.unlink(temporary)
        return target

    def load(self, project_id: str, revision: int) -> ProjectSnapshot:
        if revision < 1:
            raise ValueError("revision must be >= 1")
        path = self._project_dir(project_id) / f"{revision:06d}.json"
        with path.open("r", encoding="utf-8") as handle:
            record: dict[str, Any] = json.load(handle)
        snapshot = ProjectSnapshot(
            project_id=record["projectId"],
            revision=record["revision"],
            payload=record["payload"],
            parent_fingerprint=record.get("parentFingerprint"),
            created_at=record.get("createdAt"),
        )
        if record.get("fingerprint") != snapshot.fingerprint:
            raise ValueError("revision fingerprint mismatch")
        return snapshot

    def history(self, project_id: str) -> list[int]:
        directory = self._project_dir(project_id)
        if not directory.exists():
            return []
        revisions: list[int] = []
        for path in directory.glob("[0-9][0-9][0-9][0-9][0-9][0-9].json"):
            revisions.append(int(path.stem))
        return sorted(revisions)
