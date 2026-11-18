package dao;

import java.util.List;
import model.Usuario;

/**
 * Define las operaciones CRUD y las consultas relacionadas con los usuarios y
 * sus permisos.
 *
 * @author Joel
 */
public interface UsuarioDao {

    /**
     * Busca un usuario por su nombre de usuario y comprueba la contraseña.
     *
     * @param user nombre de usuario
     * @param pw contraseña
     * @return el usuario si las credenciales son correctas, o {@code null} si no
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public Usuario obtenerUsuario(String user, String pw);

    /**
     * Da de alta un usuario nuevo. El identificador se asigna dentro de este método.
     *
     * @param user usuario que se va a guardar
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public void altaUsuario(Usuario user);

    /**
     * Elimina un usuario a partir de su identificador.
     *
     * @param user usuario que se va a eliminar
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public void eliminarUsuario(Usuario user);

    /**
     * Sustituye los datos de un usuario existente, buscándolo por su identificador.
     *
     * @param user usuario con los datos nuevos
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public void modificarUsuario(Usuario user);

    /**
     * Devuelve todos los usuarios.
     *
     * @return lista de usuarios
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public List<Usuario> listarUsuarios();

    /**
     * Devuelve todos los permisos que se pueden asignar a un usuario.
     *
     * @return lista de permisos (vacía si no hay ninguno)
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public List<String> listarPermisos();

}
