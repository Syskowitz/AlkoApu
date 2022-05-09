package alkoData;

public class AlkoStore {

    // Generic information
    private int id = -1;
    private String name = null;
    private double latitude = 0;
    private double longitude = 0;

    protected AlkoStore() {
    }

    /* Generic set and get methods.
     * These doesn't need explanation. */
    protected void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    protected void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    protected void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLatitude() {
        return latitude;
    }
    protected void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public String toString() {
        return id + ": " + name;
    }
}

