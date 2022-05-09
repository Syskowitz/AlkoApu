package com.example.alko_apu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class LoaderFragment extends Fragment {
    private AlkoDataContainer container;
    private TextView text;
    private LoaderThread loader = null;

    public LoaderFragment() {
        super(R.layout.fragment_loader);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        text = (TextView) view.findViewById(R.id.textField);

        // Get the class containing all neccessary data.
        container = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);
    }

    public void onStart () {
        super.onStart();
        // Start loading, if products are not already loaded
        if(!container.areProductsLoaded()) {
            text.setText("Ladataan. Odota hetki rauhassa!");
            loader = new LoaderThread();
            loader.start();
        }
        else text.setText("Tuotteet on jo ladattu");
    }

    public void onStop() {
        super.onStop();
        // Interrupt loading, if fragment is closed before loading is completed.
        if(loader != null) loader.interrupt();
    }

    // When loading is ready, go back to the main fragment
    private void loadingReady() {
        Navigation.findNavController(getView()).popBackStack();
    }

    // Loader thread does what it's name says
    private class LoaderThread extends Thread {
        public void run() {
            boolean ok = container.loadProducts(getContext().getFilesDir());
            Activity a = getActivity();
            if(a != null) a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(ok) loadingReady();
                    else text.setText("Virhe. Yritä myöhemmin uudelleen!");
                }
            });
        }
    }
}
