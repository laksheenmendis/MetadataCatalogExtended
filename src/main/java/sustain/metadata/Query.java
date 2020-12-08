package sustain.metadata;

import sun.util.resources.ms.CalendarData_ms_MY;
import sustain.metadata.utility.Geohash;

import java.util.List;
import java.util.concurrent.Callable;

public class Query implements Callable<Boolean> {

    boolean reqBeds;
    int beds;
    boolean reqOwner;
    String owner;
    boolean reqStatus;
    String status;

    // it is assumed that latitude and longitude is a must
    double latitude;
    double longitude;
    String geoHash;

    public Query(boolean reqBeds, int beds, boolean reqOwner, String owner, boolean reqStatus, String status, double latitude, double longitude) {
        this.reqBeds = reqBeds;
        this.beds = beds;
        this.reqOwner = reqOwner;
        this.owner = owner;
        this.reqStatus = reqStatus;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public boolean isReqBeds() {
        return reqBeds;
    }

    public void setReqBeds(boolean reqBeds) {
        this.reqBeds = reqBeds;
    }

    public int getBeds() {
        return beds;
    }

    public void setBeds(int beds) {
        this.beds = beds;
    }

    public boolean isReqOwner() {
        return reqOwner;
    }

    public void setReqOwner(boolean reqOwner) {
        this.reqOwner = reqOwner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(boolean reqStatus) {
        this.reqStatus = reqStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
        if(geoHash == null)
            geoHash = Geohash.encode(this.getLatitude(), this.getLongitude(), MultipleDifferentHashes.noOfCharsInGeoHash);
        return geoHash;
    }

    /**
     * fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash), bf1);
    *   fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString(), bf2);
    *   fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "STATUS" + hospital.getStatus(), bf3);
    *   fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "OWNER" + hospital.getOwner() + "STATUS" + hospital.getStatus(), bf4);
    *   fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "OWNER" + hospital.getOwner(), bf5);
    *   fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "OWNER" + hospital.getOwner() + "STATUS" + hospital.getStatus(), bf6);
    *   fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "OWNER" + hospital.getOwner() , bf7);
    *   fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "STATUS" + hospital.getStatus(), bf8);
    */
    public Boolean call() throws Exception {

        boolean exists = false;
        if(reqBeds)
        {
            if(reqOwner)
            {
                if(reqStatus)
                {
                    //b4
                    String str = this.getGeoHash() + "BEDS" + this.getBeds() + "OWNER" + this.getOwner() + "STATUS" + this.getStatus();
                    exists = checkBloomFilters(MultipleDifferentHashes.bf4, str);
                }
                else
                {
                    // b7
                    String str = this.getGeoHash() + "BEDS" + this.getBeds() + "OWNER" + this.getOwner();
                    exists = checkBloomFilters(MultipleDifferentHashes.bf7, str);
                }
            }
            else if(reqStatus)
            {
                // b3
                String str = this.getGeoHash() + "BEDS" + this.getBeds() + "STATUS" + this.getStatus();
                exists = checkBloomFilters(MultipleDifferentHashes.bf3, str);
            }
            else
            {
                // bf2
                String str = this.getGeoHash() + "BEDS" + this.getBeds();
                exists = checkBloomFilters(MultipleDifferentHashes.bf2, str);
            }
        }
        else if(reqOwner)
        {
            if(reqStatus)
            {
                // b6
                String str = this.getGeoHash() + "OWNER" + this.getOwner() + "STATUS" + this.getStatus();
                exists = checkBloomFilters(MultipleDifferentHashes.bf6, str);
            }
            else
            {
                 //b5
                String str = this.getGeoHash() + "OWNER" + this.getOwner();
                exists = checkBloomFilters(MultipleDifferentHashes.bf5, str);
            }
        }
        else if(reqStatus)
        {
            // b8
            String str = this.getGeoHash() + "STATUS" + this.getStatus();
            exists = checkBloomFilters(MultipleDifferentHashes.bf8, str);

        }
        else
        {
            // bf1
            String str = this.getGeoHash();
            exists = checkBloomFilters(MultipleDifferentHashes.bf1, str);
        }


        return exists;
    }


    private boolean checkBloomFilters(List<boolean[]> bfs, String checkString)
    {
        int[] hashVals = MultipleDifferentHashes.hash(checkString);

        for(int i=0; i<MultipleDifferentHashes.noOfHashFunctions; i++)
        {
            boolean[] bf = bfs.get(i);
            boolean currCheck = bf[hashVals[i]];

            if(!currCheck)
            {
//                System.out.println(checkString + " returns false at i = "+ i);
                return false;
            }
        }
        return true;
    }
}
