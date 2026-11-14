package services;

import dao.UsuarioDao;
import dao.implement.UsuarioDaoImp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import model.Empleado;
import model.Usuario;

/**
 * Servicio de negocio para la gestión de usuarios, empleados y permisos del
 * módulo de Administración.
 *
 * @author Joel
 */
public class AdministracionService {

    // ---- Singleton ----
    private static AdministracionService instance = null;

    private final UsuarioDao usuarioDao;

    private AdministracionService() {
        this.usuarioDao = UsuarioDaoImp.getInstance();
    }

    /**
     * Devuelve la instancia única del servicio de administración.
     *
     * @return instancia única de {@link AdministracionService}
     */
    public static AdministracionService getInstance() {
        if (instance == null) {
            instance = new AdministracionService();
        }
        return instance;
    }

    // ---- Read ----
    /**
     * Obtiene el listado completo de usuarios registrados.
     *
     * @return lista de usuarios
     */
    public List<Usuario> listarUsuarios() {
        return usuarioDao.listarUsuarios();
    }

    /**
     * Obtiene el listado de permisos disponibles en el sistema.
     *
     * @return lista de nombres de permisos
     */
    public List<String> listarPermisos() {
        return usuarioDao.listarPermisos();
    }

    /**
     * Busca un usuario por su identificador y valida que exista.
     *
     * @param id identificador del usuario
     * @return usuario encontrado
     */
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

    // ---- Create ----
    /**
     * Valida los datos del usuario y su empleado, asigna fechas por defecto y
     * registra el alta.
     *
     * @param user usuario a registrar
     */
    public void altaUsuario(Usuario user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }

        if (user.getEmpleado() != null) {
            if (user.getEmpleado().getFecha_alta() == null) {
                user.getEmpleado().setFecha_alta(LocalDate.now());
            }
            user.getEmpleado().validar();
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

    // ---- Update ----
    /**
     * Valida los datos del usuario y su empleado, comprueba que existan cambios
     * reales y actualiza el registro.
     *
     * @param user usuario con los datos modificados
     */
    public void modificarUsuario(Usuario user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("El id del usuario no puede ser nulo.");
        }

        if (user.getEmpleado() != null) {
            user.getEmpleado().validar();
        }

        user.validar();

        Usuario existente = consultarUsuario(user.getId());

        Usuario conMismoUsername = buscarPorUsername(user.getUsername());
        if (conMismoUsername != null && !conMismoUsername.getId().equals(user.getId())) {
            throw new IllegalArgumentException("Ya existe otro usuario con ese username.");
        }

        if (sonIguales(existente, user)) {
            throw new IllegalArgumentException("No se ha realizado ninguna modificación.");
        }

        usuarioDao.modificarUsuario(user);
    }

    // ---- Delete ----
    /**
     * Comprueba la existencia del usuario y lo elimina del sistema.
     *
     * @param user usuario a eliminar
     */
    public void eliminarUsuario(Usuario user) {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (user.getId() == null) {
            throw new IllegalArgumentException("El id del usuario no puede ser nulo.");
        }

        Usuario existente = consultarUsuario(user.getId());

        usuarioDao.eliminarUsuario(existente);
    }

    // ---- Auxiliares privados ----
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

    private boolean sonIguales(Usuario actual, Usuario nuevo) {
        List<String> permActual = actual.getPermisos() != null ? actual.getPermisos() : List.of();
        List<String> permNuevo = nuevo.getPermisos() != null ? nuevo.getPermisos() : List.of();

        boolean mismoUsuario = Objects.equals(actual.getUsername(), nuevo.getUsername())
                && Objects.equals(actual.getPassword_hash(), nuevo.getPassword_hash())
                && Objects.equals(actual.getRol(), nuevo.getRol())
                && actual.isActivo() == nuevo.isActivo()
                && new HashSet<>(permActual).equals(new HashSet<>(permNuevo));

        if (!mismoUsuario) {
            return false;
        }

        Empleado empActual = actual.getEmpleado();
        Empleado empNuevo = nuevo.getEmpleado();
        if (empActual == null || empNuevo == null) {
            return empActual == empNuevo;
        }

        return Objects.equals(empActual.getPuesto(), empNuevo.getPuesto())
                && Objects.equals(empActual.getEmail(), empNuevo.getEmail())
                && Objects.equals(empActual.getTelefono(), empNuevo.getTelefono());
    }
}
