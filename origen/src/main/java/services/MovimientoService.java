/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.MovimientoDao;
import dao.implement.MovimientoDaoImpl;
import exceptions.DaoException;
import java.util.List;
import model.Movimiento;

/**
 * Lógica de negocio de los movimientos económicos (ingresos y gastos que no son compras ni
 * ventas de la caja). Valida con {@link Movimiento#validar()} antes de guardar.
 *
 * @author anazk
 */
public class MovimientoService {

    /**
     * DAO de movimientos.
     */
    private final MovimientoDao movimientoDao = MovimientoDaoImpl.getInstance();

    /**
     * Constructor vacío.
     */
    public MovimientoService() {
    }

    /**
     * Devuelve todos los movimientos.
     *
     * @return la lista de movimientos
     * @throws DaoException si falla la base de datos
     */
    public List<Movimiento> listarMovimientos() {
        return movimientoDao.obtenerMovimientos();
    }

    /**
     * Valida y guarda un movimiento nuevo.
     *
     * @param movimiento movimiento que se va a registrar
     * @throws IllegalArgumentException si el movimiento no es válido
     * @throws DaoException si falla la base de datos
     */
    public void registrarMovimiento(Movimiento movimiento) {
        if (movimiento == null) {
            throw new IllegalArgumentException(
                    "El movimiento no puede ser nulo.");
        }

        movimiento.validar();

        // consulta con el dao
        movimientoDao.insertarMovimiento(movimiento);
    }

}
