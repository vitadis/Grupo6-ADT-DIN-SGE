package services;

import dao.ProductoDao;
import dao.implement.ProductoDaoImp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import model.CategoriaProducto;
import model.Producto;
import model.TipoProducto;

public class ProductoService {

    private final ProductoDao productoDao = ProductoDaoImp.getInstance();

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

    private boolean coincide(
            Producto producto,
            CategoriaProducto categoria) {

        return producto.getCategoria() == categoria;
    }
}
