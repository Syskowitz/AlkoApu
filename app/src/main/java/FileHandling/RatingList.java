package FileHandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import alkoData.AlkoCrawler;

public class RatingList extends ArrayList<Rating> {
    private final AlkoCrawler crawler;
    private File localFile;

    public RatingList(AlkoCrawler ac) {
        super();
        crawler = ac;
    }

    /*
    This method loads data from local cache.
     */
    public void loadDataFromLocalFile(File file) throws IOException {
        localFile = file;
        // Do nothing, if the file does not exists.
        // File will be created when list is saved.
        if(file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

                while(true) {
                    // Read ratings from file one by one
                    Rating rating = Rating.readFromBufferedReader(reader, crawler);
                    // If readFromBufferedReader returns null, there is no more ratings in the file
                    if(rating == null) break;

                    // There may be ratings without product.
                    // They are created when a product belongs no longer to Alko's catalog.
                    // Do'nt add these to the list
                    if(rating.getProduct() != null) add(rating);
                }
            } finally {
                if (reader != null) reader.close();
            }
        }
    }

    /*
    This method saves data to local file
     */
    public void saveDataToLocalFile() throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileOutputStream(localFile));

            // Write each rating to the file
            for(Rating rating : this) {
                rating.writeToPrintWriter(writer);
            }
        } finally {
            if(writer != null) writer.close();
        }
    }
}
