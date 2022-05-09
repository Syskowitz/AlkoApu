package com.example.alko_apu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.util.ArrayList;
//Otto Manninen

public class MainPageFragment extends Fragment {
    private AlkoDataContainer container;
    private Button btnHaku;
    private SeekBar dokabilitySeek;

    public MainPageFragment() { super(R.layout.fragment_main_page); }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dokabilitySeek = (SeekBar) view.findViewById(R.id.docabilitySlider);
        btnHaku = (Button) view.findViewById(R.id.getRandomProductBtn);
        btnHaku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSearch();
                Navigation.findNavController(view).navigate(R.id.searchReadyFragment);
            }
        });

        // Get the class containing all neccessary data.
        container = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if data is not loaded
        if(!container.areProductsLoaded()) {
            Navigation.findNavController(getView()).navigate(R.id.loaderFragment);
        }
    }

    public void doSearch() {
        float searchCriteria = (float) dokabilitySeek.getProgress()/100; // pretty useless but anyway
        float minDocability = (float) 1.6129032 * searchCriteria; // Highest docability in the products
        ArrayList<Integer> matchIndexList = new ArrayList<>();
        int bestIndex;
        // Incase of highest docability, show Gambina no matter what (slider at max)
        if (Float.compare(searchCriteria,(float) 1) == 0) {
            for (int i = 0; i < container.getCrawler().size(); i++) {
                // ID number for gambina, hopefully it does not change :)
                if (container.getCrawler().get(i).getId() == 319027) {
                    // System.out.println(container.getCrawler().get(i));
                    container.setSelectedProduct(container.getCrawler().get(i));
                    return;
                }
            }
        }
        for (int i = 0; i < container.getCrawler().size(); i++) {
            // Nonalcoholic drinks are quite bad when trying to get drunk :)
            if (Float.compare(container.getCrawler().get(i).getPercents(), (float) 0) > 0) {
                // Add product to matches if the docability is higher than minDocability
                if (Float.compare(container.getCrawler().get(i).getVolume() *
                        container.getCrawler().get(i).getPercents() /
                        container.getCrawler().get(i).getPrice(), minDocability) > 0 ) {
                    matchIndexList.add(i);
                }
            }
        }
        // Select a random index from product that match search criteria
        // Random integer from 0 to max size of matchIndexList
        bestIndex = (int)(Math.random() * matchIndexList.size());
        // Set found drink to selected product to be used else where
        container.setSelectedProduct(container.getCrawler().get(matchIndexList.get(bestIndex)));
    }
}