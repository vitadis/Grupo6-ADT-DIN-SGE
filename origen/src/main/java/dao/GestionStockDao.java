package dao;

import exceptions.DaoException;
import model.MovimientoStock;
import model.Producto;

/**
 *
 * @author Hodei.Torres
 */
public interface GestionStockDao {
    
    /**
     * Registra un movimiento de pérdida como POJO y descuenta las existencias del producto.
     *
     * @param movimiento Objeto POJO con el detalle de la merma/pérdida.
     * @param producto   Producto al cual se le descontará el stock.
     * @throws DaoException Si ocurre un error de validación o conexión con MongoDB.
     */
    void registrarPerdida(MovimientoStock movimiento, Producto producto) throws DaoException;
}