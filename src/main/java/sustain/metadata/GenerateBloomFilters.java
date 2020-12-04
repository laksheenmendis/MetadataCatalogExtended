package sustain.metadata;

import sustain.metadata.mongodb.Connector;
import sustain.metadata.schema.Hospital;
import sustain.metadata.utility.exceptions.ValueNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by laksheenmendis on 11/27/20 at 10:53 PM
 */
public class GenerateBloomFilters {

    static Set<Integer> geohashes = new HashSet<Integer>();
    static final int ARRAY_SIZE = 3001;
    static final int noOfCharsInGeoHash = 2;

    public static void main(String[] args) {

        try {
            Connector connector = new Connector();
            List<Hospital> hospitals = connector.readHospitalData();

            // bloomfilter with geohash
            boolean[] bf1 = new boolean[ARRAY_SIZE];

            // bloomfilter with geohash and noOfBeds
            boolean[] bf2 = new boolean[ARRAY_SIZE];

            // bloomfilter with geohash, noOfBeds and status
            boolean[] bf3 = new boolean[ARRAY_SIZE];

            // bloomfilter with geohash, noOfBeds, status and Owner
            boolean[] bf4 = new boolean[ARRAY_SIZE];

            // bloomfilter with geohash and owner
            boolean[] bf5 = new boolean[ARRAY_SIZE];

            // bloomfilter with geohash, owner and status
            boolean[] bf6 = new boolean[ARRAY_SIZE];

            // bloomfilter with geohash, owner and noOfBeds
            boolean[] bf7 = new boolean[ARRAY_SIZE];

            // bloomfilter with geohash and status
            boolean[] bf8 = new boolean[ARRAY_SIZE];

            for (Hospital hospital : hospitals) {
                try {
                    bf1[hash(hospital.getGeoHash(noOfCharsInGeoHash))] = true;
                    bf2[hash(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString())] = true;
                    bf3[hash(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "STATUS" + hospital.getStatus())] = true;
                    bf4[hash(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "OWNER" + hospital.getOwner() + "STATUS" + hospital.getStatus())] = true;
                    bf5[hash(hospital.getGeoHash(noOfCharsInGeoHash) + "OWNER" + hospital.getOwner())] = true;
                    bf6[hash(hospital.getGeoHash(noOfCharsInGeoHash) + "OWNER" + hospital.getOwner() + "STATUS" + hospital.getStatus())] = true;
                    bf7[hash(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "OWNER" + hospital.getOwner() )] = true;
                    bf8[hash(hospital.getGeoHash(noOfCharsInGeoHash) + "STATUS" + hospital.getStatus())] = true;

                } catch (NullPointerException e) {

                    e.printStackTrace();
                }
            }

            System.out.println("");
            countEntries(bf1);
            countEntries(bf2);
            countEntries(bf3);
            countEntries(bf4);
            countEntries(bf5);
            countEntries(bf6);
            countEntries(bf7);
            countEntries(bf8);

//            System.out.println(geohashes);

        } catch (ValueNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void countEntries(boolean[] bf)
    {
        int i=0;
        int count=0;

        while( i<ARRAY_SIZE )
        {
            if(bf[i])
                count++;
            i++;
        }

        System.out.println(count);
    }


    static int hash(String word)
    {
        double hash = 7;
        for (int i = 0; i < word.length(); i++) {
            hash = hash*31 + word.charAt(i);
        }

//        System.out.println("Geohash " + word);
        int val = (int)(hash % ARRAY_SIZE);
//        System.out.println("Val " + val);

        if(hash % ARRAY_SIZE < 0)
        {
            System.out.println(hash);
            return 0;
        }
        return val;

    }

    /**
     * Horner's rule
     * @param word
     * @return
     */
//    static int hash(String word)
//    {
//        double hash = 0;
//        for (int i = word.length()-1; i >= 0; i--) {
//            hash = hash*31 + word.charAt(i);
//        }
//
////        System.out.println("Geohash " + word);
//        int val = (int)(hash % ARRAY_SIZE);
////        System.out.println("Val " + val);
//
//        if(hash % ARRAY_SIZE < 0)
//        {
//            System.out.println(hash);
//            return 0;
//        }
//        return val;
//
//    }
}
