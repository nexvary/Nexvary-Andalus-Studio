# Nexvary Andalus Studio

**AI Architecture & Islamic Design**

Nexvary Andalus Studio is a design platform for Andalusian and Islamic architecture, interiors, geometric ornament, 2D/3D planning, and AR-assisted visualization.

## Product pillars

- AI-assisted exterior and interior redesign
- Andalusian / Islamic architectural style system
- Parametric Islamic geometric pattern generation
- 2D floor planning and 3D visualization
- Architectural Lock constraints to preserve selected geometry
- AR preview and room scanning
- Export pipeline for SVG / DXF / GLTF / PDF / BIM-oriented workflows
- License-aware use of open-source software and open cultural heritage data

## Repository architecture

- `apps/android/` — Android client
- `apps/web-3d/` — web-based 3D editor and viewer
- `services/ai-api/` — AI and geometry API
- `packages/andalus-pattern-engine/` — parametric Islamic geometry engine
- `packages/design-schema/` — shared design/project schemas
- `docs/` — architecture, roadmap, licensing, research and QA
- `.github/workflows/` — CI quality gates

## Current milestone

**Foundation / Stage 1**

The first release gate focuses on a deterministic geometric-pattern engine, API contract, project schema, tests, and CI before adding heavyweight AI models.

## Engineering principles

1. Working features before visual-only placeholders.
2. Preserve architectural geometry unless the user explicitly unlocks it.
3. Arabic and RTL are first-class requirements.
4. Every third-party dependency is reviewed before integration.
5. GPL/research-only components are not copied into the proprietary application core without an explicit architecture/legal decision.
6. AI-generated concepts are clearly separated from construction-ready drawings.

## Status

Initial repository bootstrap in progress.
