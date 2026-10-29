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

    public static ProductoDaoImp getInstance() {
        if (instance == null) {
            instance = new ProductoDaoImp();
        }
        return instance;
    }

    @Override
    public List<Producto> listarProductos() {
        try (MongoClient client = abrirConexion()) {
            MongoCollection<Producto> coleccion = getCollection(client, COLECCION, Producto.class);

            return coleccion.find().sort(Sorts.ascending("_id")).into(new ArrayList<>());
        } catch (MongoException e) {
            throw new DaoException("Error al listar los productos.", e);
        } catch (RuntimeException e) {
            throw new DaoException("Datos de producto no válidos: " + e.getMessage(), e);
        }
    }

    @Override
    public Producto obtenerProducto(int id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void altaProducto(Producto producto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void modificarProducto(Producto producto) {

        try (MongoClient client = abrirConexion()) {

            MongoCollection<Producto> coleccion = getCollection(client, COLECCION, Producto.class);

            Producto usuarioExistente = coleccion.find(Filters.eq("_id", producto.getId())).first();

            if (usuarioExistente == null) {
                throw new IllegalArgumentException("No existe ningún usuario con el id " + producto.getId() + ".");
            }

            coleccion.replaceOne(Filters.eq("_id", producto.getId()), producto);

        } catch (MongoException e) {
            throw new DaoException(
                    "Error al modificar producto.", e);
        }
    }
    
    @Override
    public void eliminarProducto(Producto producto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
