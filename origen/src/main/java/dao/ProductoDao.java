/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Producto;

/**
 * Operaciones de acceso a datos de los productos.
 *
 * @author Joel
 */
public interface ProductoDao {

    /**
     * Devuelve todos los productos.
     *
     * @return lista de productos (vacía si no hay ninguno)
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public List<Producto> listarProductos();

    /**
     * Sustituye los datos de un producto existente por los nuevos.
     *
     * @param producto producto con los datos nuevos; se busca por su identificador
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
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
