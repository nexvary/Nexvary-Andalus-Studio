# System Architecture

## Goal
Build a modular platform that can evolve from deterministic architectural geometry into AI-assisted redesign, 3D editing and AR without coupling every subsystem to one runtime.

## Modules

### Android client
Kotlin + Jetpack Compose. Arabic/RTL is a first-class requirement. AR capabilities are introduced behind device-capability checks.

### Web 3D
Three.js-based editor/viewer for floor plans, materials, lights and GLTF/GLB scenes.

### AI API
Python/FastAPI service. Heavy models are loaded behind adapters so model licenses and deployment targets can be changed independently.

### Andalus Pattern Engine
Deterministic geometry library for stars, rosettes, grids, borders and future zellij/muqarnas helpers. Outputs SVG first, then DXF-compatible geometry.

### Design Schema
Shared project contract containing dimensions, style school, materials, user locks and provenance metadata.

## Architectural Lock
Every redesign request carries explicit locks such as massing, floors, openings, entrance, roofline and room boundaries. AI adapters must treat locked geometry as immutable constraints unless the user unlocks it.

## Safety and professional boundary
Concept renders are not construction documents. Any structural, MEP or load-bearing recommendation must remain outside automatic approval and require qualified professional review.

## License boundary
Permissive dependencies may be linked normally after review. GPL/LGPL/research-only components require an explicit integration decision. GPL applications such as Blender should be treated as separate tools/processes unless a compliant distribution strategy is intentionally adopted.
