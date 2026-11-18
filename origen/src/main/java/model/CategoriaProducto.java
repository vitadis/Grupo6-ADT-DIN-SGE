/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Categorías en las que se clasifican los productos. Cada categoría pertenece a un
 * {@link TipoProducto} (de venta o ingrediente) y tiene una etiqueta legible para la interfaz.
 *
 * @author Joel
 */
public enum CategoriaProducto {

    // VENTA
    /** Bebidas para la venta. */
    BEBIDAS(TipoProducto.VENTA, "Bebidas"),
    /** Productos de cafetería para la venta. */
    CAFETERIA_VENTA(TipoProducto.VENTA, "Cafetería"),
    /** Bollería para la venta. */
    BOLLERIA(TipoProducto.VENTA, "Bollería"),
    /** Comida preparada para la venta. */
    COMIDA(TipoProducto.VENTA, "Comida"),
    // INGREDIENTE
    /** Verduras usadas como ingrediente. */
    VERDURAS(TipoProducto.INGREDIENTE, "Verduras"),
    /** Huevos usados como ingrediente. */
    HUEVOS(TipoProducto.INGREDIENTE, "Huevos"),
    /** Aceites usados como ingrediente. */
    ACEITES(TipoProducto.INGREDIENTE, "Aceites"),
    /** Embutidos usados como ingrediente. */
    EMBUTIDOS(TipoProducto.INGREDIENTE, "Embutidos"),
    /** Ingredientes de cafetería (café, azúcar...). */
    CAFETERIA_INGREDIENTE(TipoProducto.INGREDIENTE, "Cafetería"),
    /** Lácteos usados como ingrediente. */
    LACTEOS(TipoProducto.INGREDIENTE, "Lácteos"),
    /** Infusiones usadas como ingrediente. */
    INFUSIONES(TipoProducto.INGREDIENTE, "Infusiones"),
    /** Productos congelados usados como ingrediente. */
    CONGELADOS(TipoProducto.INGREDIENTE, "Congelados"),
    /** Pan y productos de panadería usados como ingrediente. */
    PANADERIA(TipoProducto.INGREDIENTE, "Panadería");

    private final TipoProducto tipo;
    private final String etiqueta;

    CategoriaProducto(TipoProducto tipo, String etiqueta) {
        this.tipo = tipo;
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve el tipo de producto al que pertenece la categoría.
     *
     * @return tipo de producto (venta o ingrediente)
     */
    public TipoProducto getTipo() {
        return tipo;
    }

    /**
     * Devuelve el texto que se muestra en pantalla para la categoría.
     *
     * @return etiqueta legible de la categoría
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
