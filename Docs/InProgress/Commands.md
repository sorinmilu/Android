# Lists hardware profiles 

```bash
avdmanager list device 
```


## What Are Hardware Profiles?

These are predefined templates that describe the characteristics of a typical Android device (like Pixel 4 or Nexus 5). They include:

- Screen size and resolution.
- Hardware capabilities (e.g., GPU, RAM, CPU architecture).
- Manufacturer (OEM).


# Lists local devices 

```bash
avdmanager list avd 
```


# add a virtual device

```bash
avdmanager create avd -n MyPixel4 -k "system-images;android-33;google_apis;x86_64" -d "pixel_4"
```

-k represinta tipul de imagine de sistem catre va fin instalata pe dispozitivul virtual. Este compusa


The format "system-images;<path>" corresponds to the way system images are stored and referenced in the Android SDK. It’s structured to map directly to where the images are stored in the SDK folder.

Structure of -k Option:

- system-images: Indicates this is a system image.
- android-33: Refers to Android API level 33 (Android 13).
- google_apis: Specifies that the image includes Google APIs, allowing features like Google Maps or Firebase to work.
- x86_64: Defines the CPU architecture for the emulator (optimized for desktop performance).

Use the following command to list the system images installed in your SDK or available for download:

```bash
sdkmanager --list | grep "system-images"
```

# Componente disponibile pentru sdk

```bash
sdkmanager --list 
```


## 1. add-ons

### What It Is:
Add-ons are additional SDK packages provided by third parties (like hardware vendors or Google). These often include special APIs or extensions for device-specific development. It’s a package you install into the Android SDK to enhance emulator images. It works with system images to provide emulator support for Google-specific features (e.g., Google Maps, Google Play Services). Without this, system images won’t have Google APIs.

It provides classes and methods for integrating services like Google Maps, Google Cloud Messaging (deprecated), or location-based services directly into your app. The Android SDK is open source under the AOSP (Android Open Source Project), but Google APIs are proprietary. These add-ons are separate to respect this distinction.

Without this add-on, your app or emulator cannot access Google-specific functionality unless you build those features independently. Includes the Google-specific APIs (e.g., com.google.android.gms.* packages), metadata used to define compatibility with emulator system images or physical devices, dependencies that ensures that the APIs integrate properly with the platform.


## 2. build-tools

Build tools include utilities, compilers, and optimizers used during the app development process to create a fully functioning Android application package (APK). They work with the Android Gradle plugin to automate various build tasks.

### aapt (Android Asset Packaging Tool):
Packages app resources (e.g., XML layouts, images) into the APK.
Handles conversion of raw resources into binary format.

### dx and d8 (Dex Compiler):
Translates Java bytecode into Dalvik bytecode for Android's runtime.
d8 is a newer, more efficient replacement for dx and is now the default.

### zipalign:
Optimizes the APK to ensure resources are aligned properly in memory.
This step reduces memory usage and speeds up app loading.

### apksigner:
Signs the APK with a developer's key for security and authenticity.
Required for installation on real devices or publishing to the Play Store.

### aidl (Android Interface Definition Language):
Generates code for interprocess communication (IPC) in Android.
Necessary when using Android services or bound services.

### renderscript (Deprecated):
Tools for compiling RenderScript code (used for high-performance computation).
It’s been phased out in modern Android versions.


## 3. cmake
### What It Is:
A cross-platform build system used for compiling C and C++ code. It’s part of Android’s Native Development Kit (NDK) workflow.

### Why It’s There:
If your app uses native code (e.g., for performance-critical tasks or game development), CMake helps compile that code into binaries that Android can run.

## 4. cmdline-tools
### What It Is:
Command-line tools for managing the SDK, AVDs, and other aspects of Android development. Includes utilities like sdkmanager, avdmanager, and more.

### How Is It Different from platform-tools?

|Aspect	| cmdline-tools	| platform-tools |
|-------|----------------|--------------|
|Purpose	| Manage SDK, AVDs, and overall environment |	Debugging and interacting with Android devices |
|Key Tools	| sdkmanager, avdmanager	| adb, fastboot |
|Focus	| Development environment setup	| Device-level interaction and management |
|Used By	| Developers, CI/CD pipelines	| QA teams, developers during app debugging |


### Why It’s There:
Essential for managing SDK packages and creating emulators without using Android Studio. Ideal for automation and CI/CD pipelines.

## 5. emulator

The emulator component in the Android SDK is a tool that allows developers to simulate Android devices on their computers. It provides a virtual environment for testing and debugging Android applications without needing physical hardware. The emulator is a virtual Android device that runs on your computer, mimicking the functionality of a real Android device. It is part of the Android SDK, and it integrates with tools like Android Studio, adb, and avdmanager. Useful for testing applications across various Android versions, device configurations, and screen sizes.

### Key Features of the emulator are: 

**Device Simulation** Simulates hardware features like cameras, GPS, accelerometers, and more. Can emulate various device types, including phones, tablets, and wearables.

**Customizable Configurations** Allows customization of hardware specs, Android versions, and other settings. Configurations can be managed using avdmanager or Android Studio.

**Integration with Development Tools** Works seamlessly with adb for app installation, debugging, and log analysis. Supports running apps directly from Android Studio or via the command line.

**Testing Across API Levels** Provides access to system images for different Android versions and APIs, enabling compatibility testing. 

**Simulated Network Conditions** Allows developers to test apps under various network conditions, like low bandwidth or no connectivity.

**Snapshot Support** Save and load emulator states to avoid restarting from scratch.

### Commands Related to the Emulator

List Available Virtual Devices:


Launch a Virtual Device:
```bash
emulator -list-avds
```


Kill a Running Emulator:
```bash
emulator -avd <avd-name>
```

Control Network Conditions:

```bash
adb emu kill
```

```bash
adb shell svc wifi disable
adb shell svc data disable
```


## 6. extras
### What It Is:
Optional libraries and tools that extend the functionality of the SDK.

### Examples:

extras;google;google_play_services: Play Services for emulators.
extras;android;m2repository: Support libraries and dependencies.
### Why It’s There:
Not every developer needs these, so they’re offered as optional packages for specific use cases.

## 7. ndk and ndk-bundle

The Native Development Kit (NDK) is a set of tools for writing parts of your app in C or C++. The NDK (Native Development Kit) in the Android SDK is a set of tools that allow developers to write and compile code in C and C++ for Android applications. It provides the ability to create native libraries that can be integrated into Android apps, enabling performance-critical tasks and access to lower-level system APIs.

Key Components of the NDK are: Toolchains - Includes compilers like Clang and tools to build native libraries for different architectures (e.g., arm64-v8a, x86_64), Headers and Libraries - Provides C/C++ headers for Android-specific APIs (e.g., camera, graphics, audio), Includes standard C libraries like libc, libm, and libz.

CMake or ndk-build is used to compile and link the native code. Tools like ndk-stack help analyze crashes in native code by symbolizing stack traces.

Android NDK supports multiple architectures: armeabi-v7a (32-bit ARM), arm64-v8a (64-bit ARM), x86, x86_64

When you write C or C++ code for an Android app using the NDK, the code is compiled into native libraries (.so files). These libraries are then packaged into the final Android app's APK file and are loaded dynamically at runtime.

APK structure:
├── lib/
│   ├── armeabi-v7a/
│   │   └── libmylibrary.so
│   ├── arm64-v8a/
│   │   └── libmylibrary.so
│   ├── x86_64/
│   │   └── libmylibrary.so


## 8. platform-tools

The platform-tools component of the Android SDK is a collection of tools specifically designed for interacting with Android devices (both real and emulated) and managing Android-related workflows at the platform level. These tools are crucial for tasks like app debugging, testing, and device management.

## Includes:

**adb** Android Debug Bridge for interacting with devices. Allows you to transfer files between your computer and a device, install/uninstall apps, debug apps using logs or interactive shell commands, forward ports for networking tasks.

**fastboot** A tool for modifying the Android file system directly when the device is in bootloader mode. Primarily used for flashing firmware or custom recoveries, unlocking/relocking the device bootloader.

**dmtracedump** Visualize trace logs to analyze app performance.

**hprof-conv** Convert memory profiling data to a format readable by tools like Android Studio.

**sqlite3** A command-line interface to manage SQLite databases directly on a connected device or emulator.


## 9. platforms
### What It Is:
SDK files for specific Android versions (API levels). These are used to compile and build apps targeting specific versions of Android.

Why It’s There:
Apps must target specific Android versions to ensure compatibility. Without the corresponding platform SDK, you can’t build or test for that version.

## 10. skiaparser
### What It Is:
A tool for parsing and analyzing Skia graphics library components.

### Why It’s There:
Used internally by the SDK for graphics-related development or rendering optimizations. It’s rarely used directly by developers.

## 11. sources
### What It Is:
The Android SDK source code for specific Android versions.

### Why It’s There:
If you need to debug how Android itself works or understand the underlying framework while developing, you can refer to the source code.

## 12. system-images

### What It Is:
Prebuilt Android OS images used by the emulator to simulate a device.

System images definition contains four tokens separated by semicolon. 

#### First token (system-images):

    Identifies the type of component (e.g., platforms, build-tools, or system-images).
    Tells the parser, "We’re dealing with emulator system images."

#### Second token:

    Specifies the API level or the Android version the image corresponds to.
    The prefix android- signals that it’s an Android version, while the suffix (e.g., TiramisuPrivacySandbox) indicates a specific feature set or preview build. Examples: android-10 up to android-33, android-33-ext4, android-33-ext5, android-34, android-34-ext10, android-34-ext11, android-34-ext12, android-34-ext8, android-34-ext9, android-35
    android-TiramisuPrivacySandbox, android-UpsideDownCakePrivacySandbox

#### Third token (google_apis):

    Describes the variant of the system image (e.g., plain Android, with Google APIs, or Google Play Store-enabled).
    This allows the parser to narrow down the exact type of system image. Variants: android-automotive, android-automotive-distant-display-playstore, android-automotive-playstore
    android-desktop, android-tv, android-wear, android-wear-cn, aosp_atd, default, google-tv, google_apis, google_apis_playstore, google_apis_playstore_ps16k, google_apis_ps16k, google_atd.

#### Fourth token (arm64-v8a):

    Specifies the CPU architecture for the system image.
    Allows the parser to find images matching the hardware or emulator’s architecture.
    The parser looks for this exact structure in the metadata stored in the SDK repository. If any part of the string is invalid, the tool throws an error, like “Package not found” or “Invalid syntax”. Variants: arm64-v8a, armeabi-v7a, x86, x86_64


Or, better yet, offer an interactive mode:

bash
Copy code
sdkmanager --interactive
This could guide you step-by-step:

"Choose an API level:" (List options like android-33, android-34)
"Choose a variant:" (default, google_apis, etc.)
"Choose an architecture:" (x86, arm64-v8a)

# Create a new device

## Installing system images 

```bash
sdkmanager "system-images;android-35;google_apis;x86_64"
Warning: Errors during XML parse:
Warning: Additionally, the fallback loader failed to parse the XML.
[=======================================] 100%
```

```bash
avdmanager create avd -n 'kriklik' -k "system-images;android-35;google_apis;x86_64" -d pixel_7
[=======================================] 100% Fetch remote repository...
Auto-selecting single ABI x86_64
```

```bash
emulator -list-avds
kriklik
```

```bash
cat ~/.android/avd/kriklik.ini
avd.ini.encoding=UTF-8
path=/home/sorin/.android/avd/kriklik.avd
path.rel=avd/kriklik.avd
target=android-35
```

```bash
 cat ~/.android/avd/kriklik.avd/config.ini
PlayStore.enabled=no
abi.type=x86_64
avd.id=<build>
avd.ini.encoding=UTF-8
avd.name=<build>
disk.cachePartition=yes
disk.cachePartition.size=66MB
disk.dataPartition.path=<temp>
disk.dataPartition.size=800M
... a lot more stuff

```

# Build and install

## Build

```bash
cd /path/to/your/project/akrilki_01
```

```bash
gradle build
```

After building, if successfull, there will be an apk, usually located in `app/build/outputs/apk/debug/app-debug.apk`

## Start the emulator

### List all emulators

```bash
emulator -list-avds

kriklik
testDevice
```

### Start the emulator

```bash 
emulator -avd kriklik
```

or

```bash
emulator -avd kriklik -no-snapshot-save 
```

to start with no saved state.


### Check the device name

```bash
adb devices
List of devices attached
emulator-5554   device
```

### Install the apk on device

```bash
adb -s emulator-5554 install  app/build/outputs/apk/debug/app-debug.apk
```

### Uninstall 

```bash
adb shell pm list packages | grep akrilki
package:ro.makore.akrilki_03

adb uninstall ro.makore.akrilki_03
Success
```

### Debugging

```bash
adb logcat
```

For example: 

```bash
11-17 13:13:03.341  3616  3616 E AndroidRuntime: java.lang.RuntimeException: Unable to start activity ComponentInfo{ro.makore.akrilki_03/ro.makore.akrilki_03.MainActivity}: java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.
```





