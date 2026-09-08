# Security Acceleration Architecture

This document defines the fast-path architecture for Nexvary Andalus Studio: reuse permissively licensed foundations, adopt proven security patterns, and keep risky or copyleft components isolated from the product core.

## Adopt / evaluate

### OpenPlan3D — MIT
Use as a reference or fork candidate for floor-plan editing, 2D/3D synchronization and import/export UX. Preserve MIT notices for any copied or adapted source and import only the parts that map cleanly onto the Andalus project schema.

### That Open Engine / Components — candidate BIM adapter
Evaluate for IFC/BIM viewing and component architecture where it reduces custom work. Re-verify the exact package license/version before copying or distributing any code, and keep the integration modular behind the design schema.

### Yjs — MIT
Use as the local-first collaboration foundation for shared project state, conflict-free merging and undo/redo. Network transport remains replaceable and is not trusted with unrestricted project permissions by default.

### Three.js / SceneView / Filament
Continue using the existing 3D stack. Keep Android AR hardware-gated and web rendering independent from the canonical project model.

## Security patterns adopted

### Secure Import Gateway
All user supplied photos, floorplans and 3D/BIM files must pass one gateway before parsing:
- extension allowlist
- file-size limits
- signature/magic validation independent of HTTP Content-Type
- content-addressed SHA-256 storage names
- client filenames are never used as storage paths
- no archive import by default
- reject obvious localhost/private-address remote imports
- reject credential-bearing remote URLs
- DNS resolution must be re-checked immediately before remote fetch to prevent rebinding
- every redirect must be revalidated before following it
- parsers run after validation, never before

Supported initial formats: JPG/JPEG, PNG, WebP, PDF, DXF, IFC, GLB and glTF.

### Android network policy
Android explicitly denies cleartext HTTP. HTTPS is required for production network calls. Backup remains disabled.

### Supply-chain gate
Pull requests use GitHub Dependency Review and fail when a newly introduced dependency has HIGH or CRITICAL known vulnerabilities. Dependabot proposes weekly dependency updates.

### Heavy scans outside the fast path
CodeQL runs on a scheduled/manual workflow so deep analysis does not slow every normal edit/build cycle. Release-blocking tests stay small, deterministic and parallel.

### Build provenance
APK SHA-256 is active. Signed build provenance/attestation is a planned separate hardening step; it must pass on `main` before being described as an active release guarantee.

### Immutable project history
Keep the existing Stage 825 revision fingerprints, optimistic concurrency and asset provenance. Security checks extend these controls rather than replacing them.

## Collaboration boundary

Yjs holds collaboration state, but the canonical saved revision remains the validated Andalus project schema. A collaborator update must not bypass:
- Architectural Lock
- project schema validation
- optimistic concurrency at persistence boundaries
- authorization rules when network collaboration is added

This separation lets us gain Figma-like local-first behavior without making the CRDT document itself a trusted database.

## Planned next integrations

1. Map Yjs updates onto selected project-schema fields and persist signed/validated snapshots.
2. IFC viewer adapter after license/version review; parsing remains isolated away from the core project store.
3. OpenPlan3D adapter for 2D editor primitives rather than copying unrelated application code.
4. SBOM generation for release artifacts.
5. OpenSSF Scorecard as scheduled repository-health analysis.
6. Image metadata stripping and decode/re-encode worker before AI processing.
7. Parser resource budgets for IFC/DXF/glTF to limit pathological geometry and memory exhaustion.
8. Remote-import fetcher with DNS/IP revalidation and redirect policy.

## License policy

- MIT / Apache-2.0 / BSD / CC0: preferred for product-core integration after review.
- LGPL: isolate behind a clearly documented boundary and comply with applicable obligations.
- GPL/AGPL: do not copy/link into the product core unless distribution obligations are explicitly accepted; prefer legally appropriate external process/service boundaries.
- Research-only / non-commercial model weights: never enter production manifests.
- Every model weight and media asset is reviewed separately from the library that loads it.

## Release principle

Fast does not mean skipping controls. Fast means moving expensive checks out of the critical path, parallelizing independent gates, caching deterministic dependencies, cancelling stale CI runs and fixing root causes instead of individual symptoms.
