package alkoData;

import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

//import java.math.BigDecimal;

// Apache POI Library
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

/* An AlkoCrawler object handles all data downloading and parsing.
 * If Alko's website ever changes, you have to rewrite this class only.
 * AlkoCrawler behaves also as a storage element for all products. It extends an ArrayList, which holds all products.
 * This object holds also a list of Alko's stores. */
public class AlkoCrawler extends ArrayList<AlkoProduct> {
    /* URL:s to alko's website.
     * $id$ is replaced with product or store id. */
    private static final String all_products_excel_url = "https://www.alko.fi/INTERSHOP/static/WFS/Alko-OnlineShop-Site/-/Alko-OnlineShop/fi_FI/Alkon%20Hinnasto%20Tekstitiedostona/alkon-hinnasto-tekstitiedostona.xlsx";
    private static final String product_page_url = "https://www.alko.fi/tuotteet/$id$";
    private static final String availability_url = "https://www.alko.fi/INTERSHOP/web/WFS/Alko-OnlineShop-Site/fi_FI/-/EUR/ViewProduct-Include?SKU=$id$";
    private static final String store_url = "https://www.alko.fi/myymalat-palvelut/$id$";

    private HashMap<Integer, AlkoStore> alkoStoreMap;

    /* Public constructor has no parameters */
    public AlkoCrawler() {
        super();

        alkoStoreMap = new HashMap<Integer, AlkoStore>();
    }

    /* This method loads a product list from alko's website and parses it.
     * IOException is thrown, if there's any problems with connecting to the website. */
    public void loadProducts() throws IOException {
        InputStream in = null;
        try {
            URLConnection connection = new URL(all_products_excel_url).openConnection();
            in = connection.getInputStream();

            Workbook wb = WorkbookFactory.create(in);

            // The file has only one sheet, let's open it
            Sheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                // Every relevant data cell has a fixed position on a row.
                // Current row has valid data, if all cells have a String value.
                try {
                    AlkoProduct product = new AlkoProduct();

                    // index
                    Cell cell = row.getCell(0);
                    if (cell == null) continue;
                    if (cell.getCellTypeEnum() != CellType.STRING) continue;
                    product.setId(Integer.parseInt(cell.getStringCellValue()));

                    // name
                    cell = row.getCell(1);
                    if (cell == null) continue;
                    if (cell.getCellTypeEnum() != CellType.STRING) continue;
                    product.setName(cell.getStringCellValue());

                    // volume
                    cell = row.getCell(3);
                    if (cell == null) continue;
                    if (cell.getCellTypeEnum() != CellType.STRING) continue;
                    // Replace "," with "." and remove everything after whitespace
                    String corrected = cell.getStringCellValue().replace(",", ".").split(" ")[0];
                    product.setVolume(Float.parseFloat(corrected));

                    // price
                    cell = row.getCell(4);
                    if (cell == null) continue;
                    if (cell.getCellTypeEnum() != CellType.STRING) continue;
                    product.setPrice(Float.parseFloat(cell.getStringCellValue()));

                    // type
                    cell = row.getCell(8);
                    if (cell == null) continue;
                    if (cell.getCellTypeEnum() != CellType.STRING) continue;
                    // To save memory, use intern()
                    product.setType(cell.getStringCellValue().intern());

                    // alc-percents
                    cell = row.getCell(21);
                    if (cell == null) continue;
                    if (cell.getCellTypeEnum() != CellType.STRING) continue;
                    product.setPercents(Float.parseFloat(cell.getStringCellValue()));

                    add(product);

                } catch (NumberFormatException exc) {
                    // Exceptions with formatting are ignored

                    // for debugging:
                    //exc.printStackTrace();
                }

            }
        // Wrap exceptions into an IOException, so user doesn't need to know anything about the POI library
        } catch(InvalidFormatException exc) {
            throw new IOException("Invalid file format", exc);
        } catch(EncryptedDocumentException exc) {
            throw new IOException("Encrypted file", exc);
        } finally {
            // Finally, close the stream
            if(in != null) in.close();
        }
    }

    /* Returns the store, corresponding to the id */
    public AlkoStore getStoreById(int id) throws IOException {
        final String storeNameStart = "<span class=\"store-name\" itemprop=\"name\">";
        final String storeNameEnd = "</";
        //final String storeStreetAddressStart = "<span itemprop=\"streetAddress\">";
        //final String storeStreetAddressEnd = "</";
        //final String storePostalCodeStart = "<span itemprop=\"postalCode\">";
        //final String storePostalCodeEnd = "</";
        final String latitudeStart = "<meta itemprop=\"latitude\" content=\"";
        final String latitudeEnd = "\"/>";
        final String longitudeStart = "<meta itemprop=\"longitude\" content=\"";
        final String longitudeEnd = "\"/>";

        if(alkoStoreMap.containsKey(Integer.valueOf(id))) {
            return alkoStoreMap.get(Integer.valueOf(id));
        }
        else {
            AlkoStore store = new AlkoStore();
            store.setId(id);

            BufferedReader in = null;
            try {
                URLConnection connection = new URL(store_url.replace("$id$", String.valueOf(id))).openConnection();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                // Read the site line by line
                String line = in.readLine();
                while(line != null) {
                    // Look for name
                    int startPos = line.indexOf(storeNameStart);
                    if(startPos != -1) {
                        // Extract name from code
                        String cut1 = line.substring(startPos + storeNameStart.length());
                        String cut2 = cut1.substring(0, cut1.indexOf(storeNameEnd));
                        String replaced = cut2.replace("&auml;", "ä").replace("&ouml;", "ö");
                        store.setName(replaced);
                    }

                    // Find coordinates from html document
                    startPos = line.indexOf(latitudeStart);
                    if(startPos != -1) {
                        String cut1 = line.substring(startPos + storeNameStart.length());
                        String cut2 = cut1.substring(0, cut1.indexOf(latitudeEnd));
                        // Do not crash, if latitude is incorrect
                        try {
                            store.setLatitude(Float.parseFloat(cut2));
                        } catch(NumberFormatException exc) {
                        }
                    }
                    startPos = line.indexOf(longitudeStart);
                    if(startPos != -1) {
                        String cut1 = line.substring(startPos + storeNameStart.length());
                        String cut2 = cut1.substring(0, cut1.indexOf(longitudeEnd));
                        try {
                            store.setLongitude(Float.parseFloat(cut2));
                        } catch(NumberFormatException exc) {
                        }
                    }

                    line = in.readLine();
                }

            } finally {
                if(in != null) in.close();
            }

            alkoStoreMap.put(Integer.valueOf(id), store);
            return store;
        }
    }

    // This method returns a URL, pointing to the product's page at alko's website.
    public static String getProductPageURL(AlkoProduct product) {
        // Replace $id$ with actual product id
        return product_page_url.replace("$id$", String.valueOf(product.getId()));
    }

    // Get a URL to a store's website
    public static String getStorePageURL(AlkoStore store) {
        return store_url.replace("$id$", String.valueOf(store.getId()));
    }

    public AlkoProduct getProductById(int id) {
        for(AlkoProduct ap : this) {
            if(ap.getId() == id) return ap;
        }
        return null;
    }

    public ArrayList<AlkoStore> whereIsAvailable(AlkoProduct product) throws IOException {
        // character sequences before and after a store ID
        final String storeIdStart = "&StoreID=";
        final String storeIdEnd = "&";

        ArrayList<AlkoStore> list = new ArrayList<AlkoStore>();

        BufferedReader in = null;
        try {
            URLConnection connection = new URL(availability_url.replace("$id$", String.valueOf(product.getId()))).openConnection();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // Read the site line by line
            String line = in.readLine();
            while(line != null) {
                // Find store ID:s from source code
                int startPos = line.indexOf(storeIdStart);
                if(startPos != -1) {
                    // Extract ID string from code
                    String cut1 = line.substring(startPos + storeIdStart.length());
                    String cut2 = cut1.substring(0, cut1.indexOf(storeIdEnd));

                    int id = Integer.parseInt(cut2);
                    list.add(getStoreById(id));
                }

                line = in.readLine();
            }
        } finally {
            if(in != null) in.close();
        }

        return list;
    }
}
