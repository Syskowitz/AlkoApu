package com.example.alko_apu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.io.IOException;

import alkoData.AlkoProduct;

public class AnalyzeFragment extends Fragment {

    private Button findClosest;   // TODO Add find closest Alko-store if time permits :)
    private Button giveRating;
    private Button analyzeAddFavourite;

    private TextView productDataText;

    private AlkoDataContainer container;

    public AnalyzeFragment() {
        super(R.layout.fragment_analyze);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        container = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);

        findClosest = (Button) view.findViewById(R.id.findNearestBtn);
        giveRating = (Button) view.findViewById(R.id.giveRatingBtn);
        analyzeAddFavourite = (Button) view.findViewById(R.id.addFavouriteBtn);

        productDataText = (TextView) view.findViewById(R.id.productInfoTxt) ;

        // Switch to rating activity
        giveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.rateFragment);
            }
        });


        analyzeAddFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the product is already in favourites
                int selectedID = container.getSelectedProduct().getId();
                for (int i = 0; container.getFavouriteList().size() > i; i++) {
                    if (container.getFavouriteList().get(i).getId() == selectedID) {
                        Toast toast = Toast.makeText(getContext(),"Oli jo suosikeissa!", Toast.LENGTH_SHORT);
                        toast.show();
                        return; // Already in favourites
                    }
                }
                container.getFavouriteList().add(container.getSelectedProduct());

                // Save the list to the local file system
                try {
                    container.getFavouriteList().saveDataToLocalFile();
                } catch(IOException exc) {
                    exc.printStackTrace();
                }
                Toast toast = Toast.makeText(getContext(),"Tallennettu suosikkeihin!", Toast.LENGTH_SHORT);
                toast.show();

            }
        });


        findClosest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // One day this may do something :(
                Toast toast = Toast.makeText(getContext(),"Lisätään syssymmällä!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // Show product information at text fields
        AlkoProduct selected = container.getSelectedProduct();
        productDataText.setText(selected.toString());
    }
}