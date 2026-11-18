package dao;

import exceptions.DaoException;
import java.util.List;
import model.Movimiento;

/**
 * Operaciones de la colección {@code registros_economicos}: todos los ingresos y gastos, tanto
 * los que salen de compras y pedidos como los que se meten a mano.
 *
 * @author anazk
 */
public interface MovimientoDao {

    /**
     * Devuelve todos los movimientos ordenados por identificador.
     *
     * @return la lista de movimientos (vacía si no hay ninguno)
     * @throws DaoException si falla la conexión o los datos no se pueden leer
     */
    List<Movimiento> obtenerMovimientos();

    /**
     * Guarda un movimiento nuevo. El identificador lo asigna el DAO.
     *
     * @param movimiento movimiento que se va a guardar
     * @throws DaoException si falla la base de datos
     */
    void insertarMovimiento(Movimiento movimiento);
}
