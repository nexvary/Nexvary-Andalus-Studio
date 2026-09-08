from __future__ import annotations

from dataclasses import asdict

from fastapi import APIRouter, HTTPException, Query, Request
from nexvary_andalus_security import DEFAULT_POLICY, inspect_upload, safe_remote_url
from pydantic import BaseModel, HttpUrl

router = APIRouter(prefix="/v1/security", tags=["security"])


class RemoteImportCheck(BaseModel):
    url: HttpUrl


async def _read_limited_body(request: Request, limit: int) -> bytes:
    chunks: list[bytes] = []
    total = 0
    async for chunk in request.stream():
        total += len(chunk)
        if total > limit:
            raise HTTPException(status_code=413, detail="file_too_large")
        chunks.append(chunk)
    return b"".join(chunks)


@router.post("/imports/inspect")
async def inspect_import(
    request: Request,
    filename: str = Query(min_length=1, max_length=255),
) -> dict[str, object]:
    data = await _read_limited_body(request, DEFAULT_POLICY.max_bytes)
    decision = inspect_upload(filename, data)
    if not decision.accepted:
        raise HTTPException(status_code=422, detail=asdict(decision))
    return asdict(decision)


@router.post("/imports/check-remote")
def check_remote_import(request: RemoteImportCheck) -> dict[str, object]:
    url = str(request.url)
    accepted = safe_remote_url(url)
    return {
        "accepted": accepted,
        "reason": "accepted" if accepted else "remote_url_rejected",
    }
