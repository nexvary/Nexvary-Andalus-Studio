# Nexvary Andalus Studio

**AI Architecture & Islamic Design**

Nexvary Andalus Studio is an Arabic-first design platform for Andalusian and Islamic architecture,
parametric ornament, floor planning, 3D visualization, and AI-assisted redesign.

Current integrated checkpoint: **Stage 425 / v0.4.25**

## What works in the Stage-425 core

- deterministic Islamic geometry: stars, rosettes, zellij grids, borders
- SVG export for patterns
- deterministic 2D floor-plan geometry
- room area/perimeter, wall length, opening validation, plan bounds
- SVG floor-plan preview
- parametric horseshoe arch and courtyard validation
- bilingual Andalusian/Islamic style catalog
- Architectural Lock design constraints
- FastAPI endpoints for styles, patterns, architectural elements, prompt planning and floor-plan analysis
- responsive RTL/LTR Three.js Web Studio foundation
- Android Jetpack Compose foundation with RTL, safe insets and Back behavior
- SceneView/ARSceneView dependencies reserved for native 3D/AR
- cross-client JSON project schema
- automated Python tests and multi-platform CI definitions

## Repository

- `packages/andalus-pattern-engine/`
- `packages/floorplan-engine/`
- `packages/andalus-architecture-engine/`
- `packages/design-schema/`
- `services/ai-api/`
- `apps/web-3d/`
- `apps/android/`
- `docs/`

## Run the API

```bash
python -m venv .venv
source .venv/bin/activate
pip install -e "packages/andalus-pattern-engine[dev]"
pip install -e "packages/floorplan-engine[dev]"
pip install -e "packages/andalus-architecture-engine[dev]"
pip install -e "services/ai-api[dev]"
uvicorn nexvary_andalus_api:app --reload
```

Windows PowerShell activation:

```powershell
.venv\Scripts\Activate.ps1
```

## Run tests

```bash
pytest -q packages/andalus-pattern-engine/tests packages/floorplan-engine/tests packages/andalus-architecture-engine/tests services/ai-api/tests
```

Stage-425 local deterministic result: **33 tests passed**.

## Web 3D

```bash
cd apps/web-3d
npm install
npm run dev
```

## Android

Open `apps/android` in Android Studio. The project is configured for AGP 9.4 / Gradle 9.6,
Compose, and Android 15 compatibility.

## Important product rule

Generative output is a concept layer. Construction dimensions and quantities must come from the
deterministic geometry/project model. `Architectural Lock` constraints are mandatory invariants for
all future image-generation and redesign pipelines.

See:
- `docs/ARCHITECTURE.md`
- `docs/STAGE_LEDGER_002_425.md`
- `docs/QA_STAGE_425.md`
- `docs/THIRD_PARTY_LICENSES.md`
