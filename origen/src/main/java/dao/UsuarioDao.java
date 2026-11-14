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

    public Usuario obtenerUsuario(String user, String pw); // obtener el usuario por el username, password

    public void altaUsuario(Usuario user); // otorgar el id dentro de este metodo

    public void eliminarUsuario(Usuario user); // eliminar con el id

    public void modificarUsuario(Usuario user); // modificar por el id

    public List<Usuario> listarUsuarios(); // retornar una lista

    public List<String> listarPermisos(); // listaPermisos

}
