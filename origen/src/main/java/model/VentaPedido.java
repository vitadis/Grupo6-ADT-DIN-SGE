package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pedido de venta de la caja: líneas de producto, estado, método de pago y total. Se guarda en
 * MongoDB con los mismos nombres de campo.
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

    /**
     * Crea un pedido vacío, en estado PENDIENTE y con la fecha de hoy.
     */
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
     * @param producto producto que se añade
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
     *
     * @param producto producto del que se quita una unidad
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
     *
     * @param producto producto cuya línea se elimina
     */
    public void quitar(Producto producto) {
        LineaPedido linea = buscar(producto);
        if (linea != null) {
            detalle.remove(linea);
            recalcularTotal();
        }
    }

    /**
     * Elimina todas las líneas del pedido.
     */
    public void limpiar() {
        detalle.clear();
        recalcularTotal();
    }

    /**
     * Indica si el pedido no tiene líneas.
     *
     * @return {@code true} si el pedido está vacío
     */
    public boolean estaVacio() {
        return detalle.isEmpty();
    }

    /**
     * Total de unidades (suma de las cantidades de todas las líneas).
     *
     * @return número total de unidades
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
     *
     * @return total del pedido con IVA
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
     *
     * @return copia de las líneas del pedido
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
     *
     * @param id identificador del pedido
     * @param usuarioId identificador del usuario que cobra
     * @param metodoPago método de pago
     * @return este mismo pedido, ya cerrado y validado
     * @throws IllegalStateException si el pedido está vacío
     */
    public VentaPedido cobrar(int id, int usuarioId, MetodoPago metodoPago) {
        return cerrar(id, usuarioId, metodoPago, EstadoPedido.COBRADO);
    }

    /**
     * Guarda el pedido como PENDIENTE (por ejemplo, una cuenta abierta).
     *
     * @param id identificador del pedido
     * @param usuarioId identificador del usuario que lo guarda
     * @param metodoPago método de pago previsto
     * @return este mismo pedido, ya validado
     * @throws IllegalStateException si el pedido está vacío
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
     *
     * @param nuevo estado al que se quiere pasar
     * @throws IllegalStateException si la transición no está permitida
     */
    public void cambiarEstado(EstadoPedido nuevo) {
        if (!estado.puedePasarA(nuevo)) {
            throw new IllegalStateException(
                    "No se puede pasar de " + estado + " a " + nuevo + ".");
        }
        this.estado = nuevo;
    }

    /**
     * Comprueba las reglas de negocio del pedido: ids positivos, fecha válida, estado y método
     * de pago obligatorios, al menos una línea válida y total igual a la suma de subtotales.
     *
     * @throws IllegalArgumentException si algún dato no es válido
     */
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
    /**
     * Devuelve el identificador del pedido.
     *
     * @return el identificador del pedido
     */
    public int getId() {
        return id;
    }

    /**
     * Asigna el identificador del pedido.
     *
     * @param id el identificador del pedido
     */
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo.");
        }
        this.id = id;
    }

    /**
     * Devuelve el identificador del usuario que registró el pedido.
     *
     * @return el identificador del usuario que registró el pedido
     */
    public int getUsuario_id() {
        return usuario_id;
    }

    /**
     * Asigna el identificador del usuario que registró el pedido.
     *
     * @param usuario_id el identificador del usuario que registró el pedido
     */
    public void setUsuario_id(int usuario_id) {
        if (usuario_id <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser positivo.");
        }
        this.usuario_id = usuario_id;
    }

    /**
     * Devuelve la fecha del pedido (yyyy-MM-dd).
     *
     * @return la fecha del pedido (yyyy-MM-dd)
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Asigna la fecha del pedido (yyyy-MM-dd).
     *
     * @param fecha la fecha del pedido (yyyy-MM-dd)
     */
    public void setFecha(String fecha) {
        validarFecha(fecha);
        this.fecha = fecha;
    }

    /**
     * Devuelve el estado del pedido.
     *
     * @return el estado del pedido
     */
    public EstadoPedido getEstado() {
        return estado;
    }

    // No comprueba transiciones para poder cargar cualquier estado de Mongo.
    // Desde la aplicación usa cambiarEstado().
    /**
     * Asigna el estado del pedido.
     *
     * @param estado el estado del pedido
     */
    public void setEstado(EstadoPedido estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado es obligatorio.");
        }
        this.estado = estado;
    }

    /**
     * Devuelve el método de pago del pedido.
     *
     * @return el método de pago del pedido
     */
    public MetodoPago getMetodo_pago() {
        return metodo_pago;
    }

    /**
     * Asigna el método de pago del pedido.
     *
     * @param metodo_pago el método de pago del pedido
     */
    public void setMetodo_pago(MetodoPago metodo_pago) {
        if (metodo_pago == null) {
            throw new IllegalArgumentException("El método de pago es obligatorio.");
        }
        this.metodo_pago = metodo_pago;
    }

    /**
     * Devuelve las líneas del pedido.
     *
     * @return las líneas del pedido
     */
    public List<LineaPedido> getDetalle() {
        return new ArrayList<>(detalle);
    }

    /**
     * Asigna las líneas del pedido.
     *
     * @param detalle las líneas del pedido
     */
    public void setDetalle(List<LineaPedido> detalle) {
        this.detalle = detalle != null ? new ArrayList<>(detalle) : new ArrayList<>();
    }

    /**
     * Devuelve el total del pedido sin IVA.
     *
     * @return el total del pedido sin IVA
     */
    public double getTotal() {
        return total;
    }

    /**
     * Asigna el total del pedido sin IVA.
     *
     * @param total el total del pedido sin IVA
     */
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
