package dao;

import exceptions.DaoException;
import java.util.List;
import model.Producto;
import model.Proveedor;

/**
 *
 * @author Hodei.Torres
 */
public interface GestionStockDao {

    /**
     * Descuenta stock (primero de los lotes que caducan antes) y guarda el
     * movimiento de pérdida.
     */
    List<Proveedor> listarActivos() throws DaoException;

    List<Producto> listarProductos() throws DaoException;
}
