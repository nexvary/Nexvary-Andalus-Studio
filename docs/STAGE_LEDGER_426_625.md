# Stage Ledger — 426 → 625

This ledger records the second autonomous development pass. Stage numbers are consolidated into engineering batches and do not imply that production GPU models or hardware validation are complete.

| Stages | Delivery | Status at 0.6.25 |
|---|---|---|
| 426–440 | Canonical project serialization and SHA-256 project fingerprints | DONE |
| 441–455 | Immutable revision snapshots, atomic save, history and tamper detection | DONE |
| 456–470 | Andalusian material catalog with Arabic/English names and purchase units | DONE |
| 471–485 | Surface takeoff, coverage, waste and validation engine | DONE |
| 486–500 | BOM aggregation and user-supplied price/currency calculation | DONE |
| 501–515 | Deterministic ASCII DXF R12 wall export with metric/imperial units | DONE |
| 516–525 | UTF-8 BOM CSV and fingerprinted export manifest | DONE |
| 526–540 | AI generation-plan compiler: depth/edges/segmentation/opening/floorplan controls | DONE |
| 541–555 | Post-generation Architectural Lock candidate validator | DONE |
| 556–565 | AI provider/model/license/source provenance records | DONE |
| 566–580 | Stage-625 API routes: project fingerprints, materials, takeoff, BOM, DXF/CSV, AI plan/validation | DONE |
| 581–592 | Web Studio Stage-625 client wired to material, quantity, AI-plan and DXF endpoints | DONE |
| 593–600 | Android shared models for locks, takeoff and revision fingerprints | DONE |
| 601–610 | Project schema 0.6.25: revisions, source digests, materials, pricing, AI provenance and exports | DONE |
| 611–617 | Input limits, finite-number checks, path traversal defense and overwrite protection | DONE |
| 618–621 | Export/construction disclaimer and user-price policy | DONE |
| 622 | CI expanded for all new deterministic packages | DONE |
| 623 | Strict TypeScript Stage-625 client check | DONE locally with Three type stub |
| 624 | Standalone Kotlin Stage-625 model compile | DONE locally |
| 625 | Stage-625 Project Intelligence checkpoint | DONE |

## Explicitly not claimed at Stage 625

- no production diffusion/ControlNet/SAM/Depth weights are bundled yet
- no claim of model license suitability until each selected weight is reviewed
- no live market price feed; prices are user supplied
- DXF export is a deterministic floor-plan aid, not a stamped construction document
- no IFC/PDF production exporter yet
- no real-device AR or room-scan validation yet
- GitHub-hosted runner execution remains a release gate if jobs receive no runner/steps
