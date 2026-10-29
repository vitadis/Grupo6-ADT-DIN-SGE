/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import dao.ProductoDao;
import dao.implement.ProductoDaoImp;
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
import model.Producto;

/**
 * FXML Controller class
 *
 * @author Hodei.Torres
 */
public class RegistrarPerdidaController implements Initializable {

    @FXML private Label titulo;
    @FXML private ComboBox<Producto> comboProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtNota;

    private final ProductoDao productoDao = ProductoDaoImp.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarFormatoCombo();
        cargarProductos();
    }

    /**
     * Muestra solo el nombre del producto en la vista del ComboBox,
     * pero conserva el objeto Producto completo en la selección.
     */
    private void configurarFormatoCombo() {
        comboProducto.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto p) {
                return (p != null) ? p.getNombre() : "";
            }

            @Override
            public Producto fromString(String string) {
                // No se necesita convertir texto a objeto a menos que sea editable
                return null;
            }
        });
    }

    /**
     * Carga los productos desde el DAO. Alternativamente, puedes exponer un método público
     * setProductos(List<Producto> lista) si prefieres pasarlos directamente desde GestionStockController.
     */
    public void cargarProductos() {
        try {
            List<Producto> lista = productoDao.listarProductos();
            comboProducto.setItems(FXCollections.observableArrayList(lista));
            if (!lista.isEmpty()) {
                comboProducto.getSelectionModel().selectFirst();
            }
        } catch (DaoException e) {
            new Alert(Alert.AlertType.ERROR, "Error al cargar productos: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void guardarPerdida(ActionEvent event) {
        // Obtener el objeto Producto seleccionado completo
        Producto seleccionado = comboProducto.getValue();

        if (seleccionado == null) {
            new Alert(Alert.AlertType.WARNING, "Selecciona un producto").showAndWait();
            return;
        }

        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            String nota = txtNota.getText();

            // Aquí ejecutas la lógica de persistencia o actualización hacia MongoDB / DAO:
            // Por ejemplo: perdidaDao.registrar(seleccionado.getId(), cantidad, nota);

            cerrarVentana(event);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "La cantidad debe ser un número entero válido").showAndWait();
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
