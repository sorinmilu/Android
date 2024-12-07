# Android SDK and Command-Line Tools on Ubuntu Without Android Studio

## Prerequisites

```bash
sudo apt update
sudo apt install unzip wget libpulse0
```

## 1. Install JAVA SDK

Access [Java SE Downloads page](https://www.oracle.com/java/technologies/downloads/) and click Accept License Agreement. Under the Download menu, click the Download link corresponding your requirement.

```bash
sudo dpkg -i jdk-21_linux-x64_bin.deb
```

Verify that the java and javac are installed: 

```bash
java -version
java version "21.0.5" 2024-10-15 LTS
Java(TM) SE Runtime Environment (build 21.0.5+9-LTS-239)
Java HotSpot(TM) 64-Bit Server VM (build 21.0.5+9-LTS-239, mixed mode, sharing)

javac -version
javac 21.0.5

```

## 1. Install Android SDK

### 1.1 Download the SDK

Navigate to the Android Studio download page to find the latest command-line tools for Linux. As of the time of writing, you can download them using the following command:

```bash
wget https://dl.google.com/android/repository/commandlinetools-linux-10406996_latest.zip -O commandlinetools.zip
```

    --2024-11-18 13:43:30--  https://dl.google.com/android/repository/commandlinetools-linux-10406996_latest.zip
    Resolving dl.google.com (dl.google.com)... 142.251.208.142, 2a00:1450:400d:80d::200e
    Connecting to dl.google.com (dl.google.com)|142.251.208.142|:443... connected.
    HTTP request sent, awaiting response... 200 OK
    Length: 148832188 (142M) [application/zip]
    Saving to: ‘commandlinetools.zip’

    commandlinetools.zip         100%[=============================================>] 141.94M  12.4MB/s    in 11s

    2024-11-18 13:43:42 (12.7 MB/s) - ‘commandlinetools.zip’ saved [148832188/148832188]

### 1.2 Create a directory for Android SDK

```bash
mkdir -p ~/Android/Sdk/cmdline-tools
```

### 1.3 Unzip the downloaded file

```bash
unzip commandlinetools.zip -d ~/Android/Sdk/cmdline-tools
```

### 1.4 Rename the Unzipped folder

```bash
mv ~/Android/Sdk/cmdline-tools/cmdline-tools ~/Android/Sdk/cmdline-tools/latest
```

### 1.5 Add the Android SDK tools and command line tools to your path

```bash
nano ~/.bashrc
```

Add the following to the end of file

```bash
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin
export PATH=$PATH:$ANDROID_SDK_ROOT/platform-tools
```
Save and close the file (Ctrl+X, then Y, then Enter in nano). After editing the profile file you need to reload your profile uding: 

```bash
source ~/.bashrc
```

### 1.6 Verify the installation

```bash
sdkmanager --version

11.0
```

You should see the version of the SDK manager printed in the terminal.

## 2 Install essential SDK packages

```bash
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0" 
sdkmanager "emulator"

```
### Add emulator path to PATH in ~/.bashrc

```bash
export PATH=$PATH:$ANDROID_SDK_ROOT/emulator
```



## 3. Install Gradle

### 3.1.  Download the latest Gradle distribution from https://gradle.org/releases

The current Gradle release is version 8.11, released on 11 Nov 2024. The distribution zip file comes in two flavors:

- Binary-only
- Complete, with docs and sources
If in doubt, choose the binary-only version and browse docs and sources online.

### 3.2. Unzip the downloaded archive in /opt folder

```bash
sudo unzip commandlinetools.zip -d /opt
```

### 3.3. Create a symlink from /opt/gradle-version to /opt/gradle

```bash
ln -s /opt/gradle-8.11/ /opt/gradle
```

### 3.4. Add gradle bin directory to your path

Edit again your ~/.bashrc file and add the following line

```bash
export PATH=$PATH:/opt/gradle/bin
```

### 3.5. Test your instalation

```bash

gradle -v

------------------------------------------------------------
Gradle 8.11
------------------------------------------------------------

Build time:    2024-11-11 13:58:01 UTC
Revision:      b2ef976169a05b3c76d04f0fa76a940859f96fa4

Kotlin:        2.0.20
Groovy:        3.0.22
Ant:           Apache Ant(TM) version 1.10.14 compiled on August 16 2023
Launcher JVM:  21.0.5 (Oracle Corporation 21.0.5+9-LTS-239)
Daemon JVM:    /usr/lib/jvm/jdk-21.0.5-oracle-x64 (no JDK specified, using current Java home)
OS:            Linux 5.15.153.1-microsoft-standard-WSL2 amd64

```

## Final steps

Add your user to kvm group

```bash
 sudo usermod -aG kvm sorin
``` 



## Initialize tools

Download platform and create emulator

```bash
sdkmanager "system-images;android-34;google_apis;x86_64"

avdmanager create avd -n 'kriklik' -k "system-images;android-34;google_apis;x86_64" -d pixel_7

emulator -list-avds
kriklik

```

Start the emulator with the device you created

```bash
emulator -avd kriklik
emulator -avd kriklik -no-snapshot-save 
```

Delete the avd if necessary

```bash
 avdmanager delete avd -n kriklik

AVD 'kriklik' deleted.
```