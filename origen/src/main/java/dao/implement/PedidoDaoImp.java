/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.implement;

/**
 *
 * @author Joel
 */
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import dao.PedidoDao;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.VentaPedido;

public class PedidoDaoImp extends AccesoMongoDB implements PedidoDao {

    private static final String COLECCION = "pedidos";

    // singleton
    private static PedidoDaoImp instance = null;

    private PedidoDaoImp() {
    }

    public static PedidoDaoImp getInstance() {
        if (instance == null) {
            instance = new PedidoDaoImp();
        }
        return instance;
    }

    @Override
    public List<VentaPedido> consultar() {
        try (MongoClient client = abrirConexion()) {
            MongoCollection<VentaPedido> coleccion = getCollection(client, COLECCION, VentaPedido.class);

            return coleccion.find()
                    .sort(Sorts.ascending("_id"))
                    .into(new ArrayList<>());
        } catch (MongoException e) {
            throw new DaoException("Error al listar los productos.", e);
        } catch (RuntimeException e) {
            throw new DaoException("Datos de producto no válidos: " + e.getMessage(), e);
        }
    }

    @Override
    public void agregar(VentaPedido pedido) {
        // estas validaciones lo hare en services
        /*
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }
        pedido.validar()^*/

        try (MongoClient client = abrirConexion()) {
            MongoCollection<VentaPedido> coleccion
                    = getCollection(client, COLECCION, VentaPedido.class);

            coleccion.insertOne(pedido);
        } catch (MongoException e) {
            throw new DaoException("Error al consultar el usuario en la base de datos.", e);
        }
    }
}
