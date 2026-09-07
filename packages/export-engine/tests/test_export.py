import csv
import io

import pytest

from andalus_export import DxfWall, bom_to_csv, build_manifest, walls_to_dxf


def test_dxf_contains_line_entities_and_units() -> None:
    text = walls_to_dxf([DxfWall(0, 0, 5, 0), DxfWall(5, 0, 5, 4)])
    assert text.count("\nLINE\n") == 2
    assert "$INSUNITS" in text
    assert text.endswith("0\nEOF\n")


def test_dxf_rejects_zero_length_wall() -> None:
    with pytest.raises(ValueError, match="zero length"):
        walls_to_dxf([DxfWall(1, 1, 1, 1)])


def test_dxf_rejects_empty_export() -> None:
    with pytest.raises(ValueError):
        walls_to_dxf([])


def test_bom_csv_round_trip_arabic() -> None:
    text = bom_to_csv(
        [
            {
                "material_id": "zellij-handmade",
                "name_en": "Handmade zellij",
                "name_ar": "زليج يدوي",
                "purchase_unit": "m2",
                "base_quantity": 10,
                "waste_fraction": 0.12,
                "purchase_quantity": 11.2,
            }
        ]
    )
    rows = list(csv.DictReader(io.StringIO(text)))
    assert rows[0]["name_ar"] == "زليج يدوي"


def test_manifest_is_deterministic() -> None:
    digest = "a" * 64
    a = build_manifest("p1", digest, ["plan.dxf", "bom.csv"])
    b = build_manifest("p1", digest, ["bom.csv", "plan.dxf"])
    assert a.manifest_fingerprint == b.manifest_fingerprint
    assert "verify" in a.disclaimer.lower()


def test_manifest_requires_output() -> None:
    with pytest.raises(ValueError):
        build_manifest("p1", "a" * 64, [])
