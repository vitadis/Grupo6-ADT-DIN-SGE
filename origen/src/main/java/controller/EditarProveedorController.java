/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import exceptions.DaoException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Contacto;
import model.Proveedor;
import services.ComprasService;

/**
 * Controlador de la ventana {@code editarProveedor.fxml}.
 *
 * La misma ventana sirve para dar de alta un proveedor y para editarlo:
 * <ul>
 * <li>Si antes de mostrarla se llama a {@link #setProveedor(Proveedor)}, funciona en modo edición.</li>
 * <li>Si no, funciona en modo alta.</li>
 * </ul>
 * Al pulsar Guardar crea el {@link Proveedor} y lo guarda con {@link ComprasService}, que lo
 * valida con {@link Proveedor#validar()}.
 * Quien la abre ({@link ComprasController}) consulta {@link #isGuardado()} al cerrarse.
 *
 * @author anazk
 */
public class EditarProveedorController implements Initializable {

    /**
     * Nombres de los días tal y como se guardan en MongoDB (sin tildes), en el mismo orden que {@link #checksDias}.
     */
    private static final String[] DIAS = {"lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"};

    /**
     * Service para validar y guardar el proveedor.
     */
    private final ComprasService comprasService = new ComprasService();

    /**
     * Proveedor que se está editando; es {@code null} cuando la ventana se usa para dar de alta uno nuevo.
     */
    private Proveedor proveedor;
    /**
     * Indica si se ha guardado el proveedor antes de cerrar la ventana.
     */
    private boolean guardado = false;
    /**
     * CheckBox de los días laborales, en el mismo orden que {@link #DIAS}.
     */
    private CheckBox[] checksDias;

    /**
     * Título de la ventana: «NUEVO PROVEEDOR» o «EDITAR PROVEEDOR».
     */
    @FXML
    private Label lblTitulo;
    /**
     * Nombre del proveedor (obligatorio).
     */
    @FXML
    private TextField txtNombre;
    /**
     * NIF o CIF del proveedor (obligatorio).
     */
    @FXML
    private TextField txtNifCif;
    /**
     * IBAN del proveedor.
     */
    @FXML
    private TextField txtIban;
    /**
     * Plazo de entrega en días (número entero, no negativo).
     */
    @FXML
    private TextField txtPlazoEntrega;
    /**
     * Día laboral: lunes.
     */
    @FXML
    private CheckBox chkLunes;
    /**
     * Día laboral: martes.
     */
    @FXML
    private CheckBox chkMartes;
    /**
     * Día laboral: miércoles.
     */
    @FXML
    private CheckBox chkMiercoles;
    /**
     * Día laboral: jueves.
     */
    @FXML
    private CheckBox chkJueves;
    /**
     * Día laboral: viernes.
     */
    @FXML
    private CheckBox chkViernes;
    /**
     * Día laboral: sábado.
     */
    @FXML
    private CheckBox chkSabado;
    /**
     * Día laboral: domingo.
     */
    @FXML
    private CheckBox chkDomingo;
    /**
     * Indica si el proveedor está activo.
     */
    @FXML
    private CheckBox chkActivo;
    /**
     * Nombre de la persona de contacto.
     */
    @FXML
    private TextField txtPersona;
    /**
     * Teléfono de contacto.
     */
    @FXML
    private TextField txtTelefono;
    /**
     * Correo electrónico de contacto.
     */
    @FXML
    private TextField txtEmail;
    /**
     * Dirección del proveedor.
     */
    @FXML
    private TextField txtDireccion;
    /**
     * Muestra los errores de validación o de la base de datos.
     */
    @FXML
    private Label lblError;
    /**
     * Cierra la ventana sin guardar.
     */
    @FXML
    private Button btnCancelar;
    /**
     * Valida y guarda el proveedor.
     */
    @FXML
    private Button btnGuardar;

    /**
     * Constructor vacío. Lo usa JavaFX al cargar {@code editarProveedor.fxml}.
     */
    public EditarProveedorController() {
    }

    /**
     * Prepara la ventana en modo alta: agrupa los CheckBox de los días y pone el título.
     *
     * @param url ubicación del FXML (no se usa)
     * @param rb recursos de idioma (no se usa)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        checksDias = new CheckBox[]{chkLunes, chkMartes, chkMiercoles, chkJueves, chkViernes, chkSabado, chkDomingo};
        lblTitulo.setText("NUEVO PROVEEDOR");
    }

    /**
     * Pasa la ventana a modo edición y rellena los campos con los datos del proveedor.
     * Lo llama {@link ComprasController} antes de mostrar la ventana.
     *
     * @param p proveedor que se va a editar
     */
    public void setProveedor(Proveedor p) {
        this.proveedor = p;
        lblTitulo.setText("EDITAR PROVEEDOR");

        txtNombre.setText(p.getNombre());
        txtNifCif.setText(p.getNif_cif());
        txtIban.setText(p.getIban());
        txtPlazoEntrega.setText(String.valueOf(p.getPlazo_entrega()));
        chkActivo.setSelected(p.isActivo());

        for (int i = 0; i < DIAS.length; i++) {
            checksDias[i].setSelected(p.getDias_laborales().contains(DIAS[i]));
        }

        if (p.getContacto() != null) {
            txtPersona.setText(p.getContacto().getPersona());
            txtTelefono.setText(p.getContacto().getTelefono());
            txtEmail.setText(p.getContacto().getEmail());
            txtDireccion.setText(p.getContacto().getDireccion());
        }
    }

    /**
     * Indica si el usuario ha guardado el proveedor.
     *
     * @return {@code true} si se guardó en la base de datos
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Crea un {@link Proveedor} con los datos del formulario y lo da de alta o lo actualiza
     * según el modo. Aquí solo se comprueba que el plazo es un número; el resto lo valida el
     * modelo. Si todo va bien cierra la ventana; si no, muestra el error en {@link #lblError}.
     *
     * @param event evento del botón Guardar
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        lblError.setText("");

        int plazo = 0;
        if (!txtPlazoEntrega.getText().trim().isEmpty()) {
            try {
                plazo = Integer.parseInt(txtPlazoEntrega.getText().trim());
            } catch (NumberFormatException e) {
                lblError.setText("El plazo de entrega debe ser un número entero.");
                return;
            }
        }

        List<String> dias = new ArrayList<>();
        for (int i = 0; i < DIAS.length; i++) {
            if (checksDias[i].isSelected()) {
                dias.add(DIAS[i]);
            }
        }

        Contacto contacto = new Contacto();
        contacto.setPersona(txtPersona.getText().trim());
        contacto.setTelefono(txtTelefono.getText().trim());
        contacto.setEmail(txtEmail.getText().trim());
        contacto.setDireccion(txtDireccion.getText().trim());

        Proveedor datos = new Proveedor();
        // con id 0 el service lo da de alta; con el id del original lo actualiza
        if (proveedor != null) {
            datos.setId(proveedor.getId());
        }
        datos.setNombre(txtNombre.getText().trim());
        datos.setNif_cif(txtNifCif.getText().trim());
        datos.setIban(txtIban.getText().trim());
        datos.setPlazo_entrega(plazo);
        datos.setDias_laborales(dias);
        datos.setActivo(chkActivo.isSelected());
        datos.setContacto(contacto);

        try {
            // valida con Proveedor.validar() y Contacto.validar() y guarda
            comprasService.guardarProveedor(datos);
            guardado = true;
            cerrar(event);
        } catch (DaoException e) {
            lblError.setText(e.getMessage());
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        }
    }

    /**
     * Cierra la ventana sin guardar.
     *
     * @param event evento del botón Cancelar
     */
    @FXML
    private void onCancelar(ActionEvent event) {
        cerrar(event);
    }

    /**
     * Cierra la ventana a la que pertenece el botón pulsado.
     *
     * @param event evento del botón pulsado
     */
    private void cerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
