package dao.implement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import dao.RegistrarPerdidaDao;
import exceptions.DaoException;
import java.util.logging.Level;
import model.MovimientoStock;
import model.Producto;
import util.AppLogger;

/**
 * Implementación con MongoDB de {@link dao.RegistrarPerdidaDao}. Es un singleton.
 *
 * @author Hodei.Torres
 */
public class RegistrarPerdidaDaoImp extends AccesoMongoDB implements RegistrarPerdidaDao {

    private static final String COLECCION_PRODUCTOS = "productos";
    private static final String COLECCION_MOVIMIENTOS = "movimientos_stock";
    
    private final AppLogger logger = AppLogger.getInstance();


    private static RegistrarPerdidaDao instance = null;

    private RegistrarPerdidaDaoImp() {
        super();
    }

    /**
     * Devuelve la única instancia del DAO (patrón Singleton).
     *
     * @return instancia del DAO
     */
    public static synchronized RegistrarPerdidaDao getInstance() {
        if (instance == null) {
            instance = new RegistrarPerdidaDaoImp();
        }
        return instance;
    }

    @Override
    public void registrarPerdida(MovimientoStock movimiento, Producto producto) throws DaoException {
        if (movimiento == null || producto == null) {
            NullPointerException causa
                    = new NullPointerException("Referencia nula en movimiento o producto");
            logger.log(Level.WARNING, "Se intentó registrar una pérdida con movimiento o producto nulo", causa);
            throw new DaoException(
                    "El movimiento y el producto no pueden ser nulos.",
                    new NullPointerException("Referencia nula en movimiento o producto"));
        }

        if (movimiento.getCantidad() <= 0) {
            IllegalArgumentException causa
                    = new IllegalArgumentException("Cantidad inválida: " + movimiento.getCantidad());
            logger.log(Level.WARNING, "Cantidad inválida al registrar pérdida del producto "
                    + producto.getId(), causa); 
            throw new DaoException(
                    "La cantidad debe ser mayor que cero.",
                    new IllegalArgumentException("Cantidad inválida: " + movimiento.getCantidad()));
        }

        try {
            MongoClient client = getClient();
            MongoCollection<Producto> colProductos
                    = getCollection(client, COLECCION_PRODUCTOS, Producto.class);
            MongoCollection<MovimientoStock> colMovimientos
                    = getCollection(client, COLECCION_MOVIMIENTOS, MovimientoStock.class);

            UpdateResult resultado = colProductos.updateOne(
                    Filters.and(
                            Filters.eq("_id", producto.getId()),
                            Filters.gte("stock_actual", movimiento.getCantidad())),
                    Updates.inc("stock_actual", -movimiento.getCantidad()));

            if (resultado.getModifiedCount() == 0) {
                throw new DaoException(
                        "No hay suficiente stock para descontar esa cantidad.",
                        new IllegalStateException("Stock insuficiente o producto no encontrado (id "
                                + producto.getId() + ")"));
            }

            colMovimientos.insertOne(movimiento);
            
            logger.info("Pérdida registrada en MongoDB: producto " + producto.getId()
                    + " (" + producto.getNombre() + "), -" + movimiento.getCantidad()
                    + ". Nota: " + movimiento.getNota());

        } catch (DaoException e) {
            logger.log(Level.WARNING, "No se pudo registrar la pérdida: " + e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al registrar la pérdida en MongoDB", e);
            throw new DaoException("Error al registrar la pérdida en MongoDB.", e);
        }
    }
}
