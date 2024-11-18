# 1. Set Up Your Environment

Ensure you have the Android SDK installed, including build-tools and platform-tools.
Add the SDK's tools and platform-tools directories to your system PATH.

# 2. Directory Structure

Ensure your project structure aligns with the requirements of the Android build system:

```
app/
├── AndroidManifest.xml
├── src/
│   └── main/
│       └── java/
│           └── ro/
│               └── makore/
│                   └── akril/
│                       └── MainActivity.java
├── res/
│   ├── layout/
│   │   └── activity_main.xml
└── assets/ (if needed)
```

# 3. Compile Java Code

Navigate to the src directory containing the MainActivity.java file:

```bash
cd app/src/main/java
```

# 4. Compile Java source code to .class files using javac:

```bash
javac -source 1.8 -target 1.8 -d ../../../../bin \
      -classpath ${ANDROID_SDK}/platforms/android-<API_LEVEL>/android.jar \
      ro/makore/akril/MainActivity.java

```
Replace <API_LEVEL> with the appropriate API level (e.g., 33 for Android 13).

# 5. Convert to DEX

Use the d8 or dx tool to convert .class files to Dalvik Executable (.dex):
```bash
${ANDROID_SDK}/build-tools/<VERSION>/d8 ../../bin/*.class --output ../../bin/
```
Replace <VERSION> with the appropriate build tools version (e.g., 33.0.0).

# 6. Package Resources

### Compile XML resources to a binary format using aapt2:

```bash
${ANDROID_SDK}/build-tools/<VERSION>/aapt2 compile -o ../../bin/res.zip ../../res/layout/activity_main.xml
```
Here the layouts (xml) files are used


### Link resources and generate an APK package using aapt2:

```bash
${ANDROID_SDK}/build-tools/<VERSION>/aapt2 link -o ../../bin/app.unaligned.apk \
      -I ${ANDROID_SDK}/platforms/android-<API_LEVEL>/android.jar \
      --manifest ../../AndroidManifest.xml \
      -R ../../bin/res.zip
```

# 7. Sign the APK

## 1. Generate a keystore (if you don’t already have one):

```bash
keytool -genkey -v -keystore my-release-key.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

## 2. Sign the APK using apksigner

```bash
${ANDROID_SDK}/build-tools/<VERSION>/apksigner sign --ks my-release-key.keystore \
      --out ../../bin/app-signed.apk ../../bin/app.unaligned.apk
```

# 8. Align the APK

Optimize the APK using zipalign:

```bash
${ANDROID_SDK}/build-tools/<VERSION>/zipalign -v 4 ../../bin/app-signed.apk ../../bin/app-release.apk
```

# 9. Install and Test

```bash
adb install ../../bin/app-release.apk
```


