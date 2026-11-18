package dao;

import exceptions.DaoException;
import model.MovimientoStock;
import model.Producto;

/**
 * Operaciones de acceso a datos para registrar pérdidas de stock.
 *
 * @author Hodei.Torres
 */
public interface RegistrarPerdidaDao {

    /**
     * Descuenta stock del producto (primero de los lotes que caducan antes) y guarda el
     * movimiento de pérdida.
     *
     * @param movimiento movimiento de pérdida que se registra
     * @param producto producto al que se descuenta el stock
     * @throws DaoException si algún parámetro es nulo o falla el acceso a la base de datos
     */
    void registrarPerdida(MovimientoStock movimiento, Producto producto) throws DaoException;
}