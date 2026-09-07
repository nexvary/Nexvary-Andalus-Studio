# Nexvary Andalus Studio

**AI Architecture & Islamic Design**

Arabic-first platform for Andalusian and Islamic architecture, parametric ornament, floor planning, 3D visualization and AI-assisted redesign.

Current integrated checkpoint: **Stage 625 / v0.6.25 — Project Intelligence**

## Stage-625 capabilities

- Stage-425 geometry core: Islamic patterns, 2D plans, horseshoe arches and courtyards
- immutable project snapshots and SHA-256 revision fingerprints
- Arabic/English Andalusian material catalog
- surface takeoff, coverage and waste calculations
- BOM aggregation with costs only from user-supplied prices
- deterministic DXF R12 wall export and UTF-8 BOM CSV
- fingerprinted export manifests
- AI generation-plan compiler driven by source type and Architectural Locks
- post-generation candidate validator that rejects changes to locked geometry
- provider/model/license/source provenance structure
- FastAPI routes for materials, quantities, BOM, exports, fingerprints and AI planning
- Web Stage-625 client wired to the API for material quantity, AI planning and DXF download
- Android Stage-625 shared models for locks/takeoff/revisions
- project schema v0.6.25 for provenance, revisions, materials, pricing, AI and exports

## Engineering rule

Generative output remains a concept layer. Geometry, quantities and contractor-oriented exports must originate from the deterministic project model. A visually successful AI result is rejected if it violates an Architectural Lock.

## Verification

New Stage-625 deterministic tests: **29 passed locally**.
Stage-425 deterministic baseline: **33 passed**.
The CI workflow is configured to run the combined suite plus Web and Android builds when GitHub allocates a runner.

See `docs/STAGE_LEDGER_426_625.md`, `docs/QA_STAGE_625.md`, `docs/AI_ORCHESTRATION.md`, and `docs/EXPORT_POLICY.md`.
