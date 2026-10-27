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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author anazk
 */
public class ComprasController implements Initializable {

    @FXML
    private TextField txtBuscar;
    @FXML
    private Label lblUsuario;
    @FXML
    private Label lblAvatar;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSubtitulo;
    @FXML
    private Button btnGenerarLista;
    @FXML
    private ScrollPane scrollCompras;
    @FXML
    private VBox contenedorCompras;
    @FXML
    private Label lblContador;
    @FXML
    private VBox panelSugerencias;
    @FXML
    private Label lblTituloSugerencias;
    @FXML
    private Label lblSubtituloSugerencias;
    @FXML
    private ScrollPane scrollSugerencias;
    @FXML
    private VBox contenedorSugerencias;
    @FXML
    private Button btnCrearCompra;
    @FXML
    private Label lblRuta;
    @FXML
    private HBox contenedorBotonesAccion;
    @FXML
    private Button btnAccionPrincipal;
    @FXML
    private TabPane tabPaneCompras;
    @FXML
    private Tab tabCompras;
    @FXML
    private Tab tabProveedores;
    @FXML
    private ScrollPane scrollListaProveedores;
    @FXML
    private VBox contenedorListaProveedores;
    @FXML
    private StackPane avatarProveedor;
    @FXML
    private Label lblInicialesProveedor;
    @FXML
    private Label lblNombreProveedor;
    @FXML
    private Label lblContactoProveedor;
    @FXML
    private Label lblCondicionesProveedor;
    @FXML
    private Button btnEditarProveedor;
    @FXML
    private Label lblSubtituloHistorial;
    @FXML
    private ScrollPane scrollHistorialProveedor;
    @FXML
    private VBox contenedorHistorialProveedor;
    @FXML
    private Label lblSubtituloComparacion;
    @FXML
    private HBox cabeceraComparacionProveedor;
    @FXML
    private HBox contenedorCabeceraProveedores;
    @FXML
    private ScrollPane scrollComparacionProveedor;
    @FXML
    private VBox contenedorComparacionProveedor;
    @FXML
    private Tab tabCompararPrecios;
    @FXML
    private ScrollPane scrollCompararPrecios;
    @FXML
    private VBox contenedorCompararPrecios;
    @FXML
    private Label lblContadorComparar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onGenerarLista(ActionEvent event) {
    }


    @FXML
    private void onCrearCompraConLista(ActionEvent event) {
    }

    @FXML
    private void onAccionPrincipal(ActionEvent event) {
    }

    @FXML
    private void onEditarProveedor(ActionEvent event) {
    }
    
}
