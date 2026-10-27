/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

/**
 *
 * @author Joel
 */
public enum TipoProducto {
    VENTA("venta", "Venta"),
    INGREDIENTE("ingrediente", "Ingrediente");

    private final String valor; // lo que guardo en mongo
    private final String etiqueta; // para enseñarlo en pantalla

    TipoProducto(String valor, String etiqueta) {
        this.valor = valor;
        this.etiqueta = etiqueta;
    }

    public String getValor() {
        return valor;
    }

    /**
     * Las categorías de este tipo: Tipo.VENTA.getCategorias() -> bebidas,
     * cafetería...
     */
    public List<CategoriaProducto> getCategorias() {
        return Arrays.stream(CategoriaProducto.values())
                .filter(c -> c.getTipo() == this)
                .collect(Collectors.toList());
    }

    public static TipoProducto deValor(String valor) {
        for (TipoProducto t : values()) {
            if (valor != null
                    && (t.valor.equalsIgnoreCase(valor.trim())
                    || t.etiqueta.equalsIgnoreCase(valor.trim()))) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo desconocido: " + valor);
    }

    @Override
    public String toString() {
        return etiqueta;
    } // para el ComboBox
}
