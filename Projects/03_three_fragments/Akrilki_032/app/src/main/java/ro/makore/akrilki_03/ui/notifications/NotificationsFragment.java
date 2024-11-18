package ro.makore.akrilki_03.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.squareup.picasso.Picasso;

import ro.makore.akrilki_03.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        TextView cocktailTextView = binding.cocktailName;
        TextView cocktailInstructions = binding.instructions;
        TextView cocktailIngredients = binding.ingredients;
        ImageView cocktailImageView = binding.cocktailImage;

        // Observe LiveData from the ViewModel
        notificationsViewModel.getCocktailLiveData().observe(getViewLifecycleOwner(), cocktailData -> {
            // Update the UI with the fetched cocktail data
            if (cocktailData != null) {
                cocktailTextView.setText(cocktailData.getName()); // Assuming CocktailData has a getName method
                cocktailInstructions.setText(cocktailData.getInstructions());
                cocktailIngredients.setText(cocktailData.getIngredients());

                if (cocktailData.getImageUrl() != null && !cocktailData.getImageUrl().isEmpty()) {
                    Picasso.get().load(cocktailData.getImageUrl()).into(cocktailImageView);
                }

            } else {
                cocktailTextView.setText("No data available");
            }
        });

        // Button to trigger data fetching
        Button refreshButton = binding.refreshButton;
        refreshButton.setOnClickListener(v -> {
            notificationsViewModel.fetchCocktailData(); // Fetch new data
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Avoid memory leaks
    }
}
