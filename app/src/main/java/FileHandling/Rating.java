package FileHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import alkoData.AlkoCrawler;
import alkoData.AlkoProduct;

public class Rating {
    private AlkoProduct product;
    int rating;
    String description;

    public Rating() {
    }

    // Default setters and getters
    public AlkoProduct getProduct() { return product; }
    public void setProduct(AlkoProduct product) { this.product = product; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Place holder toString() method
    @Override
    public String toString() {
        String[][] productTempInfo = product.getUserReadableInfo();
        return productTempInfo[0][1]+ ", " +productTempInfo[2][1]+ ", " +productTempInfo[3][1]+"\n"+
                rating + " Tähteä\n" + description;

    }

    /*
    This method creates a new Rating object.
    It reads neccessary lines from a BufferedReader and generates a new Rating object.
    If BufferedReader has no more lines, null value is returned.
     */
    public static Rating readFromBufferedReader(BufferedReader reader, AlkoCrawler crawler) throws IOException {
        Rating newRating = new Rating();

        while(true) {
            String line = reader.readLine();
            if(line == null) return null; // End of file, there is no more ratings in the file
            line = line.trim();
            if(line.equals("end")) break; // End of data reached

            // Find delimiter from file
            int delimiterLocation = line.indexOf(';');
            if(delimiterLocation == -1) continue; // Delimiter not found, invalid line

            // Split line to type and data
            String valType = line.substring(0, delimiterLocation);
            String value = line.substring(delimiterLocation + 1);
            try {

                // The most important data, the product id
                if(valType.equals("id")) {
                    int id = Integer.parseInt(value);
                    newRating.setProduct(crawler.getProductById(id));
                }
                // Product rating
                else if(valType.equals("rating")) {
                    int rating = Integer.parseInt(value);
                    newRating.setRating(rating);
                }
                // Description
                else if(valType.equals("description")) {
                    // Line breaks are separated with semicolons
                    newRating.setDescription(value.replaceAll(";", "\n"));
                }
            } catch(NumberFormatException exc) {
                // Incorrect lines may cause a NumberFormatException.
                // Simply ignore these lines and continue to a next line
                exc.printStackTrace();
            }

        }

        // Return the newly created rating
        return newRating;
    }

    /*
    This method writes all data to a PrintWriter.
    Data can be read later with readFromBufferedReader method
     */
    public void writeToPrintWriter(PrintWriter writer) throws IOException {
        // Write id
        writer.print("id;");
        writer.println(product.getId());
        // Write rating
        writer.print("rating;");
        writer.println(rating);
        // Write description
        // Replace line breaks with semicolons before writing
        writer.print("description;");
        writer.println(description.replaceAll("\n", ";"));
        // Write end marker
        writer.println("end");
    }
}
