package dao.implement;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import exceptions.DaoException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.CategoriaProducto;
import model.Producto;
import model.TipoProducto;
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

    public static ProductoDaoImp getInstance() {
        if (instance == null) {
            instance = new ProductoDaoImp();
        }
        return instance;
    }

    @Override
    public List<Producto> listarProductos() {
        try (MongoClient client = abrirConexion()) {
            MongoCollection<Document> coleccion = getCollection(client, COLECCION);

            List<Producto> productos = new ArrayList<>();
            for (Document doc : coleccion.find().sort(Sorts.ascending("_id"))) {
                productos.add(documentoAProducto(doc));
            }
            return productos;
        } catch (MongoException e) {
            throw new DaoException("Error al listar los productos.", e);
        } catch (RuntimeException e) {
            // datos mal formados en Mongo: se avisa con DaoException para que
            // la pantalla muestre la alerta en vez de romper el FXML
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminarProducto(Producto producto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private Producto documentoAProducto(Document doc) {
        Producto p = new Producto();

        p.setId(doc.get(Producto.ID, Number.class).intValue());
        p.setNombre(doc.getString(Producto.NOMBRE));

        p.setTipo(TipoProducto.deValor(doc.getString(Producto.TIPO)));
        p.setCategoria(CategoriaProducto.deValor(p.getTipo(), doc.getString(Producto.CATEGORIA)));

        p.setPrecio_venta(doc.get(Producto.PRECIO_VENTA, Number.class).doubleValue());
        p.setIva(doc.get(Producto.IVA, Number.class).doubleValue());

        Document stock = doc.get(Producto.STOCK, Document.class);
        if (stock != null) {
            p.setStock_actual(stock.get(Producto.STOCK_ACTUAL, Number.class).intValue());
            p.setStock_minimo(stock.get(Producto.STOCK_MINIMO, Number.class).intValue());
        }

        List<Document> lotes = doc.getList(Producto.LOTES, Document.class);
        if (lotes != null) {
            for (Document lote : lotes) {
                Object fecha = lote.get(Producto.LOTE_FECHA_CADUCIDAD);
                if (fecha instanceof Date) {
                    lote.put(Producto.LOTE_FECHA_CADUCIDAD,
                            new SimpleDateFormat("yyyy-MM-dd").format((Date) fecha));
                }
            }
        }
        p.setLotes(lotes);

        p.setReceta(doc.getList(Producto.RECETA, Document.class));

        // Mongo puede guardar los números como Long/Double: se convierten uno a uno
        List<Integer> proveedores = new ArrayList<>();
        List<Number> proveedoresDoc = doc.getList(Producto.PROVEEDORES_ID, Number.class);
        if (proveedoresDoc != null) {
            for (Number n : proveedoresDoc) {
                proveedores.add(n.intValue());
            }
        }
        p.setProveedores_id(proveedores);

        p.setActivo(Boolean.TRUE.equals(doc.getBoolean(Producto.ACTIVO)));
        p.setImagen(doc.getString(Producto.RUTA_IMAGEN));

        return p;
    }

}
