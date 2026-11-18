/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Métodos de pago aceptados al cobrar un pedido.
 *
 * @author Joel
 */
public enum MetodoPago {
    /** Pago en efectivo. */
    EFECTIVO("Efectivo"),
    /** Pago con tarjeta. */
    TARJETA("Tarjeta"),
    /** Pago por Bizum. */
    BIZUM("Bizum");

    private final String etiqueta;

    MetodoPago(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve el texto que se muestra en pantalla para el método de pago.
     *
     * @return etiqueta legible del método de pago
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
