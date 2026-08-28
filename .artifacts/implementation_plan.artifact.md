# Fix 'kotlin.sourceSets' DSL Restriction with Built-in Kotlin

The project is failing to sync because the KSP plugin is attempting to add generated sources using the `kotlin.sourceSets` DSL, which is restricted when "built-in Kotlin" is enabled in AGP 9.x.

## Proposed Changes

### Project Root

#### [MODIFY] [gradle.properties](file:///D:/MobileApp_labs/EBank/gradle.properties)
- Add `android.disallowKotlinSourceSets=false` to allow the KSP plugin to continue using the Kotlin source sets DSL temporarily while built-in Kotlin is active.

## Verification Plan

### Automated Tests
- Perform a Gradle Sync in Android Studio.
- Run `./gradlew :app:assembleDebug` to ensure KSP generated sources are correctly picked up and the project compiles.

### Manual Verification
- Verify that the error "Using kotlin.sourceSets DSL to add Kotlin sources is not allowed with built-in Kotlin" no longer appears during sync.
