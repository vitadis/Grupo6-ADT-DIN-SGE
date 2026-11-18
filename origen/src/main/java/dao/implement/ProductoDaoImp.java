package dao.implement;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.Producto;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.Document;

/**
 *
 * @author Joel
 */
public class ProductoDaoImp extends AccesoMongoDB implements dao.ProductoDao {

    private static final String COLECCION = "productos";

    // singleton
    private static ProductoDaoImp instance = null;

    private ProductoDaoImp() {
    }

    public static synchronized ProductoDaoImp getInstance() {
        if (instance == null) {
            instance = new ProductoDaoImp();
        }
        return instance;
    }

    @Override
    public List<Producto> listarProductos() {
        try {
            MongoClient client = getClient();
            MongoCollection<Producto> coleccion = getCollection(client, COLECCION, Producto.class);

            return coleccion.find().sort(Sorts.ascending("_id")).into(new ArrayList<>());
        } catch (MongoException e) {
            throw new DaoException("Error al listar los productos.", e);
        } catch (RuntimeException e) {
            throw new DaoException("Datos de producto no válidos: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc} Actualiza con {@code $set} todos los campos del producto salvo
     * {@code _id} y {@code lotes}. Los lotes no se tocan aquí porque el producto que llega
     * puede ser una copia antigua (p. ej. la que tiene cargada la caja) y reescribirlos
     * borraría los lotes añadidos después; los lotes se añaden con {@link #anadirLote}.
     */
    @Override
    public void modificarProducto(Producto producto) {

        try {
            MongoClient client = getClient();

            MongoCollection<Document> coleccion = getCollection(client, COLECCION, Document.class);

            BsonDocument campos = BsonDocumentWrapper.asBsonDocument(producto, POJO_CODEC_REGISTRY).clone();
            campos.remove("_id");
            campos.remove("lotes");

            UpdateResult resultado = coleccion.updateOne(
                    Filters.eq("_id", producto.getId()), new BsonDocument("$set", campos));

            if (resultado.getMatchedCount() == 0) {
                throw new IllegalArgumentException("No existe ningún producto con el id " + producto.getId() + ".");
            }

        } catch (MongoException e) {
            throw new DaoException(
                    "Error al modificar producto.", e);
        }
    }

    /**
     * {@inheritDoc} Usa {@code $inc} y {@code $push} en una sola operación, así no se
     * reescribe el documento entero y los lotes existentes se conservan.
     */
    @Override
    public void anadirLote(int productoId, int cantidad, String fechaCaducidad) {
        try {
            MongoClient client = getClient();
            MongoCollection<Document> coleccion = getCollection(client, COLECCION, Document.class);

            Document lote = new Document(Producto.LOTE_CANTIDAD, cantidad)
                    .append(Producto.LOTE_FECHA_CADUCIDAD, fechaCaducidad);

            UpdateResult resultado = coleccion.updateOne(
                    Filters.eq("_id", productoId),
                    Updates.combine(
                            Updates.inc("stock_actual", cantidad),
                            Updates.push("lotes", lote)));

            if (resultado.getMatchedCount() == 0) {
                throw new IllegalArgumentException("No existe ningún producto con el id " + productoId + ".");
            }
        } catch (MongoException e) {
            throw new DaoException("Error al añadir el lote al producto.", e);
        }
    }

}
