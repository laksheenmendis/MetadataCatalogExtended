package sustain.metadata.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import sustain.metadata.schema.Hospital;
import sustain.metadata.utility.Geohash;
import sustain.metadata.utility.exceptions.ValueNotFoundException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by laksheenmendis on 11/27/20 at 6:05 PM
 */
public class Connector {

    private MongoClient mongoClient;
    private MongoDatabase database;

    public Connector() throws ValueNotFoundException {
        this.mongoClient = MongoClientProvider.getMongoClient();
        //TODO MongoDB Name
        this.database = mongoClient.getDatabase("sustaindb");
    }

    public MongoIterable<String> getCollectionNames() throws ValueNotFoundException {
        MongoIterable<String> collectionIterator = database.listCollectionNames();
        return collectionIterator;
    }

    public List<Hospital> readHospitalData()
    {
        List<Hospital> hospitalList = new ArrayList<Hospital>();

        //Creating a collection object
        MongoCollection<Document> collection = database.getCollection("hospitals_geo");
        //Retrieving the documents
        FindIterable<Document> iterDoc = collection.find();

        Iterator it = iterDoc.iterator();

        Hospital hospital = null;
        while (it.hasNext()) {
            Document resultDoc = (Document) it.next();
            Document propertiesDoc = (Document)resultDoc.get("properties");

            hospital = new Hospital();
            hospital.setLatitude((Double) propertiesDoc.get("LATITUDE"));
            hospital.setLongitude((Double) propertiesDoc.get("LONGITUDE"));
            hospital.setStatus((String) propertiesDoc.get("STATUS"));
            hospital.setBeds((Integer) propertiesDoc.get("BEDS"));
            hospital.setOwner((String) propertiesDoc.get("OWNER"));

            //generate the geohash
            hospital.setGeoHash(Geohash.encode(hospital.getLatitude(), hospital.getLongitude(), 5));

//            if(hospital.getGeoHash().equals("c1") || hospital.getGeoHash().equals("c4") || hospital.getGeoHash().equals("2j"))
//            {
//
//                System.out.println("GeoHash :" + hospital.getGeoHash());
//                System.out.println(hospital.getLatitude());
//                System.out.println(hospital.getLongitude());
//            }


            hospitalList.add(hospital);

//            System.out.println(it.next());
        }

        return hospitalList;
    }


    public int readDamData(int[] drainArea, int minDrainArea, int[] damHeight, int minDamHeight)
    {
        List<Hospital> hospitalList = new ArrayList<Hospital>();

        //Creating a collection object
        MongoCollection<Document> collection = database.getCollection("dams_geo");
        //Retrieving the documents
        FindIterable<Document> iterDoc = collection.find();

        Iterator it = iterDoc.iterator();

        int collectionSize = 0;

        while (it.hasNext()) {
            collectionSize++;
            Document resultDoc = (Document) it.next();
            Document propertiesDoc = (Document)resultDoc.get("properties");

            double drainAreaVal;
            if(propertiesDoc.get("DRAIN_AREA") instanceof Integer)
            {
                int drainAreaValInt = (Integer) propertiesDoc.get("DRAIN_AREA");
                drainAreaVal = drainAreaValInt;
            }
            else
            {
                drainAreaVal = (Double)propertiesDoc.get("DRAIN_AREA");
            }

            double damHeightVal;
            if(propertiesDoc.get("DAM_HEIGHT") instanceof Integer)
            {
                int damHeightValInt = (Integer) propertiesDoc.get("DAM_HEIGHT");
                damHeightVal = damHeightValInt;
            }
            else
            {
                damHeightVal = (Double) propertiesDoc.get("DAM_HEIGHT");
            }


            int idx1= (int)Math.floor(drainAreaVal) - minDrainArea;
            drainArea[idx1] += 1;

            int idx2 = (int)Math.floor(damHeightVal) - minDamHeight;
            damHeight[idx2] += 1;

        }
        return collectionSize;
    }
}
