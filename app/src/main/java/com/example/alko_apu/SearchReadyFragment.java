package com.example.alko_apu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import alkoData.AlkoCrawler;
import alkoData.AlkoProduct;

public class SearchReadyFragment extends Fragment {
    private AlkoDataContainer container;

    private Button uusiHaku;
    private Button lisatiedot;
    private Button goToAlkoBtn;
    private TextView productNameText;

    public SearchReadyFragment() {
        super(R.layout.fragment_search_ready);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the class containing all neccessary data.
        container = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);

        uusiHaku = (Button) view.findViewById(R.id.fetchNewBtn);
        lisatiedot = (Button) view.findViewById(R.id.toAnalyzeBtn);
        goToAlkoBtn = (Button) view.findViewById(R.id.toAlkoWebBtn);
        productNameText = (TextView) view.findViewById(R.id.recommendedTxt);

        // Go back, when user clicks 'New Search'
        uusiHaku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });

        // Go to the analyze fragment, if 'More info' is selected
        lisatiedot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.analyzeFragment);
            }
        });

        goToAlkoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Resolve product page url
                AlkoProduct selected = container.getSelectedProduct();
                String url = AlkoCrawler.getProductPageURL(selected);
                // Open url
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Get selected product and set text at the text field
        AlkoProduct selected = container.getSelectedProduct();
        productNameText.setText(selected.getName());
    }
}