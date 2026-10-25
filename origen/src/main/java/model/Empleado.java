package model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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

        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.puesto = puesto;
        this.fecha_alta = fecha_alta;

        validar();
    }

    public void validar() {
        validarTexto(nombre, "El nombre");
        validarTexto(apellido, "El apellido");
        validarTexto(dni, "El DNI");
        validarTexto(telefono, "El teléfono");
        validarTexto(email, "El email");
        validarTexto(puesto, "El puesto");
        validarFecha(fecha_alta);

        if (!dni.matches("^[0-9]{8}[A-Za-z]$")) {
            throw new IllegalArgumentException(
                    "El DNI debe contener 8 números y una letra.");
        }

        if (!telefono.matches("^[0-9+() -]{9,20}$")) {
            throw new IllegalArgumentException(
                    "El formato del teléfono no es válido.");
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "El formato del email no es válido.");
        }
    }

    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(
                    campo + " es obligatorio.");
        }

        if (!valor.equals(valor.trim())) {
            throw new IllegalArgumentException(
                    campo + " no puede comenzar ni terminar con espacios.");
        }
    }

    private void validarFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            throw new IllegalArgumentException(
                    "La fecha de alta es obligatoria.");
        }

        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "La fecha de alta debe tener el formato yyyy-MM-dd.");
        }
    }

    // Getters y setters
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
