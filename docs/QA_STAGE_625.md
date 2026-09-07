# Stage 625 QA Gate

## Local deterministic checks

- Project Core: canonical JSON, fingerprints, revisions, tamper detection, path traversal rejection
- Materials: catalog, coverage, waste, aggregation, prices only when supplied by user
- Export: DXF structure, unit header, zero-length rejection, Arabic CSV, deterministic manifests
- AI Orchestrator: control-plan selection and Architectural Lock candidate rejection
- API pure-function smoke: Stage-625 routes, takeoff and AI plan
- Web client: strict TypeScript compile using a local Three declaration stub
- Android data models: standalone Kotlin compile

New Stage-625 deterministic tests: **29 passed**.
Stage-425 deterministic baseline previously recorded: **33 passed**.

The new code does not replace or weaken the Stage-425 geometry tests; CI is configured to run both sets together when a runner is allocated.

## Security/data invariants

1. project IDs cannot escape the revision root
2. existing revision files are never silently overwritten
3. project/revision/export fingerprints are SHA-256 based
4. non-finite numeric values are rejected before canonical serialization/export
5. locked candidate geometry is rejected after AI generation if the locked fingerprint/value changes
6. model/provider/license provenance is stored separately from design geometry
7. costs are calculated only from user-supplied unit prices

## Release gate

Do not publish production artifacts until hosted CI or an equivalent controlled build verifies Python, Web and Android builds, followed by Android 15 and AR device tests.
