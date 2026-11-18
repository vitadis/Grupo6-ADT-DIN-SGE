package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Un producto de la lista de compra sugerida: qué producto comprar, cuántas unidades y, si se
 * pide para elaborar otros productos, para cuáles (por ejemplo, barras de pan para el bocadillo
 * de jamón).
 *
 * Solo se usa en memoria, no se guarda en MongoDB.
 *
 * @author anazk
 */
public class ProductoSugerido {

    /**
     * Producto que hay que comprar.
     */
    private final Producto producto;
    /**
     * Unidades que hay que pedir.
     */
    private final int cantidad;
    /**
     * Nombres de los productos con receta para los que hace falta este ingrediente.
     */
    private final List<String> paraRecetas;

    /**
     * Crea una sugerencia.
     *
     * @param producto producto que hay que comprar
     * @param cantidad unidades que hay que pedir
     * @param paraRecetas productos con receta que lo necesitan (puede estar vacía)
     */
    public ProductoSugerido(Producto producto, int cantidad, List<String> paraRecetas) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.paraRecetas = paraRecetas == null ? new ArrayList<String>() : new ArrayList<>(paraRecetas);
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public List<String> getParaRecetas() {
        return new ArrayList<>(paraRecetas);
    }

    @Override
    public String toString() {
        return "ProductoSugerido{" + "producto=" + producto.getNombre() + ", cantidad=" + cantidad
                + ", paraRecetas=" + paraRecetas + '}';
    }
}
