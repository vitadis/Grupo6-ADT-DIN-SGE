package services;

import dao.ComprasDao;
import dao.MovimientoDao;
import dao.ProveedorDao;
import exceptions.DaoException;
import java.util.List;
import model.Compra;
import model.Movimiento;
import model.Proveedor;

/**
 * Lógica de negocio de la pantalla «Gestión económica» y de la ventana «Registrar movimiento».
 *
 * Los importes salen todos de {@code registros_economicos}, donde ya están los gastos de las
 * compras y los ingresos de los pedidos cobrados (los apuntan {@code ComprasService} y
 * {@code CajaService} al guardarlos). Las compras y los proveedores solo se leen para escribir
 * el concepto de los registros que vienen de una compra.
 *
 * Los DAOs se reciben por el constructor (como {@code CajaService}), así en los tests se le
 * pueden pasar DAOs falsos y probar el service aislado, sin MongoDB.
 *
 * @author anazk
 */
public class MovimientoService {

    /**
     * DAO de registros económicos.
     */
    private final MovimientoDao movimientoDao;
    /**
     * DAO de compras (para el concepto de los registros de compras).
     */
    private final ComprasDao comprasDao;
    /**
     * DAO de proveedores (para poner su nombre en el concepto de las compras).
     */
    private final ProveedorDao proveedorDao;

    /**
     * Crea el service con los DAOs que va a usar.
     *
     * @param movimientoDao DAO de registros económicos
     * @param comprasDao DAO de compras
     * @param proveedorDao DAO de proveedores
     */
    public MovimientoService(MovimientoDao movimientoDao, ComprasDao comprasDao, ProveedorDao proveedorDao) {
        this.movimientoDao = movimientoDao;
        this.comprasDao = comprasDao;
        this.proveedorDao = proveedorDao;
    }

    /**
     * Devuelve todos los registros económicos.
     *
     * @return la lista de registros
     * @throws DaoException si falla la base de datos
     */
    public List<Movimiento> listarMovimientos() {
        return movimientoDao.obtenerMovimientos();
    }

    /**
     * Valida y guarda un registro económico metido a mano (sin origen).
     *
     * @param movimiento registro que se va a guardar
     * @throws IllegalArgumentException si el registro no es válido
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
     * Devuelve todas las compras.
     *
     * @return la lista de compras
     * @throws DaoException si falla la base de datos
     */
    public List<Compra> listarCompras() {
        return comprasDao.obtenerCompras();
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
