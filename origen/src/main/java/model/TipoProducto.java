/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 * @author Joel
 */
public enum TipoProducto {
    VENTA("Venta"),
    INGREDIENTE("Ingrediente");

    private final String etiqueta; // para enseñarlo en pantalla

    TipoProducto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Categorías de este tipo: TipoProducto.VENTA.getCategorias() -> bebidas,
     * cafetería...
     */
    public List<CategoriaProducto> getCategorias() {
        return Arrays.stream(CategoriaProducto.values())
                .filter(c -> c.getTipo() == this)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return etiqueta; // para el ComboBox
    }
}
