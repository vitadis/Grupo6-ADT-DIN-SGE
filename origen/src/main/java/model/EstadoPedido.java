/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Estados por los que puede pasar un pedido de venta, con las transiciones permitidas entre
 * ellos.
 *
 * @author Joel
 */
public enum EstadoPedido {
    /** Pedido guardado pero aún no cobrado. */
    PENDIENTE("Pendiente"),
    /** Pedido cobrado. */
    COBRADO("Cobrado"),
    /** Pedido pendiente que se ha cancelado. Estado final. */
    CANCELADO("Cancelado"),
    /** Pedido cobrado que se ha devuelto. Estado final. */
    DEVUELTO("Devuelto");

    private final String etiqueta; // para enseñarlo en pantalla

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve el texto que se muestra en pantalla para el estado.
     *
     * @return etiqueta legible del estado
     */
    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Transiciones permitidas: PENDIENTE -&gt; COBRADO / CANCELADO, COBRADO -&gt;
     * DEVUELTO. CANCELADO y DEVUELTO son estados finales.
     *
     * @param nuevo estado al que se quiere pasar
     * @return {@code true} si se permite pasar de este estado a {@code nuevo}
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
