package dao.implement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dao.GestionStockDao;
import exceptions.DaoException;
import model.MovimientoStock;
import model.Producto;

/**
 *
 * @author Hodei.Torres
 */
public class GestionStockDaoImp extends AccesoMongoDB implements GestionStockDao {

    private static final String COLECCION_PRODUCTOS = "productos";
    private static final String COLECCION_MOVIMIENTOS = "movimientos_stock";

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
    public void registrarPerdida(MovimientoStock movimiento, Producto producto) throws DaoException {
        // Validaciones previas con causa explícita
        if (movimiento == null || producto == null) {
            throw new DaoException(
                "El movimiento y el producto no pueden ser nulos.",
                new NullPointerException("Referencia nula en movimiento o producto")
            );
        }

        if (movimiento.getCantidad() <= 0) {
            throw new DaoException(
                "La cantidad debe ser mayor que cero.",
                new IllegalArgumentException("Cantidad inválida: " + movimiento.getCantidad())
            );
        }

        if (producto.getStock_actual() < movimiento.getCantidad()) {
            throw new DaoException(
                "No hay suficiente stock para descontar esa cantidad.",
                new IllegalStateException(
                    "Stock actual (" + producto.getStock_actual() + ") menor que la cantidad a descontar (" + movimiento.getCantidad() + ")"
                )
            );
        }

        // Se abre la conexión y se cierra automáticamente al terminar
        try (MongoClient client = abrirConexion()) {
            // 1. Obtener la colección tipada para el POJO MovimientoStock e insertar
            MongoCollection<MovimientoStock> colMovimientos = getCollection(client, COLECCION_MOVIMIENTOS, MovimientoStock.class);
            colMovimientos.insertOne(movimiento);

            // 2. Obtener la colección de productos y descontar existencias
            // Se usa producto.getId() numérico directamente sin ObjectId
            MongoCollection<Producto> colProductos = getCollection(client, COLECCION_PRODUCTOS, Producto.class);
            colProductos.updateOne(
                    Filters.eq("_id", producto.getId()),
                    Updates.inc("stock_actual", -movimiento.getCantidad())
            );

        } catch (Exception e) {
            throw new DaoException("Error al registrar la pérdida en MongoDB.", e);
        }
    }
}