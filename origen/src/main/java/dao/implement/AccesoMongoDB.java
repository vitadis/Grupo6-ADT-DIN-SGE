package dao.implement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 *
 * @author Joel
 */
public class AccesoMongoDB {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
        private static final String DATABASE = "origen";

    // conexion
    protected MongoClient abrirConexion() {
        return MongoClients.create(CONNECTION_STRING);
    }

    protected MongoCollection<Document> getCollection(MongoClient client, String nombre) {
        return client.getDatabase(DATABASE).getCollection(nombre);
    }
}
