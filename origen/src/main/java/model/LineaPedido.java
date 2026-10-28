package model;

/**
 * Una línea del pedido: un producto y cuántas unidades se llevan. Es también el
 * elemento de "detalle" en Mongo.
 *
 * @author Joel
 */
public class LineaPedido {

    // Atributos que se guardan en Mongo (mismos nombres que el JSON)
    private int producto_id;
    private String nombre;
    private int cantidad;
    private double precio_unitario; // sin IVA
    private double iva;
    private double subtotal;        // sin IVA

    // Solo en memoria no se guarda en las collections
    private transient Producto producto;

    // Obligatorio para el POJO codec
    public LineaPedido() {
    }

    public LineaPedido(Producto producto) {
        this.producto = producto;
        this.producto_id = producto.getId();
        this.nombre = producto.getNombre();
        this.precio_unitario = producto.getPrecio_venta();
        this.iva = producto.getIva();
        this.cantidad = 1;
        recalcular();
    }

    // =========================================================================
    // MÉTODOS DE LA CAJA
    // =========================================================================
    public Producto producto() {
        return producto;
    }

    public void incrementar() {
        cantidad++;
        recalcular();
    }

    public void decrementar() {
        if (cantidad > 0) {
            cantidad--;
            recalcular();
        }
    }

    /**
     * true si aún queda stock para añadir otra unidad.
     */
    public boolean puedeIncrementar() {
        return producto != null && cantidad < producto.getStock_actual();
    }

    /**
     * Precio de venta de una unidad (con IVA).
     */
    public double precioUnidad() {
        return precio_unitario + (iva * precio_unitario);
    }

    /**
     * Precio unidad por cantidad (con IVA).
     */
    public double subtotalConIva() {
        return precioUnidad() * cantidad;
    }

    // =========================================================================
    // ============================ REGLAS DE NEGOCIO ==========================
    // =========================================================================
    public void validar() {
        if (producto_id <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser positivo.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva.");
        }
        if (!Double.isFinite(precio_unitario) || precio_unitario < 0) {
            throw new IllegalArgumentException("El precio unitario debe ser válido y no negativo.");
        }
        if (!Double.isFinite(iva) || iva < 0 || iva > 1) {
            throw new IllegalArgumentException("El IVA debe estar entre 0 y 1.");
        }
        if (!Double.isFinite(subtotal) || subtotal < 0) {
            throw new IllegalArgumentException("El subtotal debe ser válido y no negativo.");
        }
        if (Math.abs(subtotal - redondear(precio_unitario * cantidad)) > 0.01) {
            throw new IllegalArgumentException("El subtotal no coincide con precio x cantidad.");
        }
    }

    private void recalcular() {
        this.subtotal = redondear(precio_unitario * cantidad);
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    // =========================================================================
    // ========================= GETTERS Y SETTERS =============================
    // =========================================================================
    public int getProducto_id() {
        return producto_id;
    }

    public void setProducto_id(int producto_id) {
        if (producto_id <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser positivo.");
        }
        this.producto_id = producto_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva.");
        }
        this.cantidad = cantidad;
    }

    public double getPrecio_unitario() {
        return precio_unitario;
    }

    public void setPrecio_unitario(double precio_unitario) {
        if (!Double.isFinite(precio_unitario) || precio_unitario < 0) {
            throw new IllegalArgumentException("El precio unitario debe ser válido y no negativo.");
        }
        this.precio_unitario = precio_unitario;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        if (!Double.isFinite(iva) || iva < 0 || iva > 1) {
            throw new IllegalArgumentException("El IVA debe estar entre 0 y 1.");
        }
        this.iva = iva;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        if (!Double.isFinite(subtotal) || subtotal < 0) {
            throw new IllegalArgumentException("El subtotal debe ser válido y no negativo.");
        }
        this.subtotal = subtotal;
    }
}
