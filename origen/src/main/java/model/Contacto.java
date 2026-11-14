/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Persona de contacto de un proveedor.
 *
 * No es una colección propia: va embebido en el campo {@code contacto} de cada documento de
 * la colección {@code proveedores}.
 *
 * @author anazk
 */
public class Contacto {

    /**
     * El nombre de la persona de contacto.
     */
    private String persona;
    /**
     * El teléfono de contacto.
     */
    private String telefono;
    /**
     * El correo electrónico de contacto.
     */
    private String email;
    /**
     * La dirección del proveedor.
     */
    private String direccion;

    /**
     * Constructor vacío.
     * Lo necesita el driver de MongoDB para crear el objeto al leer un documento.
     */
    public Contacto() {
    }

    /**
     * Constructor con todos los datos.
     *
     * @param persona el nombre de la persona de contacto
     * @param telefono el teléfono de contacto
     * @param email el correo electrónico de contacto
     * @param direccion la dirección del proveedor
     * @throws IllegalArgumentException si algún dato no es válido (ver {@link #validar()})
     */
    public Contacto(String persona, String telefono, String email, String direccion) {
        this.persona = persona;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;

        validar();
    }

    // reglas del negocio (validaciones)
    /**
     * Comprueba que los datos de contacto son válidos. Todos son opcionales, pero si hay
     * correo tiene que llevar {@code @} y si hay teléfono solo puede tener números, espacios y {@code +}.
     *
     * @throws IllegalArgumentException con el mensaje del primer dato que no es válido
     */
    public void validar() {
        if (email != null && !email.isBlank() && !email.contains("@")) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido.");
        }

        if (telefono != null && !telefono.isBlank() && !telefono.matches("[0-9 +]+")) {
            throw new IllegalArgumentException(
                    "El teléfono solo puede tener números, espacios y '+'.");
        }
    }

    /**
     * Devuelve el nombre de la persona de contacto.
     *
     * @return el nombre de la persona de contacto
     */
    public String getPersona() {
        return persona;
    }

    /**
     * Cambia el nombre de la persona de contacto.
     *
     * @param persona el nombre de la persona de contacto
     */
    public void setPersona(String persona) {
        this.persona = persona;
    }

    /**
     * Devuelve el teléfono de contacto.
     *
     * @return el teléfono de contacto
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Cambia el teléfono de contacto.
     *
     * @param telefono el teléfono de contacto
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Devuelve el correo electrónico de contacto.
     *
     * @return el correo electrónico de contacto
     */
    public String getEmail() {
        return email;
    }

    /**
     * Cambia el correo electrónico de contacto.
     *
     * @param email el correo electrónico de contacto
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Devuelve la dirección del proveedor.
     *
     * @return la dirección del proveedor
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Cambia la dirección del proveedor.
     *
     * @param direccion la dirección del proveedor
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Devuelve todos los datos del contacto en texto. Útil para depurar.
     *
     * @return el contacto en texto
     */
    @Override
    public String toString() {
        return "Contacto{" + "persona=" + persona + ", telefono=" + telefono + ", email=" + email + ", direccion=" + direccion + '}';
    }

}
