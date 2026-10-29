/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Hodei.Torres
 */
public class FilaStock {
    private final Producto producto;

    public FilaStock(Producto producto) {
        this.producto = producto;
    }

    public Producto getProducto() { return producto; }
    public String getNombre()     { return producto.getNombre(); }
    public CategoriaProducto getCategoria() { return producto.getCategoria(); }
    public int getExistencia()    { return producto.getStock_actual(); }
    public int getMinimo()        { return producto.getStock_minimo(); }

    public String getPrecio() {
        return String.format("%.2f €", producto.getPrecio_venta());
    }

    public String getValor() {
        return String.format("%.2f €", producto.getStock_actual() * producto.getPrecio_venta());
    }

    public String getEstado() {
        if (producto.getStock_actual() == 0) return "Agotado";
        if (producto.tieneStockBajo()) return "Stock bajo";
        return "OK";
    }
}
