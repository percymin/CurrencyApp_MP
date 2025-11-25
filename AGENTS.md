# Repository Guidelines

## Project Structure & Module Organization
- Single `app` module using Android application plugin; root Gradle settings in `settings.gradle.kts` and build defaults in `build.gradle.kts`.
- Source lives in `app/src/main/java/com/cookandroid/currencytest` with resources under `app/src/main/res`.
- Local JVM tests live in `app/src/test`, instrumentation tests in `app/src/androidTest`.
- Assets/config: ProGuard rules in `app/proguard-rules.pro`; shared dependencies declared in `app/build.gradle.kts`.

## Build, Test, and Development Commands
- Build debug APK: `./gradlew :app:assembleDebug` (outputs to `app/build/outputs/apk/debug`).
- Run unit tests on JVM: `./gradlew :app:testDebugUnitTest`.
- Run instrumentation tests (device/emulator required): `./gradlew :app:connectedAndroidTest`.
- Static analysis: `./gradlew :app:lintDebug` for Android Lint reports.
- Clean builds if Gradle cache misbehaves: `./gradlew clean`.

## Coding Style & Naming Conventions
- Language: Java 8; keep Android Studio default formatter (4-space indent, braces on new lines for methods, no wildcard imports).
- Classes/activities/fragments in PascalCase; member variables in camelCase; constants in UPPER_SNAKE_CASE.
- Layout XML filenames in lowercase_snake_case (e.g., `activity_main.xml`); resource IDs in camelCase.
- Keep UI strings in `res/values/strings.xml`; avoid hard-coded literals in layouts or Java.

## Testing Guidelines
- Prefer JVM unit tests for logic; use AndroidX JUnit and Espresso for UI/behavior in `androidTest`.
- Name tests with intent (`methodName_condition_expectedResult`); keep one assertion focus per test when possible.
- Ensure new features include either unit coverage or instrumentation coverage; run `testDebugUnitTest` before PRs, and `connectedAndroidTest` when UI changes.

## Commit & Pull Request Guidelines
- Commit messages: short imperative summaries (e.g., "Add currency conversion screen"); group related changes per commit.
- PRs should include: brief description of behavior, screenshots for UI-visible changes, and test command output (or note why not run).
- Reference related issue/task IDs in PR description; keep diffs focused and avoid unrelated formatting churn.

## Environment & Configuration
- Minimum SDK 24, target/compile SDK 34; ensure emulator/device matches or exceeds minSdk for instrumentation runs.
- Use Android Studio with Gradle wrapper (`./gradlew`) instead of system Gradle to match toolchain versions.
