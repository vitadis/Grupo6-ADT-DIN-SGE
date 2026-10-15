/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.InterpretePermisos;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class NavController implements Initializable {

    @FXML
    private StackPane contenido;
    @FXML
    private VBox vbContenido;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // modificar el usuario y tener un get
        List<String> permisos = List.of("caja", "stock", "gestion.ingresosGastos", "administracion", "inicio");
        
        List<String> sinAcceso = InterpretePermisos.apartadosSinAcceso(permisos);
        if (!sinAcceso.isEmpty()) {
            vbContenido.getChildren().removeIf(nodo
                    -> nodo instanceof ToggleButton && sinAcceso.contains(((ToggleButton) nodo).getText())
            );
        }
    }

    @FXML
    private void irInicio(ActionEvent event) {
        seleccionarOpcion((ToggleButton) event.getSource());
        contenido.setStyle("-fx-background-color: red;");
        
    }

    @FXML
    private void irCaja(ActionEvent event) {
        seleccionarOpcion((ToggleButton) event.getSource());
        contenido.setStyle("-fx-background-color: blue;");
    }

    @FXML
    private void irEconomia(ActionEvent event) {
        seleccionarOpcion((ToggleButton) event.getSource());
    }

    @FXML
    private void irStock(ActionEvent event) {
        seleccionarOpcion((ToggleButton) event.getSource());
    }

    @FXML
    private void irCompras(ActionEvent event) {
        seleccionarOpcion((ToggleButton) event.getSource());
    }

    @FXML
    private void irAnalisis(ActionEvent event) {
        seleccionarOpcion((ToggleButton) event.getSource());
    }

    @FXML
    private void irAdmin(ActionEvent event) {
        seleccionarOpcion((ToggleButton) event.getSource());
    }

    @FXML
    private void abrirPerfil(ActionEvent event) {

    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            Button btnCS = (Button) event.getSource();
            Stage stage = (Stage) btnCS.getScene().getWindow();

            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.initOwner(stage);
            alerta.setTitle("Cerrar sesión");
            alerta.setHeaderText(null);
            alerta.setContentText("¿Seguro que quieres cerrar sesión?");

            if (alerta.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
                InstanciarEscena.cambiarVista(stage, "/view/loginView.fxml", "Inicio Sesión");
            }
        } catch (IOException ex) {
            InstanciarEscena.mostrarError("No se pudo cerrar sesión: " + ex.getMessage());
        }
    }

    // metodos auxiliares
    private void seleccionarOpcion(ToggleButton elegido) {
        vbContenido.getChildren().stream()
                .filter(n -> n instanceof ToggleButton)
                .map(n -> (ToggleButton) n)
                .forEach(tb -> tb.setSelected(tb == elegido));
    }
}
