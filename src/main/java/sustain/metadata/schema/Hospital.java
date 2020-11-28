package sustain.metadata.schema;

/**
 * Created by laksheenmendis on 11/27/20 at 7:21 PM
 */

/**
 * 7596 entries in the collection
 */
public class Hospital {

    double latitude;
    double longitude;
    String geoHash;
    int beds;
    String status;
    String owner;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getGeoHash() {
        return geoHash;
    }

    public void setGeoHash(String geoHash) {
        this.geoHash = geoHash;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Create the beds string to be length of 4
     * @return
     */
    public String getBedsString()
    {
        String sBeds = String.valueOf(this.beds);

        if(sBeds.length() < 4)
        {
            while(sBeds.length() != 4)
            {
                sBeds = "0" + sBeds;
            }
        }
        return sBeds;
    }
}
