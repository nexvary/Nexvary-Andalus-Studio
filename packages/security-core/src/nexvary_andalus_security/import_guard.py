from __future__ import annotations

from dataclasses import dataclass
from hashlib import sha256
from ipaddress import ip_address
from pathlib import PurePath
from urllib.parse import urlparse


@dataclass(frozen=True)
class ImportPolicy:
    allowed_extensions: frozenset[str]
    max_bytes: int


@dataclass(frozen=True)
class ImportDecision:
    accepted: bool
    reason: str
    stored_name: str | None
    sha256: str | None
    size_bytes: int


DEFAULT_POLICY = ImportPolicy(
    allowed_extensions=frozenset({".jpg", ".jpeg", ".png", ".webp", ".pdf", ".dxf", ".ifc", ".glb", ".gltf"}),
    max_bytes=64 * 1024 * 1024,
)


def _extension(filename: str) -> str:
    return PurePath(filename).suffix.lower()


def _signature_matches(ext: str, data: bytes) -> bool:
    head = data[:64]
    stripped = data[:4096].lstrip()
    if ext in {".jpg", ".jpeg"}:
        return data.startswith(b"\xff\xd8\xff")
    if ext == ".png":
        return data.startswith(b"\x89PNG\r\n\x1a\n")
    if ext == ".webp":
        return len(data) >= 12 and data[:4] == b"RIFF" and data[8:12] == b"WEBP"
    if ext == ".pdf":
        return head.startswith(b"%PDF-")
    if ext == ".glb":
        return head.startswith(b"glTF")
    if ext == ".ifc":
        return b"ISO-10303-21;" in data[:4096].upper()
    if ext == ".dxf":
        upper = data[:8192].upper()
        return b"SECTION" in upper and b"EOF" in data[-512:].upper()
    if ext == ".gltf":
        return stripped.startswith(b"{") and b'"asset"' in stripped[:2048]
    return False


def inspect_upload(
    filename: str,
    data: bytes,
    policy: ImportPolicy = DEFAULT_POLICY,
) -> ImportDecision:
    ext = _extension(filename)
    size = len(data)

    if ext not in policy.allowed_extensions:
        return ImportDecision(False, "extension_not_allowed", None, None, size)
    if size == 0:
        return ImportDecision(False, "empty_file", None, None, size)
    if size > policy.max_bytes:
        return ImportDecision(False, "file_too_large", None, None, size)
    if not _signature_matches(ext, data):
        return ImportDecision(False, "signature_mismatch", None, None, size)

    digest = sha256(data).hexdigest()
    stored_name = f"{digest}{ext}"
    return ImportDecision(True, "accepted", stored_name, digest, size)


def safe_remote_url(url: str) -> bool:
    """Reject obvious SSRF targets before the fetch layer resolves DNS.

    The network fetcher must still resolve the hostname and re-check every resolved
    address immediately before connecting to prevent DNS rebinding.
    """

    parsed = urlparse(url)
    if parsed.scheme != "https" or not parsed.hostname:
        return False

    host = parsed.hostname.lower().rstrip(".")
    if host in {"localhost", "localhost.localdomain"} or host.endswith(".local"):
        return False

    try:
        address = ip_address(host)
    except ValueError:
        return True

    return not (
        address.is_private
        or address.is_loopback
        or address.is_link_local
        or address.is_multicast
        or address.is_reserved
        or address.is_unspecified
    )
