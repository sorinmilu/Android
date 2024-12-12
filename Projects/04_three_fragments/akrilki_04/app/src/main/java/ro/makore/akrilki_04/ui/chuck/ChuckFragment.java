package ro.makore.akrilki_04.ui.chuck;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ro.makore.akrilki_04.databinding.FragmentChuckBinding;

public class ChuckFragment extends Fragment {

    private FragmentChuckBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChuckViewModel chuckViewModel =
                new ViewModelProvider(this).get(ChuckViewModel.class);

        binding = FragmentChuckBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textChuck;
        chuckViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Button refreshButton = binding.refreshButton;
        // Set an OnClickListener for the refresh button
        refreshButton.setOnClickListener(v -> {
            // Call the fetchJoke method when the button is clicked
            chuckViewModel.fetchCNFact();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}