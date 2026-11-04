/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.implement;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import dao.ProveedorDao;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.Proveedor;

/**
 * Implementación de {@link ProveedorDao} con MongoDB.
 *
 * Trabaja con la colección {@code proveedores}. Es un singleton: se obtiene con {@link #getInstance()}.
 * Cada operación abre su conexión y la cierra al terminar (try-with-resources).
 *
 * @author anazk
 */
public class ProveedorDaoImpl extends AccesoMongoDB implements ProveedorDao {

    /**
     * Nombre de la colección de proveedores en MongoDB.
     */
    private static final String COLECCION_PROVEEDOR = "proveedores";

    /**
     * Única instancia de la clase (singleton).
     */
    private static ProveedorDao instance = null;

    /**
     * Constructor privado para que solo se pueda crear desde {@link #getInstance()}.
     */
    private ProveedorDaoImpl() {
        super();
    }

    /**
     * Devuelve la única instancia del DAO; la crea la primera vez.
     *
     * @return el DAO de proveedores
     */
    public static synchronized ProveedorDao getInstance() {
        if (instance == null) {
            instance = new ProveedorDaoImpl();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Proveedor> obtenerProveedores() {
        try (MongoClient client = abrirConexion()) {
            MongoCollection<Proveedor> coleccion = getCollection(client, COLECCION_PROVEEDOR, Proveedor.class);

            return coleccion.find().sort(Sorts.ascending("_id")).into(new ArrayList<>());
        } catch (MongoException e) {
            throw new DaoException("Error al listar los proveedores.", e);
        } catch (RuntimeException e) {
            throw new DaoException("Datos de los proveedores no válidos: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * Asigna como identificador el mayor existente + 1, porque los {@code _id} son enteros.
     */
    @Override
    public void insertarProveedores(Proveedor proveedor) {
        try (MongoClient client = abrirConexion()) {
            MongoCollection<Proveedor> coleccion = getCollection(client, COLECCION_PROVEEDOR, Proveedor.class);

            Proveedor ultimo = coleccion.find().sort(Sorts.descending("_id")).first();
            if (ultimo == null) {
                proveedor.setId(1);
            } else {
                proveedor.setId(ultimo.getId() + 1);
            }

            coleccion.insertOne(proveedor);
        } catch (MongoException e) {
            throw new DaoException("Error al dar de alta el proveedor.", e);
        }
    }

    /**
     * {@inheritDoc}
     * Usa {@code replaceOne}, que cambia el documento entero.
     */
    @Override
    public void actualizarProveedores(Proveedor proveedor) {
        try (MongoClient client = abrirConexion()) {

            MongoCollection<Proveedor> coleccion = getCollection(client, COLECCION_PROVEEDOR, Proveedor.class);

            Proveedor prov = coleccion.find(Filters.eq("_id", proveedor.getId())).first();

            if (prov == null) {
                throw new IllegalArgumentException("No existe ningún proveedor con el id " + proveedor.getId() + ".");
            }

            coleccion.replaceOne(Filters.eq("_id", proveedor.getId()), proveedor);

        } catch (MongoException e) {
            throw new DaoException(
                    "Error al modificar proveedor.", e);
        }
    }

}
