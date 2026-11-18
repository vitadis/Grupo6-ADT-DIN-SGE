package dao;

import exceptions.DaoException;
import java.util.List;
import model.Producto;
import model.Proveedor;

/**
 * Operaciones de acceso a datos de la pantalla de gestión de stock.
 *
 * @author Hodei.Torres
 */
public interface GestionStockDao {

    /**
     * Devuelve los proveedores que están activos.
     *
     * @return lista de proveedores activos
     * @throws DaoException si falla el acceso a la base de datos
     */
    List<Proveedor> listarActivos() throws DaoException;

    /**
     * Devuelve todos los productos del inventario.
     *
     * @return lista de productos
     * @throws DaoException si falla el acceso a la base de datos
     */
    List<Producto> listarProductos() throws DaoException;
}
