package model;

import java.time.LocalDate;

/**
 * Datos personales y laborales del empleado asociado a un {@link Usuario}.
 */
public class Empleado {

    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String email;
    private String puesto;
    private LocalDate fecha_alta;

    /**
     * Constructor vacío, necesario para el codec POJO de MongoDB.
     */
    public Empleado() {
    }

    /**
     * Crea un empleado con todos sus datos y los valida.
     *
     * @param nombre nombre del empleado
     * @param apellido apellido del empleado
     * @param dni DNI (8 números y una letra)
     * @param telefono teléfono de contacto
     * @param email email de contacto
     * @param puesto puesto de trabajo
     * @param fecha_alta fecha de alta en la empresa
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Empleado(String nombre, String apellido, String dni,
            String telefono, String email, String puesto,
            LocalDate fecha_alta) {

        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
        this.puesto = puesto;
        this.fecha_alta = fecha_alta;

        validar();
    }

    /**
     * Comprueba que todos los campos son obligatorios y no tienen espacios al principio ni al
     * final, y que el DNI, el teléfono y el email tienen un formato válido.
     *
     * @throws IllegalArgumentException si algún dato no es válido
     */
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

    private void validarFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException(
                    "La fecha de alta es obligatoria.");
        }
    }

    // Getters y setters
    /**
     * Devuelve el nombre del empleado.
     *
     * @return el nombre del empleado
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre del empleado.
     *
     * @param nombre el nombre del empleado
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el apellido del empleado.
     *
     * @return el apellido del empleado
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Asigna el apellido del empleado.
     *
     * @param apellido el apellido del empleado
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Devuelve el DNI del empleado.
     *
     * @return el DNI del empleado
     */
    public String getDni() {
        return dni;
    }

    /**
     * Asigna el DNI del empleado.
     *
     * @param dni el DNI del empleado
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Devuelve el teléfono del empleado.
     *
     * @return el teléfono del empleado
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Asigna el teléfono del empleado.
     *
     * @param telefono el teléfono del empleado
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Devuelve el email del empleado.
     *
     * @return el email del empleado
     */
    public String getEmail() {
        return email;
    }

    /**
     * Asigna el email del empleado.
     *
     * @param email el email del empleado
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve el puesto del empleado.
     *
     * @return el puesto del empleado
     */
    public String getPuesto() {
        return puesto;
    }

    /**
     * Asigna el puesto del empleado.
     *
     * @param puesto el puesto del empleado
     */
    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    /**
     * Devuelve la fecha de alta del empleado.
     *
     * @return la fecha de alta del empleado
     */
    public LocalDate getFecha_alta() {
        return fecha_alta;
    }

    /**
     * Asigna la fecha de alta del empleado.
     *
     * @param fecha_alta la fecha de alta del empleado
     */
    public void setFecha_alta(LocalDate fecha_alta) {
        this.fecha_alta = fecha_alta;
    }
}
