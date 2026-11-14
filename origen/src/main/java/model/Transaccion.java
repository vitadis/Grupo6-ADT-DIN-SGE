/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

/**
 * Movimiento económico que se muestra en la tabla de «Gestión económica».
 *
 * No se guarda en MongoDB: se crea a partir de los pedidos cobrados (ingresos)
 * y de las compras (gastos), para poder tenerlos todos en una misma lista,
 * ordenarlos y filtrarlos.
 *
 * @author anazk
 */
public class Transaccion {

    /**
     * La fecha del movimiento.
     */
    private LocalDate fecha;
    /**
     * El concepto, p. ej. «Ventas del día (TPV)» o «Bebidas del Sur · FAC
     * 2231».
     */
    private String concepto;
    /**
     * La categoría, p. ej. «Ventas» o «Compras».
     */
    private String categoria;
    /**
     * {@code true} si es un ingreso; {@code false} si es un gasto.
     */
    private boolean ingreso;
    /**
     * El importe, siempre en positivo (el signo lo indica {@link #ingreso}).
     */
    private double importe;

    /**
     * Constructor vacío.
     */
    public Transaccion() {
    }

    /**
     * Constructor con todos los datos.
     *
     * @param fecha la fecha del movimiento
     * @param concepto el concepto del movimiento
     * @param categoria la categoría del movimiento
     * @param ingreso {@code true} si es un ingreso; {@code false} si es un
     * gasto
     * @param importe el importe, en positivo
     * @throws IllegalArgumentException si algún dato no es válido (ver
     * {@link #validar()})
     */
    public Transaccion(LocalDate fecha, String concepto, String categoria, boolean ingreso, double importe) {
        this.fecha = fecha;
        this.concepto = concepto;
        this.categoria = categoria;
        this.ingreso = ingreso;
        this.importe = importe;

        validar();
    }

    // reglas del negocio (validaciones)
    /**
     * Comprueba que hay fecha, concepto y categoría y que el importe no es
     * negativo.
     *
     * @throws IllegalArgumentException con el mensaje del primer dato que no es
     * válido
     */
    public void validar() {
        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException(
                    "El concepto es obligatorio.");
        }

        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria.");
        }

        if (!Double.isFinite(importe) || importe < 0) {
            throw new IllegalArgumentException(
                    "El importe no puede ser negativo.");
        }
    }

    /**
     * Devuelve la fecha del movimiento.
     *
     * @return la fecha del movimiento
     */
    public LocalDate getFecha() {
        return fecha;
    }

    /**
     * Cambia la fecha del movimiento.
     *
     * @param fecha la fecha del movimiento
     */
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    /**
     * Devuelve el concepto del movimiento.
     *
     * @return el concepto del movimiento
     */
    public String getConcepto() {
        return concepto;
    }

    /**
     * Cambia el concepto del movimiento.
     *
     * @param concepto el concepto del movimiento
     */
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    /**
     * Devuelve la categoría del movimiento.
     *
     * @return la categoría del movimiento
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Cambia la categoría del movimiento.
     *
     * @param categoria la categoría del movimiento
     */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /**
     * Indica si el movimiento es un ingreso.
     *
     * @return {@code true} si es un ingreso; {@code false} si es un gasto
     */
    public boolean isIngreso() {
        return ingreso;
    }

    /**
     * Marca el movimiento como ingreso o como gasto.
     *
     * @param ingreso {@code true} si es un ingreso; {@code false} si es un
     * gasto
     */
    public void setIngreso(boolean ingreso) {
        this.ingreso = ingreso;
    }

    /**
     * Devuelve el importe, en positivo.
     *
     * @return el importe
     */
    public double getImporte() {
        return importe;
    }

    /**
     * Cambia el importe.
     *
     * @param importe el importe, en positivo
     */
    public void setImporte(double importe) {
        this.importe = importe;
    }

    /**
     * Devuelve los datos del movimiento en una línea.
     *
     * @return el texto con la fecha, el concepto y el importe
     */
    @Override
    public String toString() {
        return "Transaccion{" + "fecha=" + fecha + ", concepto=" + concepto + ", categoria=" + categoria
                + ", ingreso=" + ingreso + ", importe=" + importe + '}';
    }

}
