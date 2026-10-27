package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Pedido en curso de la caja: lista de líneas (producto + cantidad).
 *
 * @author Joel
 */
public class VentaPedido {

    private final List<LineaPedido> lineas = new ArrayList<>();

    /**
     * Añade una unidad. Si el producto ya está en el pedido sube su cantidad;
     * si no, crea una línea nueva. No permite superar el stock disponible.
     *
     * @return true si se añadió, false si no hay stock suficiente
     */
    public boolean agregar(Producto producto) {
        if (producto == null) {
            return false;
        }
        LineaPedido linea = buscar(producto);
        int enPedido = (linea == null) ? 0 : linea.getCantidad();

        if (enPedido >= producto.getStock_actual()) {
            return false;
        }

        if (linea == null) {
            lineas.add(new LineaPedido(producto));
        } else {
            linea.incrementar();
        }
        return true;
    }

    /**
     * Quita una unidad. Si la cantidad llega a 0 se elimina la línea.
     */
    public void restar(Producto producto) {
        LineaPedido linea = buscar(producto);
        if (linea == null) {
            return;
        }
        linea.decrementar();
        if (linea.getCantidad() <= 0) {
            lineas.remove(linea);
        }
    }

    /**
     * Elimina la línea entera, sea cual sea su cantidad.
     */
    public void quitar(Producto producto) {
        LineaPedido linea = buscar(producto);
        if (linea != null) {
            lineas.remove(linea);
        }
    }

    public void limpiar() {
        lineas.clear();
    }

    public boolean estaVacio() {
        return lineas.isEmpty();
    }

    /**
     * Total de unidades (suma de las cantidades de todas las líneas).
     */
    public int getNumProductos() {
        int total = 0;
        for (LineaPedido l : lineas) {
            total += l.getCantidad();
        }
        return total;
    }

    public double getTotal() {
        double total = 0;
        for (LineaPedido l : lineas) {
            total += l.getSubtotal();
        }
        return total;
    }

    /**
     * Copia de la lista, para recorrerla mientras se modifica el pedido.
     */
    public List<LineaPedido> getLineas() {
        return new ArrayList<>(lineas);
    }

    // Dos productos son el mismo si tienen el mismo id
    private LineaPedido buscar(Producto producto) {
        for (LineaPedido l : lineas) {
            if (l.getProducto().getId() == producto.getId()) {
                return l;
            }
        }
        return null;
    }
}
