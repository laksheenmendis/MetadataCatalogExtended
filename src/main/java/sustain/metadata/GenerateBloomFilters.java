package sustain.metadata;

import sustain.metadata.mongodb.Connector;
import sustain.metadata.schema.Hospital;
import sustain.metadata.utility.exceptions.ValueNotFoundException;

import java.util.List;

/**
 * Created by laksheenmendis on 11/27/20 at 10:53 PM
 */
public class GenerateBloomFilters {

    public static void main(String[] args) {

        try {
            Connector connector = new Connector();
            List<Hospital> hospitals = connector.readHospitalData();

            // bloomfilter with geohash
            boolean[] bf1 = new boolean[1000];

            // bloomfilter with geohash and noOfBeds
            boolean[] bf2 = new boolean[1000];

            // bloomfilter with geohash, noOfBeds and status
            boolean[] bf3 = new boolean[1000];

            // bloomfilter with geohash, noOfBeds, status and Owner
            boolean[] bf4 = new boolean[1000];

            // bloomfilter with geohash and owner
            boolean[] bf5 = new boolean[1000];

            // bloomfilter with geohash, owner and status
            boolean[] bf6 = new boolean[1000];

            // bloomfilter with geohash, owner and noOfBeds
            boolean[] bf7 = new boolean[1000];

            // bloomfilter with geohash and status
            boolean[] bf8 = new boolean[1000];

            for (Hospital hospital : hospitals) {
                bf1[hash(hospital.getGeoHash())] = true;
                bf2[hash(hospital.getGeoHash()+"BEDS" +hospital.getBedsString())] = true;
                bf3[hash(hospital.getGeoHash()+"BEDS" +hospital.getBedsString()+ "STATUS" + hospital.getStatus())] = true;
                bf4[hash(hospital.getGeoHash()+"BEDS" +hospital.getBedsString() +"OWNER" + hospital.getOwner()+ "STATUS" + hospital.getStatus())] = true;
                bf5[hash(hospital.getGeoHash()+"OWNER" + hospital.getOwner())] = true;
                bf6[hash(hospital.getGeoHash()+"OWNER" + hospital.getOwner()+ "STATUS" + hospital.getStatus())] = true;
                bf7[hash(hospital.getGeoHash()+ "OWNER" + hospital.getOwner() + "BEDS" +hospital.getBedsString())] = true;
                bf8[hash(hospital.getGeoHash() + "STATUS" + hospital.getStatus())] = true;
            }


        } catch (ValueNotFoundException e) {
            e.printStackTrace();
        }
    }

    static int hash(String word)
    {
        int hash = 7;
        for (int i = 0; i < word.length(); i++) {
            hash = hash*31 + word.charAt(i);
        }

        return hash % 1000;

    }
}
