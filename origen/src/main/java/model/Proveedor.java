/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Proveedor del bar.
 *
 * Corresponde a un documento de la colección {@code proveedores} de MongoDB. Los nombres de los
 * atributos coinciden con los de los campos para que el driver los rellene automáticamente.
 *
 * @author anazk
 */
public class Proveedor {

    /**
     * Los días laborales permitidos, tal y como se guardan en MongoDB (sin tildes).
     */
    private static final List<String> DIAS_VALIDOS = Arrays.asList(
            "lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo");

    /**
     * El identificador del proveedor ({@code _id} en MongoDB).
     */
    private int id;
    /**
     * El nombre o razón social del proveedor.
     */
    private String nombre;
    /**
     * El NIF o CIF del proveedor.
     */
    private String nif_cif;
    /**
     * Los datos de la persona de contacto.
     */
    private Contacto contacto;
    /**
     * El plazo de entrega en días.
     */
    private int plazo_entrega;
    /**
     * Los días de la semana en los que reparte ({@code "lunes"}, {@code "martes"}...).
     */
    private List<String> dias_laborales;
    /**
     * El IBAN de la cuenta del proveedor.
     */
    private String iban;
    /**
     * El proveedor está activo.
     */
    private boolean activo;

    /**
     * Constructor vacío.
     * Lo necesita el driver de MongoDB para crear el objeto al leer un documento.
     */
    public Proveedor() {
        this.dias_laborales = new ArrayList<>();
    }

    /**
     * Constructor con todos los datos.
     *
     * @param id el identificador del proveedor ({@code _id} en MongoDB)
     * @param nombre el nombre o razón social del proveedor
     * @param nif_cif el NIF o CIF del proveedor
     * @param contacto los datos de la persona de contacto
     * @param plazo_entrega el plazo de entrega en días
     * @param dias_laborales los días de la semana en los que reparte ({@code "lunes"}, {@code "martes"}...)
     * @param iban el IBAN de la cuenta del proveedor
     * @param activo el proveedor está activo
     * @throws IllegalArgumentException si algún dato no es válido (ver {@link #validar()})
     */
    public Proveedor(int id, String nombre, String nif_cif, Contacto contacto, int plazo_entrega,
            List<String> dias_laborales, String iban, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.nif_cif = nif_cif;
        this.contacto = contacto;
        this.plazo_entrega = plazo_entrega;
        this.dias_laborales = dias_laborales;
        this.iban = iban;
        this.activo = activo;

        validar();
    }

    // reglas del negocio (validaciones)
    /**
     * Comprueba que los datos del proveedor son válidos: nombre y NIF/CIF obligatorios, plazo de
     * entrega no negativo y días laborales escritos como se guardan en MongoDB. Si tiene
     * contacto, también lo valida con {@link Contacto#validar()}.
     *
     * @throws IllegalArgumentException con el mensaje del primer dato que no es válido
     */
    public void validar() {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del proveedor es obligatorio.");
        }

        if (nif_cif == null || nif_cif.isBlank()) {
            throw new IllegalArgumentException(
                    "El NIF/CIF es obligatorio.");
        }

        if (plazo_entrega < 0) {
            throw new IllegalArgumentException(
                    "El plazo de entrega no puede ser negativo.");
        }

        if (dias_laborales == null) {
            throw new IllegalArgumentException(
                    "La lista de días laborales no puede ser nula.");
        }

        for (String dia : dias_laborales) {
            if (!DIAS_VALIDOS.contains(dia)) {
                throw new IllegalArgumentException(
                        "El día laboral '" + dia + "' no es válido.");
            }
        }

        if (contacto != null) {
            contacto.validar();
        }
    }

    /**
     * Devuelve el identificador del proveedor ({@code _id} en MongoDB).
     *
     * @return el identificador del proveedor ({@code _id} en MongoDB)
     */
    public int getId() {
        return id;
    }

    /**
     * Cambia el identificador del proveedor ({@code _id} en MongoDB).
     *
     * @param id el identificador del proveedor ({@code _id} en MongoDB)
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Devuelve el nombre o razón social del proveedor.
     *
     * @return el nombre o razón social del proveedor
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Cambia el nombre o razón social del proveedor.
     *
     * @param nombre el nombre o razón social del proveedor
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Devuelve el NIF o CIF del proveedor.
     *
     * @return el NIF o CIF del proveedor
     */
    public String getNif_cif() {
        return nif_cif;
    }

    /**
     * Cambia el NIF o CIF del proveedor.
     *
     * @param nif_cif el NIF o CIF del proveedor
     */
    public void setNif_cif(String nif_cif) {
        this.nif_cif = nif_cif;
    }

    /**
     * Devuelve los datos de la persona de contacto.
     *
     * @return los datos de la persona de contacto
     */
    public Contacto getContacto() {
        return contacto;
    }

    /**
     * Cambia los datos de la persona de contacto.
     *
     * @param contacto los datos de la persona de contacto
     */
    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    /**
     * Devuelve el plazo de entrega en días.
     *
     * @return el plazo de entrega en días
     */
    public int getPlazo_entrega() {
        return plazo_entrega;
    }

    /**
     * Cambia el plazo de entrega en días.
     *
     * @param plazo_entrega el plazo de entrega en días
     */
    public void setPlazo_entrega(int plazo_entrega) {
        this.plazo_entrega = plazo_entrega;
    }

    /**
     * Devuelve los días de la semana en los que reparte ({@code "lunes"}, {@code "martes"}...).
     *
     * @return los días de la semana en los que reparte ({@code "lunes"}, {@code "martes"}...)
     */
    public List<String> getDias_laborales() {
        return dias_laborales;
    }

    /**
     * Cambia los días de la semana en los que reparte ({@code "lunes"}, {@code "martes"}...).
     *
     * @param dias_laborales los días de la semana en los que reparte ({@code "lunes"}, {@code "martes"}...)
     */
    public void setDias_laborales(List<String> dias_laborales) {
        this.dias_laborales = dias_laborales;
    }

    /**
     * Devuelve el IBAN de la cuenta del proveedor.
     *
     * @return el IBAN de la cuenta del proveedor
     */
    public String getIban() {
        return iban;
    }

    /**
     * Cambia el IBAN de la cuenta del proveedor.
     *
     * @param iban el IBAN de la cuenta del proveedor
     */
    public void setIban(String iban) {
        this.iban = iban;
    }

    /**
     * Indica si el proveedor está activo.
     *
     * @return {@code true} si el proveedor está activo
     */
    public boolean isActivo() {
        return activo;
    }

    /**
     * Marca el proveedor como activo o inactivo.
     *
     * @param activo {@code true} si el proveedor está activo
     */
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Devuelve el nombre del proveedor.
     * Los ComboBox de JavaFX muestran lo que devuelve este método, así que se ve el nombre.
     *
     * @return el nombre del proveedor
     */
    @Override
    public String toString() {
        return nombre;
    }

}
