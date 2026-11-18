package dao.implement;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import dao.GestionStockDao;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import model.Producto;
import model.Proveedor;
import util.AppLogger;


/**
 * Implementación con MongoDB de {@link dao.GestionStockDao}. Es un singleton.
 *
 * @author Hodei.Torres
 */
public class GestionStockDaoImp extends AccesoMongoDB implements GestionStockDao {

    private static final String COLECCION_PRODUCTOS = "productos";
    private static final String COLECCION_PROVEEDORES = "proveedores";
    
    private final AppLogger logger = AppLogger.getInstance();

    private static GestionStockDao instance = null;

    private GestionStockDaoImp() {
        super();
    }

    /**
     * Devuelve la única instancia del DAO (patrón Singleton).
     *
     * @return instancia del DAO
     */
    public static synchronized GestionStockDao getInstance() {
        if (instance == null) {
            instance = new GestionStockDaoImp();
        }
        return instance;
    }

    @Override
    public List<Proveedor> listarActivos() throws DaoException {
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
    public List<Producto> listarProductos() throws DaoException {
        try {
            MongoClient client = getClient();
            MongoCollection<Producto> col
                    = getCollection(client, COLECCION_PRODUCTOS, Producto.class);
            return col.find(Filters.eq("activo", true))
                    .sort(Sorts.ascending("nombre"))
                    .into(new ArrayList<>());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al obtener los productos de MongoDB", e);
            throw new DaoException("Error al obtener los productos de MongoDB.", e);
        }
    }
}
