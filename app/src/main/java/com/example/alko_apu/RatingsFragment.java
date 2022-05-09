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
import androidx.navigation.Navigation;

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
    ArrayAdapter<Rating> adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings, container, false);
        ratingsToShow = new ArrayList<Rating>();
        /* try {
            ratingsToShow = alkoContainer.getRatingList();
        } catch (NullPointerException e) { // Proceed to show empty list if no ratings are found :)
        }*/

        spinnerContent = new ArrayList<>();
        alkoContainer = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);
        ratingsListing = (ListView) view.findViewById(R.id.ratingsList);
        ratingsChooser = (Spinner) view.findViewById(R.id.ratingsFilter);

        ArrayAdapter<Rating> adapter = new ArrayAdapter<Rating>(inflater.getContext(), android.R.layout.simple_list_item_1, ratingsToShow);
        ratingsListing.setAdapter(adapter);

        // Clicking list shows information about the product
        ratingsListing.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                // Use clicked product as selected product
                Rating rating = (Rating) adapterView.getItemAtPosition(i);
                alkoContainer.setSelectedProduct(rating.getProduct());
                // Show product info
                Navigation.findNavController(view).navigate(R.id.searchReadyFragment);
            }
        });
        // Long click to delete a product from favourites
        ratingsListing.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {
                // Find out, what product is being deleted and pass it to the dialog
                Rating rating = (Rating) adapterView.getItemAtPosition(i);
                RatingsFragment.ConfirmDeleteDialogRating dialog = new RatingsFragment.ConfirmDeleteDialogRating(rating, alkoContainer.getRatingList(),
                        (ArrayAdapter<Rating>)ratingsListing.getAdapter());
                dialog.show(getActivity().getSupportFragmentManager(), "rating_delete");
                return true;
            }
        });

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
                adapter.clear();
                int selection = (int) ratingsChooser.getSelectedItemPosition();
                System.out.println(selection);
                if (selection==6) {
                    adapter.addAll(alkoContainer.getRatingList());
                } else {
                    System.out.println("No PRKL!");
                    adapter.addAll(filterRatings(selection));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { } // Do nothing :)
        });

        return view;
    }

    /*@Override
    public void onStart() {
        super.onStart();
        // Update the list when the fragment is shown
        adapter = new ArrayAdapter<Rating>(getContext(), android.R.layout.simple_list_item_1, alkoContainer.getRatingList());
        ratingsListing.setAdapter(adapter);
    }*/

    // Simple method to find ratings that match the wanted stars (given rate)
    public ArrayList<Rating> filterRatings(int filterID) {
        ArrayList<Rating> newList = new ArrayList<>();
        System.out.println("rating " + filterID);
        for (Rating rating : alkoContainer.getRatingList()) {
            System.out.println(rating);
            if (rating.getRating() == filterID) {
                newList.add(rating); // Add rating to tempListing
            }
        }
        return newList;
    }
    // Lissää loputkin

    public static class ConfirmDeleteDialogRating extends DialogFragment {
        private Rating ratingToRemove;
        private RatingList ratingList;
        private ArrayAdapter<Rating> listAdapter;

        public ConfirmDeleteDialogRating(Rating rating, RatingList list, ArrayAdapter adapter) {
            super();
            ratingToRemove = rating;
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
                            ratingList.remove(ratingToRemove);
                            // Save changes to the file
                            try {
                                ratingList.saveDataToLocalFile();
                            } catch(IOException exc) {
                                exc.printStackTrace();
                            }
                            // Remove item also from the list view
                            listAdapter.remove(ratingToRemove);
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

    }
}