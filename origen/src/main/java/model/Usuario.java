package model;

import java.util.ArrayList;
import java.util.List;

public class Usuario {

    private String id;
    private String username;
    private String password_hash;
    private String rol;
    private List<String> permisos;
    private boolean activo;
    private Empleado empleado;
    private String creado_en;

    public Usuario() {
        this.permisos = new ArrayList<>();
    }

    public Usuario(String id, String username, String password_hash,
            String rol, List<String> permisos, boolean activo,
            Empleado empleado, String creado_en) {

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
    }

    // Comprobación de permisos
    public boolean tienePermiso(String permiso) {
        return activo
                && (permisos != null
                && (permisos.contains("total")
                || permisos.contains(permiso)));
    }

    public boolean puedeAcceder() {
        return activo;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        return permisos != null
                ? new ArrayList<>(permisos)
                : new ArrayList<>();
    }

    public void setPermisos(List<String> permisos) {
        this.permisos = permisos != null
                ? new ArrayList<>(permisos)
                : new ArrayList<>();
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

    public String getCreado_en() {
        return creado_en;
    }

    public void setCreado_en(String creado_en) {
        this.creado_en = creado_en;
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "id='" + id + '\''
                + ", username='" + username + '\''
                + ", password_hash='" + password_hash + '\''
                + ", rol='" + rol + '\''
                + ", permisos=" + permisos
                + ", activo=" + activo
                + ", empleado=" + empleado
                + ", creado_en='" + creado_en + '\''
                + '}';
    }
}
