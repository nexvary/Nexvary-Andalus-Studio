from nexvary_andalus_security import ImportPolicy, inspect_upload, safe_remote_url


def test_png_is_content_addressed():
    payload = b"\x89PNG\r\n\x1a\n" + b"safe-image"
    decision = inspect_upload("facade.png", payload)
    assert decision.accepted is True
    assert decision.stored_name is not None
    assert decision.stored_name.endswith(".png")
    assert decision.sha256 is not None
    assert decision.stored_name.startswith(decision.sha256)


def test_path_like_filename_is_rejected():
    payload = b"\x89PNG\r\n\x1a\n" + b"safe-image"
    decision = inspect_upload("../../evil.png", payload)
    assert decision.accepted is False
    assert decision.reason == "unsafe_filename"


def test_extension_signature_mismatch_is_rejected():
    decision = inspect_upload("plan.pdf", b"not-a-pdf")
    assert decision.accepted is False
    assert decision.reason == "signature_mismatch"


def test_size_limit_is_enforced():
    policy = ImportPolicy(frozenset({".png"}), max_bytes=8)
    decision = inspect_upload("large.png", b"\x89PNG\r\n\x1a\nX", policy)
    assert decision.accepted is False
    assert decision.reason == "file_too_large"


def test_archives_are_not_allowed_by_default():
    decision = inspect_upload("project.zip", b"PK\x03\x04payload")
    assert decision.accepted is False
    assert decision.reason == "extension_not_allowed"


def test_ifc_signature_is_checked():
    payload = b"ISO-10303-21;\nHEADER;\nENDSEC;\nDATA;\nENDSEC;\nEND-ISO-10303-21;"
    decision = inspect_upload("model.ifc", payload)
    assert decision.accepted is True


def test_remote_url_rejects_local_private_and_credentialed_targets():
    assert safe_remote_url("http://example.com/file.glb") is False
    assert safe_remote_url("https://localhost/file.glb") is False
    assert safe_remote_url("https://127.0.0.1/file.glb") is False
    assert safe_remote_url("https://10.0.0.5/file.glb") is False
    assert safe_remote_url("https://user:pass@example.com/file.glb") is False
    assert safe_remote_url("https://example.com/file.glb") is True
