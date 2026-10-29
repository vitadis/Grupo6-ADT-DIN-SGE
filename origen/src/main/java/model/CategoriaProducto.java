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
    BEBIDAS(TipoProducto.VENTA, "Bebidas"),
    CAFETERIA_VENTA(TipoProducto.VENTA, "Cafetería"),
    BOLLERIA(TipoProducto.VENTA, "Bollería"),
    COMIDA(TipoProducto.VENTA, "Comida"),
    // INGREDIENTE
    VERDURAS(TipoProducto.INGREDIENTE, "Verduras"),
    HUEVOS(TipoProducto.INGREDIENTE, "Huevos"),
    ACEITES(TipoProducto.INGREDIENTE, "Aceites"),
    EMBUTIDOS(TipoProducto.INGREDIENTE, "Embutidos"),
    CAFETERIA_INGREDIENTE(TipoProducto.INGREDIENTE, "Cafetería"),
    LACTEOS(TipoProducto.INGREDIENTE, "Lácteos"),
    INFUSIONES(TipoProducto.INGREDIENTE, "Infusiones"),
    CONGELADOS(TipoProducto.INGREDIENTE, "Congelados"),
    PANADERIA(TipoProducto.INGREDIENTE, "Panadería");

    private final TipoProducto tipo;
    private final String etiqueta;

    CategoriaProducto(TipoProducto tipo, String etiqueta) {
        this.tipo = tipo;
        this.etiqueta = etiqueta;
    }

    public TipoProducto getTipo() {
        return tipo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
