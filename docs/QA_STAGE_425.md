# Stage 425 QA Gate

## Deterministic Python gate

Local verification on the Stage-425 source:
- pattern engine tests
- floor-plan geometry tests
- architectural element tests
- API contract tests
- architectural lock tests
- invalid-input tests

Result: **33 passed**.

## UI release invariants

### Android
- Arabic-first UI and `supportsRtl=true`
- `safeDrawingPadding()` used to avoid system-bar overlap
- Back from a feature returns to Home before activity exit
- no fixed bottom toolbar overlapping content
- dashboard is scrollable
- touch controls use Material components

### Web
- RTL and LTR switching
- mobile breakpoint with bottom tool rail
- 3D canvas resizes with `ResizeObserver`
- inspector remains within viewport width
- no visual-only dependency on remote image assets

## CI note

GitHub Actions was configured earlier but GitHub produced jobs with no runner assigned and no steps.
This is an account/runner execution condition, not evidence of a passing build. The workflow remains
in the repository and the deterministic core is locally verified.

## Release rule

Do not publish an APK/installer merely because sources compile locally. A release candidate must pass:
1. dependency restore
2. unit tests
3. web TypeScript build
4. Android assembleDebug/assembleRelease
5. RTL visual QA
6. device test on Android 15
7. AR camera/permission/device-capability tests
