package ro.makore.akrilki_04.ui.joke;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ro.makore.akrilki_04.databinding.FragmentJokeBinding;

public class JokeFragment extends Fragment {

    private FragmentJokeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        JokeViewModel jokeViewModel =
                new ViewModelProvider(this).get(JokeViewModel.class);

        binding = FragmentJokeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textJoke;
        jokeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button refreshButton = binding.refreshButton;
        // Set an OnClickListener for the refresh button
        refreshButton.setOnClickListener(v -> {
            // Call the fetchJoke method when the button is clicked
            jokeViewModel.fetchJoke();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}