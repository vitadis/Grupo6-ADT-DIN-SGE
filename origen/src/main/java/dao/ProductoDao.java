/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Producto;

/**
 *
 * @author Joel
 */
public interface ProductoDao {

    public List<Producto> listarProductos();

    public Producto obtenerProducto(int id);

    public void altaProducto(Producto producto);

    public void modificarProducto(Producto producto);

    public void eliminarProducto(Producto producto);
}
