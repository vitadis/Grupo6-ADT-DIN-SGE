package dao;

import exceptions.DaoException;
import java.util.List;
import model.Producto;
import model.Proveedor;

/**
 * Operaciones de acceso a datos para dar de alta productos nuevos y añadir stock a los
 * existentes.
 *
 * @author Hodei.Torres
 */
public interface AñadirProductoDao {

    /**
     * Devuelve los proveedores que están activos.
     *
     * @return lista de proveedores activos
     * @throws DaoException si falla el acceso a la base de datos
     */
    List<Proveedor> listarProveedoresActivos() throws DaoException;

    /**
     * Asigna el siguiente id, valida e inserta el producto.
     *
     * @param producto producto que se va a insertar; al terminar tiene asignado su id
     * @throws DaoException si falla el acceso a la base de datos
     */
    void insertarProducto(Producto producto) throws DaoException;

    /**
     * Suma stock a un producto existente, añade el lote y registra la entrada.
     *
     * @param productoId identificador del producto
     * @param cantidad unidades que entran
     * @param fechaCaducidad fecha de caducidad del lote (yyyy-MM-dd)
     * @throws DaoException si falla el acceso a la base de datos
     */
    void anadirStock(int productoId, int cantidad, String fechaCaducidad) throws DaoException;
}