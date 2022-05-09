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
import alkoData.AlkoProduct;

public class FavouriteList extends ArrayList<AlkoProduct>  {
    private final AlkoCrawler crawler;
    private File localFile = null;

    public FavouriteList(AlkoCrawler ac) {
        super();
        crawler = ac;
    }

    /*
    This method loads data from local cahce.
     */
    public void loadDataFromLocalFile(File file) throws IOException {
        localFile = file;
        // Do nothing, if the file does not exists.
        // File will be created when list is saved.
        if(file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

                String line = reader.readLine();
                while(line != null) {
                    try {
                        int id = Integer.parseInt(line.trim());
                        AlkoProduct product = crawler.getProductById(id);
                        // If there is no product at the specified id, the product has been removed from
                        // Alko's catalog. There is no point to add a nonexisting product to favourites.
                        if (product != null) add(product);
                    } catch(NumberFormatException exc) {
                        exc.printStackTrace();
                    }
                    line = reader.readLine();
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

            for(AlkoProduct product : this) {
                int id = product.getId();
                writer.println(id);
            }
        } finally {
            if(writer != null) writer.close();
        }
    }
}
