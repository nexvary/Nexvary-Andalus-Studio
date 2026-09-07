# Third-party & License Policy

Nexvary Andalus Studio uses a license-aware integration policy. This file is a tracking document,
not legal advice. Every concrete dependency, dataset, model weight and asset is rechecked at the
version actually shipped.

## Approved integration classes

| Component / family | Typical license | Role | Integration rule |
|---|---|---|---|
| Three.js | MIT | Web 3D renderer | May link/package with notices |
| SceneView | Apache-2.0 | Android 3D/AR | May link/package with notices |
| Google Filament | Apache-2.0 | Renderer under SceneView / direct option | Preserve notices |
| OpenCV modern releases | Apache-2.0 | Future image geometry | Version-by-version review |
| Open3D | MIT | Future scan/point-cloud processing | May integrate with notices |
| Shapely | BSD-3-Clause | Geometry option | May integrate with notices |
| Trimesh | MIT | Mesh-processing option | May integrate with notices |
| Diffusers | Apache-2.0 | Future inference orchestration | Library license does NOT grant model-weight rights |
| SAM-family releases | repo/model specific | Future segmentation | Check exact release and weight license |
| ControlNet ecosystem | code/model specific | Future conditioning | Check code AND checkpoint licenses |
| Poly Haven assets | CC0 | PBR/HDRI/assets | Keep source/provenance even when attribution is not required |
| Smithsonian Open Access | item specific / CC0 where marked | Heritage reference | Verify each item |
| The Met Open Access | public-domain/open-access where marked | Heritage reference | Verify each item |
| Wikimedia Commons | per-file | Heritage reference | Store attribution/license metadata |

## Stage-825 asset gate

External assets are not treated as usable merely because a URL can be downloaded. The asset registry
stores the source URL, license identifier, attribution, content SHA-256, commercial-use flag and a
separate human-review flag. Product code requesting reviewed assets rejects records that have not
passed the review gate.

The registry recognizes license identifiers such as CC0-1.0, CC-BY-4.0, CC-BY-SA-4.0, MIT,
Apache-2.0 and BSD-3-Clause, but recognition is not a blanket permission to ship every asset under
that license. Share-alike, attribution, trademark, moral-rights, database-rights and source-specific
terms must still be handled for each imported asset.

## ComfyUI boundary

ComfyUI is treated as an **optional external service**, not copied into the proprietary application
core. Nexvary Andalus Studio only exposes a controlled adapter contract. Public clients do not submit
arbitrary raw ComfyUI workflows; server-owned reviewed templates are required before real inference
is enabled. ComfyUI's own license and all installed custom nodes remain independently applicable.

No model checkpoint, LoRA, ControlNet, VAE or other weight inherits permission from ComfyUI,
Diffusers, or the adapter code. Every selected weight requires an independent shipping review.

## Restricted / isolate-before-use

- GPL components: do not copy into the proprietary application core by default.
- LGPL components: dynamic/separate integration only after architecture and compliance review.
- Research-only / non-commercial model weights: do not ship in a commercial build.
- Unknown-license GitHub repositories/assets: treat as unusable until clarified.
- Assets with missing provenance or unresolved attribution: do not mark reviewed.

## Source provenance requirement

Every imported heritage asset should retain:
- source institution / repository
- canonical object URL or identifier
- creator/period when known
- license/public-domain statement
- downloaded/verified date
- content SHA-256 when bytes are retained
- transformations applied by Nexvary Andalus Studio
- reviewer / review state in the internal ingestion workflow
