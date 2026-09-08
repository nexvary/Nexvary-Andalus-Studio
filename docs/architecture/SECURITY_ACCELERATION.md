# Security Acceleration Architecture

This document defines the fast-path architecture for Nexvary Andalus Studio: reuse permissively licensed foundations, borrow security patterns from mature platforms, and keep risky or copyleft components isolated from the product core.

## Adopt now

### OpenPlan3D — MIT
Use as a reference/fork candidate for floor-plan editing, 2D/3D synchronization and import/export UX. Preserve MIT notices for any copied or adapted source.

### That Open Engine / Components — MIT
Use for IFC/BIM viewing and component architecture where it reduces custom work. Keep integrations modular behind the design schema.

### Yjs — MIT
Planned collaboration layer for local-first editing, snapshots, undo/redo and conflict-free multi-user changes. Network transport remains replaceable.

### Three.js / SceneView / Filament
Continue using the existing 3D stack. Keep Android AR hardware-gated and web rendering independent from the project model.

## Security patterns adopted

### Secure Import Gateway
All user supplied photos, floorplans and 3D/BIM files must pass a single gateway before parsing:
- extension allowlist
- file-size limits
- signature/magic validation independent of HTTP Content-Type
- content-addressed SHA-256 storage names
- no archive import by default
- reject obvious localhost/private-address remote imports
- DNS resolution must be re-checked immediately before remote fetch to prevent rebinding
- parsers run after validation, never before

Supported initial formats: JPG/JPEG, PNG, WebP, PDF, DXF, IFC, GLB and glTF.

### Android network policy
Android explicitly denies cleartext HTTP. HTTPS is required for production network calls. Backup remains disabled.

### Supply-chain gate
Pull requests use GitHub Dependency Review and fail when a newly introduced dependency has HIGH or CRITICAL known vulnerabilities.

### Build provenance
Public `main` APK builds generate a GitHub/Sigstore artifact attestation in addition to SHA-256. This binds the APK to the workflow, repository and commit that produced it.

### Immutable project history
Keep the existing Stage 825 revision fingerprints, optimistic concurrency and asset provenance. Security checks extend these controls rather than replacing them.

## Planned next integrations

1. Yjs-backed project changes mapped onto `andalus-project.schema.json` for local-first collaboration.
2. IFC viewer adapter using That Open components; IFC parsing remains sandboxed away from the core project store.
3. OpenPlan3D adapter for 2D editor primitives rather than copying unrelated application code.
4. SBOM generation with Syft (Apache-2.0) for release artifacts.
5. OpenSSF Scorecard as a scheduled repository health scan, kept outside the fast release gate.
6. Image metadata stripping and decode/re-encode service for photos before AI processing.
7. Parser resource budgets for IFC/DXF/glTF to limit pathological geometry and memory exhaustion.

## License policy

- MIT / Apache-2.0 / BSD / CC0: preferred for product-core integration.
- LGPL: isolate behind a clearly documented service/library boundary and comply with replacement/linking obligations.
- GPL/AGPL: do not copy into the MIT product core. Use only as a clearly separated external tool/service when legally appropriate.
- Research-only / non-commercial model weights: never enter production manifests.
- Every model weight is reviewed separately from the library that loads it.

## Release principle

Fast does not mean skipping controls. Fast means moving expensive checks out of the critical path, parallelizing independent gates, caching deterministic dependencies, cancelling stale CI runs and fixing root causes rather than individual symptoms.
