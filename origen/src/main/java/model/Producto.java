package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

public class Producto {

    // =========================================================================
    // ============================= CLAVES BSON ===============================
    // =========================================================================
    // Claves de los documentos de lotes
    public static final String LOTE_CANTIDAD = "cantidad";
    public static final String LOTE_FECHA_CADUCIDAD = "fecha_caducidad";

    // Claves de los documentos de receta
    public static final String RECETA_PRODUCTO_ID = "producto_id";
    public static final String RECETA_CANTIDAD = "cantidad";
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
    public Producto() {
        this.lotes = new ArrayList<>();
        this.receta = new ArrayList<>();
        this.proveedores_id = new ArrayList<>();
    }

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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID debe ser positivo.");
        }
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }
        this.nombre = nombre;
    }

    public TipoProducto getTipo() {
        return tipo;
    }

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

    public CategoriaProducto getCategoria() {
        return categoria;
    }

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

    public double getPrecio_venta() {
        return precio_venta;
    }

    public void setPrecio_venta(double precio_venta) {
        if (!Double.isFinite(precio_venta) || precio_venta < 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser válido y no negativo.");
        }
        this.precio_venta = precio_venta;
    }

    public double getIva() {
        return iva;
    }

    public void setIva(double iva) {
        if (!Double.isFinite(iva) || iva < 0 || iva > 1) {
            throw new IllegalArgumentException(
                    "El IVA debe estar entre 0 y 1.");
        }
        this.iva = iva;
    }

    public int getStock_actual() {
        return stock_actual;
    }

    public void setStock_actual(int stock_actual) {
        if (stock_actual < 0) {
            throw new IllegalArgumentException(
                    "El stock actual no puede ser negativo.");
        }
        this.stock_actual = stock_actual;
    }

    public int getStock_minimo() {
        return stock_minimo;
    }

    public void setStock_minimo(int stock_minimo) {
        if (stock_minimo < 0) {
            throw new IllegalArgumentException(
                    "El stock mínimo no puede ser negativo.");
        }
        this.stock_minimo = stock_minimo;
    }

    public List<Document> getLotes() {
        return new ArrayList<>(lotes);
    }

    public void setLotes(List<Document> lotes) {
        this.lotes = copiarDocumentos(lotes);
        validarLotes();
    }

    public List<Document> getReceta() {
        return new ArrayList<>(receta);
    }

    public void setReceta(List<Document> receta) {
        this.receta = copiarDocumentos(receta);
        validarReceta();
    }

    public List<Integer> getProveedores_id() {
        return new ArrayList<>(proveedores_id);
    }

    public void setProveedores_id(List<Integer> proveedores_id) {
        this.proveedores_id = proveedores_id != null
                ? new ArrayList<>(proveedores_id)
                : new ArrayList<>();

        validarProveedores();
    }

    public boolean isActivo() {
        return activo;
    }

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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public boolean tieneStockBajo() {
        return stock_actual > 0 && stock_actual <= stock_minimo;
    }
}
