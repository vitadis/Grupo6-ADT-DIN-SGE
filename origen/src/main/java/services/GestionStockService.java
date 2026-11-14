/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.GestionStockDao;
import dao.implement.GestionStockDaoImp;
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
 *
 * @author Hodei.Torres
 */
public class GestionStockService {

    private final GestionStockDao stockDao = GestionStockDaoImp.getInstance();

    public List<Producto> listarProductos() throws DaoException {
        return stockDao.listarProductos();
    }

    public List<Proveedor> listarProveedoresActivos() throws DaoException {
        return stockDao.listarActivos();
    }

    /** Categorías que pertenecen a un tipo (Venta / Ingrediente). */
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
        stockDao.registrarPerdida(mov, producto);
    }

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
        stockDao.anadirStock(producto.getId(), cantidad, caducidad.toString());
    }

    /**
     * Valida y crea un producto nuevo. El IVA llega en porcentaje (0-100) y se
     * guarda de 0 a 1. Devuelve el producto insertado.
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

        // El tipo antes que la categoría (el setter las compara)
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

        stockDao.insertarProducto(p);
        return p;
    }

    public double valorTotal(List<Producto> productos) {
        double total = 0;
        for (Producto p : productos) {
            total += p.getStock_actual() * p.getPrecio_venta();
        }
        return total;
    }

    public int contarAgotados(List<Producto> productos) {
        int n = 0;
        for (Producto p : productos) {
            if (p.getStock_actual() == 0) {
                n++;
            }
        }
        return n;
    }

    public int contarStockBajo(List<Producto> productos) {
        int n = 0;
        for (Producto p : productos) {
            if (p.tieneStockBajo()) {
                n++;
            }
        }
        return n;
    }

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
