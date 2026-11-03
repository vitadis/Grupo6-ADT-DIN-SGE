/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Línea de una compra: un producto con su cantidad y precio.
 *
 * No es una colección propia: va embebida en el campo {@code detalle} de cada documento de
 * la colección {@code compras}.
 *
 * @author anazk
 */
public class LineaCompra {

    /**
     * El identificador del producto comprado.
     */
    private int producto_id;
    /**
     * El nombre del producto.
     */
    private String nombre;
    /**
     * La cantidad comprada (puede tener decimales, p. ej. kilos).
     */
    private double cantidad;
    /**
     * El precio por unidad sin IVA.
     */
    private double precio_unidad;
    /**
     * El tipo de IVA del producto, entre 0 y 1 (0.21 = 21 %).
     */
    private double iva;
    /**
     * El subtotal de la línea sin IVA (cantidad × precio por unidad).
     */
    private double subtotal;

    /**
     * Constructor vacío.
     * Lo necesita el driver de MongoDB para crear el objeto al leer un documento.
     */
    public LineaCompra() {
    }

    /**
     * Constructor con todos los datos.
     *
     * @param producto_id el identificador del producto comprado
     * @param nombre el nombre del producto
     * @param cantidad la cantidad comprada (puede tener decimales, p. ej. kilos)
     * @param precio_unidad el precio por unidad sin IVA
     * @param iva el tipo de IVA del producto, entre 0 y 1 (0.21 = 21 %)
     * @param subtotal el subtotal de la línea sin IVA (cantidad × precio por unidad)
     */
    public LineaCompra(int producto_id, String nombre, double cantidad, double precio_unidad, double iva, double subtotal) {
        this.producto_id = producto_id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio_unidad = precio_unidad;
        this.iva = iva;
        this.subtotal = subtotal;
    }

    /**
     * Devuelve el identificador del producto comprado.
     *
     * @return el identificador del producto comprado
     */
    public int getProducto_id() {
        return producto_id;
    }

    /**
     * Cambia el identificador del producto comprado.
     *
     * @param producto_id el identificador del producto comprado
     */
    public void setProducto_id(int producto_id) {
        this.producto_id = producto_id;
    }

    /**
     * Devuelve el nombre del producto.
     *
     * @return el nombre del producto
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre del producto.
     *
     * @param nombre el nombre del producto
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve la cantidad comprada (puede tener decimales, p. ej. kilos).
     *
     * @return la cantidad comprada (puede tener decimales, p. ej. kilos)
     */
    public double getCantidad() {
        return cantidad;
    }

    /**
     * Cambia la cantidad comprada (puede tener decimales, p. ej. kilos).
     *
     * @param cantidad la cantidad comprada (puede tener decimales, p. ej. kilos)
     */
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Devuelve el precio por unidad sin IVA.
     *
     * @return el precio por unidad sin IVA
     */
    public double getPrecio_unidad() {
        return precio_unidad;
    }

    /**
     * Cambia el precio por unidad sin IVA.
     *
     * @param precio_unidad el precio por unidad sin IVA
     */
    public void setPrecio_unidad(double precio_unidad) {
        this.precio_unidad = precio_unidad;
    }

    /**
     * Devuelve el tipo de IVA del producto, entre 0 y 1 (0.21 = 21 %).
     *
     * @return el tipo de IVA del producto, entre 0 y 1 (0.21 = 21 %)
     */
    public double getIva() {
        return iva;
    }

    /**
     * Cambia el tipo de IVA del producto, entre 0 y 1 (0.21 = 21 %).
     *
     * @param iva el tipo de IVA del producto, entre 0 y 1 (0.21 = 21 %)
     */
    public void setIva(double iva) {
        this.iva = iva;
    }

    /**
     * Devuelve el subtotal de la línea sin IVA (cantidad × precio por unidad).
     *
     * @return el subtotal de la línea sin IVA (cantidad × precio por unidad)
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Cambia el subtotal de la línea sin IVA (cantidad × precio por unidad).
     *
     * @param subtotal el subtotal de la línea sin IVA (cantidad × precio por unidad)
     */
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Devuelve todos los datos de la línea en texto. Útil para depurar.
     *
     * @return la línea en texto
     */
    @Override
    public String toString() {
        return "LineaCompra{" + "producto_id=" + producto_id + ", nombre=" + nombre + ", cantidad=" + cantidad + ", precio_unidad=" + precio_unidad + ", iva=" + iva + ", subtotal=" + subtotal + '}';
    }

}
