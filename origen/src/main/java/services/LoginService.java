/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.UsuarioDao;
import model.Usuario;

/**
 *
 * @author Joel
 */
public class LoginService {

    private final UsuarioDao usuarioDao;

    public LoginService(UsuarioDao usuarioDao) {
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
}
