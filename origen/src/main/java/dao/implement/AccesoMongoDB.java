package dao.implement;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 *
 * @author Joel
 */
public class AccesoMongoDB {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE = "origen";

    // POJO Autonatico
    protected static final CodecRegistry POJO_CODEC_REGISTRY = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder()
                    .automatic(true)
                    .build())
    );

    // conexion
    protected MongoClient abrirConexion() {
        return MongoClients.create(
                MongoClientSettings.builder()
                        .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                        .codecRegistry(POJO_CODEC_REGISTRY)
                        .build());
    }

    // La colección queda vinculada directamente a la clase (POJO)
    protected <T> MongoCollection<T> getCollection(MongoClient client, String nombre, Class<T> clase) {
        return client.getDatabase(DATABASE).getCollection(nombre, clase);
    }
}
