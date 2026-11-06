package services;

import dao.UsuarioDao;
import dao.implement.UsuarioDaoImp;
import java.time.LocalDate;
import java.util.List;
import model.Usuario;

/**
 *
 * @author Joel
 */
public class AdministracionService {

    // singleton
    private static AdministracionService instance = null;

    private final UsuarioDao usuarioDao;

    private AdministracionService() {
        this.usuarioDao = UsuarioDaoImp.getInstance();
    }

    public static AdministracionService getInstance() {
        if (instance == null) {
            instance = new AdministracionService();
        }
        return instance;
    }

    // READ
    public List<Usuario> listarUsuarios() {
        return usuarioDao.listarUsuarios();
    }

    public List<String> listarPermisos() {
        return usuarioDao.listarPermisos();
    }

    public Usuario consultarUsuario(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del usuario no puede ser nulo.");
        }

        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("No existe ningún usuario con el id " + id + ".");
        }
        return usuario;
    }

    // CREATE
    public void altaUsuario(Usuario user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        user.validar();

        if (buscarPorUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username.");
        }

        if (user.getCreado_en() == null) {
            user.setCreado_en(LocalDate.now());
        }

        usuarioDao.altaUsuario(user);
    }

    // UPDATE
    public void modificarUsuario(Usuario user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("El id del usuario no puede ser nulo.");
        }

        user.validar();

        // Debe existir
        consultarUsuario(user.getId());

        // El username no puede coincidir con el de otro usuario
        Usuario conMismoUsername = buscarPorUsername(user.getUsername());
        if (conMismoUsername != null && !conMismoUsername.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Ya existe otro usuario con ese username.");
        }

        usuarioDao.modificarUsuario(user);
    }

    // DELETE
    public void eliminarUsuario(Usuario user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("El id del usuario no puede ser nulo.");
        }

        // Debe existir
        Usuario existente = consultarUsuario(user.getId());

        usuarioDao.eliminarUsuario(existente);
    }

    // Búsquedas internas (sobre listarUsuarios, para no ampliar el DAO)
    private Usuario buscarPorId(Integer id) {
        for (Usuario u : usuarioDao.listarUsuarios()) {
            if (id.equals(u.getId())) {
                return u;
            }
        }
        return null;
    }

    private Usuario buscarPorUsername(String username) {
        for (Usuario u : usuarioDao.listarUsuarios()) {
            if (username != null && username.equals(u.getUsername())) {
                return u;
            }
        }
        return null;
    }
}
