/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
/**
 * FXML Controller class
 *
 * @author Joel
 */
public class NavController implements Initializable {


    @FXML
    private HBox btnInicio;
    @FXML
    private HBox btnCaja;
    @FXML
    private HBox btnEconomia;
    @FXML
    private HBox btnStock;
    @FXML
    private HBox btnCompras;
    @FXML
    private HBox btnAnalisis;
    @FXML
    private HBox btnAdmin;
    @FXML
    private HBox btnPerfil;
    @FXML
    private HBox btnCerrarSesion;
    @FXML
    private StackPane contenido;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void irInicio(MouseEvent event) {
    }

    @FXML
    private void irCaja(MouseEvent event) {
    }

    @FXML
    private void irEconomia(MouseEvent event) {
    }

    @FXML
    private void irStock(MouseEvent event) {
    }

    @FXML
    private void irCompras(MouseEvent event) {
    }

    @FXML
    private void irAnalisis(MouseEvent event) {
    }

    @FXML
    private void irAdmin(MouseEvent event) {
    }

    @FXML
    private void abrirPerfil(MouseEvent event) {
    }

    @FXML
    private void cerrarSesion(MouseEvent event) {
    }

}
