/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Compra;

/**
 * Operaciones de acceso a datos de las compras.
 *
 * Separa la capa de datos del resto de la aplicación: los controladores usan esta interfaz y no
 * saben que por debajo hay MongoDB. La implementación es {@link dao.implement.ComprasDaoImpl}.
 *
 * @author anazk
 */
public interface ComprasDao {
    
    /**
     * Devuelve todas las compras, ordenadas por identificador.
     *
     * @return lista de compras (vacía si no hay ninguna)
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    List<Compra> obtenerCompras();
    /**
     * Devuelve las compras hechas a un proveedor, ordenadas por identificador.
     *
     * @param idProveedor identificador del proveedor
     * @return lista de compras del proveedor (vacía si no tiene)
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    List<Compra> obtenerComprasPorProveedor(int idProveedor);
    /**
     * Guarda una compra nueva. El identificador se genera automáticamente.
     *
     * @param compra compra que se va a guardar; al terminar tiene asignado su identificador
     * @throws IllegalArgumentException si ya existe una compra con el mismo número de factura
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    void insertarCompra(Compra compra);

}
