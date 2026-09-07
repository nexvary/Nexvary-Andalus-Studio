# Stage 825 QA Gate

## Scope

Stage 825 is the Production Runtime Foundation checkpoint. It extends the deterministic Stage-625
core without claiming that production AI weights, cloud collaboration, or hardware AR are released.

## Local deterministic gates completed during development

- SQLite repository revision/optimistic-concurrency tests
- asset registry validation, duplicate-digest and review-gate tests
- scene manifest and glTF geometry tests
- AI adapter/job state-machine tests
- Stage-825 API route tests
- regression coverage for earlier Stage-425/625 API contracts

Latest Stage-825 runtime block result during development: **27 tests passed locally**.
Earlier deterministic baselines remain part of the combined hosted workflow.

Additional local source gates completed:
- strict TypeScript source check for the Stage-825 Web client
- standalone Kotlin compilation for Stage-825 Android data contracts
- JSON syntax/schema/sample checks during the development pass

## Release invariants

### Projects
- revisions are append-only
- parent fingerprints are checked before accepting concurrent writes
- stale clients receive a conflict instead of silently overwriting the head
- project IDs are validated before being used for storage

### Assets
- source URL and license identifier are mandatory
- non-commercial records are rejected from the product registry
- CC-BY records require attribution information
- content SHA-256 can be retained for integrity/duplicate detection
- product-use lookup requires the separate reviewed flag

### AI runtime
- AI output remains concept-only
- Architectural Lock validation from Stage 625 remains authoritative
- arbitrary raw ComfyUI workflows are not accepted from public clients
- external provider URLs/configuration are server-side concerns
- model/checkpoint licenses are reviewed independently from adapter/library licenses
- job state transitions and idempotency are validated

### Scene export
- glTF output is geometric interchange tied to deterministic project geometry
- real-world dimensions remain derived from the deterministic model
- glTF/DXF are not represented as stamped construction documents

### Android/Web
- Arabic-first / RTL remains a release requirement
- Android Back from a feature returns to Home before normal activity exit
- safe drawing insets remain required
- Web client must pass TypeScript and Vite build before release

## Hosted CI rule

A GitHub Actions job that receives no runner and executes no steps is **not** a passing or failing
source build. It is recorded as an external runner-allocation condition. A release candidate still
requires an actual Python test run, Web build and Android assemble job with executed steps.

## Post-825 release blockers

1. successful hosted CI with real runner execution
2. Android assembleDebug/assembleRelease
3. Android 15 device and RTL visual QA
4. real-device AR camera/permission/capability validation
5. selected AI model/checkpoint license review
6. controlled external-provider integration test if ComfyUI is enabled
7. signed production packaging and security/configuration review
