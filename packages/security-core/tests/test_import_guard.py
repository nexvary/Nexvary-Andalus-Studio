from nexvary_andalus_security import ImportPolicy, inspect_upload, safe_remote_url


def test_png_is_content_addressed_and_path_safe():
    payload = b"\x89PNG\r\n\x1a\n" + b"safe-image"
    decision = inspect_upload("../../evil.png", payload)
    assert decision.accepted is True
    assert decision.stored_name is not None
    assert ".." not in decision.stored_name
    assert decision.stored_name.endswith(".png")


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


def test_remote_url_rejects_local_and_private_targets():
    assert safe_remote_url("http://example.com/file.glb") is False
    assert safe_remote_url("https://localhost/file.glb") is False
    assert safe_remote_url("https://127.0.0.1/file.glb") is False
    assert safe_remote_url("https://10.0.0.5/file.glb") is False
    assert safe_remote_url("https://example.com/file.glb") is True
