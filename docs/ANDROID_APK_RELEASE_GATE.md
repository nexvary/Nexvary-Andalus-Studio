# Android APK Release Gate

## Current source

- Project: Nexvary Andalus Studio
- Stage: 825
- Version: 0.8.25
- Android application id: `com.nexvary.andalus`
- Target SDK: 35
- Compile SDK: 37
- Minimum SDK: 26

## Build artifact workflow

`.github/workflows/android-apk.yml` is the dedicated APK build path.

A successful run must:

1. restore the exact repository source;
2. configure Java 17, Android SDK and Gradle 9.6.0;
3. install Android platform 37 and build-tools 36.0.0;
4. run `gradle :app:assembleDebug --stacktrace --no-daemon`;
5. verify that `app/build/outputs/apk/debug/app-debug.apk` exists;
6. rename it to `Nexvary-Andalus-Studio-v0.8.25-debug.apk`;
7. generate its SHA-256 checksum;
8. upload both files as the `Nexvary-Andalus-Studio-v0.8.25-debug` Actions artifact.

## Hosted runner blocker observed before this workflow

Previous repository workflows created Python, Web and Android jobs but GitHub reported no executed steps. Job log retrieval returned no log blob because execution had not started. Those runs are therefore not evidence of a source compile failure.

For a private repository, GitHub-hosted Actions execution is also subject to the account's Actions billing/usage/budget state. If the new APK workflow again terminates before the Checkout step, the account-level Actions usage/budget state must be checked before treating the result as a build failure.

## Release acceptance gates

An APK is not considered release-ready until all of the following pass:

- hosted or equivalent clean-environment `assembleDebug` build;
- APK exists and SHA-256 is recorded;
- install/launch smoke test;
- Android 15 navigation and system-bar/safe-inset check;
- Arabic RTL visual pass;
- Back from inner pages returns to the previous/home screen rather than incorrectly exiting;
- dashboard scrolling and no bottom/system UI overlap;
- rotation/resizing smoke check where applicable;
- camera permission flow test;
- AR capability behavior on supported and unsupported hardware;
- no claim that DXF/glTF output is a stamped construction document.

## Current limitation

Source-level and deterministic runtime gates have passed during development, but an APK must not be claimed as built until this workflow (or an equivalent clean Android toolchain) actually completes and produces the artifact.
