package controller;

import dao.GestionStockDao;
import dao.implement.GestionStockDaoImp;
import exceptions.DaoException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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
import model.MovimientoStock;
import model.Producto;

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

    private final GestionStockDao stockDao = GestionStockDaoImp.getInstance();

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
            List<Producto> lista = stockDao.listarProductos();
            comboProducto.setItems(FXCollections.observableArrayList(lista));
            if (!lista.isEmpty()) {
                comboProducto.getSelectionModel().selectFirst();
            }
        } catch (DaoException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Error al cargar productos: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void guardarPerdida(ActionEvent event) {
        Producto seleccionado = comboProducto.getValue();
        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un producto.").showAndWait();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR,
                    "La cantidad debe ser un número entero válido.").showAndWait();
            return;
        }

        if (cantidad <= 0) {
            new Alert(Alert.AlertType.WARNING,
                    "La cantidad debe ser mayor que cero.").showAndWait();
            return;
        }
        if (cantidad > seleccionado.getStock_actual()) {
            new Alert(Alert.AlertType.WARNING,
                    "Solo hay " + seleccionado.getStock_actual() + " unidades en stock.").showAndWait();
            return;
        }

        try {
            String nota = txtNota.getText() == null ? "" : txtNota.getText().trim();
            MovimientoStock mov = new MovimientoStock(
                    seleccionado.getId(), seleccionado.getNombre(), "PERDIDA", cantidad, nota);

            stockDao.registrarPerdida(mov, seleccionado);
            cerrarVentana(event);
        } catch (DaoException e) {
            e.printStackTrace();
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
