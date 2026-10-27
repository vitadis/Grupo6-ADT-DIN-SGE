package model;

/**
 * Una línea del pedido: un producto y cuántas unidades se llevan.
 *
 * @author Joel
 */
public class LineaPedido {

    private final Producto producto;
    private int cantidad;

    public LineaPedido(Producto producto) {
        this.producto = producto;
        this.cantidad = 1;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void incrementar() {
        cantidad++;
    }

    public void decrementar() {
        if (cantidad > 0) {
            cantidad--;
        }
    }

    /**
     * true si aún queda stock para añadir otra unidad.
     */
    public boolean puedeIncrementar() {
        return cantidad < producto.getStock_actual();
    }

    /**
     * Precio de venta de una unidad.
     */
    public double getPrecioUnidad() {
        return producto.productoIvaAplicado();
    }

    /**
     * Precio unidad por cantidad.
     */
    public double getSubtotal() {
        return producto.productoIvaAplicado() * cantidad;
    }
}
