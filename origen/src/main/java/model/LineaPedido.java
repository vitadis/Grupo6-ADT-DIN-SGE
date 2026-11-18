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

    /**
     * Constructor vacío, obligatorio para el codec POJO de MongoDB.
     */
    public LineaPedido() {
    }

    /**
     * Crea una línea con una unidad del producto indicado, copiando su id, nombre, precio e
     * IVA.
     *
     * @param producto producto de la línea
     */
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
    /**
     * Devuelve el producto en memoria asociado a la línea (no se guarda en MongoDB).
     *
     * @return producto de la línea, o {@code null} si la línea se cargó de MongoDB
     */
    public Producto producto() {
        return producto;
    }

    /**
     * Descuenta del stock del producto las unidades de la línea.
     *
     * @return el mismo producto con el stock ya modificado
     */
    public Producto productoModificadoStock() {
        Producto p = producto;
        p.setStock_actual(p.getStock_actual() - cantidad);
        return p;
    }

    /**
     * Suma una unidad a la línea y recalcula el subtotal.
     */
    public void incrementar() {
        cantidad++;
        recalcular();
    }

    /**
     * Resta una unidad a la línea (si quedan) y recalcula el subtotal.
     */
    public void decrementar() {
        if (cantidad > 0) {
            cantidad--;
            recalcular();
        }
    }

    /**
     * true si aún queda stock para añadir otra unidad.
     *
     * @return {@code true} si se puede añadir otra unidad
     */
    public boolean puedeIncrementar() {
        return producto != null && cantidad < producto.getStock_actual();
    }

    /**
     * Precio de venta de una unidad (con IVA).
     *
     * @return precio unitario con IVA
     */
    public double precioUnidad() {
        return precio_unitario + (iva * precio_unitario);
    }

    /**
     * Precio unidad por cantidad (con IVA).
     *
     * @return subtotal de la línea con IVA
     */
    public double subtotalConIva() {
        return precioUnidad() * cantidad;
    }

    // =========================================================================
    // ============================ REGLAS DE NEGOCIO ==========================
    // =========================================================================
    /**
     * Comprueba que el id, el nombre, la cantidad, el precio, el IVA y el subtotal son
     * válidos y que el subtotal coincide con precio por cantidad.
     *
     * @throws IllegalArgumentException si algún dato no es válido
     */
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
    /**
     * Devuelve el identificador del producto de la línea.
     *
     * @return el identificador del producto de la línea
     */
    public int getProducto_id() {
        return producto_id;
    }

    /**
     * Asigna el identificador del producto de la línea.
     *
     * @param producto_id el identificador del producto de la línea
     */
    public void setProducto_id(int producto_id) {
        if (producto_id <= 0) {
            throw new IllegalArgumentException("El ID del producto debe ser positivo.");
        }
        this.producto_id = producto_id;
    }

    /**
     * Devuelve el nombre del producto de la línea.
     *
     * @return el nombre del producto de la línea
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre del producto de la línea.
     *
     * @param nombre el nombre del producto de la línea
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        this.nombre = nombre;
    }

    /**
     * Devuelve las unidades de la línea.
     *
     * @return las unidades de la línea
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Asigna las unidades de la línea.
     *
     * @param cantidad las unidades de la línea
     */
    public void setCantidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser positiva.");
        }
        this.cantidad = cantidad;
    }

    /**
     * Devuelve el precio unitario sin IVA.
     *
     * @return el precio unitario sin IVA
     */
    public double getPrecio_unitario() {
        return precio_unitario;
    }

    /**
     * Asigna el precio unitario sin IVA.
     *
     * @param precio_unitario el precio unitario sin IVA
     */
    public void setPrecio_unitario(double precio_unitario) {
        if (!Double.isFinite(precio_unitario) || precio_unitario < 0) {
            throw new IllegalArgumentException("El precio unitario debe ser válido y no negativo.");
        }
        this.precio_unitario = precio_unitario;
    }

    /**
     * Devuelve el IVA aplicado (entre 0 y 1).
     *
     * @return el IVA aplicado (entre 0 y 1)
     */
    public double getIva() {
        return iva;
    }

    /**
     * Asigna el IVA aplicado (entre 0 y 1).
     *
     * @param iva el IVA aplicado (entre 0 y 1)
     */
    public void setIva(double iva) {
        if (!Double.isFinite(iva) || iva < 0 || iva > 1) {
            throw new IllegalArgumentException("El IVA debe estar entre 0 y 1.");
        }
        this.iva = iva;
    }

    /**
     * Devuelve el subtotal de la línea sin IVA.
     *
     * @return el subtotal de la línea sin IVA
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Asigna el subtotal de la línea sin IVA.
     *
     * @param subtotal el subtotal de la línea sin IVA
     */
    public void setSubtotal(double subtotal) {
        if (!Double.isFinite(subtotal) || subtotal < 0) {
            throw new IllegalArgumentException("El subtotal debe ser válido y no negativo.");
        }
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "LineaPedido{" + "producto_id=" + producto_id + ", nombre=" + nombre + ", cantidad=" + cantidad + ", precio_unitario=" + precio_unitario + ", iva=" + iva + ", subtotal=" + subtotal + ", producto=" + producto + '}';
    }

}
