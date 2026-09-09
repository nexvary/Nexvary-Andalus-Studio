# Release Integrity Gates

This release track prevents visually broken or disconnected builds from being handed to users.

## Navigation Integrity Gate

Runs on Android 15 and clicks the real Compose UI. It must:

- open Home, Services and About
- open every service route from the central `StudioRoutes` registry
- verify each route renders its expected screen
- press the in-page Back button and return to Services
- invoke the Android system Back action from every service screen and return to Services
- confirm all About social/contact controls remain reachable

A route added to the application must also be added to `StudioRoutes`; the test iterates that registry so newly registered pages cannot silently remain disconnected.

## UI Release Gate

Runs Android lint plus instrumented UI checks on a compact and a modern phone profile. It checks:

- primary navigation is visible and does not overlap
- Arabic RTL and English LTR remain usable after runtime language switching
- About controls are reachable on phone layouts
- screenshots and UI hierarchy evidence are uploaded for review

## Localization gate

The application exposes Arabic, English, Turkish, Spanish, German, Italian, French, Urdu, Persian and Russian. Arabic, Urdu and Persian are explicitly RTL; all other languages are LTR. Unit tests verify route copy exists for every service in every supported language.

## Link and security gate

Contact links are validated as structured HTTPS/mailto URIs. Existing security gates continue to enforce HTTPS-only Android traffic, secure imports, dependency audits, CodeQL and build provenance.

## Release rule

An APK is not considered release-ready when it merely compiles. It must pass build, navigation integrity, UI release, security and Android 15 visual evidence gates.
