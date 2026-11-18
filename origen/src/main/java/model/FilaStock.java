/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Fila de la tabla de gestión de stock. Envuelve un {@link Producto} y expone sus datos ya
 * formateados para mostrarlos en las columnas de la tabla.
 *
 * @author Hodei.Torres
 */
public class FilaStock {

    private final Producto producto;

    /**
     * Crea una fila para el producto indicado.
     *
     * @param producto producto que representa la fila
     */
    public FilaStock(Producto producto) {
        this.producto = producto;
    }

    /**
     * Devuelve el producto que representa la fila.
     *
     * @return producto de la fila
     */
    public Producto getProducto() {
        return producto;
    }

    /**
     * Devuelve el nombre del producto.
     *
     * @return nombre del producto
     */
    public String getNombre() {
        return producto.getNombre();
    }

    /**
     * Devuelve la categoría del producto.
     *
     * @return categoría del producto
     */
    public CategoriaProducto getCategoria() {
        return producto.getCategoria();
    }

    /**
     * Devuelve las unidades en stock del producto.
     *
     * @return stock actual
     */
    public int getExistencia() {
        return producto.getStock_actual();
    }

    /**
     * Devuelve el stock mínimo configurado para el producto.
     *
     * @return stock mínimo
     */
    public int getMinimo() {
        return producto.getStock_minimo();
    }

    /**
     * Devuelve el precio de venta formateado en euros.
     *
     * @return precio con dos decimales y el símbolo del euro
     */
    public String getPrecio() {
        return String.format("%.2f €", producto.getPrecio_venta());
    }

    /**
     * Devuelve el valor total del stock (unidades por precio) formateado en euros.
     *
     * @return valor del stock con dos decimales y el símbolo del euro
     */
    public String getValor() {
        return String.format("%.2f €", producto.getStock_actual() * producto.getPrecio_venta());
    }

    /**
     * Devuelve el estado del stock del producto.
     *
     * @return "Agotado", "Stock bajo" u "OK"
     */
    public String getEstado() {
        if (producto.getStock_actual() == 0) {
            return "Agotado";
        }
        if (producto.tieneStockBajo()) {
            return "Stock bajo";
        }
        return "OK";
    }
}
