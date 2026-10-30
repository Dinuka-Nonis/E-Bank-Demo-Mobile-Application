# Fix 'org.jetbrains.kotlin.kapt' and Sync Incompatibilities

The project was experiencing sync errors due to the `kapt` plugin and `kotlin.sourceSets` DSL being incompatible with the "built-in Kotlin support" enabled in AGP 9.3.1.

## Changes Made

### Project Configuration
- **gradle.properties**: Added `android.disallowKotlinSourceSets=false`. This allows the KSP plugin to continue using the restricted `kotlin.sourceSets` DSL, which is required for its generated code in this AGP version.
- **app/build.gradle.kts**: Verified removal of `kotlin("kapt")`.

### Code Fixes
Fixed pre-existing compilation errors that were discovered during the verification phase:
- **SuccessActivity.kt**: Updated reference from the missing `HomeActivity` to `MainActivity`.
- **ConfirmationFragment.kt**: Fixed a type mismatch error when retrieving the `TransferRequest` from fragment arguments.

## Verification Results

### Automated Tests
- **Gradle Sync**: Successful.
- **Build**: `./gradlew :app:assembleDebug` completed successfully.

> [!NOTE]
> The project is now syncing and building correctly.
