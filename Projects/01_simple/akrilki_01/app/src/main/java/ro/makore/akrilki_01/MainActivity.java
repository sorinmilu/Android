package ro.makore.akrilki_01;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Find the quit button
        Button quitButton = findViewById(R.id.quitButton);

        // Add the quit button action
        quitButton.setOnClickListener(v -> finishAffinity());

    }
}