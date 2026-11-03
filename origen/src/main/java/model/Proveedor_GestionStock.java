/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;
<<<<<<< HEAD
import org.bson.codecs.pojo.annotations.BsonProperty;

/**
 *
 * @author Hodei.Torres
 */
public class Proveedor_GestionStock {

    private Integer id;                       // se mapea solo a "_id"
    private String nombre;
    @BsonProperty("nif_cif")
    private String nifCif;
    private Contacto contacto;
    @BsonProperty("plazo_entrega")
    private int plazoEntrega;
    @BsonProperty("dias_laborales")
    private List<String> diasLaborales = new ArrayList<>();
    private String iban;
    private boolean activo;

    public static class Contacto {

        private String persona;
        private String telefono;
        private String email;
        private String direccion;

        public Contacto() {
        }

        public String getPersona() {
            return persona;
        }

        public void setPersona(String persona) {
            this.persona = persona;
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

        public String getDireccion() {
            return direccion;
        }

        public void setDireccion(String direccion) {
            this.direccion = direccion;
        }
    }

    public Proveedor_GestionStock() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

=======

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
>>>>>>> origin/dev
    public String getNombre() {
        return nombre;
    }

<<<<<<< HEAD
=======
    /**
     * Cambia el nombre o razón social del proveedor.
     *
     * @param nombre el nombre o razón social del proveedor
     */
>>>>>>> origin/dev
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

<<<<<<< HEAD
    public String getNifCif() {
        return nifCif;
    }

    public void setNifCif(String nifCif) {
        this.nifCif = nifCif;
    }

=======
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
>>>>>>> origin/dev
    public Contacto getContacto() {
        return contacto;
    }

<<<<<<< HEAD
=======
    /**
     * Cambia los datos de la persona de contacto.
     *
     * @param contacto los datos de la persona de contacto
     */
>>>>>>> origin/dev
    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

<<<<<<< HEAD
    public int getPlazoEntrega() {
        return plazoEntrega;
    }

    public void setPlazoEntrega(int plazoEntrega) {
        this.plazoEntrega = plazoEntrega;
    }

    public List<String> getDiasLaborales() {
        return diasLaborales;
    }

    public void setDiasLaborales(List<String> diasLaborales) {
        this.diasLaborales = diasLaborales;
    }

=======
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
>>>>>>> origin/dev
    public String getIban() {
        return iban;
    }

<<<<<<< HEAD
=======
    /**
     * Cambia el IBAN de la cuenta del proveedor.
     *
     * @param iban el IBAN de la cuenta del proveedor
     */
>>>>>>> origin/dev
    public void setIban(String iban) {
        this.iban = iban;
    }

<<<<<<< HEAD
=======
    /**
     * Indica si el proveedor está activo.
     *
     * @return {@code true} si el proveedor está activo
     */
>>>>>>> origin/dev
    public boolean isActivo() {
        return activo;
    }

<<<<<<< HEAD
=======
    /**
     * Marca el proveedor como activo o inactivo.
     *
     * @param activo {@code true} si el proveedor está activo
     */
>>>>>>> origin/dev
    public void setActivo(boolean activo) {
        this.activo = activo;
    }

<<<<<<< HEAD
=======
    /**
     * Devuelve el nombre del proveedor.
     * Los ComboBox de JavaFX muestran lo que devuelve este método, así que se ve el nombre.
     *
     * @return el nombre del proveedor
     */
>>>>>>> origin/dev
    @Override
    public String toString() {
        return nombre;
    }
<<<<<<< HEAD
=======

>>>>>>> origin/dev
}
