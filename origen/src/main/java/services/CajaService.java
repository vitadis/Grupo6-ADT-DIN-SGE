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

public class CajaService {

    private final ProductoDao productoDao;
    private final PedidoDao pedidoDao;

    public CajaService(ProductoDao productoDao, PedidoDao pedidoDao) {
        this.productoDao = productoDao;
        this.pedidoDao = pedidoDao;
    }

    /**
     * Obtiene los productos de venta activos.
     */
    public List<Producto> listarProductosVenta() {
        return productoDao.listarProductos().stream()
                .filter(p -> p.getTipo() == TipoProducto.VENTA)
                .filter(Producto::isActivo)
                .collect(Collectors.toList());
    }

    /**
     * Filtra por categoría. Si es null, devuelve todos.
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
     * Devuelve las categorías de venta que tienen productos activos.
     */
    public List<CategoriaProducto> categoriasDeVenta(
            List<Producto> productosVenta) {

        return Arrays.stream(CategoriaProducto.values())
                .filter(c -> c.getTipo() == TipoProducto.VENTA)
                .filter(c -> productosVenta.stream()
                .anyMatch(p -> coincide(p, c)))
                .collect(Collectors.toList());
    }

    // modificación
    public void modificarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo.");
        }
        productoDao.modificarProducto(producto);
    }

    public void realizarPedido(VentaPedido pedido) {
        if (pedido.estaVacio()) {
            throw new IllegalArgumentException("El pedido no puede estar vacio");
        }
        pedido.validar();
        pedidoDao.agregar(pedido);
    }

    public List<VentaPedido> listaPedidos() {
        List<VentaPedido> pedidos = pedidoDao.consultar();
        return pedidos;
    }

    // metodos auxiliares
    private boolean coincide(
            Producto producto,
            CategoriaProducto categoria) {

        return producto.getCategoria() == categoria;
    }
}
