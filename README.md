# Nexvary Andalus Studio

**AI Architecture & Islamic Design**

Arabic-first platform for Andalusian and Islamic architecture, parametric ornament, floor planning,
3D visualization, licensed heritage assets and AI-assisted redesign.

Current integrated development checkpoint: **Stage 825 / v0.8.25 — Production Runtime Foundation**

## What Stage 825 adds

- SQLite transactional project repository
- immutable project revisions and optimistic-concurrency conflict protection
- project list/load/history API
- asset registry with source, license, attribution, SHA-256 and review state
- product-use gate that blocks unreviewed or non-commercial assets
- AI adapter protocol and deterministic dry-run provider
- optional external ComfyUI adapter boundary without bundling ComfyUI or model weights
- AI job state machine, idempotency and terminal-state protection
- deterministic scene manifests
- self-contained glTF 2.0 wall-geometry export with embedded binary buffer
- Stage-825 Web client for revisions, assets, adapters, dry-run jobs and glTF download
- Android v0.8.25 UI/contracts aligned with projects, assets, runtime and exports
- shared project schema v0.8.25
- expanded multi-platform CI and release-gate documentation

## Earlier core retained

Stage 825 builds on rather than replaces the previous deterministic foundation:

- Islamic star, rosette, zellij and border generation
- SVG pattern export
- 2D floor-plan geometry, room metrics and opening validation
- horseshoe-arch and courtyard geometry
- bilingual Andalusian style catalog
- Architectural Lock prompt planning and post-generation validation
- material takeoff, waste, BOM and user-supplied pricing
- DXF R12 and UTF-8 CSV export
- export/project fingerprints and provenance records

## Repository layout

- `packages/andalus-pattern-engine/`
- `packages/floorplan-engine/`
- `packages/andalus-architecture-engine/`
- `packages/project-core/`
- `packages/materials-engine/`
- `packages/export-engine/`
- `packages/asset-registry/`
- `packages/scene-export/`
- `packages/design-schema/`
- `services/ai-api/`
- `apps/web-3d/`
- `apps/android/`
- `docs/`

## Engineering rules

1. Generative output is a **concept layer**. Geometry and quantities come from deterministic models.
2. A visually successful AI candidate is rejected if it violates an enabled Architectural Lock.
3. External assets are not product-ready until provenance/license review passes.
4. Model/checkpoint rights are reviewed independently from inference-library or adapter licenses.
5. ComfyUI, when enabled, is an optional external service; public clients do not submit arbitrary raw workflows.
6. Prices are supplied by the user or a separately approved data source; the core does not invent live prices.
7. DXF/glTF exports are interchange aids and are not stamped construction documents.
8. Arabic RTL, Android Back behavior, safe insets and real-device QA remain release requirements.

## Verification status

The latest Stage-825 runtime development block recorded **27 local tests passing**, plus strict
TypeScript and standalone Kotlin source gates. The hosted workflow now covers old and new Python
packages, schema/sample validation, Web build and Android assemble whenever GitHub allocates a runner.

A workflow run with zero executed steps is not counted as a successful or failed source build.

## Documentation

- `docs/STAGE_LEDGER_002_425.md`
- `docs/STAGE_LEDGER_426_625.md`
- `docs/STAGE_LEDGER_626_825.md`
- `docs/QA_STAGE_825.md`
- `docs/AI_ORCHESTRATION.md`
- `docs/EXPORT_POLICY.md`
- `docs/THIRD_PARTY_LICENSES.md`
