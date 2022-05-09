package com.example.alko_apu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.ArrayList;

import FileHandling.FavouriteList;
import FileHandling.Rating;
import FileHandling.RatingList;
import alkoData.AlkoProduct;
//Otto Manninen

public class RatingsFragment extends Fragment {


    private ListView ratingsListing;
    private AlkoDataContainer alkoContainer;
    private Spinner ratingsChooser;
    private ArrayList<String> spinnerContent;
    private ArrayList<Rating> ratingsToShow;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings, container, false);
        ratingsToShow = new ArrayList<>();
        try {
            ratingsToShow = alkoContainer.getRatingList();
        } catch (NullPointerException e) { // Proceed to show empty list if no ratings are found :)
        }

        spinnerContent = new ArrayList<>();
        alkoContainer = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);
        ratingsListing = (ListView) view.findViewById(R.id.ratingsList);
        ratingsChooser = (Spinner) view.findViewById(R.id.ratingsFilter);
        ArrayAdapter<Rating> adapter = new ArrayAdapter<Rating>(inflater.getContext(), android.R.layout.simple_list_item_1, ratingsToShow);
        ratingsListing.setAdapter(adapter);
        for (int i = 0; i<6; i++) {
            spinnerContent.add(Integer.toString(i) + " tähden arvostelut");
        }
        spinnerContent.add("Kaikki arvostelut");
        ArrayAdapter<String> adapterSpinner = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,spinnerContent);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingsChooser.setAdapter(adapterSpinner);
        ratingsChooser.setSelection(6); // Set default to Listing all Ratings
        // Filter ratings based on spinner selection
        ratingsChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Not sure if switch state is really even necessary here -_- but anyway
                switch ((int) ratingsChooser.getSelectedItemPosition()) {
                    case 0:
                        adapter.clear();
                        // Filter ratingList and display the matching ratings
                        adapter.addAll(filterRatings(0));
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        adapter.clear();
                        adapter.addAll(filterRatings(1));
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        adapter.clear();
                        adapter.addAll(filterRatings(2));
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        adapter.clear();
                        adapter.addAll(filterRatings(3));
                        adapter.notifyDataSetChanged();
                        break;
                    case 4:
                        adapter.clear();
                        adapter.addAll(filterRatings(4));
                        adapter.notifyDataSetChanged();
                        break;
                    case 5:
                        adapter.clear();
                        adapter.addAll(filterRatings(5));
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        adapter.clear();
                        // Displays all ratings
                        adapter.addAll(alkoContainer.getRatingList());
                        adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { } // Do nothing :)
        });

        return view;
    }
    // Simple method to find ratings that match the wanted stars (given rate)
    public ArrayList<Rating> filterRatings(int filterID) {
        ArrayList<Rating> newList = new ArrayList<>();
        for (Rating rating : alkoContainer.getRatingList()) {
            if (rating.getRating() == filterID) {
                newList.add(rating); // Add rating to tempListing
            }
        }
        return newList;
    }





    // Lissää loputkin
    /*
    public static class ConfirmDeleteDialog extends DialogFragment {
        private AlkoProduct productToRemove;
        private RatingList ratingList;
        private ArrayAdapter<AlkoProduct> listAdapter;

        public ConfirmDeleteDialog(AlkoProduct product, ratingList list, ArrayAdapter adapter) {
            super();
            productToRemove = product;
            ratingList = list;
            listAdapter = adapter;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Poistetaanko arvostelu?")
                    .setPositiveButton("Kyllä", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Remove selected item from favourites
                            ratingList.remove(productToRemove);
                            // Save changes to the file
                            try {
                                ratingList.saveDataToLocalFile();
                            } catch(IOException exc) {
                                exc.printStackTrace();
                            }
                            // Remove item also from the list view
                            listAdapter.remove(productToRemove);
                        }
                    })
                    .setNegativeButton("Ei", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Do nothing, if user selected 'No'
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

    }*/
}