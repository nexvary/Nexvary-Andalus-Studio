# Third-party & License Policy

Nexvary Andalus Studio uses a license-aware integration policy. This file is a tracking document,
not legal advice. Every concrete dependency or dataset is rechecked at the version actually shipped.

## Approved integration classes

| Component / family | Typical license | Stage-425 role | Integration rule |
|---|---|---|---|
| Three.js | MIT | Web 3D renderer | May link/package with notices |
| SceneView | Apache-2.0 | Android 3D/AR | May link/package with notices |
| Google Filament | Apache-2.0 | Under SceneView / renderer option | Preserve notices |
| OpenCV modern releases | Apache-2.0 | Future image geometry | Version-by-version review |
| Open3D | MIT | Future scan/point-cloud processing | May integrate with notices |
| Shapely | BSD-3-Clause | Future geometry option | May integrate with notices |
| Trimesh | MIT | Future mesh processing | May integrate with notices |
| Diffusers | Apache-2.0 | Future orchestration option | Library license does NOT grant model-weight rights |
| SAM-family releases | model/repo specific | Future segmentation | Check exact release/model license |
| ControlNet ecosystem | code/model specific | Future conditioning | Check code AND checkpoint licenses |
| Poly Haven assets | CC0 | Future PBR/HDRI assets | Track source/provenance anyway |
| Smithsonian Open Access | item specific / CC0 where marked | Heritage reference | Verify each item |
| The Met Open Access | public-domain/open-access where marked | Heritage reference | Verify each item |
| Wikimedia Commons | per-file | Heritage reference | Store attribution/license metadata |

## Restricted / isolate-before-use

- GPL components: do not copy into the proprietary application core by default.
- LGPL components: dynamic/separate integration only after architecture and compliance review.
- Research-only / non-commercial model weights: do not ship in a commercial build.
- Unknown-license GitHub repositories/assets: treat as unusable until clarified.

## Source provenance requirement

Every imported heritage asset should retain:
- source institution / repository
- canonical object URL or identifier
- creator/period when known
- license/public-domain statement
- downloaded/verified date
- transformations applied by Nexvary Andalus Studio
