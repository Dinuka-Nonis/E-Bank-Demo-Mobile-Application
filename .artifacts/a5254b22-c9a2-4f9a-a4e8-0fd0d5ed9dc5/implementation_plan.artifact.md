# Fix Unresolved Reference 'kapt' in build.gradle.kts

The project is failing to sync because `kapt` is used in the `dependencies` block of `app/build.gradle.kts`, but the `kotlin-kapt` plugin is not applied. Since the `com.google.devtools.ksp` plugin is already applied, the recommended fix is to migrate the Room compiler dependency from KAPT to KSP.

## Proposed Changes

### [app](file:///D:/MobileApp_labs/EBank/app/build.gradle.kts)

#### [MODIFY] [build.gradle.kts](file:///D:/MobileApp_labs/EBank/app/build.gradle.kts)
- Replace `kapt("androidx.room:room-compiler:2.8.4")` with `ksp("androidx.room:room-compiler:2.8.4")`.

## Verification Plan

### Automated Tests
- Run Gradle sync to verify the error is resolved.
- Build the project to ensure Room code generation works with KSP.
