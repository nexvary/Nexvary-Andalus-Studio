# Third-Party License Register

This file is the approval ledger for external code, models, datasets and media. An item is not production-approved merely because it appears here.

| Component | Intended use | License family | Status | Notes |
|---|---|---|---|---|
| Three.js | Web 3D rendering | MIT | Candidate | Review pinned release before integration |
| SceneView | Android 3D/AR | Apache-2.0 | Candidate | Review transitive dependencies |
| Google Filament | PBR rendering | Apache-2.0 | Candidate | Native packaging review required |
| Open3D | Point clouds / reconstruction | MIT | Candidate | Server/desktop service preferred initially |
| OpenCV | Vision utilities | Apache-2.0 (modern releases) | Candidate | Pin reviewed release |
| SAM 2 | Segmentation adapter | Apache-2.0 code/model terms to verify per artifact | Research candidate | Record exact model artifact before shipping |
| Depth Anything V2 Small | Depth estimation | Apache-2.0 for Small model per upstream notice | Research candidate | Do not substitute non-commercial variants |
| Hugging Face Diffusers | Model pipeline framework | Apache-2.0 | Candidate | Every model weight has a separate license review |
| ControlNet | Conditioning architecture/code | Apache-2.0 code | Candidate | Exact checkpoints require separate review |
| Trimesh | Mesh geometry | MIT | Candidate | Pin reviewed release |
| Shapely | 2D geometry | BSD-3-Clause | Candidate | Pin reviewed release |
| IfcOpenShell | IFC/BIM bridge | LGPL-family components | Isolated evaluation | Keep integration boundary explicit |
| FreeCAD | CAD automation | LGPL-family | External-tool evaluation | Prefer process boundary |
| Blender | High-end render automation | GPL | External process only by default | Do not copy GPL code into proprietary core |
| Poly Haven assets | Materials/HDRI/assets | CC0 | Content candidate | Store source/provenance metadata |
| The Met Open Access | Heritage references | Open Access / object-specific | Data candidate | Validate object rights field |
| Smithsonian Open Access | Heritage references | CC0 where marked | Data candidate | Validate item rights metadata |
| Wikimedia Commons | Heritage references | Per-item | Conditional | Preserve attribution/license per asset |

## Rules

1. Exact version, repository URL, copyright notice and license text must be recorded before production use.
2. Model weights and datasets are reviewed independently from the framework that loads them.
3. No `research-only`, `non-commercial`, unknown, or incompatible asset enters a commercial build.
4. Generated training datasets must preserve source provenance and rights metadata.
5. A dependency update reopens the license review if terms or transitive dependencies change.
