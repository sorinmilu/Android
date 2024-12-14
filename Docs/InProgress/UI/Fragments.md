# Fragments

A fragment in Android is a reusable UI component that represents a portion of a user interface (UI) in an app. It is modular and flexible, allowing you to create dynamic and adaptable UIs for different screen sizes and orientations.

## Key Characteristics of a Fragment:

### 1. Part of an Activity:

Fragments exist within and are hosted by an Activity. They cannot exist independently. A single activity can contain multiple fragments, which enables you to build multi-pane layouts.

### 2. Lifecycle:

Fragments have their own lifecycle that is closely tied to the activity's lifecycle.

Example lifecycle states:

- onCreateView() (to inflate the layout).
- onViewCreated() (to initialize views).
- onDestroyView() (to clean up resources).

Their lifecycle is affected by the hosting activity; for example, when an activity is paused, its fragments are also paused.

### 3. UI and Behavior:

A fragment typically has a layout defined in XML. It can include UI components like buttons, text views, and images, and handle user interactions.

### 4. Modularity:

Fragments make it easier to create reusable and adaptable UI components. Example: You can use the same fragment in a phone layout (where it occupies the whole screen) and a tablet layout (where it appears alongside another fragment).

## Use Cases for Fragments:

### 1. Dynamic UI:

Fragments allow for dynamic UI changes. For example, swapping fragments in an activity without reloading the activity. For example: In a news app, one fragment might list articles, while another displays the details of a selected article.

### Responsive Design:

Fragments make it easier to adapt UIs for different screen sizes and orientations. Example: On tablets, you might show a master-detail layout with two fragments side by side, while on phones, you show one fragment at a time.

### Navigation:

When using the Navigation Component, fragments act as navigation destinations, allowing you to handle transitions, animations, and back-stack management efficiently.


## Fragment vs. Activity:

|Aspect  |	Fragment  |	Activity |
|--------|-----------|----------|
|Dependency |	Always exists within an activity	| Can exist independently.| 
|Lifecycle	| Tied to the lifecycle of the parent activity	| Manages its own lifecycle. |
|Modularity	| Designed for reuse and embedding in activities |Typically represents a single screen. |
|Navigation	| Best suited for modern, fragment-based navigation	| Handles entire app navigation by default. |


## Common Methods in a Fragment:

### onCreateView():

Inflate the fragment's layout and return the view.

### onViewCreated():

Called after the layout has been created; good for setting up logic or listeners.

### onDestroyView():

Clean up resources tied to the view (e.g., adapters, bindings).

###  onAttach() and onDetach():

Called when the fragment is attached to or detached from the host activity.

## Example:

### Fragment Layout (fragment_example.xml):

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <TextView
        android:id="@+id/example_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello, Fragment!" />
</LinearLayout>
```

### Fragment Class (ExampleFragment.java):

```java

public class ExampleFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, 
                             @Nullable ViewGroup container, 
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_example, container, false);
    }
}
```

### Adding a Fragment in an Activity:
``` xml

<FrameLayout
    android:id="@+id/fragment_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

```java
FragmentManager fragmentManager = getSupportFragmentManager();
FragmentTransaction transaction = fragmentManager.beginTransaction();
transaction.add(R.id.fragment_container, new ExampleFragment());
transaction.commit();
```

## Conclusion:

Fragments are essential for building flexible, modular, and dynamic Android apps. They simplify UI management, particularly in complex and responsive designs.

## Types of layouts involving fragments

### Multiple visible fragments

### Master-Detail Layout

One fragment displays a list (e.g., a list of emails). Another fragment shows details of the selected item (e.g., the email's content).

### Split layout

A split screen where one fragment contains controls (e.g., a form) and another displays related information (e.g., a preview).

### Dashboard layout

Dashboards, control panels, or summary screens. Multiple fragments arranged in a grid or tile-based layout to display different types of information.

### Sliding Panel Layout

Apps with collapsible or expandable menus. Description: One fragment (e.g., a menu) can slide in and out to reveal or hide another fragment.

### ViewPager with Tabs

### Persistent Bottom Sheet with Content (browser console)

Apps that use a bottom sheet for navigation or displaying secondary information.
Description: One fragment displays the main content, and another appears as a collapsible panel at the bottom.

## By type

In Android, views defined in layouts can generally be classified into two broad categories:

### Widgets (UI Elements)
These are individual, interactive components that users can see and interact with. Examples include:
- TextView
- Button
- ImageView
- EditText
- CheckBox
- RadioButton
- Switch
- ProgressBar

Widgets are primarily used to display content or receive user input.

### ViewGroups (Containers)
These act as containers for organizing and managing other views, including both widgets and other ViewGroups. Examples include:

- LinearLayout
- RelativeLayout
- ConstraintLayout
- FrameLayout
- ScrollView
- RecyclerView
- ListView
- ViewPager

ViewGroups are responsible for layout and arrangement. They determine the positioning and behavior of their child views.

#### Key Difference

- Widgets: Represent individual UI components.
- ViewGroups: Represent containers that hold and organize widgets or other ViewGroups.

#### Clarification
While these two categories cover most cases, it's worth noting that:

All UI components, whether widgets or ViewGroups, inherit from the View class.
Certain elements, like FragmentContainerView or SurfaceView, might not fit cleanly into these two categories because they serve specialized purposes but are technically still View or ViewGroup.