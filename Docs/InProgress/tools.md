# The Android SDK is a collection of tools and libraries for developing Android apps. It includes:

## ADB (Android Debug Bridge): A tool for managing devices.

	Think of ADB as a bridge between your computer and Android devices.
	It allows you to:
		Install apps onto a device.
		Debug apps running on a device.
		Access device logs, file systems, or shell commands.

## Emulator: A tool to simulate Android devices on your computer.

	The Emulator is a software tool that simulates an Android device on your computer.
		It mimics real Android hardware and software, so you can test apps without needing a physical device.

	Why Use an Emulator?
		You can test apps on different Android versions and device configurations.
		It’s useful for debugging apps in a controlled environment.

	How It Works:
		Runs on your computer’s CPU (often uses virtualization for speed).
		Simulates device properties like screen size, RAM, storage, etc.
		You start an emulator by selecting an AVD (Android Virtual Device) configuration.

## AVD (Android Virtual Device): A configuration for a specific virtual Android device.

    An AVD is a profile or configuration for a virtual Android device.

    It defines:
        The Android version (e.g., Android 13, API level 33).
        Hardware specs (screen size, CPU architecture, RAM, etc.).
        Whether it includes Google Play or other APIs.
    For example:
        A "Pixel 4 with Android 12" is an AVD configuration.
        You can create different AVDs to simulate various device types.

