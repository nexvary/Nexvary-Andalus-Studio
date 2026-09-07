# Nexvary Andalus Studio — Architecture at Stage 425

Version: 0.4.25

## Product boundary

Nexvary Andalus Studio is split into deterministic geometry, UI clients, and AI orchestration.
The deterministic layer is deliberately usable without a generative model so that measurements,
project constraints, exports, and QA do not depend on stochastic AI output.

## Components

### `packages/andalus-pattern-engine`
Parametric Islamic geometry primitives:
- stars (5–32 points)
- rosettes (6–32 petals)
- repeating zellij grids
- repeating geometric borders
- deterministic SVG export

### `packages/floorplan-engine`
2D metric geometry:
- points, walls, openings, rooms
- wall length / room area / room perimeter
- plan bounds and summary
- structural validation of openings against walls
- deterministic SVG export

### `packages/andalus-architecture-engine`
Parametric architectural primitives:
- Andalusian horseshoe arch preview geometry
- opening-area approximation for early quantities
- courtyard/arcade/fountain fit validation
- deterministic SVG arch preview

### `services/ai-api`
FastAPI orchestration:
- health/version
- bilingual style catalog
- pattern endpoints
- architectural element endpoints
- design prompt planner
- Architectural Lock constraint translation
- floor-plan analysis and SVG preview

No heavyweight diffusion model is embedded at Stage 425. The API produces a safe, explicit
generation contract that can later be sent to an approved model runner.

### `apps/web-3d`
Vite + TypeScript + Three.js studio shell:
- RTL/LTR switch
- 3D courtyard scene
- Architectural Lock inspector
- responsive desktop/mobile layout
- navigation surfaces for Plan, 3D, Zellij, Rosette, Materials, AI

### `apps/android`
Native Android/Jetpack Compose foundation:
- Android 15-compatible target
- RTL-first dashboard
- system back handling inside feature screens
- safe drawing insets
- SceneView/ARSceneView dependencies reserved for 3D/AR screens
- Architectural Lock UI

### `packages/design-schema`
Cross-client project schema:
- style school
- ornament intensity
- architectural locks
- source provenance
- floor plan
- pattern presets
- rendering profile
- audit metadata

## Architectural Lock invariant

When a lock is `true`, any AI integration MUST preserve the corresponding geometry or property.
The deterministic project model is the source of truth; generated imagery is a proposal layer,
not an authoritative architectural drawing.

## AI safety / engineering invariant

1. AI concepts are never labelled construction-ready without deterministic verification.
2. Dimensions and quantities come from the geometry layer, not pixels produced by an image model.
3. Source images and heritage references retain provenance.
4. Model licenses are reviewed separately from orchestration-library licenses.
5. Research-only/GPL code is not copied into the proprietary core without an explicit decision.

## Export roadmap after Stage 425

- SVG: implemented for patterns, arches and 2D plans.
- DXF: next deterministic export.
- GLTF/GLB: web/3D interchange.
- PDF: project sheets and contractor packs.
- IFC: BIM bridge after schema hardening.
