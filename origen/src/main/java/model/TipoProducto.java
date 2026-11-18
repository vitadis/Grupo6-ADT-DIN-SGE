/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Tipo de un producto: de venta al público o ingrediente para elaborar otros productos.
 *
 * @author Joel
 */
public enum TipoProducto {
    /** Producto que se vende directamente en caja. */
    VENTA("Venta"),
    /** Producto que se usa como ingrediente en recetas. */
    INGREDIENTE("Ingrediente");

    private final String etiqueta; // para enseñarlo en pantalla

    TipoProducto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve el texto que se muestra en pantalla para el tipo.
     *
     * @return etiqueta legible del tipo
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Categorías de este tipo: TipoProducto.VENTA.getCategorias() -&gt; bebidas,
     * cafetería...
     *
     * @return lista de categorías que pertenecen a este tipo
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
