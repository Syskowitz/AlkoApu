package com.example.alko_apu;

import androidx.lifecycle.ViewModel;

import java.io.File;
import java.io.IOException;

import FileHandling.FavouriteList;
import FileHandling.RatingList;
import alkoData.AlkoCrawler;
import alkoData.AlkoProduct;

/*
This class holds all shared data.
Fragments are created when they are needed, and destroyed automatically.
There is no point to load all data every time a fragment is recreated.
Data is stored into this object, which is created only once, automatically at startup.
 */
public class AlkoDataContainer extends ViewModel {
    private AlkoCrawler crawler;
    private FavouriteList favList;
    private RatingList rateList;

    private boolean loaded = false;
    private AlkoProduct selectedProduct = null;

    public AlkoDataContainer() {
        crawler = new AlkoCrawler();
        favList = new FavouriteList(crawler);
        rateList = new RatingList(crawler);
    }

    public AlkoCrawler getCrawler() {
        return crawler;
    }
    public FavouriteList getFavouriteList() {
        return favList;
    }
    public RatingList getRatingList() {
        return rateList;
    }

    public boolean areProductsLoaded() {
        return loaded;
    }

    public boolean loadProducts(File localDir) {
        crawler.clear();

        // Returns true, if products were loaded succesfully
        try {
            crawler.loadProducts();
            favList.loadDataFromLocalFile(new File(localDir, "favourites.txt"));
            rateList.loadDataFromLocalFile(new File(localDir, "ratings.txt"));
        } catch (IOException exc) {
            // Do not store any products, if loading has failed
            crawler.clear();
            return false;
        }
        loaded = true;
        return true;
    }

    public void setSelectedProduct(AlkoProduct product) {
        selectedProduct = product;
    }

    public AlkoProduct getSelectedProduct() {
        return selectedProduct;
    }
}
