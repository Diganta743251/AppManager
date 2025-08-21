---
description: Repository Information Overview
alwaysApply: true
---

# App Manager Information

## Summary
App Manager is an advanced Android application manager that provides extensive features for managing installed applications. It offers capabilities like viewing app components, intercepting activities, scanning for trackers, backing up/restoring apps, and many more features. The app supports both regular and root/ADB operations, making it a comprehensive tool for Android power users.

## Structure
- **app/**: Main application module
- **docs/**: Documentation module
- **hiddenapi/**: Hidden API access module
- **libcore/**: Core libraries (compat, io, ui)
- **libopenpgp/**: OpenPGP implementation
- **libserver/**: Server library
- **server/**: Server implementation
- **scripts/**: Build and utility scripts
- **schema/**: XML schema definitions
- **arts/**: App icons and graphics

## Language & Runtime
**Language**: Java
**Version**: Java 8 (with desugaring for newer features)
**Build System**: Gradle
**Package Manager**: Gradle
**Android SDK**: 
- Compile SDK: 35
- Target SDK: 35
- Min SDK: 21
- Build Tools: 35.0.0
- NDK Version: 25.2.9519653
- CMake Version: 3.22.1

## Dependencies
**Main Dependencies**:
- AndroidX Core: 1.15.0
- AndroidX AppCompat: 1.7.0
- Material: 1.12.0
- Room: 2.6.1
- Sora Editor: 0.22.1
- LibSU: 6.0.0
- BouncyCastle: 1.80
- Smali/Baksmali: 3.0.9
- JADX: 1.4.7
- HiddenApiBypass: 6.1
- Zstd: 1.5.6-10

**Development Dependencies**:
- JUnit: 4.13.2
- Robolectric: 4.14.1

## Build & Installation
```bash
# Clone repository with submodules
git clone --recurse-submodules https://github.com/MuntashirAkon/AppManager.git

# Build debug version
./gradlew packageDebugUniversalApk

# Build bundled app in APKS format
./scripts/aab_to_apks.sh debug  # or 'release'
```

## Testing
**Framework**: JUnit with Robolectric
**Test Location**: app/src/test/java
**Run Command**:
```bash
./gradlew test
```

## Native Components
**Native Build System**: CMake
**Native Source**: app/src/main/cpp
**Supported ABIs**: armeabi-v7a, arm64-v8a, x86, x86_64

## Modules
**Main Modules**:
- **app**: Main application
- **docs**: Documentation
- **hiddenapi**: Hidden API access
- **libcore:compat**: Compatibility library
- **libcore:io**: I/O operations library
- **libcore:ui**: UI components library
- **libopenpgp**: OpenPGP implementation
- **libserver**: Server library
- **server**: Server implementation