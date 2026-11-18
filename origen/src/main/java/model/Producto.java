package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

/**
 * Producto del inventario, ya sea de venta o ingrediente. Guarda su precio, IVA, stock, lotes
 * con fecha de caducidad, receta (si se elabora con otros productos) y proveedores.
 */
public class Producto {

    // =========================================================================
    // ============================= CLAVES BSON ===============================
    // =========================================================================
    // Claves de los documentos de lotes
    /** Clave de la cantidad en un documento de lote. */
    public static final String LOTE_CANTIDAD = "cantidad";
    /** Clave de la fecha de caducidad en un documento de lote. */
    public static final String LOTE_FECHA_CADUCIDAD = "fecha_caducidad";

    // Claves de los documentos de receta
    /** Clave del id del ingrediente en un documento de receta. */
    public static final String RECETA_PRODUCTO_ID = "producto_id";
    /** Clave de la cantidad del ingrediente en un documento de receta. */
    public static final String RECETA_CANTIDAD = "cantidad";
    /** Clave de la unidad de medida del ingrediente en un documento de receta. */
    public static final String RECETA_UNIDAD = "unidad";

    // =======================================================================
    // ============================== ATRIBUTOS ==============================
    // =======================================================================
    private int id;
    private String nombre;
    private TipoProducto tipo;
    private CategoriaProducto categoria;
    private double precio_venta;
    private double iva;

    // Stock independiente en Java
    private int stock_actual;
    private int stock_minimo;

    // Documentos BSON anidados
    private List<Document> lotes;
    private List<Document> receta;

    private List<Integer> proveedores_id;
    private boolean activo;

    // imagen
    private String imagen;

    // =========================================================================
    // =============================== CONSTRUCTORES ===========================
    // =========================================================================
    /**
     * Constructor vacío, necesario para el codec POJO de MongoDB. Inicializa las listas de
     * lotes, receta y proveedores vacías.
     */
    public Producto() {
        this.lotes = new ArrayList<>();
        this.receta = new ArrayList<>();
        this.proveedores_id = new ArrayList<>();
    }

    /**
     * Crea un producto con todos sus datos y lo valida.
     *
     * @param id identificador del producto
     * @param nombre nombre del producto
     * @param tipo tipo del producto
     * @param categoria categoría (debe corresponder al tipo)
     * @param precio_venta precio de venta sin IVA
     * @param iva IVA entre 0 y 1
     * @param stock_actual unidades en stock
     * @param stock_minimo stock mínimo
     * @param lotes lotes con cantidad y fecha de caducidad
     * @param receta ingredientes de la receta
     * @param proveedores_id identificadores de los proveedores
     * @param activo si el producto está activo
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Producto(
            int id,
            String nombre,
            TipoProducto tipo,
            CategoriaProducto categoria,
            double precio_venta,
            double iva,
            int stock_actual,
            int stock_minimo,
            List<Document> lotes,
            List<Document> receta,
            List<Integer> proveedores_id,
            boolean activo) {

        this();

        setId(id);
        setNombre(nombre);
        setTipo(tipo);
        setCategoria(categoria);
        setPrecio_venta(precio_venta);
        setIva(iva);
        setStock_actual(stock_actual);
        setStock_minimo(stock_minimo);
        setLotes(lotes);
        setReceta(receta);
        setProveedores_id(proveedores_id);
        setActivo(activo);

        validar();
    }

    // =========================================================================
    // =============================== REGLAS DE NEGOCIO =======================
    // =========================================================================
    /**
     * Comprueba todas las reglas de negocio del producto: datos obligatorios, que la categoría
     * corresponde al tipo, precios, IVA y stock no negativos, y que los lotes, la receta y los
     * proveedores son válidos.
     *
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public void validar() {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser positivo.");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio.");
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de producto es obligatorio.");
        }

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "La categoría del producto es obligatoria.");
        }

        if (categoria.getTipo() != tipo) {
            throw new IllegalArgumentException(
                    "La categoría no corresponde al tipo de producto.");
        }

        if (!Double.isFinite(precio_venta) || precio_venta < 0) {
            throw new IllegalArgumentException(
                    "El precio de venta debe ser válido y no negativo.");
        }

        if (!Double.isFinite(iva) || iva < 0 || iva > 1) {
            throw new IllegalArgumentException(
                    "El IVA debe estar entre 0 y 1.");
        }

        if (stock_actual < 0) {
            throw new IllegalArgumentException(
                    "El stock actual no puede ser negativo.");
        }

        if (stock_minimo < 0) {
            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo.");
        }

        validarLotes();
        validarReceta();
        validarProveedores();
    }

    // =========================================================================
    // =============================== GESTIÓN DE LOTES ========================
    // =========================================================================
    /**
     * Añade un lote al producto.
     *
     * @param cantidad unidades del lote (positiva)
     * @param fecha_caducidad fecha de caducidad con formato yyyy-MM-dd
     * @throws IllegalArgumentException si la cantidad o la fecha no son válidas
     */
    public void agregarLote(int cantidad, String fecha_caducidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad del lote debe ser positiva.");
        }

        validarFecha(fecha_caducidad);

        Document lote = new Document(LOTE_CANTIDAD, cantidad)
                .append(LOTE_FECHA_CADUCIDAD, fecha_caducidad);

        lotes.add(lote);
    }

    private void validarLotes() {

        for (Document lote : lotes) {

            if (lote == null) {
                throw new IllegalArgumentException(
                        "La lista de lotes no puede contener valores nulos.");
            }

            int cantidad = obtenerEntero(lote, LOTE_CANTIDAD);

            if (cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad del lote debe ser positiva.");
            }

            Object fecha = lote.get(LOTE_FECHA_CADUCIDAD);

            // Se acepta también java.util.Date (así lo devuelve Mongo si se
            // guardó como fecha); solo se valida el formato si es texto.
            if (fecha instanceof String) {
                validarFecha((String) fecha);
            } else if (!(fecha instanceof java.util.Date)) {
                throw new IllegalArgumentException(
                        "La fecha de caducidad debe ser una cadena o una fecha.");
            }
        }
    }

    private void validarFecha(String fecha) {

        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException(
                    "La fecha de caducidad es obligatoria.");
        }

        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "La fecha debe tener formato yyyy-MM-dd.");
        }
    }

    // =========================================================================
    // =============================== GESTIÓN DE RECETAS ======================
    // =========================================================================
    /**
     * Añade un ingrediente a la receta del producto.
     *
     * @param producto_id identificador del producto ingrediente
     * @param cantidad cantidad necesaria (positiva)
     * @param unidad unidad de medida de la cantidad
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public void agregarIngredienteReceta(
            int producto_id,
            double cantidad,
            String unidad) {

        if (producto_id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del ingrediente debe ser positivo.");
        }

        if (!Double.isFinite(cantidad) || cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad del ingrediente debe ser positiva.");
        }

        if (unidad == null || unidad.isBlank()) {
            throw new IllegalArgumentException(
                    "La unidad del ingrediente es obligatoria.");
        }

        Document ingrediente = new Document(
                RECETA_PRODUCTO_ID, producto_id)
                .append(RECETA_CANTIDAD, cantidad)
                .append(RECETA_UNIDAD, unidad);

        receta.add(ingrediente);
    }

    private void validarReceta() {

        for (Document ingrediente : receta) {

            if (ingrediente == null) {
                throw new IllegalArgumentException(
                        "La receta no puede contener valores nulos.");
            }

            int productoId = obtenerEntero(
                    ingrediente, RECETA_PRODUCTO_ID);

            if (productoId <= 0) {
                throw new IllegalArgumentException(
                        "El ID del ingrediente debe ser positivo.");
            }

            Object cantidadObj = ingrediente.get(RECETA_CANTIDAD);

            if (!(cantidadObj instanceof Number)) {
                throw new IllegalArgumentException(
                        "La cantidad del ingrediente debe ser numérica.");
            }

            double cantidad = ((Number) cantidadObj).doubleValue();

            if (!Double.isFinite(cantidad) || cantidad <= 0) {
                throw new IllegalArgumentException(
                        "La cantidad del ingrediente debe ser positiva.");
            }

            Object unidad = ingrediente.get(RECETA_UNIDAD);

            if (!(unidad instanceof String)
                    || ((String) unidad).isBlank()) {

                throw new IllegalArgumentException(
                        "La unidad del ingrediente es obligatoria.");
            }
        }
    }

    // =========================================================================
    // =============================== GESTIÓN DE PROVEEDORES ==================
    // =========================================================================
    private void validarProveedores() {

        for (Integer proveedorId : proveedores_id) {

            if (proveedorId == null || proveedorId <= 0) {
                throw new IllegalArgumentException(
                        "Los IDs de proveedores deben ser positivos.");
            }
        }
    }

    // =========================================================================
    // =========================== UTILIDADES DE VALIDACIÓN BSON ===============
    // =========================================================================
    private int obtenerEntero(Document documento, String clave) {

        Object valor = documento.get(clave);

        if (!(valor instanceof Number)) {
            throw new IllegalArgumentException(
                    "El campo '" + clave + "' debe ser numérico.");
        }

        double numero = ((Number) valor).doubleValue();

        if (!Double.isFinite(numero)
                || numero != Math.rint(numero)
                || numero < Integer.MIN_VALUE
                || numero > Integer.MAX_VALUE) {

            throw new IllegalArgumentException(
                    "El campo '" + clave + "' debe ser un entero válido.");
        }

        return (int) numero;
    }

    // =========================================================================
    // =============================== GETTERS Y SETTERS =======================
    // =========================================================================
    /**
     * Devuelve el identificador del producto.
     *
     * @return el identificador del producto
     */
    public int getId() {
        return id;
    }

    /**
     * Asigna el identificador del producto.
     *
     * @param id el identificador del producto
     */
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser positivo.");
        }
        this.id = id;
    }

    /**
     * Devuelve el nombre del producto.
     *
     * @return el nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre del producto.
     *
     * @param nombre el nombre del producto
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }
        this.nombre = nombre;
    }

    /**
     * Devuelve el tipo del producto.
     *
     * @return el tipo del producto
     */
    public TipoProducto getTipo() {
        return tipo;
    }

    /**
     * Asigna el tipo del producto.
     *
     * @param tipo el tipo del producto
     */
    public void setTipo(TipoProducto tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo es obligatorio.");
        }

        if (categoria != null && categoria.getTipo() != tipo) {
            throw new IllegalArgumentException(
                    "El tipo no corresponde a la categoría actual.");
        }

        this.tipo = tipo;
    }

    /**
     * Devuelve la categoría del producto.
     *
     * @return la categoría del producto
     */
    public CategoriaProducto getCategoria() {
        return categoria;
    }

    /**
     * Asigna la categoría del producto.
     *
     * @param categoria la categoría del producto
     */
    public void setCategoria(CategoriaProducto categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria.");
        }

        if (tipo != null && categoria.getTipo() != tipo) {
            throw new IllegalArgumentException(
                    "La categoría no corresponde al tipo actual.");
        }

        this.categoria = categoria;
    }

    /**
     * Devuelve el precio de venta sin IVA.
     *
     * @return el precio de venta sin IVA
     */
    public double getPrecio_venta() {
        return precio_venta;
    }

    /**
     * Asigna el precio de venta sin IVA.
     *
     * @param precio_venta el precio de venta sin IVA
     */
    public void setPrecio_venta(double precio_venta) {
        if (!Double.isFinite(precio_venta) || precio_venta < 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser válido y no negativo.");
        }
        this.precio_venta = precio_venta;
    }

    /**
     * Devuelve el IVA del producto (entre 0 y 1).
     *
     * @return el IVA del producto (entre 0 y 1)
     */
    public double getIva() {
        return iva;
    }

    /**
     * Asigna el IVA del producto (entre 0 y 1).
     *
     * @param iva el IVA del producto (entre 0 y 1)
     */
    public void setIva(double iva) {
        if (!Double.isFinite(iva) || iva < 0 || iva > 1) {
            throw new IllegalArgumentException(
                    "El IVA debe estar entre 0 y 1.");
        }
        this.iva = iva;
    }

    /**
     * Devuelve las unidades en stock.
     *
     * @return las unidades en stock
     */
    public int getStock_actual() {
        return stock_actual;
    }

    /**
     * Asigna las unidades en stock.
     *
     * @param stock_actual las unidades en stock
     */
    public void setStock_actual(int stock_actual) {
        if (stock_actual < 0) {
            throw new IllegalArgumentException(
                    "El stock actual no puede ser negativo.");
        }
        this.stock_actual = stock_actual;
    }

    /**
     * Devuelve el stock mínimo antes de avisar de reposición.
     *
     * @return el stock mínimo antes de avisar de reposición
     */
    public int getStock_minimo() {
        return stock_minimo;
    }

    /**
     * Asigna el stock mínimo antes de avisar de reposición.
     *
     * @param stock_minimo el stock mínimo antes de avisar de reposición
     */
    public void setStock_minimo(int stock_minimo) {
        if (stock_minimo < 0) {
            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo.");
        }
        this.stock_minimo = stock_minimo;
    }

    /**
     * Devuelve la lista de lotes (cantidad y fecha de caducidad).
     *
     * @return la lista de lotes (cantidad y fecha de caducidad)
     */
    public List<Document> getLotes() {
        return new ArrayList<>(lotes);
    }

    /**
     * Asigna la lista de lotes (cantidad y fecha de caducidad).
     *
     * @param lotes la lista de lotes (cantidad y fecha de caducidad)
     */
    public void setLotes(List<Document> lotes) {
        this.lotes = copiarDocumentos(lotes);
        validarLotes();
    }

    /**
     * Devuelve la lista de ingredientes de la receta.
     *
     * @return la lista de ingredientes de la receta
     */
    public List<Document> getReceta() {
        return new ArrayList<>(receta);
    }

    /**
     * Asigna la lista de ingredientes de la receta.
     *
     * @param receta la lista de ingredientes de la receta
     */
    public void setReceta(List<Document> receta) {
        this.receta = copiarDocumentos(receta);
        validarReceta();
    }

    /**
     * Devuelve los identificadores de los proveedores del producto.
     *
     * @return los identificadores de los proveedores del producto
     */
    public List<Integer> getProveedores_id() {
        return new ArrayList<>(proveedores_id);
    }

    /**
     * Asigna los identificadores de los proveedores del producto.
     *
     * @param proveedores_id los identificadores de los proveedores del producto
     */
    public void setProveedores_id(List<Integer> proveedores_id) {
        this.proveedores_id = proveedores_id != null
                ? new ArrayList<>(proveedores_id)
                : new ArrayList<>();

        validarProveedores();
    }

    /**
     * Devuelve si el producto está activo.
     *
     * @return si el producto está activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Asigna si el producto está activo.
     *
     * @param activo si el producto está activo
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    // Copia los documentos para evitar modificar las listas internas
    // directamente desde fuera del modelo.
    private List<Document> copiarDocumentos(List<Document> documentos) {

        List<Document> copia = new ArrayList<>();

        if (documentos != null) {
            for (Document documento : documentos) {
                copia.add(documento != null
                        ? new Document(documento)
                        : null);
            }
        }

        return copia;
    }

    /**
     * Devuelve la ruta de la imagen del producto.
     *
     * @return la ruta de la imagen del producto
     */
    public String getImagen() {
        return imagen;
    }

    /**
     * Calcula el precio de venta con el IVA aplicado.
     *
     * @return precio de venta con IVA
     */
    public double productoIvaAplicado() {
        return precio_venta + (iva * precio_venta);
    }

    /**
     * Asigna la ruta de la imagen del producto.
     *
     * @param imagen la ruta de la imagen del producto
     */
    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    /**
     * Indica si al producto le queda poco stock: no está agotado pero no supera el mínimo.
     *
     * @return {@code true} si el stock es mayor que 0 y menor o igual que el mínimo
     */
    public boolean tieneStockBajo() {
        return stock_actual > 0 && stock_actual <= stock_minimo;
    }

    /**
     * Indica si el producto debe ir a la lista de compra: está agotado o tiene stock bajo
     * (mismo criterio que las alertas, {@code stock_actual <= stock_minimo}).
     *
     * @return {@code true} si hay que reponerlo
     */
    public boolean necesitaReposicion() {
        return stock_actual <= stock_minimo;
    }

    /**
     * Cantidad que hay que pedir para dejar el stock en el doble del mínimo. Así, después de
     * reponer, el stock queda por encima del mínimo y la alerta de stock bajo desaparece.
     *
     * @return unidades a pedir (al menos 1), o 0 si no hace falta reponer
     */
    public int cantidadAReponer() {
        if (!necesitaReposicion()) {
            return 0;
        }
        return Math.max(1, stock_minimo * 2 - stock_actual);
    }

    /**
     * Indica si el producto se elabora en casa (bocadillos, cafés, tortilla...): es así cuando
     * tiene receta. Estos productos no se compran a un proveedor, se reponen sin coste desde
     * «Añadir stock».
     *
     * @return {@code true} si el producto tiene receta
     */
    public boolean esElaboracionPropia() {
        return !receta.isEmpty();
    }

    @Override
    public String toString() {
        return "Producto{" + "id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + ", categoria=" + categoria + ", precio_venta=" + precio_venta + ", iva=" + iva + ", stock_actual=" + stock_actual + ", stock_minimo=" + stock_minimo + ", lotes=" + lotes + ", receta=" + receta + ", proveedores_id=" + proveedores_id + ", activo=" + activo + ", imagen=" + imagen + '}';
    }

}
