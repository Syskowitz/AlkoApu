package alkoData;

public class AlkoProduct {

    // Generic information about the product
    private int id = -1;
    private String name = null;
    private String type = null;
    private float price = 0;
    private float percents = 0;
    private float volume = 0;

    protected AlkoProduct() {
    }

    /* Generic set and get methods.
     * These doesn't need explanation. */
    protected void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    protected void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    protected void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    protected void setPrice(float price) {
        this.price = price;
    }
    public float getPrice() {
        return price;
    }
    protected void setPercents(float percents) {
        this.percents = percents;
    }
    public float getPercents() {
        return percents;
    }
    protected void setVolume(float volume) {
        this.volume = volume;
    }
    public float getVolume() {
        return volume;
    }

    /* getUserReadableInfo() returns a String array.
     * Each row contains a title-value -pair.
     * First cell is the title, second cell is the value. */
    public String[][] getUserReadableInfo() {
        String[][] infoString = {
                {"Nimi", name},
                {"ID", String.valueOf(id)},
                {"Tyyppi", type},
                {"Hinta", price + " €"},
                {"Vahvuus", percents + " %"},
                {"Tilavuus", volume + " l"}
        };
        return infoString;
    }

    /* toString() creates a single String from getUserReadableInfo()'s output */
    public String toString() {
        String returnStr = "";
        String[][] infoArray = getUserReadableInfo();

        for(int i = 0; i < infoArray.length; i++) {
            returnStr += infoArray[i][0] + ": " + infoArray[i][1] + "\n";
        }

        return returnStr;
    }
}

