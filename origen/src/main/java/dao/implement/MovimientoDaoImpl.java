/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.implement;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import dao.MovimientoDao;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.Movimiento;

/**
 * Implementación de {@link MovimientoDao} con MongoDB.
 *
 * Trabaja con la colección {@code movimientos}. Es un singleton: se obtiene con
 * {@link #getInstance()}. Cada operación abre su conexión y la cierra al
 * terminar (try-with-resources).
 *
 * @author anazk
 */
public class MovimientoDaoImpl extends AccesoMongoDB implements MovimientoDao {

    /**
     * Nombre de la colección de movimientos en MongoDB.
     */
    private static final String COLECCION_MOVIMIENTOS = "movimientos";

    /**
     * Única instancia de la clase (singleton).
     */
    private static MovimientoDao instance = null;

    /**
     * Constructor privado para que solo se pueda crear desde
     * {@link #getInstance()}.
     */
    private MovimientoDaoImpl() {
        super();
    }

    /**
     * Devuelve la única instancia del DAO; la crea la primera vez.
     *
     * @return el DAO de movimientos
     */
    public static synchronized MovimientoDao getInstance() {
        if (instance == null) {
            instance = new MovimientoDaoImpl();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Movimiento> obtenerMovimientos() {
        try {
            MongoClient client = getClient();
            MongoCollection<Movimiento> coleccion = getCollection(client, COLECCION_MOVIMIENTOS, Movimiento.class);

            return coleccion.find().sort(Sorts.ascending("_id")).into(new ArrayList<>());
        } catch (MongoException e) {
            throw new DaoException("Error al listar los movimientos.", e);
        } catch (RuntimeException e) {
            throw new DaoException("Datos de los movimientos no válidos: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc} Asigna como identificador el mayor existente + 1, porque
     * los {@code _id} son enteros.
     */
    @Override
    public void insertarMovimiento(Movimiento movimiento) {
        try {
            MongoClient client = getClient();
            MongoCollection<Movimiento> coleccion = getCollection(client, COLECCION_MOVIMIENTOS, Movimiento.class);

            // Los _id son enteros y el driver no los genera: siguiente = mayor + 1
            Movimiento ultimo = coleccion.find().sort(Sorts.descending("_id")).first();
            if (ultimo == null) {
                movimiento.setId(1);
            } else {
                movimiento.setId(ultimo.getId() + 1);
            }

            coleccion.insertOne(movimiento);
        } catch (MongoException e) {
            throw new DaoException("Error al registrar el movimiento.", e);
        }
    }

}
