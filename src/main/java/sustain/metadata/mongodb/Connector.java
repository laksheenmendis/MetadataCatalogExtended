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
            hospital.setLatitude((Double) resultDoc.get("property.LATITUDE"));
            hospital.setLongitude((Double) resultDoc.get("property.LONGITUDE"));
            hospital.setStatus((String) resultDoc.get("property.STATUS"));
            hospital.setBeds((Integer) resultDoc.get("property.BEDS"));
            hospital.setOwner((String) resultDoc.get("property.OWNER"));

            //generate the geohash
            hospital.setGeoHash(Geohash.encode(hospital.getLatitude(), hospital.getLongitude(), 2));

            hospitalList.add(hospital);

            System.out.println(it.next());
        }

        return hospitalList;
    }

}
