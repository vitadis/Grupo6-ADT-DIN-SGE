package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Usuario de la aplicación: credenciales, rol, permisos y datos del empleado asociado.
 */
public class Usuario {

    private Integer id;
    private String username;
    private String password_hash;
    private String rol;
    private List<String> permisos;
    private boolean activo;
    private Empleado empleado;
    private LocalDate creado_en;

    /**
     * Constructor vacío, necesario para el codec POJO de MongoDB. Inicializa la lista de
     * permisos vacía.
     */
    public Usuario() {
        this.permisos = new ArrayList<>();
    }

    /**
     * Crea un usuario con todos sus datos y lo valida.
     *
     * @param id identificador del usuario
     * @param username nombre de usuario
     * @param password_hash hash de la contraseña
     * @param rol rol del usuario
     * @param permisos lista de permisos (si es {@code null} se usa una lista vacía)
     * @param activo si el usuario está activo
     * @param empleado datos del empleado asociado
     * @param creado_en fecha de creación
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Usuario(Integer id, String username, String password_hash,
            String rol, List<String> permisos, boolean activo,
            Empleado empleado, LocalDate creado_en) {

        this.id = id;
        this.username = username;
        this.password_hash = password_hash;
        this.rol = rol;
        this.permisos = permisos != null
                ? new ArrayList<>(permisos)
                : new ArrayList<>();
        this.activo = activo;
        this.empleado = empleado;
        this.creado_en = creado_en;

        validar();
    }

    /**
     * Reglas de negocio: comprueba que el nombre de usuario, la contraseña y el rol son
     * obligatorios y que ningún permiso es nulo o está vacío.
     *
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public void validar() {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de usuario es obligatorio.");
        }

        if (!username.equals(username.trim())) {
            throw new IllegalArgumentException(
                    "El nombre de usuario no puede comenzar ni terminar con espacios.");
        }

        if (password_hash == null || password_hash.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        if (rol == null || rol.isBlank()) {
            throw new IllegalArgumentException(
                    "El rol es obligatorio.");
        }

        if (permisos == null) {
            throw new IllegalArgumentException(
                    "La lista de permisos no puede ser nula.");
        }

        for (String permiso : permisos) {
            if (permiso == null || permiso.isBlank()) {
                throw new IllegalArgumentException(
                        "Los permisos no pueden ser nulos ni estar vacíos.");
            }
        }
    }

    /**
     * Indica si el usuario está activo y tiene el permiso indicado (o el permiso "total").
     *
     * @param permiso nombre del permiso a comprobar
     * @return {@code true} si el usuario tiene el permiso
     */
    public boolean tienePermiso(String permiso) {
        return activo
                && permiso != null
                && permisos != null
                && (permisos.contains("total")
                || permisos.contains(permiso));
    }

    /**
     * Indica si el usuario puede iniciar sesión.
     *
     * @return {@code true} si el usuario está activo
     */
    public boolean puedeAcceder() {
        return activo;
    }

    // Getters y setters
    /**
     * Devuelve el identificador del usuario.
     *
     * @return el identificador del usuario
     */
    public Integer getId() {
        return id;
    }

    /**
     * Asigna el identificador del usuario.
     *
     * @param id el identificador del usuario
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre de usuario.
     *
     * @return el nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Asigna el nombre de usuario.
     *
     * @param username el nombre de usuario
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Devuelve el hash de la contraseña.
     *
     * @return el hash de la contraseña
     */
    public String getPassword_hash() {
        return password_hash;
    }

    /**
     * Asigna el hash de la contraseña.
     *
     * @param password_hash el hash de la contraseña
     */
    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    /**
     * Devuelve el rol del usuario.
     *
     * @return el rol del usuario
     */
    public String getRol() {
        return rol;
    }

    /**
     * Asigna el rol del usuario.
     *
     * @param rol el rol del usuario
     */
    public void setRol(String rol) {
        this.rol = rol;
    }

    /**
     * Devuelve la lista de permisos del usuario.
     *
     * @return la lista de permisos del usuario
     */
    public List<String> getPermisos() {
        return permisos;
    }

    /**
     * Asigna la lista de permisos del usuario.
     *
     * @param permisos la lista de permisos del usuario
     */
    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    /**
     * Devuelve si el usuario está activo.
     *
     * @return si el usuario está activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Asigna si el usuario está activo.
     *
     * @param activo si el usuario está activo
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Devuelve los datos del empleado asociado al usuario.
     *
     * @return los datos del empleado asociado al usuario
     */
    public Empleado getEmpleado() {
        return empleado;
    }

    /**
     * Asigna los datos del empleado asociado al usuario.
     *
     * @param empleado los datos del empleado asociado al usuario
     */
    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    /**
     * Devuelve la fecha de creación del usuario.
     *
     * @return la fecha de creación del usuario
     */
    public LocalDate getCreado_en() {
        return creado_en;
    }

    /**
     * Asigna la fecha de creación del usuario.
     *
     * @param creado_en la fecha de creación del usuario
     */
    public void setCreado_en(LocalDate creado_en) {
        this.creado_en = creado_en;
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "id='" + id + '\''
                + ", username='" + username + '\''
                + ", rol='" + rol + '\''
                + ", permisos=" + permisos
                + ", activo=" + activo
                + ", empleado=" + empleado
                + ", creado_en='" + creado_en + '\''
                + '}';
    }
}
