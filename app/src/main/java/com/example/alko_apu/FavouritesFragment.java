package com.example.alko_apu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import java.io.IOException;

import FileHandling.FavouriteList;
import alkoData.AlkoProduct;
//Otto Manninen

public class FavouritesFragment extends Fragment {
    private ListView favouritesListView;
    private AlkoDataContainer alkoContainer;

    public FavouritesFragment() {
        super(R.layout.fragment_favourites);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alkoContainer = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);

        favouritesListView = (ListView) view.findViewById(R.id.favouritesListView);
        // Clicking list shows information about the product
        favouritesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                // Use clicked product as selected product
                AlkoProduct product = (AlkoProduct) adapterView.getItemAtPosition(i);
                alkoContainer.setSelectedProduct(product);
                // Show product info
                Navigation.findNavController(view).navigate(R.id.searchReadyFragment);
            }
        });
        // Long click to delete a product from favourites
        favouritesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView adapterView, View view, int i, long l) {
                // Find out, what product is being deleted and pass it to the dialog
                AlkoProduct product = (AlkoProduct) adapterView.getItemAtPosition(i);
                ConfirmDeleteDialog dialog = new ConfirmDeleteDialog(product, alkoContainer.getFavouriteList(),
                        (ArrayAdapter<AlkoProduct>)favouritesListView.getAdapter());
                dialog.show(getActivity().getSupportFragmentManager(), "fav_delete");

                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Update the list when the fragment is shown
        ArrayAdapter<AlkoProduct> adapter = new ArrayAdapter<AlkoProduct>(getContext(), android.R.layout.simple_list_item_1, alkoContainer.getFavouriteList());
        favouritesListView.setAdapter(adapter);
    }

    public static class ConfirmDeleteDialog extends DialogFragment {
        private AlkoProduct productToRemove;
        private FavouriteList favList;
        private ArrayAdapter<AlkoProduct> listAdapter;

        public ConfirmDeleteDialog(AlkoProduct product, FavouriteList list, ArrayAdapter adapter) {
            super();
            productToRemove = product;
            favList = list;
            listAdapter = adapter;
        }

        /*
        Following method is mostly copid from Android Dialog guide
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Poistetaanko suosikki?")
                    .setPositiveButton("Kyllä", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Remove selected item from favourites
                            favList.remove(productToRemove);
                            // Save changes to the file
                            try {
                                favList.saveDataToLocalFile();
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

    }
}