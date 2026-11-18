/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.AñadirProductoDao;
import dao.GestionStockDao;
import dao.RegistrarPerdidaDao;
import dao.implement.AnadirProductoDaoImp;
import dao.implement.GestionStockDaoImp;
import dao.implement.RegistrarPerdidaDaoImp;
import exceptions.DaoException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.CategoriaProducto;
import model.MovimientoStock;
import model.Producto;
import model.Proveedor;
import model.TipoProducto;
import org.bson.Document;

/**
 * Lógica de negocio de la gestión de stock: listados, pérdidas, entradas de stock, alta de
 * productos y estadísticas del inventario.
 *
 * @author Hodei.Torres
 */
public class GestionStockService {

    private final GestionStockDao stockDao = GestionStockDaoImp.getInstance();
    private final RegistrarPerdidaDao perdidaDao = RegistrarPerdidaDaoImp.getInstance();
    private final AñadirProductoDao añadirDao = AnadirProductoDaoImp.getInstance();

    /**
     * Crea el servicio usando los DAO de stock, pérdidas y alta de productos.
     */
    public GestionStockService() {
    }

    /**
     * Devuelve todos los productos del inventario.
     *
     * @return lista de productos
     * @throws DaoException si falla el acceso a la base de datos
     */
    public List<Producto> listarProductos() throws DaoException {
        return stockDao.listarProductos();
    }

    /**
     * Devuelve los proveedores activos.
     *
     * @return lista de proveedores activos
     * @throws DaoException si falla el acceso a la base de datos
     */
    public List<Proveedor> listarProveedoresActivos() throws DaoException {
        return stockDao.listarActivos();
    }

    /**
     * Devuelve las categorías que pertenecen a un tipo de producto.
     *
     * @param tipo tipo de producto
     * @return lista de categorías (vacía si el tipo es {@code null})
     */
    public List<CategoriaProducto> categoriasDeTipo(TipoProducto tipo) {
        List<CategoriaProducto> filtradas = new ArrayList<>();
        if (tipo != null) {
            for (CategoriaProducto c : CategoriaProducto.values()) {
                if (c.getTipo() == tipo) {
                    filtradas.add(c);
                }
            }
        }
        return filtradas;
    }

    /**
     * Valida y registra una pérdida de stock de un producto.
     *
     * @param producto producto afectado
     * @param cantidad unidades perdidas (entre 1 y el stock actual)
     * @param nota nota explicativa (puede ser {@code null})
     * @throws IllegalArgumentException si los datos no son válidos
     * @throws DaoException si falla el acceso a la base de datos
     */
    public void registrarPerdida(Producto producto, int cantidad, String nota) throws DaoException {
        if (producto == null) {
            throw new IllegalArgumentException("Selecciona un producto.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (cantidad > producto.getStock_actual()) {
            throw new IllegalArgumentException(
                    "Solo hay " + producto.getStock_actual() + " unidades en stock.");
        }

        String texto = (nota == null) ? "" : nota.trim();
        MovimientoStock mov = new MovimientoStock(
                producto.getId(), producto.getNombre(), "PERDIDA", cantidad, texto);
        perdidaDao.registrarPerdida(mov, producto);
    }

    /**
     * Valida y añade stock a un producto existente como un lote nuevo.
     *
     * @param producto producto al que se añade stock
     * @param cantidad unidades que entran (mayor que 0)
     * @param caducidad fecha de caducidad del lote
     * @throws IllegalArgumentException si los datos no son válidos
     * @throws DaoException si falla el acceso a la base de datos
     */
    public void anadirStock(Producto producto, int cantidad, LocalDate caducidad) throws DaoException {
        if (producto == null) {
            throw new IllegalArgumentException("Selecciona un producto.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (caducidad == null) {
            throw new IllegalArgumentException("Indica la fecha de caducidad.");
        }
        añadirDao.anadirStock(producto.getId(), cantidad, caducidad.toString());
    }

    /**
     * Valida y crea un producto nuevo. El IVA llega en porcentaje (0-100) y se
     * guarda de 0 a 1. Devuelve el producto insertado.
     *
     * @param nombre nombre del producto (no puede repetirse)
     * @param tipo tipo del producto
     * @param categoria categoría del producto
     * @param proveedor proveedor del producto
     * @param precio precio de venta sin IVA
     * @param ivaPorcentaje IVA en porcentaje (0-100)
     * @param stockMinimo stock mínimo
     * @param cantidadInicial stock inicial (0 si no hay)
     * @param caducidad fecha de caducidad del stock inicial (obligatoria si hay stock inicial)
     * @return el producto insertado
     * @throws IllegalArgumentException si los datos no son válidos o el nombre ya existe
     * @throws DaoException si falla el acceso a la base de datos
     */
    public Producto crearProducto(String nombre, TipoProducto tipo, CategoriaProducto categoria,
            Proveedor proveedor, double precio, double ivaPorcentaje, int stockMinimo,
            int cantidadInicial, LocalDate caducidad) throws DaoException {

        String limpio = (nombre == null) ? "" : nombre.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        for (Producto existente : stockDao.listarProductos()) {
            if (existente.getNombre().equalsIgnoreCase(limpio)) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con ese nombre. Desmarca \"Es un producto nuevo\" para añadirle stock.");
            }
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona un tipo.");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Selecciona una categoría.");
        }
        if (proveedor == null) {
            throw new IllegalArgumentException("Selecciona un proveedor.");
        }
        if (ivaPorcentaje < 0 || ivaPorcentaje > 100) {
            throw new IllegalArgumentException("El IVA debe estar entre 0 y 100.");
        }
        if (cantidadInicial > 0 && caducidad == null) {
            throw new IllegalArgumentException("Indica la fecha de caducidad del stock inicial.");
        }

        List<Integer> proveedores = new ArrayList<>();
        proveedores.add(proveedor.getId());

        Producto p = new Producto();
        p.setNombre(limpio);
        p.setTipo(tipo);
        p.setCategoria(categoria);
        p.setPrecio_venta(precio);
        p.setIva(ivaPorcentaje / 100.0);
        p.setStock_minimo(stockMinimo);
        p.setStock_actual(cantidadInicial);
        p.setProveedores_id(proveedores);
        p.setActivo(true);
        if (cantidadInicial > 0) {
            p.agregarLote(cantidadInicial, caducidad.toString());
        }

        añadirDao.insertarProducto(p);
        return p;
    }

    /**
     * Calcula el valor total del stock (unidades por precio de venta).
     *
     * @param productos productos que se suman
     * @return valor total en euros
     */
    public double valorTotal(List<Producto> productos) {
        double total = 0;
        for (Producto p : productos) {
            total += p.getStock_actual() * p.getPrecio_venta();
        }
        return total;
    }

    /**
     * Cuenta los productos sin stock.
     *
     * @param productos productos que se revisan
     * @return número de productos agotados
     */
    public int contarAgotados(List<Producto> productos) {
        int n = 0;
        for (Producto p : productos) {
            if (p.getStock_actual() == 0) {
                n++;
            }
        }
        return n;
    }

    /**
     * Cuenta los productos con stock bajo (sin estar agotados).
     *
     * @param productos productos que se revisan
     * @return número de productos con stock bajo
     */
    public int contarStockBajo(List<Producto> productos) {
        int n = 0;
        for (Producto p : productos) {
            if (p.tieneStockBajo()) {
                n++;
            }
        }
        return n;
    }

    /**
     * Cuenta los productos que tienen algún lote que caduca entre hoy y dentro de
     * {@code dias} días.
     *
     * @param productos productos que se revisan
     * @param dias número de días hacia delante
     * @return número de productos que caducan pronto
     */
    public int contarCaducan(List<Producto> productos, int dias) {
        int n = 0;
        for (Producto p : productos) {
            if (caduca(p, dias)) {
                n++;
            }
        }
        return n;
    }

    private boolean caduca(Producto p, int dias) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(dias);
        for (Document lote : p.getLotes()) {
            LocalDate f = fechaDeLote(lote);
            if (f != null && !f.isBefore(hoy) && !f.isAfter(limite)) {
                return true;
            }
        }
        return false;
    }

    private LocalDate fechaDeLote(Document lote) {
        Object f = lote.get(Producto.LOTE_FECHA_CADUCIDAD);
        if (f instanceof String) {
            return LocalDate.parse((String) f);
        }
        if (f instanceof Date) {
            return ((Date) f).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }
}
