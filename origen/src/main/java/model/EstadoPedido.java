/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Joel
 */
public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    COBRADO("Cobrado"),
    CANCELADO("Cancelado"),
    DEVUELTO("Devuelto");

    private final String etiqueta; // para enseñarlo en pantalla

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Transiciones permitidas: PENDIENTE -> COBRADO / CANCELADO, COBRADO ->
     * DEVUELTO. CANCELADO y DEVUELTO son estados finales.
     */
    public boolean puedePasarA(EstadoPedido nuevo) {
        if (nuevo == null) {
            return false;
        }
        switch (this) {
            case PENDIENTE:
                return nuevo == COBRADO || nuevo == CANCELADO;
            case COBRADO:
                return nuevo == DEVUELTO;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return etiqueta; // para el ComboBox
    }
}
