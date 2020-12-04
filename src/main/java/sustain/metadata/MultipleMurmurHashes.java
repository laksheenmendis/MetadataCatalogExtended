package sustain.metadata;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import sustain.metadata.mongodb.Connector;
import sustain.metadata.schema.Hospital;
import sustain.metadata.utility.exceptions.ValueNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by laksheenmendis on 12/3/20 at 4:27 PM
 */
public class MultipleMurmurHashes {

    static final int ARRAY_SIZE = 5009;
    static final int noOfHashFunctions = 5;
    static final int noOfCharsInGeoHash = 5;

    public static void main(String[] args) {
        try {
            Connector connector = new Connector();
            List<Hospital> hospitals = connector.readHospitalData();

            // bloomfilters with geohash
            List<boolean[]> bf1 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash and noOfBeds
            List<boolean[]> bf2 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash, noOfBeds and status
            List<boolean[]> bf3 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash, noOfBeds, status and Owner
            List<boolean[]> bf4 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash and owner
            List<boolean[]> bf5 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash, owner and status
            List<boolean[]> bf6 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash, owner and noOfBeds
            List<boolean[]> bf7 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash and status
            List<boolean[]> bf8 = generateEmptyBloomFilters(noOfHashFunctions);

            for (Hospital hospital : hospitals) {
                try {

                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash), noOfHashFunctions, bf1);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString(), noOfHashFunctions, bf2);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "STATUS" + hospital.getStatus(), noOfHashFunctions, bf3);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "OWNER" + hospital.getOwner() + "STATUS" + hospital.getStatus(), noOfHashFunctions, bf4);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "OWNER" + hospital.getOwner(), noOfHashFunctions, bf5);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "OWNER" + hospital.getOwner() + "STATUS" + hospital.getStatus(), noOfHashFunctions, bf6);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "OWNER" + hospital.getOwner() , noOfHashFunctions, bf7);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "STATUS" + hospital.getStatus(), noOfHashFunctions, bf8);

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

    private static List<boolean[]> generateEmptyBloomFilters(int noOfBf)
    {
        List<boolean[]> listOfBfs = new ArrayList<boolean[]>();

        for (int i = 0; i < noOfBf; i++) {
            boolean [] b = new boolean[ARRAY_SIZE];
            listOfBfs.add(b);
        }

        return listOfBfs;
    }

    private static void countEntries(List<boolean[]> bloomfilters)
    {
        int sum =0;
        for (int i1 = 0; i1 < bloomfilters.size(); i1++) {
            boolean[] b = bloomfilters.get(i1);
            int i=0;
            int count = 0;
            while( i<ARRAY_SIZE )
            {
                if(b[i])
                    count++;
                i++;
            }

            sum += count;
//            System.out.println(i1 + " " + count);
        }

        // print out the % usage out of all x number of arrays (x is equal to the noOfHashFunctions)
        System.out.println( String.format("%.2f",(double) (sum*100)/(ARRAY_SIZE*noOfHashFunctions)) + "%");
    }

    private static void fillBloomFilters(String hashingString, int noOfArrays, List<boolean[]> bloomFilters)
    {
        // contains x number of hash indexes (x = noOfArrays or x = noOfHashCodesNeeded)
        int[] hash = hash(hashingString, noOfArrays);

        for(int i=0; i<noOfArrays; i++)
        {
            boolean[] bf = bloomFilters.get(i);
            bf[hash[i]] = true;
        }
    }

    private static int[] hash(String stringToHash, int noOfHashCodesNeeded)
    {
        int[] arr = new int[noOfHashCodesNeeded];

        HashFunction hashFunction = Hashing.murmur3_32();
        for(int i=0; i<noOfHashCodesNeeded; i++)
        {
            if(i==0)
            {
                hashFunction = Hashing.murmur3_32();
            }
            else if(i==1)
            {
                hashFunction = Hashing.murmur3_32(7);
            }
            else if(i==2)
            {
                hashFunction = Hashing.murmur3_128();
            }
            else if(i==3)
            {
                hashFunction = Hashing.murmur3_128(7);
            }
            else if(i==4)
            {
                hashFunction = Hashing.murmur3_128(11);
            }

            HashCode hashCode = hashFunction.hashBytes(stringToHash.getBytes());

            arr[i] = Math.abs(hashCode.asInt()) % ARRAY_SIZE;

        }

        return arr;
    }
}
