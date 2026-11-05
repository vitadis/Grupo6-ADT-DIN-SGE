package controller;

import exceptions.DaoException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Producto;
import services.GestionStockService;
import util.AppLogger;

/**
 *
 * @author Hodei.Torres
 */
public class RegistrarPerdidaController implements Initializable {

    @FXML
    private Label titulo;
    @FXML
    private ComboBox<Producto> comboProducto;
    @FXML
    private TextField txtCantidad;
    @FXML
    private TextArea txtNota;

    private final GestionStockService stockService = new GestionStockService();
    private final AppLogger logger = AppLogger.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFormatoCombo();
        cargarProductos();
    }

    /**
     * Muestra nombre y stock disponible, pero conserva el Producto completo.
     */
    private void configurarFormatoCombo() {
        comboProducto.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto p) {
                if (p == null) {
                    return "";
                }
                return p.getNombre() + " (stock: " + p.getStock_actual() + ")";
            }

            @Override
            public Producto fromString(String string) {
                return null;
            }
        });
    }

    public void cargarProductos() {
        try {
            List<Producto> lista = stockService.listarProductos();
            comboProducto.setItems(FXCollections.observableArrayList(lista));
            if (!lista.isEmpty()) {
                comboProducto.getSelectionModel().selectFirst();
            }
        } catch (DaoException e) {
            logger.log(Level.SEVERE, "Error al cargar productos en registrar pérdida", e);
            new Alert(Alert.AlertType.ERROR,
                    "Error al cargar productos: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void guardarPerdida(ActionEvent event) {
        Producto seleccionado = comboProducto.getValue();

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR,
                    "La cantidad debe ser un número entero válido.").showAndWait();
            return;
        }

        try {
            String nota = txtNota.getText() == null ? "" : txtNota.getText().trim();
            stockService.registrarPerdida(seleccionado, cantidad, nota);
            logger.info("Pérdida registrada: " + seleccionado.getNombre() + " (-" + cantidad + "). Nota: " + nota);

            int restante = seleccionado.getStock_actual() - cantidad;
            if (restante == 0) {
                logger.warning("Producto agotado: " + seleccionado.getNombre());
            } else if (restante <= seleccionado.getStock_minimo()) {
                logger.warning("Stock bajo: " + seleccionado.getNombre()
                        + " (existencia " + restante + ", mínimo " + seleccionado.getStock_minimo() + ")");
            }

            cerrarVentana(event);
        } catch (IllegalArgumentException e) {
            logger.warning("Validación rechazada al registrar pérdida: " + e.getMessage());
            new Alert(Alert.AlertType.WARNING, e.getMessage()).showAndWait();
        } catch (DaoException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error al registrar pérdida: " + e.getMessage(), e);
            String causa = (e.getCause() != null) ? "\n" + e.getCause().getMessage() : "";
            new Alert(Alert.AlertType.ERROR, e.getMessage() + causa).showAndWait();
        }
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
