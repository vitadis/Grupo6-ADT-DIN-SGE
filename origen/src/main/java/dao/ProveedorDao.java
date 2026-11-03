/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.Proveedor_GestionStock;

/**
 * Operaciones de acceso a datos de los proveedores.
 *
 * Separa la capa de datos del resto de la aplicación: los controladores usan esta interfaz y no
 * saben que por debajo hay MongoDB. La implementación es {@link dao.implement.ProveedorDaoImpl}.
 *
 * @author anazk
 */
public interface ProveedorDao {
    
    /**
     * Devuelve todos los proveedores, ordenados por identificador.
     *
     * @return lista de proveedores (vacía si no hay ninguno)
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    List<Proveedor_GestionStock> obtenerProveedores();
    /**
     * Da de alta un proveedor nuevo. El identificador se genera automáticamente.
     *
     * @param proveedor proveedor que se va a guardar; al terminar tiene asignado su identificador
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    void insertarProveedores(Proveedor_GestionStock proveedor);
    /**
     * Sustituye los datos de un proveedor existente por los nuevos.
     *
     * @param proveedor proveedor con los datos nuevos; se busca por su identificador
     * @throws IllegalArgumentException si no existe ningún proveedor con ese identificador
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    void actualizarProveedores(Proveedor_GestionStock proveedor);
}
