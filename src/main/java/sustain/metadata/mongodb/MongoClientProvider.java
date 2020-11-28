package sustain.metadata.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
//import sustain.metadata.utility.PropertyLoader;
import sustain.metadata.utility.exceptions.ValueNotFoundException;

/**
 * Created by laksheenmendis on 8/15/20 at 11:33 AM
 */
public class MongoClientProvider {

    private static MongoClient mongoClient = null;

    public static MongoClient getMongoClient() throws ValueNotFoundException {
        if (mongoClient == null) {
            mongoClient = createMongoClient();
        }
        return mongoClient;
    }

    private static MongoClient createMongoClient() throws ValueNotFoundException {

        MongoClientURI mongoClientURI = new MongoClientURI(getConnectionString());
        return new MongoClient(mongoClientURI);
    }

    private static String getConnectionString() throws ValueNotFoundException {
        StringBuilder sb = new StringBuilder("mongodb://");

        //TODO change host and port accordingly
        sb.append("lattice-47");
        sb.append(":");
        sb.append("27017");

        return sb.toString();
    }
}
