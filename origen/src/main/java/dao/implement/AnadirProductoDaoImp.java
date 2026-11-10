package dao.implement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import dao.AñadirProductoDao;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import model.Producto;
import model.Proveedor;
import org.bson.Document;
import util.AppLogger;

/**
 *
 * @author Hodei.Torres
 */
public class AnadirProductoDaoImp extends AccesoMongoDB implements AñadirProductoDao {

    private static final String COLECCION_PRODUCTOS = "productos";
    private static final String COLECCION_PROVEEDORES = "proveedores";

    private final AppLogger logger = AppLogger.getInstance();

    private static AñadirProductoDao instance = null;

    private AnadirProductoDaoImp() {
        super();
    }

    public static synchronized AñadirProductoDao getInstance() {
        if (instance == null) {
            instance = new AnadirProductoDaoImp();
        }
        return instance;
    }

    @Override
    public List<Proveedor> listarProveedoresActivos() throws DaoException {
        try {
            MongoClient client = getClient();
            MongoCollection<Proveedor> col
                    = getCollection(client, COLECCION_PROVEEDORES, Proveedor.class);
            return col.find(Filters.eq("activo", true))
                    .sort(Sorts.ascending("nombre"))
                    .into(new ArrayList<>());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener los proveedores de MongoDB", e);
            throw new DaoException("Error al obtener los proveedores de MongoDB.", e);
        }
    }

    @Override
    public void insertarProducto(Producto producto) throws DaoException {
        if (producto == null) {
            NullPointerException causa = new NullPointerException("producto nulo");
            logger.log(Level.SEVERE, "Se intentó insertar un producto nulo", causa);
            throw new DaoException("El producto no puede ser nulo.", causa);
        }

        try {
            MongoClient client = getClient();
            MongoCollection<Document> colDocs
                    = getCollection(client, COLECCION_PRODUCTOS, Document.class);
            Document ultimo = colDocs.find().sort(Sorts.descending("_id")).first();
            int siguienteId = (ultimo == null) ? 1 : ((Number) ultimo.get("_id")).intValue() + 1;

            producto.setId(siguienteId);
            producto.validar();

            MongoCollection<Producto> col
                    = getCollection(client, COLECCION_PRODUCTOS, Producto.class);
            col.insertOne(producto);

            logger.info("Producto insertado en MongoDB: id " + producto.getId()
                    + ", " + producto.getNombre());

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Producto no válido: " + e.getMessage(), e);
            throw new DaoException(e.getMessage(), e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al insertar el producto en MongoDB", e);
            throw new DaoException("Error al insertar el producto en MongoDB.", e);
        }
    }

    @Override
    public void anadirStock(int productoId, int cantidad, String fechaCaducidad) throws DaoException {
        if (cantidad <= 0) {
            IllegalArgumentException causa
                    = new IllegalArgumentException("Cantidad inválida: " + cantidad);
            logger.log(Level.WARNING, "Cantidad inválida al añadir stock al producto " + productoId, causa);
            throw new DaoException("La cantidad debe ser mayor que cero.", causa);
        }

        try {
            MongoClient client = getClient();
            MongoCollection<Document> col
                    = getCollection(client, COLECCION_PRODUCTOS, Document.class);

            Document lote = new Document(Producto.LOTE_CANTIDAD, cantidad)
                    .append(Producto.LOTE_FECHA_CADUCIDAD, fechaCaducidad);

            UpdateResult resultado = col.updateOne(
                    Filters.eq("_id", productoId),
                    Updates.combine(
                            Updates.inc("stock_actual", cantidad),
                            Updates.push("lotes", lote)));

            if (resultado.getMatchedCount() == 0) {
                throw new DaoException("No existe el producto con id " + productoId + ".",
                        new IllegalStateException("updateOne sin coincidencias"));
            }

            logger.info("Stock añadido en MongoDB: producto " + productoId
                    + " (+" + cantidad + ", caduca " + fechaCaducidad + ")");

        } catch (DaoException e) {
            logger.log(Level.WARNING, "No se pudo añadir stock: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al añadir stock en MongoDB", e);
            throw new DaoException("Error al añadir stock en MongoDB.", e);
        }
    }
}