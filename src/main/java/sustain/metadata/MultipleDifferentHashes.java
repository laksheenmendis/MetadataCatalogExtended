package sustain.metadata;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import sustain.metadata.mongodb.Connector;
import sustain.metadata.schema.Hospital;
import sustain.metadata.utility.GenerateQueries;
import sustain.metadata.utility.exceptions.ValueNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by laksheenmendis on 12/3/20 at 6:29 PM
 */
public class MultipleDifferentHashes {

    static final int ARRAY_SIZE = 3001;
    static final int noOfHashFunctions = 5;
    static final int noOfCharsInGeoHash = 5;
    static final int noOfThreads = 100;

    static List<boolean[]> bf1; // only geohash
    static List<boolean[]> bf2; // geohash and beds
    static List<boolean[]> bf3; // geohash, beds, status
    static List<boolean[]> bf4; // geohash, beds, owner, status
    static List<boolean[]> bf5; // geohash, owner
    static List<boolean[]> bf6;// geohash, owner and status
    static List<boolean[]> bf7;// geohash, beds, owner
    static List<boolean[]> bf8;// geohash, status


    public static void main(String[] args) {
        try {
            Connector connector = new Connector();
            List<Hospital> hospitals = connector.readHospitalData();

            // bloomfilters with geohash
            bf1 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash and noOfBeds
            bf2 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash, noOfBeds and status
            bf3 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash, noOfBeds, status and Owner
            bf4 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash and owner
            bf5 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash, owner and status
            bf6 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash, owner and noOfBeds
            bf7 = generateEmptyBloomFilters(noOfHashFunctions);

            // bloomfilters with geohash and status
            bf8 = generateEmptyBloomFilters(noOfHashFunctions);

            for (Hospital hospital : hospitals) {
                try {

                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash), bf1);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString(), bf2);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "STATUS" + hospital.getStatus(), bf3);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "OWNER" + hospital.getOwner() + "STATUS" + hospital.getStatus(), bf4);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "OWNER" + hospital.getOwner(), bf5);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "OWNER" + hospital.getOwner() + "STATUS" + hospital.getStatus(), bf6);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "BEDS" + hospital.getBedsString() + "OWNER" + hospital.getOwner() , bf7);
                    fillBloomFilters(hospital.getGeoHash(noOfCharsInGeoHash) + "STATUS" + hospital.getStatus(), bf8);

                } catch (NullPointerException e) {

                    e.printStackTrace();
                }
            }

            List<Query> queries = GenerateQueries.getQueries();
            evaluateQueries(queries);


//            System.out.println("");
//            countEntries(bf1);
//            countEntries(bf2);
//            countEntries(bf3);
//            countEntries(bf4);
//            countEntries(bf5);
//            countEntries(bf6);
//            countEntries(bf7);
//            countEntries(bf8);

//            System.out.println(geohashes);

        } catch (ValueNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void evaluateQueries(List<Query> callableTasks) {

        ExecutorService executor = Executors.newFixedThreadPool(noOfThreads);

        long start = System.currentTimeMillis();

        try {
            List<Future<Boolean>> futures = executor.invokeAll(callableTasks);

            for (Future<Boolean> future : futures) {

                try {
                    Boolean aBoolean = future.get();
//                    System.out.println(aBoolean);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();

        System.out.println("Time taken for " + GenerateQueries.noOfQueries + " queries :" + (end-start) + " ms");
        executor.shutdown();
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

    private static void fillBloomFilters(String hashingString, List<boolean[]> bloomFilters)
    {
        // contains x number of hash indexes (x = noOfArrays or x = noOfHashCodesNeeded)
        int[] hash = hash(hashingString);

        for(int i=0; i<noOfHashFunctions; i++)
        {
            boolean[] bf = bloomFilters.get(i);
            bf[hash[i]] = true;
        }
    }

    public static int[] hash(String stringToHash)
    {
        int[] arr = new int[noOfHashFunctions];

        HashFunction hashFunction = Hashing.murmur3_32();
        for(int i=0; i<noOfHashFunctions; i++)
        {
            if(i==0)
            {
                hashFunction = Hashing.murmur3_32();
            }
            else if(i==1)
            {
                hashFunction = Hashing.murmur3_128();
            }
            else if(i==2)
            {
                hashFunction = Hashing.adler32();
            }
            else if(i==3)
            {
                hashFunction = Hashing.crc32c();
            }
            else if(i==4)
            {
                hashFunction = Hashing.farmHashFingerprint64();
            }
            else if(i==5)
            {
                hashFunction = Hashing.goodFastHash(64);
            }
            else if(i==6)
            {
                hashFunction = Hashing.sipHash24();
            }
            else if(i==7)
            {
                hashFunction = Hashing.sha256();
            }

            HashCode hashCode = hashFunction.hashBytes(stringToHash.getBytes());

            arr[i] = Math.abs(hashCode.asInt()) % ARRAY_SIZE;

        }

        return arr;
    }
}
