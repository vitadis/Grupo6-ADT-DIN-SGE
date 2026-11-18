package dao;

import exceptions.DaoException;
import java.util.List;
import model.Producto;
import model.Proveedor;

public interface AñadirProductoDao {

    List<Proveedor> listarProveedoresActivos() throws DaoException;

    /** Asigna el siguiente id, valida e inserta el producto. */
    void insertarProducto(Producto producto) throws DaoException;

     /**
     * Suma stock a un producto existente, añade el lote y registra la entrada.
     */
    void anadirStock(int productoId, int cantidad, String fechaCaducidad) throws DaoException;
}