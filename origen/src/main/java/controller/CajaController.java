/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
/**
 * FXML Controller class
 *
 * @author Joel
 */
public class CajaController implements Initializable {


    @FXML
    private ToggleGroup grupoCategorias;
    @FXML
    private HBox contenidoCaja;
    @FXML
    private Label lblMigas;
    @FXML
    private Label lblSubtituloPedido;
    @FXML
    private Button btnHistorial;
    @FXML
    private FlowPane boxCategorias;
    @FXML
    private RadioButton rbTodo;
    @FXML
    private FlowPane gridProductos;
    @FXML
    private VBox panelPedido;
    @FXML
    private StackPane capaHistorial;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void mostrarHistorial(ActionEvent event) {
    }

}
