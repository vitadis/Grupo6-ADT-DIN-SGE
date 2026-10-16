/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Joel
 */
import java.time.LocalDate;

public class Empleado {

    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
    private String puesto;
    private String fecha_alta;

    public Empleado() {
    }

    public Empleado(String nombre, String apellido, String dni,
            String telefono, String email, String puesto,
            String fecha_alta) {

        setNombre(nombre);
        setApellido(apellido);
        setDni(dni);
        setTelefono(telefono);
        setEmail(email);
        setPuesto(puesto);
        setFecha_alta(fecha_alta);

        validar();
    }

    public void validar() {
        validarTexto(nombre, "nombre");
        validarTexto(apellido, "apellido");
        validarTexto(dni, "DNI");
        validarTexto(telefono, "teléfono");
        validarTexto(puesto, "puesto");

        if (email == null
                || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "El email no tiene un formato válido."
            );
        }

        try {
            LocalDate.parse(fecha_alta);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "La fecha de alta debe tener formato yyyy-MM-dd."
            );
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " es obligatorio."
            );
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getFecha_alta() {
        return fecha_alta;
    }

    public void setFecha_alta(String fecha_alta) {
        this.fecha_alta = fecha_alta;
    }
}
