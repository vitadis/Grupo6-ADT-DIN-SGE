/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dao;

import exceptions.DaoException;
import java.util.List;
import model.Movimiento;

/**
 * Operaciones de la colección {@code movimientos} (ingresos y gastos que no son compras ni
 * ventas de la caja).
 *
 * @author anazk
 */
public interface MovimientoDao {

    /**
     * Devuelve todos los movimientos ordenados por identificador.
     *
     * @return la lista de movimientos (vacía si no hay ninguno)
     * @throws DaoException si falla la conexión o los datos no se pueden leer
     */
    List<Movimiento> obtenerMovimientos();

    /**
     * Guarda un movimiento nuevo. El identificador lo asigna el DAO.
     *
     * @param movimiento movimiento que se va a guardar
     * @throws DaoException si falla la base de datos
     */
    void insertarMovimiento(Movimiento movimiento);
}
