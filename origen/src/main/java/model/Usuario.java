package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private Integer id;
    private String username;
    private String password_hash;
    private String rol;
    private List<String> permisos;
    private boolean activo;
    private Empleado empleado;
    private LocalDate creado_en;

    public Usuario() {
        this.permisos = new ArrayList<>();
    }

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

    // reglas del negocio (validaciones)
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

    public boolean tienePermiso(String permiso) {
        return activo
                && permiso != null
                && permisos != null
                && (permisos.contains("total")
                || permisos.contains(permiso));
    }

    public boolean puedeAcceder() {
        return activo;
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public List<String> getPermisos() {
        return permisos;
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public LocalDate getCreado_en() {
        return creado_en;
    }

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
