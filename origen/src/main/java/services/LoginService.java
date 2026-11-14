package services;

import dao.UsuarioDao;
import model.Usuario;

/**
 * Servicio de negocio encargado de la validación de credenciales e inicio de sesión.
 *
 * @author Joel
 */
public class LoginService {

    // ---- Dependencias ----
    private final UsuarioDao usuarioDao;

    /**
     * Inicializa el servicio de autenticación con el DAO de usuarios.
     *
     * @param usuarioDao acceso a datos de usuarios
     */
    public LoginService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    // ---- Autenticación ----

    /**
     * Valida que las credenciales no estén vacías y comprueba si corresponden a un usuario registrado.
     *
     * @param nombre     nombre de usuario introducido
     * @param contrasena contraseña introducida
     * @return usuario autenticado correspondiente a las credenciales
     */
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