package services;

import dao.ComprasDao;
import dao.MovimientoDao;
import dao.PedidoDao;
import dao.ProveedorDao;
import exceptions.DaoException;
import java.util.List;
import model.Compra;
import model.Movimiento;
import model.Proveedor;
import model.VentaPedido;

/**
 * Lógica de negocio de la pantalla «Gestión económica» y de la ventana «Registrar movimiento».
 *
 * Es el único service que usan esas dos pantallas: además de los movimientos (ingresos y gastos
 * que no son compras ni ventas de la caja), da las compras, las ventas y los proveedores que la
 * pantalla necesita, leyéndolos directamente de sus DAOs y no de otros services.
 *
 * Los DAOs se reciben por el constructor (como {@code CajaService}), así en los tests se le
 * pueden pasar DAOs falsos y probar el service aislado, sin MongoDB.
 *
 * @author anazk
 */
public class MovimientoService {

    /**
     * DAO de movimientos.
     */
    private final MovimientoDao movimientoDao;
    /**
     * DAO de compras (los gastos a proveedores).
     */
    private final ComprasDao comprasDao;
    /**
     * DAO de pedidos (las ventas de la caja).
     */
    private final PedidoDao pedidoDao;
    /**
     * DAO de proveedores (para poner su nombre en el concepto de las compras).
     */
    private final ProveedorDao proveedorDao;

    /**
     * Crea el service con los DAOs que va a usar.
     *
     * @param movimientoDao DAO de movimientos
     * @param comprasDao DAO de compras
     * @param pedidoDao DAO de pedidos
     * @param proveedorDao DAO de proveedores
     */
    public MovimientoService(MovimientoDao movimientoDao, ComprasDao comprasDao,
            PedidoDao pedidoDao, ProveedorDao proveedorDao) {
        this.movimientoDao = movimientoDao;
        this.comprasDao = comprasDao;
        this.pedidoDao = pedidoDao;
        this.proveedorDao = proveedorDao;
    }

    /**
     * Devuelve todos los movimientos.
     *
     * @return la lista de movimientos
     * @throws DaoException si falla la base de datos
     */
    public List<Movimiento> listarMovimientos() {
        return movimientoDao.obtenerMovimientos();
    }

    /**
     * Valida y guarda un movimiento nuevo.
     *
     * @param movimiento movimiento que se va a registrar
     * @throws IllegalArgumentException si el movimiento no es válido
     * @throws DaoException si falla la base de datos
     */
    public void registrarMovimiento(Movimiento movimiento) {
        if (movimiento == null) {
            throw new IllegalArgumentException(
                    "El movimiento no puede ser nulo.");
        }

        movimiento.validar();

        movimientoDao.insertarMovimiento(movimiento);
    }

    /**
     * Devuelve todas las compras (los gastos a proveedores).
     *
     * @return la lista de compras
     * @throws DaoException si falla la base de datos
     */
    public List<Compra> listarCompras() {
        return comprasDao.obtenerCompras();
    }

    /**
     * Devuelve todas las ventas de la caja.
     *
     * @return la lista de pedidos
     * @throws DaoException si falla la base de datos
     */
    public List<VentaPedido> listarVentas() {
        return pedidoDao.consultar();
    }

    /**
     * Devuelve todos los proveedores.
     *
     * @return la lista de proveedores
     * @throws DaoException si falla la base de datos
     */
    public List<Proveedor> listarProveedores() {
        return proveedorDao.obtenerProveedores();
    }

}
