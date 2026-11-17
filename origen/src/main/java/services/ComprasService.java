package services;

import dao.ComprasDao;
import dao.MovimientoDao;
import dao.ProductoDao;
import dao.ProveedorDao;
import dao.implement.ComprasDaoImpl;
import dao.implement.MovimientoDaoImpl;
import dao.implement.ProductoDaoImp;
import dao.implement.ProveedorDaoImpl;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.Compra;
import model.LineaCompra;
import model.Movimiento;
import model.Producto;
import model.Proveedor;

/**
 * Lógica de negocio de compras y proveedores.
 *
 * Los controllers de compras solo hablan con esta clase. Antes de guardar valida los datos con
 * el {@code validar()} de cada modelo y después llama a los DAOs. Al registrar una compra
 * también suma al stock de cada producto la cantidad comprada y apunta su gasto en
 * {@code registros_economicos}.
 *
 * Los DAOs se reciben por el constructor (como {@code CajaService}), así en los tests se le
 * pueden pasar DAOs falsos y probar el service aislado, sin MongoDB.
 *
 * @author anazk
 */
public class ComprasService {

    /**
     * DAO de compras.
     */
    private final ComprasDao comprasDao;
    /**
     * DAO de proveedores.
     */
    private final ProveedorDao proveedorDao;
    /**
     * DAO de productos, para leer los productos y actualizar su stock.
     */
    private final ProductoDao productoDao;
    /**
     * DAO de registros económicos, para apuntar el gasto de cada compra.
     */
    private final MovimientoDao movimientoDao;

    /**
     * Crea el service con los DAOs que va a usar.
     *
     * @param comprasDao DAO de compras
     * @param proveedorDao DAO de proveedores
     * @param productoDao DAO de productos
     * @param movimientoDao DAO de registros económicos
     */
    public ComprasService(ComprasDao comprasDao, ProveedorDao proveedorDao, ProductoDao productoDao,
            MovimientoDao movimientoDao) {
        this.comprasDao = comprasDao;
        this.proveedorDao = proveedorDao;
        this.productoDao = productoDao;
        this.movimientoDao = movimientoDao;
    }

    /**
     * Crea el service con los DAOs de MongoDB.
     *
     * TEMPORAL: solo lo usa {@code InicioService} (de Hodei), que todavía crea este service con
     * {@code new ComprasService()}. Cuando lo cambie, este constructor se puede borrar.
     */
    public ComprasService() {
        this(ComprasDaoImpl.getInstance(), ProveedorDaoImpl.getInstance(), ProductoDaoImp.getInstance(),
                MovimientoDaoImpl.getInstance());
    }

    /**
     * Devuelve todas las compras ordenadas por identificador.
     *
     * @return la lista de compras
     * @throws DaoException si falla la base de datos
     */
    public List<Compra> listarCompras() {
        return comprasDao.obtenerCompras();
    }

    /**
     * Devuelve las compras hechas a un proveedor.
     *
     * @param idProveedor identificador del proveedor
     * @return la lista de compras de ese proveedor
     * @throws DaoException si falla la base de datos
     */
    public List<Compra> listarComprasPorProveedor(int idProveedor) {
        return comprasDao.obtenerComprasPorProveedor(idProveedor);
    }

    /**
     * Valida y guarda una compra, apunta su gasto de «Aprovisionamiento» en
     * {@code registros_economicos} (con origen la compra) y después suma al stock de cada
     * producto la cantidad comprada. La cantidad se redondea a entero porque el stock de los
     * productos es entero.
     *
     * @param compra compra que se va a registrar
     * @throws IllegalArgumentException si la compra no es válida, la factura está repetida o
     * algún producto no existe
     * @throws DaoException si falla la base de datos
     */
    public void registrarCompra(Compra compra) {
        if (compra == null) {
            throw new IllegalArgumentException(
                    "La compra no puede ser nula.");
        }

        compra.validar();

        comprasDao.insertarCompra(compra);
        movimientoDao.insertarMovimiento(Movimiento.deCompra(compra));

        List<Producto> productos = productoDao.listarProductos();
        for (LineaCompra l : compra.getDetalle()) {
            Producto p = buscarProducto(productos, l.getProducto_id());
            if (p == null) {
                throw new IllegalArgumentException(
                        "No existe el producto " + l.getNombre() + ".");
            }
            p.setStock_actual(p.getStock_actual() + (int) Math.round(l.getCantidad()));
            productoDao.modificarProducto(p);
        }
    }

    /**
     * Busca un producto por su identificador en una lista.
     * Se hace así porque {@code ProductoDao.obtenerProducto(int)} todavía no está implementado.
     *
     * @param productos lista en la que se busca
     * @param id identificador del producto
     * @return el producto, o {@code null} si no está en la lista
     */
    private Producto buscarProducto(List<Producto> productos, int id) {
        for (Producto p : productos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
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

    /**
     * Devuelve solo los proveedores activos.
     *
     * @return la lista de proveedores activos
     * @throws DaoException si falla la base de datos
     */
    public List<Proveedor> listarProveedoresActivos() {
        List<Proveedor> activos = new ArrayList<>();
        for (Proveedor p : proveedorDao.obtenerProveedores()) {
            if (p.isActivo()) {
                activos.add(p);
            }
        }
        return activos;
    }

    /**
     * Valida y guarda un proveedor: si su identificador es 0 lo da de alta y si no lo actualiza.
     *
     * @param proveedor proveedor que se va a guardar
     * @throws IllegalArgumentException si el proveedor no es válido o no existe al actualizarlo
     * @throws DaoException si falla la base de datos
     */
    public void guardarProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            throw new IllegalArgumentException(
                    "El proveedor no puede ser nulo.");
        }

        proveedor.validar();

        if (proveedor.getId() == 0) {
            proveedorDao.insertarProveedores(proveedor);
        } else {
            proveedorDao.actualizarProveedores(proveedor);
        }
    }

    /**
     * Devuelve todos los productos, para la lista sugerida.
     *
     * @return la lista de productos
     * @throws DaoException si falla la base de datos
     */
    public List<Producto> listarProductos() {
        return productoDao.listarProductos();
    }

    /**
     * Devuelve solo los productos activos, para el desplegable de la ventana de compra.
     *
     * @return la lista de productos activos
     * @throws DaoException si falla la base de datos
     */
    public List<Producto> listarProductosActivos() {
        List<Producto> activos = new ArrayList<>();
        for (Producto p : productoDao.listarProductos()) {
            if (p.isActivo()) {
                activos.add(p);
            }
        }
        return activos;
    }

}
