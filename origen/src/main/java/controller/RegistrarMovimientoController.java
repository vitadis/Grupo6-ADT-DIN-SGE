/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import exceptions.DaoException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Movimiento;
import services.MovimientoService;

/**
 * Controlador de la ventana {@code registrarMovimiento.fxml}.
 *
 * Sirve para anotar un ingreso o un gasto que no es ni una compra ni una venta de la caja
 * (luz, nóminas, alquiler...). Lo abre {@link GestionEconomicaController}, que antes llama a
 * {@link #setDatos(int, List)} y, al cerrarse, consulta {@link #isGuardado()}.
 *
 * @author anazk
 */
public class RegistrarMovimientoController implements Initializable {

    /**
     * Categorías que se ofrecen siempre en el desplegable. También se puede escribir otra.
     */
    private static final String[] CATEGORIAS = {"Alquiler", "Personal", "Suministros", "Mantenimiento",
        "Seguros", "Impuestos", "Servicios", "Eventos", "Subvenciones", "Otros"};

    /**
     * Service para validar y guardar el movimiento.
     */
    private final MovimientoService movimientoService = new MovimientoService();

    /**
     * Movimientos que ya había, para calcular la media de cada categoría.
     */
    private final List<Movimiento> anteriores = new ArrayList<>();
    /**
     * Identificador del usuario que registra el movimiento.
     */
    private int usuarioId;
    /**
     * Indica si se ha guardado el movimiento antes de cerrar la ventana.
     */
    private boolean guardado = false;

    /**
     * Agrupa los dos botones de tipo para que solo se pueda elegir uno.
     */
    @FXML
    private ToggleGroup grupoTipo;
    /**
     * Tipo de movimiento: ingreso.
     */
    @FXML
    private ToggleButton tgIngreso;
    /**
     * Tipo de movimiento: gasto (seleccionado al abrir).
     */
    @FXML
    private ToggleButton tgGasto;
    /**
     * Concepto del movimiento (obligatorio).
     */
    @FXML
    private TextField txtConcepto;
    /**
     * Importe en euros (obligatorio, mayor que 0).
     */
    @FXML
    private TextField txtImporte;
    /**
     * Fecha del movimiento (obligatoria; hoy al abrir).
     */
    @FXML
    private DatePicker dpFecha;
    /**
     * Categoría: se elige de la lista o se escribe una nueva.
     */
    @FXML
    private ComboBox<String> cbCategoria;
    /**
     * Observaciones (opcional).
     */
    @FXML
    private TextArea txtObservaciones;
    /**
     * Caja ámbar del aviso de importe superior a la media.
     */
    @FXML
    private VBox cajaAviso;
    /**
     * Texto del aviso de importe superior a la media.
     */
    @FXML
    private Label lblAviso;
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
     * Valida y guarda el movimiento.
     */
    @FXML
    private Button btnGuardar;

    /**
     * Constructor vacío. Lo usa JavaFX al cargar {@code registrarMovimiento.fxml}.
     */
    public RegistrarMovimientoController() {
    }

    /**
     * Pone la fecha de hoy y carga las categorías por defecto.
     *
     * @param url ubicación del FXML (no se usa)
     * @param rb recursos de idioma (no se usa)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dpFecha.setValue(LocalDate.now());
        cbCategoria.getItems().addAll(CATEGORIAS);
    }

    /**
     * Recibe el usuario que registra el movimiento y los movimientos que ya había. Las
     * categorías de esos movimientos que no estén en la lista se añaden al desplegable.
     * Lo llama {@link GestionEconomicaController} antes de mostrar la ventana.
     *
     * @param usuarioId identificador del usuario con la sesión iniciada
     * @param movimientos movimientos que ya había
     */
    public void setDatos(int usuarioId, List<Movimiento> movimientos) {
        this.usuarioId = usuarioId;
        anteriores.clear();
        anteriores.addAll(movimientos);

        for (Movimiento m : movimientos) {
            if (!cbCategoria.getItems().contains(m.getCategoria())) {
                cbCategoria.getItems().add(m.getCategoria());
            }
        }
    }

    /**
     * Indica si el usuario ha guardado el movimiento.
     *
     * @return {@code true} si se guardó en la base de datos
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Enseña el aviso ámbar si el movimiento es un gasto y su importe es mayor que la media de
     * los gastos anteriores de la misma categoría; si no, lo esconde. Se llama al cambiar el
     * tipo, el importe o la categoría.
     *
     * @param event evento del control que ha cambiado
     */
    @FXML
    private void onRevisarAviso(Event event) {
        mostrarAviso(false);

        String categoria = leerCategoria();
        if (!tgGasto.isSelected() || categoria.isEmpty()) {
            return;
        }

        double importe;
        try {
            importe = leerImporte();
        } catch (NumberFormatException e) {
            return;
        }

        double suma = 0;
        int cuantos = 0;
        for (Movimiento m : anteriores) {
            if (!m.esIngreso() && categoria.equalsIgnoreCase(m.getCategoria())) {
                suma += m.getImporte();
                cuantos++;
            }
        }

        if (cuantos > 0) {
            double media = suma / cuantos;
            if (importe > media) {
                lblAviso.setText(String.format("▲ Importe superior a la media de «%s» (%,.2f €).", categoria, media));
                mostrarAviso(true);
            }
        }
    }

    /**
     * Crea el {@link Movimiento} con los datos del formulario y lo guarda con
     * {@link MovimientoService}, que lo valida con {@link Movimiento#validar()}. Aquí solo se
     * comprueba que el importe es un número. Si todo va bien cierra la ventana; si no, muestra el
     * error en {@link #lblError}.
     *
     * @param event evento del botón Guardar movimiento
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        lblError.setText("");

        double importe;
        try {
            importe = leerImporte();
        } catch (NumberFormatException e) {
            lblError.setText("El importe debe ser un número.");
            return;
        }

        Movimiento m = new Movimiento();
        // si no hay tipo o fecha se quedan en null y validar() da el error
        if (tgIngreso.isSelected()) {
            m.setTipo("ingreso");
        } else if (tgGasto.isSelected()) {
            m.setTipo("gasto");
        }
        if (dpFecha.getValue() != null) {
            m.setFecha(dpFecha.getValue().toString());
        }
        m.setConcepto(txtConcepto.getText().trim());
        m.setCategoria(leerCategoria());
        m.setImporte(Math.round(importe * 100) / 100.0);
        m.setUsuario_id(usuarioId);
        if (!txtObservaciones.getText().trim().isEmpty()) {
            m.setObservaciones(txtObservaciones.getText().trim());
        }

        try {
            movimientoService.registrarMovimiento(m);
            guardado = true;
            cerrar(event);
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        } catch (DaoException e) {
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
     * Lee el importe del campo de texto. Acepta la coma como separador decimal.
     *
     * @return el importe escrito
     * @throws NumberFormatException si el texto no es un número
     */
    private double leerImporte() {
        return Double.parseDouble(txtImporte.getText().trim().replace(',', '.'));
    }

    /**
     * Lee la categoría. Como el ComboBox es editable, se lee el texto escrito, que también
     * contiene la opción elegida de la lista.
     *
     * @return la categoría, sin espacios al principio ni al final (vacía si no hay)
     */
    private String leerCategoria() {
        String texto = cbCategoria.getEditor().getText();
        if (texto == null) {
            return "";
        }
        return texto.trim();
    }

    /**
     * Enseña o esconde la caja del aviso. Con {@code managed} a {@code false} no ocupa sitio.
     *
     * @param mostrar {@code true} para enseñarla
     */
    private void mostrarAviso(boolean mostrar) {
        cajaAviso.setVisible(mostrar);
        cajaAviso.setManaged(mostrar);
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
