package dao;

import exceptions.DaoException;
import model.MovimientoStock;
import model.Producto;

/**
 *
 * @author Hodei.Torres
 */
public interface RegistrarPerdidaDao {

    void registrarPerdida(MovimientoStock movimiento, Producto producto) throws DaoException;
}