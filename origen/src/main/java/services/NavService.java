/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.UsuarioDao;
import model.Empleado;
import model.Usuario;

/**
 *
 * @author Joel
 */
public class NavService {

    private final UsuarioDao usuarioDao;

    public NavService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }
    
    // modificarPerfil
    public void modificarPerfil(Usuario user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo.");
        }

        user.validar();

        Empleado empleado = user.getEmpleado();

        if (empleado != null) {
            empleado.validar();
        }
        // consulta con el dao
        usuarioDao.modificarUsuario(user);
    }
}
