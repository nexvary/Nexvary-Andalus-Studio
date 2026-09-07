from __future__ import annotations

from dataclasses import dataclass, asdict
from hashlib import sha256
from pathlib import Path
import re
from typing import Iterable

_ASSET_ID = re.compile(r"^[a-z0-9][a-z0-9_.-]{0,119}$")
_ALLOWED_LICENSES = {"CC0-1.0", "CC-BY-4.0", "CC-BY-SA-4.0", "MIT", "Apache-2.0", "BSD-3-Clause", "NEXVARY-INTERNAL"}


@dataclass(frozen=True)
class AssetRecord:
    asset_id: str
    title: str
    category: str
    source_url: str
    license_id: str
    author: str | None = None
    attribution_text: str | None = None
    content_sha256: str | None = None
    commercial_use: bool = True
    reviewed: bool = False

    def validate(self) -> None:
        if not _ASSET_ID.fullmatch(self.asset_id):
            raise ValueError("asset_id contains unsupported characters")
        if not self.title.strip() or not self.category.strip():
            raise ValueError("title and category are required")
        if not self.source_url.startswith(("https://", "http://")):
            raise ValueError("source_url must be http(s)")
        if self.license_id not in _ALLOWED_LICENSES:
            raise ValueError(f"license {self.license_id} is not in the reviewed allowlist")
        if self.license_id.startswith("CC-BY") and not (self.author or self.attribution_text):
            raise ValueError("attribution is required for CC-BY assets")
        if self.content_sha256 is not None:
            if len(self.content_sha256) != 64 or any(ch not in "0123456789abcdef" for ch in self.content_sha256):
                raise ValueError("content_sha256 must be lowercase SHA-256 hex")
        if not self.commercial_use:
            raise ValueError("non-commercial assets are not allowed in the product registry")

    def public_record(self) -> dict[str, object]:
        self.validate()
        data = asdict(self)
        data["usableInProduct"] = self.reviewed and self.commercial_use
        return data


def digest_file(path: str | Path) -> str:
    h = sha256()
    with Path(path).open("rb") as handle:
        for chunk in iter(lambda: handle.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


class AssetRegistry:
    def __init__(self, assets: Iterable[AssetRecord] = ()) -> None:
        self._assets: dict[str, AssetRecord] = {}
        for asset in assets:
            self.add(asset)

    def add(self, asset: AssetRecord) -> None:
        asset.validate()
        if asset.asset_id in self._assets:
            raise ValueError(f"duplicate asset_id: {asset.asset_id}")
        if asset.content_sha256 and any(a.content_sha256 == asset.content_sha256 for a in self._assets.values() if a.content_sha256):
            raise ValueError("duplicate asset content digest")
        self._assets[asset.asset_id] = asset

    def get(self, asset_id: str, *, require_reviewed: bool = True) -> AssetRecord:
        try:
            asset = self._assets[asset_id]
        except KeyError as exc:
            raise KeyError("asset not found") from exc
        if require_reviewed and not asset.reviewed:
            raise PermissionError("asset has not passed license review")
        return asset

    def search(self, *, category: str | None = None, reviewed_only: bool = True) -> list[AssetRecord]:
        values = list(self._assets.values())
        if category is not None:
            values = [item for item in values if item.category == category]
        if reviewed_only:
            values = [item for item in values if item.reviewed]
        return sorted(values, key=lambda item: item.asset_id)


DEFAULT_ASSETS = AssetRegistry(
    [
        AssetRecord(
            asset_id="builtin.nasrid-zellij-8",
            title="Parametric Nasrid-inspired 8-point zellij",
            category="pattern",
            source_url="https://nexvary.com/andalus/builtin",
            license_id="NEXVARY-INTERNAL",
            author="Nexvary Andalus Studio",
            reviewed=True,
        ),
        AssetRecord(
            asset_id="builtin.horseshoe-arch",
            title="Parametric horseshoe arch",
            category="architecture",
            source_url="https://nexvary.com/andalus/builtin",
            license_id="NEXVARY-INTERNAL",
            author="Nexvary Andalus Studio",
            reviewed=True,
        ),
    ]
)
