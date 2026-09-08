from __future__ import annotations

import csv
import io
from collections.abc import Iterable, Mapping
from typing import Any

_COLUMNS = (
    "material_id",
    "name_en",
    "name_ar",
    "purchase_unit",
    "base_quantity",
    "waste_fraction",
    "purchase_quantity",
    "unit_price",
    "currency",
    "line_total",
)


def bom_to_csv(lines: Iterable[Mapping[str, Any]]) -> str:
    output = io.StringIO(newline="")
    writer = csv.DictWriter(output, fieldnames=_COLUMNS, extrasaction="ignore", lineterminator="\n")
    writer.writeheader()
    for line in lines:
        writer.writerow({column: line.get(column, "") for column in _COLUMNS})
    return output.getvalue()
