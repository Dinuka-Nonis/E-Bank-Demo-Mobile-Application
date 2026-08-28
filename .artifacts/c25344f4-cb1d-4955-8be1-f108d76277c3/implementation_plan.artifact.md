# Fix Unresolved Reference 'kapt'

The project is experiencing a sync error because `kapt` is used in `app/build.gradle.kts` but the `kotlin-kapt` plugin is not applied. However, the `com.google.devtools.ksp` (KSP) plugin is already applied. KSP is the recommended alternative to KAPT for Room.

## Proposed Changes

### [app]

#### [MODIFY] [build.gradle.kts](file:///D:/MobileApp_labs/EBank/app/build.gradle.kts)
- Replace `kapt("androidx.room:room-compiler:2.8.4")` with `ksp("androidx.room:room-compiler:2.8.4")`.

## Verification Plan

### Automated Tests
- Run Gradle sync to verify the 'Unresolved reference' error is resolved.
- Run `./gradlew app:assembleDebug` to ensure annotation processing with KSP works correctly.

### Manual Verification
- Verify that the Room database classes are generated correctly in the `build/generated/ksp` directory.
