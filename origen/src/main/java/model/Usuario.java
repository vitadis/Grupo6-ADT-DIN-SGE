/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joel
 */
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
        setUsername(username);
        setPassword_hash(password_hash);
        setRol(rol);
        setPermisos(permisos);
        this.activo = activo;
        setEmpleado(empleado);
        setCreado_en(creado_en);
        validar();
    }

    // Reglas generales del modelo

    public void validar() {
        validarUsername(username);
        validarPasswordHash(password_hash);
        validarRol(rol);
        validarPermisos(permisos);

        if (empleado == null) {
            throw new IllegalArgumentException(
                "El usuario debe tener un empleado asociado."
            );
        }

        empleado.validar();
        validarFecha(creado_en, "creado_en");
    }

    private void validarUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                "El nombre de usuario es obligatorio."
            );
        }

        if (!username.equals(username.trim().toLowerCase())) {
            throw new IllegalArgumentException(
                "El username debe estar en minúsculas y sin espacios externos."
            );
        }

        if (!username.matches("[a-z0-9._-]{3,30}")) {
            throw new IllegalArgumentException(
                "El username debe tener entre 3 y 30 caracteres válidos."
            );
        }
    }

    private void validarPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException(
                "El hash de la contraseña es obligatorio."
            );
        }

        // Aquí no se comprueba si la contraseña es correcta.
        // La autenticación debe verificarla mediante BCrypt.
    }

    private void validarRol(String rol) {
        if (rol == null ||
            (!rol.equals("administrador") && !rol.equals("trabajador"))) {
            throw new IllegalArgumentException(
                "El rol debe ser administrador o trabajador."
            );
        }
    }

    private void validarPermisos(List<String> permisos) {
        if (permisos == null) {
            throw new IllegalArgumentException(
                "La lista de permisos no puede ser null."
            );
        }

        if (permisos.stream().anyMatch(
                p -> p == null || p.isBlank())) {
            throw new IllegalArgumentException(
                "No puede haber permisos vacíos."
            );
        }

        if (permisos.stream().distinct().count() != permisos.size()) {
            throw new IllegalArgumentException(
                "No puede haber permisos duplicados."
            );
        }

        if (permisos.contains("total") &&
            !rol.equals("administrador")) {
            throw new IllegalArgumentException(
                "Solo un administrador puede tener el permiso total."
            );
        }

        if (rol.equals("administrador") && !permisos.contains("total")) {
            throw new IllegalArgumentException(
                "El administrador debe tener el permiso total."
            );
        }
    }

    private void validarFecha(String fecha, String campo) {
        try {
            LocalDate.parse(fecha);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "El campo " + campo + " debe tener formato yyyy-MM-dd."
            );
        }
    }

    // Comprobación de permisos
    public boolean tienePermiso(String permiso) {
        return activo &&
               (permisos.contains("total") ||
                permisos.contains(permiso));
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
        validarUsername(username);
        this.username = username;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        validarPasswordHash(password_hash);
        this.password_hash = password_hash;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        validarRol(rol);
        this.rol = rol;
    }

    public List<String> getPermisos() {
        return new ArrayList<>(permisos);
    }

    public void setPermisos(List<String> permisos) {
        if (permisos == null) {
            throw new IllegalArgumentException(
                "Los permisos no pueden ser null."
            );
        }

        this.permisos = new ArrayList<>(permisos);

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
        return "Usuario{" + "id=" + id + ", username=" + username + ", password_hash=" + password_hash + ", rol=" + rol + ", permisos=" + permisos + ", activo=" + activo + ", empleado=" + empleado + ", creado_en=" + creado_en + '}';
    }
}
