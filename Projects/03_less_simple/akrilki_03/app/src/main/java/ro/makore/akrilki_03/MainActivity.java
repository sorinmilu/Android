package ro.makore.akrilki_03;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;
    private TextView jokeTextView;
    private Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the ViewModel
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        jokeTextView = findViewById(R.id.jokeTextView);
        refreshButton = findViewById(R.id.refreshButton);

        Button quitButton = findViewById(R.id.quitButton);
        quitButton.setOnClickListener(v -> finishAffinity());


        // Observe LiveData from the ViewModel
        mViewModel.getText().observe(this, joke -> {
            jokeTextView.setText(joke);
        });

        // Refresh the joke when the button is clicked
        refreshButton.setOnClickListener(v -> mViewModel.fetchJoke());
    }
}
