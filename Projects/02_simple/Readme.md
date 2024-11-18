# Project files


## AndroidManifest.xml

### Description

The AndroidManifest.xml file is a critical component of an Android app. It provides essential information about the app to the Android system, including its components, permissions, hardware and software requirements, and other configurations. Here's a detailed breakdown of its purposes:

### Components

#### 1. App Components Declaration
It declares all the major components of your app, such as:

- Activities: Defines UI screens and entry points for user interactions.
- Services: Handles background processing tasks.
- Broadcast Receivers: Responds to system-wide events or app-specific broadcasts.
- Content Providers: Manages app data shared across apps.

#### 2. Permissions
It specifies permissions required for the app to access restricted features like the internet, location, or camera.

#### 3. Hardware and Software Requirements
It declares what hardware and software features the app depends on, helping the Play Store filter the app for compatible devices.

#### 4. Application-Level Metadata
Provides metadata about the app, such as: Name, Theme, Icon, Version information

#### 5. Intent Filters
Defines how the app responds to specific intents, allowing external apps or system events to interact with it.

#### 6. Broadcast Receiver Registration
Registers BroadcastReceiver components to listen for specific system or app events.

#### 7. Specifies App Entry Point
It defines which activity is the first screen to launch when the app starts.

#### 8. Configures App Behavior
It defines app-wide configurations like:

- Backup settings
- Multi-process handling
- Data sharing with other apps

### Local file contents

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
        android:allowBackup="true"
        android:label="Alkrilki02"
        android:supportsRtl="true"
        android:theme="@style/Theme.Material3.DayNight.NoActionBar"
        tools:targetApi="31">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### Details

#### android:allowBackup="true"

Enables the ability for the app's data to be backed up and restored by the Android system (e.g., during a device migration or factory reset). If set to true, app data (like shared preferences and database files) can be saved to the user's Google account and restored on a new device. If you don't want the app's data to be backed up, especially for security or privacy reasons.

#### android:label="Alkrilki02"

Sets the name of your app that will appear to the user, such as on the home screen or in the app drawer. It appears as the app's name on the launcher icon and task manager.
You can reference a string resource instead of hardcoding the name:

#### android:supportsRtl="true"

Indicates whether the app supports right-to-left (RTL) layouts for languages like Arabic or Hebrew. When true, Android adjusts the layout automatically to support RTL when the device language is set to an RTL language. If your app doesn't need RTL support, you can disable it to avoid layout mirroring.

#### android:theme="@style/Theme.Material3.DayNight.NoActionBar"

Defines the default theme for the app, applied to all activities unless overridden. Uses Material 3 styling with Day/Night mode and no action bar. You can modify or create themes in the res/values/themes.xml file and reference them here.

#### tools:targetApi="31"

A development tool attribute that indicates you're targeting API level 31 (Android 12) features or higher. Helps tools like Android Studio understand the intended API level for advanced features during development. Doesn't Affect Users, it is a build-time hint for tools and doesn’t affect the actual runtime behavior.

### Permissions

```xml
 <uses-permission android:name="android.permission.INTERNET" />
```

This specific permission, android.permission.INTERNET, allows your app to access the internet. Without this permission, any network-related operations (like making API calls, downloading data, or loading web pages) will fail, even if the device is connected to the internet.

### Intent filters

```xml
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
```

The <intent-filter> in your example is used to declare how the app's components (like activities) can interact with the Android system and other apps. Is used to define the entry point of your app — the activity that launches when the app is started from the app launcher. Here's what each element does:

```xml
<action android:name="android.intent.action.MAIN" />
```

This specifies that the activity is the "main" entry point for the app.

android.intent.action.MAIN: A predefined action that indicates this activity is the main entry point when the app starts.
Only one activity in the app should have this action.

```xml
<category android:name="android.intent.category.LAUNCHER" />
```

This specifies that the activity should appear in the device's app launcher (the grid of installed apps).

android.intent.category.LAUNCHER: A predefined category that makes the activity launchable from the home screen or app drawer.
How They Work Together
When you combine android.intent.action.MAIN and android.intent.category.LAUNCHER, it tells the Android system:

This is the main activity of the app.
Show this activity in the launcher, so users can tap it to open the app.


## activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!-- TextView to display the joke -->
    <TextView
        android:id="@+id/jokeTextView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="Joke will appear here"
        android:textSize="24sp"
        android:textAlignment="center"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@+id/refreshButton"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:padding="16dp"
        android:gravity="center"
        android:layout_marginTop="16dp"
        android:layout_marginBottom="16dp"/>

    <!-- Refresh Joke Button -->

    <!-- Quit Button -->

    <Button
        android:id="@+id/refreshButton"
        android:layout_width="407dp"
        android:layout_height="97dp"
        android:text="Refresh Joke"
        android:textSize="18sp"
        app:layout_constraintBottom_toTopOf="@+id/quitButton"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

    <Button
        android:id="@+id/quitButton"
        android:layout_width="404dp"
        android:layout_height="98dp"
        android:text="Quit"
        android:textSize="18sp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toStartOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

### General properties

#### androidx.constraintlayout.widget.ConstraintLayout 

- androidx: This indicates that the class is part of the AndroidX library. AndroidX is a modern, backward-compatible, and modular library that replaces the older Android Support Library.

- constraintlayout: This is the specific library within AndroidX that contains the ConstraintLayout and related classes. A ConstraintLayout is a versatile and powerful layout in Android. It allows you to create complex UI designs without deeply nested layouts. You position child views (e.g., buttons, text views) by defining constraints between them or with respect to the parent container.


- widget: This signifies that the class is part of the widget package, which contains UI elements.

- ConstraintLayout: This is the name of the class itself. It is a layout manager used to arrange and position views (UI elements) on the screen.

#### xmlns:android="http://schemas.android.com/apk/res/android"

This declares the XML namespace for standard Android attributes (e.g., android:id, android:layout_width). Purpose: It allows the use of attributes prefixed with android: in the XML file. It links to the Android resource schema.


#### xmlns:app="http://schemas.android.com/apk/res-auto"

This declares the XML namespace for attributes provided by libraries (e.g., custom attributes in ConstraintLayout or Material components). It enables the use of attributes prefixed with app: in the XML file. These attributes are typically defined by AndroidX libraries or custom views.

#### xmlns:tools="http://schemas.android.com/tools"

This declares the XML namespace for attributes provided by libraries (e.g., custom attributes in ConstraintLayout or Material components). It enables the use of attributes prefixed with app: in the XML file. These attributes are typically defined by AndroidX libraries or custom views.

#### android:id="@+id/main" 

This assigns a unique ID to the view (in this case, the root layout with ID main). Purpose: IDs are used to reference the view in Java/Kotlin code or other XML layouts (e.g., findViewById(R.id.main)


####  android:layout_width="match_parent"

This sets the width of the view to match the width of its parent. Purpose: Ensures the view spans the entire width of the screen (or its parent container).


#### android:layout_height="match_parent"

This sets the height of the view to match the height of its parent. Purpose: Ensures the view spans the entire height of the screen (or its parent container).

#### tools:context=".MainActivity"

This specifies the associated context or activity class for the layout, in this case, MainActivity. Purpose: Used by Android Studio to provide better design-time tooling, such as showing a preview of the layout in the context of the specified activity. It does not affect the app at runtime.


### TextView


#### android:id="@+id/jokeTextView"

This assigns a unique ID to the TextView so it can be referenced in Java/Kotlin code or other XML layouts. Purpose: Essential for linking this view to logic in the code, such as updating the displayed joke (findViewById(R.id.jokeTextView)).

#### android:layout_width="0dp"

The width of the TextView is set to 0dp, which is specific to ConstraintLayout. Purpose: This means the actual width will be determined by the constraints (defined later with app:layout_constraintStart_toStartOf="parent" and app:layout_constraintEnd_toEndOf="parent"). This is used when you want the view to stretch or align dynamically with constraints.

#### android:layout_height="wrap_content"

The height of the TextView will adjust to fit its content. Purpose: Ensures the TextView expands vertically only as much as needed for the text it contains.

#### android:text="Joke will appear here"

This sets the initial text to be displayed in the TextView. Purpose: Serves as placeholder text to inform the user what the TextView is for.

#### android:textSize="24sp"

Sets the size of the text to 24sp (scale-independent pixels). Purpose: Ensures the text size adjusts appropriately for different screen resolutions and user accessibility settings.

#### android:textAlignment="center" 

Aligns the text horizontally in the center of the TextView. Purpose: Makes the joke appear centered within the view.

#### app:layout_constraintTop_toTopOf="parent"

Constrains the top of the TextView to the top of the parent layout. Purpose: Anchors the TextView to the top of the screen (or its container)

#### app:layout_constraintBottom_toTopOf="@+id/refreshButton"

Constrains the bottom of the TextView to the top of the refreshButton. Purpose: Ensures the TextView sits just above the refreshButton

#### app:layout_constraintStart_toStartOf="parent"

Constrains the start (left) edge of the TextView to the start (left) edge of the parent. Purpose: Centers the TextView horizontally when used with app:layout_constraintEnd_toEndOf="parent".

#### app:layout_constraintEnd_toEndOf="parent"

Constrains the end (right) edge of the TextView to the end (right) edge of the parent. Purpose: Centers the TextView horizontally

#### android:padding="16dp"

Adds 16dp padding inside the TextView on all sides. Purpose: Ensures that the text inside the TextView does not touch its edges.

#### android:gravity="center"

Aligns the text content (inside the TextView) both horizontally and vertically within its bounds. Purpose: Ensures the joke text appears centered in the TextView

#### android:layout_marginTop="16dp"

Adds a margin of 16dp above the TextView. Purpose: Creates visual space between the TextView and the top of the parent/container.

#### android:layout_marginBottom="16dp"

Adds a margin of 16dp below the TextView. Purpose: Creates visual space between the TextView and the refreshButton.

## Button



#### android:id="@+id/refreshButton"

Assigns a unique ID to the button so it can be referenced in the code. Purpose: Enables linking this button to Java/Kotlin code to set up functionality like refreshing a joke.

#### android:layout_width="407dp"

Sets the button’s width to a fixed size of 407dp (density-independent pixels). Purpose: Ensures the button has a consistent width across different screen densities. However, this is less flexible than using a responsive value like wrap_content or match_parent.

#### android:layout_height="97dp"
#### android:text="Refresh Joke"
#### android:textSize="18sp"
#### app:layout_constraintBottom_toTopOf="@+id/quitButton"
#### app:layout_constraintEnd_toEndOf="parent"
#### app:layout_constraintStart_toStartOf="parent"

## MainActivity.java



### public class MainActivity extends AppCompatActivity 

extends means that MainActivity is inheriting from another class, in this case, AppCompatActivity. AppCompatActivity is a subclass of Activity provided by AndroidX. It Provides backward compatibility for older Android versions. Offers modern app functionality such as using the Material Design components. Integrates support for managing app themes and toolbars easily.

Modern Android apps often use AppCompatActivity rather than the base Activity class because it simplifies: Adding toolbars (ActionBar). Applying Material Design themes. Maintaining compatibility across devices with different Android versions. MainActivity Inherits the lifecycle methods like onCreate() from AppCompatActivity. Displays the activity_main.xml layout via setContentView().

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
```

The @Override annotation indicates that the method that follows is overriding a method from a superclass. In this case, the onCreate() method is overriding the onCreate() method defined in the Activity (or AppCompatActivity) class. Purpose: It helps ensure that you're correctly overriding a method that exists in the superclass. If you mistakenly don't match the method signature (name, parameters), the compiler will show an error. 

### protected void onCreate(Bundle savedInstanceState)

This is the definition of the onCreate() method, which is one of the lifecycle methods in Android. It is called when the activity is created.

protected: This is the method's access modifier. It means that this method can be accessed from: Within the class itself (MainActivity in this case).
Any subclass of MainActivity (i.e., subclasses of AppCompatActivity). Any class in the same package. 

void: The return type of this method. It means that onCreate() does not return any value.

onCreate(Bundle savedInstanceState):

This method takes a single parameter: savedInstanceState, which is a Bundle object. The Bundle object contains data about the previous state of the activity, in case the activity is being recreated (for example, after being paused and resumed, or after a configuration change like screen rotation). You can use this object to restore the state of your activity (such as user inputs or UI state) when the activity is recreated. 

What Does onCreate() Do?
The onCreate() method is a critical part of an Android activity's lifecycle. It is the first method called when an activity is created (when the app is launched, or when a configuration change happens, such as device rotation). Inside this method, you usually: Set up the UI using setContentView() to link the activity with a layout file (XML).
Initialize components, such as buttons, text views, or other UI elements. Set listeners for user interactions (e.g., button clicks).

This code fragment 

```java
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        return insets;
    });
```

is using the ViewCompat class to manage window insets, particularly for system bars like the status bar and navigation bar. This is often done to ensure that your UI elements don't get overlapped or hidden by the system's status and navigation bars, especially on devices with edge-to-edge displays.

### ViewCompat.setOnApplyWindowInsetsListener():

This method registers a listener that will be triggered whenever the window insets need to be applied (e.g., when the system bars change, or when the device orientation changes).

### findViewById(R.id.main): 

This finds the view (in this case, a View with the ID main, likely a root layout) that will be modified by the insets.
### (v, insets) -> {...}: 

This is a lambda expression that defines the logic for applying the insets. It has two parameters:

v: The view that the listener is applied to (in this case, the view with ID main).
insets: The window insets, which represent the space occupied by system bars, such as the status bar (top) and navigation bar (bottom).

###  insets.getInsets(WindowInsetsCompat.Type.systemBars()):
This method retrieves the system bar insets (status bar and navigation bar) from the WindowInsetsCompat object.
WindowInsetsCompat.Type.systemBars() specifies that you want to get the space used by system bars (top and bottom) and apply it accordingly.

###  v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom):
This line sets the padding for the view based on the system bar insets.
The systemBars object contains the insets for the left, top, right, and bottom edges, which represent the space taken up by the system bars (e.g., the status bar at the top, the navigation bar at the bottom).

### v.setPadding() 

adjusts the view's padding, ensuring that the content inside the view does not overlap with the system bars.

###  return insets
This returns the insets object, which is required to indicate that the insets have been applied successfully.


