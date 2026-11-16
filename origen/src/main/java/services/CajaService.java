package services;

import dao.PedidoDao;
import dao.ProductoDao;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import model.CategoriaProducto;
import model.Producto;
import model.TipoProducto;
import model.VentaPedido;

/**
 * Servicio de negocio para la gestión de productos, pedidos y operaciones del
 * módulo Caja.
 *
 * @author Joel
 */
public class CajaService {

    // ---- Dependencias ----
    private final ProductoDao productoDao;
    private final PedidoDao pedidoDao;

    /**
     * Inicializa el servicio con los DAO de productos y pedidos.
     *
     * @param productoDao acceso a datos de productos
     * @param pedidoDao acceso a datos de pedidos
     */
    public CajaService(ProductoDao productoDao, PedidoDao pedidoDao) {
        this.productoDao = productoDao;
        this.pedidoDao = pedidoDao;
    }

    // ---- Productos y categorías ----
    /**
     * Obtiene los productos de tipo venta que se encuentran activos.
     *
     * @return lista de productos activos disponibles para la venta
     */
    public List<Producto> listarProductosVenta() {
        return productoDao.listarProductos().stream()
                .filter(p -> p.getTipo() == TipoProducto.VENTA)
                .filter(Producto::isActivo)
                .collect(Collectors.toList());
    }

    /**
     * Filtra una lista de productos según la categoría indicada o devuelve
     * todos si es null.
     *
     * @param productos lista de productos a filtrar
     * @param categoria categoría por la que filtrar, o null para no filtrar
     * @return lista de productos filtrados
     */
    public List<Producto> filtrarPorCategoria(
            List<Producto> productos,
            CategoriaProducto categoria) {

        if (categoria == null) {
            return productos;
        }

        return productos.stream()
                .filter(p -> coincide(p, categoria))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve las categorías de venta que cuentan con al menos un producto
     * activo.
     *
     * @param productosVenta lista de productos activos de venta
     * @return lista de categorías con productos disponibles
     */
    public List<CategoriaProducto> categoriasDeVenta(
            List<Producto> productosVenta) {

        return Arrays.stream(CategoriaProducto.values())
                .filter(c -> c.getTipo() == TipoProducto.VENTA)
                .filter(c -> productosVenta.stream()
                .anyMatch(p -> coincide(p, c)))
                .collect(Collectors.toList());
    }

    /**
     * Valida que el producto no sea nulo y actualiza sus datos en persistencia.
     *
     * @param producto producto con los datos actualizados
     */
    public void modificarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo.");
        }
        productoDao.modificarProducto(producto);
    }

    // ---- Pedidos ----
    /**
     * Comprueba que el pedido no esté vacío, lo valida y lo registra en
     * persistencia.
     *
     * @param pedido pedido a registrar
     */
    public void realizarPedido(VentaPedido pedido) {
        if (pedido.estaVacio()) {
            throw new IllegalArgumentException("El pedido no puede estar vacio");
        }
        pedido.validar();
        pedidoDao.agregar(pedido);
    }

    /**
     * Obtiene el listado completo de pedidos registrados.
     *
     * @return lista de pedidos
     */
    public List<VentaPedido> listaPedidos() {
        List<VentaPedido> pedidos = pedidoDao.consultar();
        return pedidos;
    }

    // ---- Métodos auxiliares ----
    /**
     * Comprueba si un producto pertenece a la categoría indicada.
     *
     * @param producto producto a comprobar
     * @param categoria categoría esperada
     * @return true si coinciden, false en caso contrario
     */
    private boolean coincide(
            Producto producto,
            CategoriaProducto categoria) {

        return producto.getCategoria() == categoria;
    }
}
