/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Joel
 */
public enum CategoriaProducto {

    // VENTA
    BEBIDAS(TipoProducto.VENTA, "bebidas", "Bebidas"),
    CAFETERIA_VENTA(TipoProducto.VENTA, "cafeteria", "Cafetería"),
    BOLLERIA(TipoProducto.VENTA, "bolleria", "Bollería"),
    COMIDA(TipoProducto.VENTA, "comida", "Comida"),
    // INGREDIENTE
    VERDURAS(TipoProducto.INGREDIENTE, "verduras", "Verduras"),
    HUEVOS(TipoProducto.INGREDIENTE, "huevos", "Huevos"),
    ACEITES(TipoProducto.INGREDIENTE, "aceites", "Aceites"),
    EMBUTIDOS(TipoProducto.INGREDIENTE, "embutidos", "Embutidos"),
    CAFETERIA_INGREDIENTE(TipoProducto.INGREDIENTE, "cafeteria", "Cafetería"),
    LACTEOS(TipoProducto.INGREDIENTE, "lacteos", "Lácteos"),
    INFUSIONES(TipoProducto.INGREDIENTE, "infusiones", "Infusiones"),
    CONGELADOS(TipoProducto.INGREDIENTE, "congelados", "Congelados"),
    PANADERIA(TipoProducto.INGREDIENTE, "panaderia", "Panadería");

    private final TipoProducto tipo;
    private final String valor;
    private final String etiqueta;

    CategoriaProducto(TipoProducto tipo, String valor, String etiqueta) {
        this.tipo = tipo;
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    public TipoProducto getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    /**
     * Hay que pasar el tipo, porque "cafeteria" existe en venta y en
     * ingrediente.
     */
    public static CategoriaProducto deValor(TipoProducto tipo, String valor) {
        for (CategoriaProducto c : values()) {
            if (c.tipo == tipo && valor != null
                    && (c.valor.equalsIgnoreCase(valor.trim())
                    || c.etiqueta.equalsIgnoreCase(valor.trim()))) {
                return c;
            }
        }
        throw new IllegalArgumentException("Categoría desconocida: " + tipo + "/" + valor);
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
