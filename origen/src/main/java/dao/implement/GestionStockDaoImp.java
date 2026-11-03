package dao.implement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import dao.GestionStockDao;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.MovimientoStock;
import model.Producto;
import model.Proveedor;
import org.bson.Document;

/**
 *
 * @author Hodei.Torres
 */
public class GestionStockDaoImp extends AccesoMongoDB implements GestionStockDao {

    private static final String COLECCION_PRODUCTOS = "productos";
    private static final String COLECCION_MOVIMIENTOS = "movimientos_stock";
    private static final String COLECCION_PROVEEDORES = "proveedores";

    private static GestionStockDao instance = null;

    private GestionStockDaoImp() {
        super();
    }

    public static synchronized GestionStockDao getInstance() {
        if (instance == null) {
            instance = new GestionStockDaoImp();
        }
        return instance;
    }

    @Override
    public List<Proveedor> listarActivos() throws DaoException {
        try (MongoClient client = abrirConexion()) {
            MongoCollection<Proveedor> col
                    = getCollection(client, COLECCION_PROVEEDORES, Proveedor.class);
            return col.find(Filters.eq("activo", true))
                    .sort(Sorts.ascending("nombre"))
                    .into(new ArrayList<>());
        } catch (Exception e) {
            throw new DaoException("Error al obtener los proveedores de MongoDB.", e);
        }
    }

    @Override
    public List<Producto> listarProductos() throws DaoException {
        try (MongoClient client = abrirConexion()) {
            MongoCollection<Producto> col
                    = getCollection(client, COLECCION_PRODUCTOS, Producto.class);
            return col.find(Filters.eq("activo", true))
                    .sort(Sorts.ascending("nombre"))
                    .into(new ArrayList<>());
        } catch (Exception e) {
            throw new DaoException("Error al obtener los productos de MongoDB.", e);
        }
    }

    @Override
    public void insertarProducto(Producto producto) throws DaoException {
        if (producto == null) {
            throw new DaoException("El producto no puede ser nulo.",
                    new NullPointerException("producto nulo"));
        }

        try (MongoClient client = abrirConexion()) {
            // El máximo id se lee como Document para no depender de los setters del POJO
            MongoCollection<Document> colDocs
                    = getCollection(client, COLECCION_PRODUCTOS, Document.class);
            Document ultimo = colDocs.find().sort(Sorts.descending("_id")).first();
            int siguienteId = (ultimo == null) ? 1 : ultimo.getInteger("_id") + 1;

            producto.setId(siguienteId);
            producto.validar();

            MongoCollection<Producto> col
                    = getCollection(client, COLECCION_PRODUCTOS, Producto.class);
            col.insertOne(producto);

        } catch (IllegalArgumentException e) {
            throw new DaoException(e.getMessage(), e);
        } catch (Exception e) {
            throw new DaoException("Error al insertar el producto en MongoDB.", e);
        }
    }

    @Override
    public void anadirStock(int productoId, int cantidad, String fechaCaducidad) throws DaoException {
        if (cantidad <= 0) {
            throw new DaoException("La cantidad debe ser mayor que cero.",
                    new IllegalArgumentException("Cantidad inválida: " + cantidad));
        }

        try (MongoClient client = abrirConexion()) {
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

        } catch (DaoException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoException("Error al añadir stock en MongoDB.", e);
        }
    }

    @Override
    public void registrarPerdida(MovimientoStock movimiento, Producto producto) throws DaoException {
        if (movimiento == null || producto == null) {
            throw new DaoException(
                    "El movimiento y el producto no pueden ser nulos.",
                    new NullPointerException("Referencia nula en movimiento o producto"));
        }

        if (movimiento.getCantidad() <= 0) {
            throw new DaoException(
                    "La cantidad debe ser mayor que cero.",
                    new IllegalArgumentException("Cantidad inválida: " + movimiento.getCantidad()));
        }

        if (producto.getStock_actual() < movimiento.getCantidad()) {
            throw new DaoException(
                    "No hay suficiente stock para descontar esa cantidad.",
                    new IllegalStateException("Stock actual (" + producto.getStock_actual()
                            + ") menor que la cantidad a descontar (" + movimiento.getCantidad() + ")"));
        }

        try (MongoClient client = abrirConexion()) {
            MongoCollection<MovimientoStock> colMovimientos
                    = getCollection(client, COLECCION_MOVIMIENTOS, MovimientoStock.class);
            colMovimientos.insertOne(movimiento);

            MongoCollection<Producto> colProductos
                    = getCollection(client, COLECCION_PRODUCTOS, Producto.class);
            colProductos.updateOne(
                    Filters.eq("_id", producto.getId()),
                    Updates.inc("stock_actual", -movimiento.getCantidad()));

        } catch (Exception e) {
            throw new DaoException("Error al registrar la pérdida en MongoDB.", e);
        }
    }
}
