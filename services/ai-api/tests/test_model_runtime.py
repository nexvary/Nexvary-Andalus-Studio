import pytest

from nexvary_andalus_api.model_runtime import (
    ComfyUIAdapter,
    DryRunAdapter,
    JobManager,
    JobStatus,
    validate_configured_base_url,
)


def test_job_idempotency_and_state_machine():
    manager = JobManager()
    first = manager.create("dry-run", idempotency_key="abc")
    second = manager.create("dry-run", idempotency_key="abc")
    assert first.job_id == second.job_id
    manager.transition(first.job_id, JobStatus.RUNNING)
    manager.transition(first.job_id, JobStatus.SUCCEEDED, result={"ok": True})
    with pytest.raises(ValueError):
        manager.transition(first.job_id, JobStatus.RUNNING)


def test_dry_run_echoes_plan():
    assert DryRunAdapter().submit({"controls": ["depth"]})["accepted"] is True


def test_comfyui_contract_uses_prompt_and_history_endpoints():
    calls = []

    def fake(method, url, payload):
        calls.append((method, url, payload))
        if url.endswith("/prompt"):
            return {"prompt_id": "p-1"}
        return {"p-1": {"status": {"completed": True}}}

    adapter = ComfyUIAdapter("http://127.0.0.1:8188", fake)
    workflow = {"1": {"class_type": "KSampler"}}
    assert adapter.submit_workflow(workflow, client_id="client") == "p-1"
    assert adapter.history("p-1")["p-1"]["status"]["completed"] is True
    assert calls[0][1].endswith("/prompt")
    assert calls[1][1].endswith("/history/p-1")


def test_base_url_rejects_credentials_and_non_http():
    for url in ["file:///tmp/x", "http://u:p@example.com", "javascript:alert(1)"]:
        with pytest.raises(ValueError):
            validate_configured_base_url(url)


def test_idempotency_key_reuse_with_different_request_is_rejected():
    manager = JobManager()
    manager.create("dry-run", idempotency_key="x", request_digest="a" * 64)
    with pytest.raises(ValueError):
        manager.create("dry-run", idempotency_key="x", request_digest="b" * 64)
