package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joel
 */
public class VentaPedido {

    private int id;
    private int usuario_id;
    private String fecha; // yyyy-MM-dd
    private EstadoPedido estado;
    private MetodoPago metodo_pago;
    private List<LineaPedido> detalle;
    private double total;

    public VentaPedido() {
        this.detalle = new ArrayList<>();
        this.fecha = LocalDate.now().toString();
        this.estado = EstadoPedido.PENDIENTE;
    }

    // =========================================================================
    // ================== MÉTODOS DE LA CAJA ===================================
    // =========================================================================
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
            detalle.add(new LineaPedido(producto));
        } else {
            linea.incrementar();
        }
        recalcularTotal();
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
            detalle.remove(linea);
        }
        recalcularTotal();
    }

    /**
     * Elimina la línea entera, sea cual sea su cantidad.
     */
    public void quitar(Producto producto) {
        LineaPedido linea = buscar(producto);
        if (linea != null) {
            detalle.remove(linea);
            recalcularTotal();
        }
    }

    public void limpiar() {
        detalle.clear();
        recalcularTotal();
    }

    public boolean estaVacio() {
        return detalle.isEmpty();
    }

    /**
     * Total de unidades (suma de las cantidades de todas las líneas).
     */
    public int numProductos() {
        int suma = 0;
        for (LineaPedido l : detalle) {
            suma += l.getCantidad();
        }
        return suma;
    }

    /**
     * Total con IVA (el de la pantalla de caja).
     */
    public double totalConIva() {
        double suma = 0;
        for (LineaPedido l : detalle) {
            suma += l.subtotalConIva();
        }
        return suma;
    }

    /**
     * Copia de la lista, para recorrerla mientras se modifica el pedido.
     */
    public List<LineaPedido> lineas() {
        return new ArrayList<>(detalle);
    }

    // Dos productos son el mismo si tienen el mismo id
    private LineaPedido buscar(Producto producto) {
        for (LineaPedido l : detalle) {
            if (l.getProducto_id() == producto.getId()) {
                return l;
            }
        }
        return null;
    }

    // =========================================================================
    // ========================== REGLAS DE NEGOCIO ============================
    // =========================================================================
    /**
     * Cierra el pedido como COBRADO con fecha de hoy. El id lo generas tú
     * (siguiente _id de la colección). Devuelve este mismo objeto, listo para
     * insertar.
     */
    public VentaPedido cobrar(int id, int usuarioId, MetodoPago metodoPago) {
        return cerrar(id, usuarioId, metodoPago, EstadoPedido.COBRADO);
    }

    /**
     * Guarda el pedido como PENDIENTE (por ejemplo, una cuenta abierta).
     */
    public VentaPedido guardarPendiente(int id, int usuarioId, MetodoPago metodoPago) {
        return cerrar(id, usuarioId, metodoPago, EstadoPedido.PENDIENTE);
    }

    private VentaPedido cerrar(int id, int usuarioId, MetodoPago metodoPago,
            EstadoPedido estado) {
        if (estaVacio()) {
            throw new IllegalStateException("No se puede guardar un pedido vacío.");
        }
        setId(id);
        setUsuario_id(usuarioId);
        setFecha(LocalDate.now().toString());
        setMetodo_pago(metodoPago);
        setEstado(estado);
        recalcularTotal();
        validar();
        return this;
    }

    /**
     * Cambia el estado respetando las transiciones permitidas.
     */
    public void cambiarEstado(EstadoPedido nuevo) {
        if (!estado.puedePasarA(nuevo)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + estado + " a " + nuevo + ".");
        }
        this.estado = nuevo;
    }

    public void validar() {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo.");
        }
        if (usuario_id <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser positivo.");
        }
        validarFecha(fecha);
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        if (metodo_pago == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        if (detalle.isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos una línea.");
        }
        for (LineaPedido l : detalle) {
            l.validar();
        }
        if (!Double.isFinite(total) || total < 0) {
            throw new IllegalArgumentException("El total debe ser válido y no negativo.");
        }
        if (Math.abs(total - calcularTotalSinIva()) > 0.01) {
            throw new IllegalArgumentException("El total no coincide con la suma de subtotales.");
        }
    }

    private double calcularTotalSinIva() {
        double suma = 0;
        for (LineaPedido l : detalle) {
            suma += l.getSubtotal();
        }
        return Math.round(suma * 100.0) / 100.0;
    }

    private void recalcularTotal() {
        this.total = calcularTotalSinIva();
    }

    private void validarFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La fecha debe tener formato yyyy-MM-dd.");
        }
    }

    // =========================================================================
    // GETTERS Y SETTERS
    // =========================================================================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo.");
        }
        this.id = id;
    }

    public int getUsuario_id() {
        return usuario_id;
    }

    public void setUsuario_id(int usuario_id) {
        if (usuario_id <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser positivo.");
        }
        this.usuario_id = usuario_id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        validarFecha(fecha);
        this.fecha = fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    // No comprueba transiciones para poder cargar cualquier estado de Mongo.
    // Desde la aplicación usa cambiarEstado().
    public void setEstado(EstadoPedido estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        this.estado = estado;
    }

    public MetodoPago getMetodo_pago() {
        return metodo_pago;
    }

    public void setMetodo_pago(MetodoPago metodo_pago) {
        if (metodo_pago == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        this.metodo_pago = metodo_pago;
    }

    public List<LineaPedido> getDetalle() {
        return new ArrayList<>(detalle);
    }

    public void setDetalle(List<LineaPedido> detalle) {
        this.detalle = detalle != null ? new ArrayList<>(detalle) : new ArrayList<>();
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        if (!Double.isFinite(total) || total < 0) {
            throw new IllegalArgumentException("El total debe ser válido y no negativo.");
        }
        this.total = total;
    }

    @Override
    public String toString() {
        return "VentaPedido{" + "id=" + id + ", usuario_id=" + usuario_id + ", fecha=" + fecha + ", estado=" + estado + ", metodo_pago=" + metodo_pago + ", detalle=" + detalle + ", total=" + total + '}';
    }
    
    
}
