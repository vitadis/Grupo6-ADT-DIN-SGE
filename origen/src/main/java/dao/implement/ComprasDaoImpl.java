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
import dao.ComprasDao;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.Compra;

/**
 * Implementación de {@link ComprasDao} con MongoDB.
 *
 * Trabaja con la colección {@code compras}. Es un singleton: se obtiene con {@link #getInstance()}.
 * Cada operación abre su conexión y la cierra al terminar (try-with-resources).
 *
 * @author anazk
 */
public class ComprasDaoImpl extends AccesoMongoDB implements ComprasDao{
    /**
     * Nombre de la colección de compras en MongoDB.
     */
    private static final String COLECCION_COMPRAS = "compras";

    /**
     * Única instancia de la clase (singleton).
     */
    private static ComprasDao instance = null;

    /**
     * Constructor privado para que solo se pueda crear desde {@link #getInstance()}.
     */
    private ComprasDaoImpl() {
        super();
    }

    /**
     * Devuelve la única instancia del DAO; la crea la primera vez.
     *
     * @return el DAO de compras
     */
    public static synchronized ComprasDao getInstance() {
        if (instance == null) {
            instance = new ComprasDaoImpl();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Compra> obtenerCompras() {
         try (MongoClient client = abrirConexion()) {
            MongoCollection<Compra> coleccion = getCollection(client, COLECCION_COMPRAS, Compra.class);

            return coleccion.find().sort(Sorts.ascending("_id")).into(new ArrayList<>());
        } catch (MongoException e) {
            throw new DaoException("Error al listar las compras.", e);
        } catch (RuntimeException e) {
            throw new DaoException("Datos de las compras no válidos: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * Filtra por el campo {@code proveedor_id}.
     */
    @Override
    public List<Compra> obtenerComprasPorProveedor(int idProveedor) {
         try (MongoClient client = abrirConexion()) {
            MongoCollection<Compra> coleccion = getCollection(client, COLECCION_COMPRAS, Compra.class);

            return coleccion.find(Filters.eq("proveedor_id", idProveedor)).sort(Sorts.ascending("_id")).into(new ArrayList<>());
        } catch (MongoException e) {
            throw new DaoException("Error al listar las compras.", e);
        } catch (RuntimeException e) {
            throw new DaoException("Datos de las compras no válidos: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     * Comprueba que el número de factura no esté repetido y asigna como
     * identificador el mayor existente + 1, porque los {@code _id} son enteros.
     */
    @Override
    public void insertarCompra(Compra compra) {
        try (MongoClient client = abrirConexion()) {
            MongoCollection<Compra> coleccion = getCollection(client, COLECCION_COMPRAS, Compra.class);

            if (coleccion.countDocuments(Filters.eq("numero_factura", compra.getNumero_factura())) > 0) {
                throw new IllegalArgumentException("Ya existe una compra con la factura " + compra.getNumero_factura() + ".");
            }

            // Los _id son enteros y el driver no los genera: siguiente = mayor + 1
            Compra ultima = coleccion.find().sort(Sorts.descending("_id")).first();
            if (ultima == null) {
                compra.setId(1);
            } else {
                compra.setId(ultima.getId() + 1);
            }

            coleccion.insertOne(compra);
        } catch (MongoException e) {
            throw new DaoException("Error al registrar la compra.", e);
        }
    }

}
