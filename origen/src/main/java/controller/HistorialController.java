/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import exceptions.DaoException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.EstadoPedido;
import model.MetodoPago;
import model.VentaPedido;
import services.PedidoService;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class HistorialController implements Initializable {

    private final PedidoService pedidoService = new PedidoService();

    @FXML
    private TableView<VentaPedido> tvPedido;
    @FXML
    private TableColumn<VentaPedido, Integer> tcId;
    @FXML
    private TableColumn<VentaPedido, Integer> tcUsuarioId;
    @FXML
    private TableColumn<VentaPedido, String> tcFecha;
    @FXML
    private TableColumn<VentaPedido, EstadoPedido> tcEstado;
    @FXML
    private TableColumn<VentaPedido, MetodoPago> tcMetodoPago;
    @FXML
    private TableColumn<VentaPedido, String> tcDetalle;
    @FXML
    private TableColumn<VentaPedido, Double> tcTotal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTabla();

    }

    private void cargarTabla() {
        try {
            List<VentaPedido> pedidos = pedidoService.listaPedidos();
            ObservableList<VentaPedido> listaPedidos = FXCollections.observableArrayList(pedidos);
            // definir el valor 
            tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
            tcUsuarioId.setCellValueFactory(new PropertyValueFactory<>("usuario_id"));
            tcFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            tcEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            tcMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodo_pago"));
            tcDetalle.setCellValueFactory(new PropertyValueFactory<>("detalle"));
            tcTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

            tvPedido.setItems(listaPedidos);

        } catch (DaoException e) {
            System.out.println(e.getMessage());
        }

    }

}
