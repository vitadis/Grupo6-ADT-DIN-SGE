/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Movimiento económico que no es ni una compra ni una venta de la caja: luz, nóminas,
 * alquiler, una subvención...
 *
 * Corresponde a un documento de la colección {@code movimientos} de MongoDB. Los nombres de los
 * atributos coinciden con los de los campos para que el driver los rellene automáticamente.
 *
 * @author anazk
 */
public class Movimiento {

    /**
     * El identificador del movimiento ({@code _id} en MongoDB).
     */
    private int id;
    /**
     * La fecha del movimiento, en texto con formato {@code yyyy-MM-dd}.
     */
    private String fecha;
    /**
     * El concepto, p. ej. «Suministro eléctrico - septiembre».
     */
    private String concepto;
    /**
     * La categoría, p. ej. «Suministros», «Personal» o «Alquiler».
     */
    private String categoria;
    /**
     * El tipo: {@code "ingreso"} o {@code "gasto"}.
     */
    private String tipo;
    /**
     * El importe, siempre en positivo (el signo lo indica {@link #tipo}).
     */
    private double importe;
    /**
     * Observaciones (opcional).
     */
    private String observaciones;
    /**
     * El identificador del usuario que registró el movimiento.
     */
    private int usuario_id;

    /**
     * Constructor vacío.
     * Lo necesita el driver de MongoDB para crear el objeto al leer un documento.
     */
    public Movimiento() {
    }

    /**
     * Constructor con todos los datos.
     *
     * @param id el identificador del movimiento ({@code _id} en MongoDB)
     * @param fecha la fecha del movimiento, en texto con formato {@code yyyy-MM-dd}
     * @param concepto el concepto del movimiento
     * @param categoria la categoría del movimiento
     * @param tipo {@code "ingreso"} o {@code "gasto"}
     * @param importe el importe, en positivo
     * @param observaciones observaciones (opcional)
     * @param usuario_id el identificador del usuario que registró el movimiento
     * @throws IllegalArgumentException si algún dato no es válido (ver {@link #validar()})
     */
    public Movimiento(int id, String fecha, String concepto, String categoria, String tipo,
            double importe, String observaciones, int usuario_id) {
        this.id = id;
        this.fecha = fecha;
        this.concepto = concepto;
        this.categoria = categoria;
        this.tipo = tipo;
        this.importe = importe;
        this.observaciones = observaciones;
        this.usuario_id = usuario_id;

        validar();
    }

    // reglas del negocio (validaciones)
    /**
     * Comprueba que los datos del movimiento son válidos: fecha con formato {@code yyyy-MM-dd},
     * concepto y categoría obligatorios, tipo {@code "ingreso"} o {@code "gasto"}, importe mayor
     * que 0 y usuario identificado.
     *
     * El {@link #id} no se comprueba porque lo asigna el DAO al insertar el movimiento.
     *
     * @throws IllegalArgumentException con el mensaje del primer dato que no es válido
     */
    public void validar() {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException(
                    "La fecha es obligatoria.");
        }

        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "La fecha debe tener formato yyyy-MM-dd.");
        }

        if (concepto == null || concepto.isBlank()) {
            throw new IllegalArgumentException(
                    "El concepto es obligatorio.");
        }

        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria.");
        }

        if (!"ingreso".equals(tipo) && !"gasto".equals(tipo)) {
            throw new IllegalArgumentException(
                    "El tipo debe ser ingreso o gasto.");
        }

        if (!Double.isFinite(importe) || importe <= 0) {
            throw new IllegalArgumentException(
                    "El importe debe ser mayor que 0.");
        }

        if (usuario_id <= 0) {
            throw new IllegalArgumentException(
                    "No hay ningún usuario identificado.");
        }
    }

    /**
     * Indica si el movimiento es un ingreso.
     *
     * @return {@code true} si {@link #tipo} es {@code "ingreso"}
     */
    public boolean esIngreso() {
        return "ingreso".equals(tipo);
    }

    /**
     * Devuelve el identificador del movimiento ({@code _id} en MongoDB).
     *
     * @return el identificador del movimiento
     */
    public int getId() {
        return id;
    }

    /**
     * Cambia el identificador del movimiento ({@code _id} en MongoDB).
     *
     * @param id el identificador del movimiento
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve la fecha del movimiento, en texto con formato {@code yyyy-MM-dd}.
     *
     * @return la fecha del movimiento
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Cambia la fecha del movimiento.
     *
     * @param fecha la fecha del movimiento, en texto con formato {@code yyyy-MM-dd}
     */
    public void setFecha(String fecha) {
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
     * Devuelve el tipo del movimiento.
     *
     * @return {@code "ingreso"} o {@code "gasto"}
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Cambia el tipo del movimiento.
     *
     * @param tipo {@code "ingreso"} o {@code "gasto"}
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
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
     * Devuelve las observaciones.
     *
     * @return las observaciones, o {@code null} si no hay
     */
    public String getObservaciones() {
        return observaciones;
    }

    /**
     * Cambia las observaciones.
     *
     * @param observaciones las observaciones (opcional)
     */
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    /**
     * Devuelve el identificador del usuario que registró el movimiento.
     *
     * @return el identificador del usuario
     */
    public int getUsuario_id() {
        return usuario_id;
    }

    /**
     * Cambia el identificador del usuario que registró el movimiento.
     *
     * @param usuario_id el identificador del usuario
     */
    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    /**
     * Devuelve los datos del movimiento en una línea.
     *
     * @return el texto con los datos del movimiento
     */
    @Override
    public String toString() {
        return "Movimiento{" + "id=" + id + ", fecha=" + fecha + ", concepto=" + concepto
                + ", categoria=" + categoria + ", tipo=" + tipo + ", importe=" + importe + '}';
    }

}
