/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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
        // tener un interprete de permisos, pedir, el usurio, 
        // puedo tener un getUser acceder a los permisos y crear las vistas 
        // a corde a eso
        List<String> ocultar = List.of("Gestión stock", "Administración");

        vbContenido.getChildren().removeIf(nodo
                -> nodo instanceof ToggleButton && ocultar.contains(((ToggleButton) nodo).getText())
        );
    }

    @FXML
    private void irInicio(ActionEvent event) {
    }

    @FXML
    private void irCaja(ActionEvent event) {
    }

    @FXML
    private void irEconomia(ActionEvent event) {
    }

    @FXML
    private void irStock(ActionEvent event) {
    }

    @FXML
    private void irCompras(ActionEvent event) {
    }

    @FXML
    private void irAnalisis(ActionEvent event) {
    }

    @FXML
    private void irAdmin(ActionEvent event) {
    }

    @FXML
    private void abrirPerfil(ActionEvent event) {
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
    }

}
