package dao;

import exceptions.DaoException;
import java.util.List;
import model.MovimientoStock;
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
    void registrarPerdida(MovimientoStock movimiento, Producto producto) throws DaoException;

    List<Proveedor> listarActivos() throws DaoException;

    List<Producto> listarProductos() throws DaoException;

    /**
     * Inserta un producto nuevo (asigna el id) y registra su entrada inicial si
     * tiene stock.
     */
    void insertarProducto(Producto producto) throws DaoException;

    /**
     * Suma stock a un producto existente, añade el lote y registra la entrada.
     */
    void anadirStock(int productoId, int cantidad, String fechaCaducidad) throws DaoException;
}
