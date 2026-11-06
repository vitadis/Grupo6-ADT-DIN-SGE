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
 * Acceso a MongoDB con una única conexión compartida por todos los DAO. La
 * conexión se abre la primera vez que se pide y permanece abierta hasta que se
 * llama a {@link #cerrarConexion()} (al terminar la aplicación).
 *
 * @author Joel
 */
public class AccesoMongoDB {

    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE = "origen";

    // POJO Automático
    protected static final CodecRegistry POJO_CODEC_REGISTRY = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder()
                    .automatic(true)
                    .build())
    );

    // unico cliente compartido
    private static MongoClient client = null;

    /**
     * Devuelve el cliente compartido, creándolo si todavía no existe. No hay
     * que cerrarlo en los DAO.
     */
    protected static synchronized MongoClient getClient() {
        if (client == null) {
            client = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyConnectionString(new ConnectionString(CONNECTION_STRING))
                            .codecRegistry(POJO_CODEC_REGISTRY)
                            .build());

            // Red de seguridad: se cierra solo al terminar la JVM
            Runtime.getRuntime().addShutdownHook(new Thread(AccesoMongoDB::cerrarConexion));
        }
        return client;
    }

    /**
     * Cierra la conexión compartida (Al final de la app :)).
     */
    public static synchronized void cerrarConexion() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    // La colección queda vinculada directamente a la clase (POJO)
    protected <T> MongoCollection<T> getCollection(MongoClient client, String nombre, Class<T> clase) {
        return client.getDatabase(DATABASE).getCollection(nombre, clase);
    }
}
