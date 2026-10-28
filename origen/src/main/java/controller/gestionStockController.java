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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Hodei.Torres
 */
public class gestionStock implements Initializable {

    @FXML
    private Label cantProduc;
    @FXML
    private Label precioStock;
    @FXML
    private Label cantStockBajo;
    @FXML
    private Label cantAgotados;
    @FXML
    private Label cantCaducar;
    @FXML
    private ComboBox<?> categoriaDisplay;
    @FXML
    private ComboBox<?> estadoDisplay;
    @FXML
    private TableView<?> tablaStock;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        // Ajustes en la tabla de GestionStock
        int n = tablaStock.getColumns().size();
        double total = n - 1 + 2.0; 

        for (int i = 0; i < n; i++) {
        double peso = (i == 0) ? 2.0 : 1.0;
        tablaStock.getColumns().get(i).prefWidthProperty()
         .bind(tablaStock.widthProperty().subtract(15).multiply(peso / total));
        }
    }   
    
    private void registrarPerdida(ActionEvent event) {
        Stage regisPerdi = new Stage();
        regisPerdi.setTitle("Registrar perdida");
    }
    
    @FXML
    private void prueba(ActionEvent event) {
        try {
            gestionStock c = InstanciarEscena.createNewStage("/view/gestionStock/gestionStock.fxml", "Gestión stock", null);        
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    
    
    
}
