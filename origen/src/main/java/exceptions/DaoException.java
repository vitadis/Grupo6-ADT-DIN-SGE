/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exceptions;

/**
 * Excepción que lanzan los DAO cuando falla el acceso a la base de datos.
 *
 * @author Joel
 */
public class DaoException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje y la causa original.
     *
     * @param mensaje mensaje descriptivo del error
     * @param causa excepción que provocó el error
     */
    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
