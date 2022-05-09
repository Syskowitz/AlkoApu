package com.example.alko_apu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import FileHandling.Rating;

public class RateFragment extends Fragment {
    private RatingBar mRatingBar; // Rating stars
    private TextView mRatingScale; // Description for ratings
    private EditText mFeedback; // Open feedback on the product (5 lines)
    private Button mSendFeedback; // Sends feedback to saved in XML-file
    private AlkoDataContainer container; // Provides access to alkoData

    public RateFragment() {
        super(R.layout.fragment_rate);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        mRatingScale = (TextView) view.findViewById(R.id.ratingScaleTxt);
        mFeedback = (EditText) view.findViewById(R.id.giveFeedbackET);
        mSendFeedback = (Button) view.findViewById(R.id.submitFeedbackBtn);
        // Get access to products and the currently selected product
        container = new ViewModelProvider(requireActivity()).get(AlkoDataContainer.class);

        //Otto Manninen
        //Define comments on the star ratings
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Kuravelliä");
                        break;
                    case 2:
                        mRatingScale.setText("Ei kehuja");
                        break;
                    case 3:
                        mRatingScale.setText("Meni alas");
                        break;
                    case 4:
                        mRatingScale.setText("Tämä maistui!");
                        break;
                    case 5:
                        mRatingScale.setText("PARASTA IKINÄ!");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });


        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if the product already has a rating
                for (Rating rating : container.getRatingList()) {
                    if (rating.getProduct() == container.getSelectedProduct()) {
                        container.getRatingList().remove(rating); // Delete the rating and go on the place a new one
                        break;
                    }
                }
                // Create rating based the user feedback
                Rating productRating = new Rating();
                productRating.setProduct(container.getSelectedProduct());
                productRating.setRating((int) mRatingBar.getRating());
                productRating.setDescription(mFeedback.getText().toString());
                // Add the created rating to list
                container.getRatingList().add(productRating);
                // For debugging print the ratingsList :)
                for (int i = 0; container.getRatingList().size() > i; i++) {
                    System.out.println( container.getRatingList().get(i));
                }

                // Save the list to the local file system
                try {
                    container.getRatingList().saveDataToLocalFile();
                } catch(IOException exc) {
                    exc.printStackTrace();
                }
                // Show a message
                Toast toast = Toast.makeText(getContext(),"Arvostelu tallennettu!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }
}