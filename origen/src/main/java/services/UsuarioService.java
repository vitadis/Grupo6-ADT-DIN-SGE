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
public class UsuarioService {

    private final UsuarioDao usuarioDao;

    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public Usuario iniciarSesion(String nombre, String contrasena) {
        if (nombre == null || nombre.isBlank()
                || contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException(
                    "Completa todos los campos obligatorios."
            );
        }

        Usuario usuario = usuarioDao.obtenerUsuario(
                nombre.trim(),
                contrasena
        );

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "El usuario no existe o las credenciales son incorrectas."
            );
        }
        return usuario;
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
