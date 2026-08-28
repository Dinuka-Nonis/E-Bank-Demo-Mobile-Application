# Fix Unresolved reference 'kapt'

The project is experiencing an `Unresolved reference 'kapt'` error during Gradle sync. This is because the `kapt` configuration is being used in `app/build.gradle.kts` without the `kotlin-kapt` plugin being applied. Since the project already has the `com.google.devtools.ksp` plugin applied and is using Room (which supports KSP), the recommended solution is to migrate from Kapt to KSP for Room.

## Proposed Changes

### [app module]

#### [MODIFY] [build.gradle.kts](file:///D:/MobileApp_labs/EBank/app/build.gradle.kts)
- Replace `kapt("androidx.room:room-compiler:2.8.4")` with `ksp("androidx.room:room-compiler:2.8.4")`.

## Verification Plan

### Automated Tests
- Run Gradle sync to ensure the unresolved reference error is gone.
- Build the project to verify that Room code generation works with KSP.
