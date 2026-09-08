import pytest

from andalus_assets import DEFAULT_ASSETS, AssetRecord, AssetRegistry


def test_default_assets_are_reviewed():
    assets = DEFAULT_ASSETS.search()
    assert len(assets) >= 2
    assert all(item.reviewed for item in assets)


def test_cc_by_requires_attribution():
    with pytest.raises(ValueError):
        AssetRecord(
            "x",
            "Tile",
            "pattern",
            "https://example.com/a",
            "CC-BY-4.0",
            reviewed=True,
        ).validate()


def test_unreviewed_asset_cannot_be_used():
    registry = AssetRegistry(
        [AssetRecord("x", "Tile", "pattern", "https://example.com/a", "MIT", reviewed=False)]
    )
    with pytest.raises(PermissionError):
        registry.get("x")


def test_noncommercial_asset_license_is_rejected():
    with pytest.raises(ValueError):
        AssetRecord(
            "x",
            "Tile",
            "pattern",
            "https://example.com/a",
            "CC-BY-NC-4.0",
            reviewed=True,
        ).validate()


def test_duplicate_digest_is_rejected():
    digest = "a" * 64
    registry = AssetRegistry(
        [
            AssetRecord(
                "a",
                "A",
                "pattern",
                "https://example.com/a",
                "MIT",
                content_sha256=digest,
                reviewed=True,
            )
        ]
    )
    with pytest.raises(ValueError):
        registry.add(
            AssetRecord(
                "b",
                "B",
                "pattern",
                "https://example.com/b",
                "MIT",
                content_sha256=digest,
                reviewed=True,
            )
        )
