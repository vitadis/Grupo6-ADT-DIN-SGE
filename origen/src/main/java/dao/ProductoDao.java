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

    public void modificarProducto(Producto producto);

    /**
     * Suma {@code cantidad} al stock del producto y añade un lote nuevo al final de su lista
     * {@code lotes}, sin tocar los lotes que ya había.
     *
     * @param productoId identificador del producto
     * @param cantidad unidades que entran (mayor que 0)
     * @param fechaCaducidad fecha de caducidad del lote (yyyy-MM-dd)
     */
    public void anadirLote(int productoId, int cantidad, String fechaCaducidad);

}
