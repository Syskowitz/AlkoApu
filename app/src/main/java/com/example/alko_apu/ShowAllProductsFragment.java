package com.example.alko_apu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import alkoData.AlkoProduct;

public class ShowAllProductsFragment extends Fragment {
    // Does not open at all :(
    private ListView allProductsLV;
    private Spinner filterChooserS;
    private ArrayList<AlkoProduct> AlkoProductTempArray; // tempArray to give to ArrayAdapter, might need lots of RAM
    private ArrayList<String> spinnerFilterText;
    private AlkoDataContainer alkoContainer; // access to AlkoProducts

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println("NO JO ON PERKELE TYÖMAA :((((((");
        View view = inflater.inflate(R.layout.fragment_show_all_products, container, false);
        spinnerFilterText = new ArrayList<>();
        AlkoProductTempArray = new ArrayList<>();
        alkoContainer = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);
        allProductsLV = view.findViewById(R.id.showAllListView);
        filterChooserS = view.findViewById(R.id.showAllFilterSelection);
        // Set adapter for ListView
        ArrayAdapter<AlkoProduct> adapterListView = new ArrayAdapter<AlkoProduct>(inflater.getContext(), android.R.layout.simple_list_item_1, AlkoProductTempArray);
        allProductsLV.setAdapter(adapterListView);
        // Add all different types of drinks :)
        // Could also be done better but who cares :/
        spinnerFilterText.add("Punaviinit"); // index 0
        spinnerFilterText.add("Roseeviinit");
        spinnerFilterText.add("Valkoviinit");
        spinnerFilterText.add("Kuohuviinit ja Samppanjat");
        spinnerFilterText.add("Hanapakkaukset");
        spinnerFilterText.add("Oluet");
        spinnerFilterText.add("Siiderit"); // den siiderin
        spinnerFilterText.add("Juomasekoitukset");
        spinnerFilterText.add("Vodkat ja Viinat");
        spinnerFilterText.add("Ginit ja muut viinat");
        spinnerFilterText.add("Rommit");
        spinnerFilterText.add("Konjakit");
        spinnerFilterText.add("Brandyt, Armanjakit ja Calvadosit");
        spinnerFilterText.add("Viskit");
        spinnerFilterText.add("Liköörit ja Katkerot");
        spinnerFilterText.add("Alkoholittomat"); // Disgusting
        spinnerFilterText.add("Näytä kaikki"); // index 16
        // Set adapter for spinner
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,spinnerFilterText);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterChooserS.setAdapter(adapterSpinner);
        filterChooserS.setSelection(16);
        filterChooserS.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                int selection = (int) filterChooserS.getSelectedItemPosition();
                if (selection == 16) { // Shows all products
                    adapterListView.clear();
                    adapterListView.addAll(alkoContainer.getCrawler());
                    adapterListView.notifyDataSetChanged();
                } else { // find the wanted products
                    adapterListView.clear();
                    adapterListView.addAll(filterProducts(spinnerFilterText.get(selection)));
                    adapterListView.notifyDataSetChanged();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { } // Do nothing :)
        });
        return view;
    }
    // Find all products of the selected type
    public ArrayList<AlkoProduct> filterProducts(String filterString) {
        ArrayList<AlkoProduct> newList = new ArrayList<>();
        for (AlkoProduct alkoproduct : alkoContainer.getCrawler()) {
            if (alkoproduct.getType().toLowerCase().equals(filterString.toLowerCase())) {
                newList.add(alkoproduct); // Add AlkoProduct to tempListing
            }
        }
        return newList;
    }
}