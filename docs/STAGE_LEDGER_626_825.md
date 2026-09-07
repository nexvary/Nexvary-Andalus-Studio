# Stage Ledger — 626 → 825

This ledger records the third autonomous development pass. Stage numbers represent grouped engineering deliveries, not cosmetic increments.

| Stages | Delivery | Status at 0.8.25 |
|---|---|---|
| 626–640 | SQLite transactional project repository and schema | DONE |
| 641–655 | Immutable append-only revisions and optimistic concurrency | DONE |
| 656–665 | Project list/load/history API contracts | DONE |
| 666–680 | Asset registry with source/license/attribution/digest fields | DONE |
| 681–690 | Product-use license gate, reviewed flag and duplicate digest rejection | DONE |
| 691–705 | AI adapter protocol, capabilities and server-side provider configuration | DONE |
| 706–720 | ComfyUI external-service adapter contract (`/prompt`, `/history`) | DONE — adapter contract only; no bundled ComfyUI code or weights |
| 721–735 | AI job state machine, idempotency and terminal-state protection | DONE |
| 736–748 | Deterministic scene manifest and node transform validation | DONE |
| 749–765 | Self-contained glTF 2.0 wall geometry exporter with embedded binary buffer | DONE |
| 766–775 | Project/asset/AI/scene API routes | DONE |
| 776–790 | Web project revision workflow, assets, adapters, dry-run and glTF download | DONE |
| 791–802 | Android Stage-825 data contracts and release version alignment | DONE |
| 803–812 | Project schema v0.8.25: licensed assets, AI jobs, scene and glTF | DONE |
| 813–817 | Provider URL hardening, no public raw-workflow execution, bounded inputs | DONE |
| 818–821 | Python unit/integration gates, Kotlin model compile, JSON validation | DONE locally |
| 822 | Web strict TypeScript source gate | DONE locally with Three declaration stub |
| 823 | CI expanded to new packages and schema checks | DONE |
| 824 | Runtime/license/asset QA documentation | DONE |
| 825 | Stage-825 Production Runtime checkpoint | DONE |

## Explicitly not claimed at Stage 825

- No production diffusion/ControlNet/SAM/Depth model weights are bundled.
- ComfyUI is optional and external; the public API does not accept arbitrary raw ComfyUI workflows.
- Every model/checkpoint/LoRA license still requires an independent shipping review.
- glTF export is geometric interchange, not a stamped construction document.
- IFC/PDF production exports remain future tracks.
- Cloud multi-user auth/sync is not yet implemented.
- Real-device AR/room-scan QA remains a hardware gate.
