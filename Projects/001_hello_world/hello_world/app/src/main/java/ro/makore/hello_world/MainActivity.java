package ro.makore.hello_world;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a LinearLayout programmatically
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Create a TextView with "Hello World"
        TextView textView = new TextView(this);
        textView.setText("Hello World");
        textView.setTextSize(24);

        // Add the TextView to the layout
        layout.addView(textView);

        // Set the layout as the content view
        setContentView(layout);
    }
}
