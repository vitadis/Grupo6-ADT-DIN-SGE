/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Compra hecha a un proveedor.
 *
 * Corresponde a un documento de la colección {@code compras} de MongoDB. Los nombres de los
 * atributos coinciden con los de los campos para que el driver los rellene automáticamente.
 * Las líneas de la compra van embebidas en {@link #detalle}.
 *
 * @author anazk
 */
public class Compra {

    /**
     * El identificador de la compra ({@code _id} en MongoDB).
     */
    private int id;
    /**
     * El identificador del proveedor al que se hizo la compra.
     */
    private int proveedor_id;
    /**
     * El identificador del usuario que registró la compra.
     */
    private int usuario_id;
    /**
     * La fecha de la compra, en texto con formato {@code yyyy-MM-dd}.
     */
    private String fecha;
    /**
     * El número de la factura del proveedor.
     */
    private String numero_factura;
    /**
     * Las líneas (productos) de la compra.
     */
    private List<LineaCompra> detalle;
    /**
     * La base imponible: suma de los subtotales sin IVA.
     */
    private double base;
    /**
     * El importe total del IVA.
     */
    private double total_iva;
    /**
     * El importe total de la compra (base + IVA).
     */
    private double total;

    /**
     * Constructor vacío.
     * Lo necesita el driver de MongoDB para crear el objeto al leer un documento.
     */
    public Compra() {
        this.detalle = new ArrayList<>();
    }

    /**
     * Constructor con todos los datos.
     *
     * @param id el identificador de la compra ({@code _id} en MongoDB)
     * @param proveedor_id el identificador del proveedor al que se hizo la compra
     * @param usuario_id el identificador del usuario que registró la compra
     * @param fecha la fecha de la compra, en texto con formato {@code yyyy-MM-dd}
     * @param numero_factura el número de la factura del proveedor
     * @param detalle las líneas (productos) de la compra
     * @param base la base imponible: suma de los subtotales sin IVA
     * @param total_iva el importe total del IVA
     * @param total el importe total de la compra (base + IVA)
     */
    public Compra(int id, int proveedor_id, int usuario_id, String fecha, String numero_factura,
            List<LineaCompra> detalle, double base, double total_iva, double total) {
        this.id = id;
        this.proveedor_id = proveedor_id;
        this.usuario_id = usuario_id;
        this.fecha = fecha;
        this.numero_factura = numero_factura;
        this.detalle = detalle;
        this.base = base;
        this.total_iva = total_iva;
        this.total = total;
    }

    /**
     * Devuelve el identificador de la compra ({@code _id} en MongoDB).
     *
     * @return el identificador de la compra ({@code _id} en MongoDB)
     */
    public int getId() {
        return id;
    }

    /**
     * Cambia el identificador de la compra ({@code _id} en MongoDB).
     *
     * @param id el identificador de la compra ({@code _id} en MongoDB)
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve el identificador del proveedor al que se hizo la compra.
     *
     * @return el identificador del proveedor al que se hizo la compra
     */
    public int getProveedor_id() {
        return proveedor_id;
    }

    /**
     * Cambia el identificador del proveedor al que se hizo la compra.
     *
     * @param proveedor_id el identificador del proveedor al que se hizo la compra
     */
    public void setProveedor_id(int proveedor_id) {
        this.proveedor_id = proveedor_id;
    }

    /**
     * Devuelve el identificador del usuario que registró la compra.
     *
     * @return el identificador del usuario que registró la compra
     */
    public int getUsuario_id() {
        return usuario_id;
    }

    /**
     * Cambia el identificador del usuario que registró la compra.
     *
     * @param usuario_id el identificador del usuario que registró la compra
     */
    public void setUsuario_id(int usuario_id) {
        this.usuario_id = usuario_id;
    }

    /**
     * Devuelve la fecha de la compra, en texto con formato {@code yyyy-MM-dd}.
     *
     * @return la fecha de la compra, en texto con formato {@code yyyy-MM-dd}
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Cambia la fecha de la compra, en texto con formato {@code yyyy-MM-dd}.
     *
     * @param fecha la fecha de la compra, en texto con formato {@code yyyy-MM-dd}
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * Devuelve el número de la factura del proveedor.
     *
     * @return el número de la factura del proveedor
     */
    public String getNumero_factura() {
        return numero_factura;
    }

    /**
     * Cambia el número de la factura del proveedor.
     *
     * @param numero_factura el número de la factura del proveedor
     */
    public void setNumero_factura(String numero_factura) {
        this.numero_factura = numero_factura;
    }

    /**
     * Devuelve las líneas (productos) de la compra.
     *
     * @return las líneas (productos) de la compra
     */
    public List<LineaCompra> getDetalle() {
        return detalle;
    }

    /**
     * Cambia las líneas (productos) de la compra.
     *
     * @param detalle las líneas (productos) de la compra
     */
    public void setDetalle(List<LineaCompra> detalle) {
        this.detalle = detalle;
    }

    /**
     * Devuelve la base imponible: suma de los subtotales sin IVA.
     *
     * @return la base imponible: suma de los subtotales sin IVA
     */
    public double getBase() {
        return base;
    }

    /**
     * Cambia la base imponible: suma de los subtotales sin IVA.
     *
     * @param base la base imponible: suma de los subtotales sin IVA
     */
    public void setBase(double base) {
        this.base = base;
    }

    /**
     * Devuelve el importe total del IVA.
     *
     * @return el importe total del IVA
     */
    public double getTotal_iva() {
        return total_iva;
    }

    /**
     * Cambia el importe total del IVA.
     *
     * @param total_iva el importe total del IVA
     */
    public void setTotal_iva(double total_iva) {
        this.total_iva = total_iva;
    }

    /**
     * Devuelve el importe total de la compra (base + IVA).
     *
     * @return el importe total de la compra (base + IVA)
     */
    public double getTotal() {
        return total;
    }

    /**
     * Cambia el importe total de la compra (base + IVA).
     *
     * @param total el importe total de la compra (base + IVA)
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * Devuelve todos los datos de la compra en texto. Útil para depurar.
     *
     * @return la compra en texto
     */
    @Override
    public String toString() {
        return "Compra{" + "id=" + id + ", proveedor_id=" + proveedor_id + ", usuario_id=" + usuario_id + ", fecha=" + fecha + ", numero_factura=" + numero_factura + ", detalle=" + detalle + ", base=" + base + ", total_iva=" + total_iva + ", total=" + total + '}';
    }

}
