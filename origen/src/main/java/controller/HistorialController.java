/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import exceptions.DaoException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.EstadoPedido;
import model.LineaPedido;
import model.MetodoPago;
import model.VentaPedido;
import services.CajaService;
import services.PedidoService;
import util.AppLogger;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class HistorialController implements Initializable {

    private CajaService cajaService;
    private final AppLogger logger = AppLogger.getInstance();

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
    // la columna trabaja con la lista ya mapeada a texto
    @FXML
    private TableColumn<VentaPedido, List<String>> tcDetalle;
    @FXML
    private TableColumn<VentaPedido, Double> tcTotal;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarTabla();
    }

    // Linea de la lista
    private String getString(LineaPedido linea) {
        return linea.getNombre() + " x" + linea.getCantidad();
    }

    // mapeo detalle
    private List<String> mapearDetalle(VentaPedido pedido) {
        List<LineaPedido> lineas = pedido.getDetalle();
        if (lineas == null) {
            return List.of();
        }
        return lineas.stream()
                .map(this::getString)
                .collect(Collectors.toList());
    }

    private void cargarTabla() {
        try {
            List<VentaPedido> pedidos = cajaService.listaPedidos();
            ObservableList<VentaPedido> listaPedidos = FXCollections.observableArrayList(pedidos);
            // definir el valor 
            tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
            tcUsuarioId.setCellValueFactory(new PropertyValueFactory<>("usuario_id"));
            tcFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            tcEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            tcMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodo_pago"));
            tcTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

            // asignar a la columna la lista ya mapeada (List<String>)
            tcDetalle.setCellValueFactory(new Callback<CellDataFeatures<VentaPedido, List<String>>, ObservableValue<List<String>>>() {
                @Override
                public ObservableValue<List<String>> call(CellDataFeatures<VentaPedido, List<String>> cell) {
                    return new SimpleObjectProperty<>(mapearDetalle(cell.getValue()));
                }
            });

            // detalle dentro de comboBox
            tcDetalle.setCellFactory(new Callback<TableColumn<VentaPedido, List<String>>, TableCell<VentaPedido, List<String>>>() {
                @Override
                public TableCell<VentaPedido, List<String>> call(TableColumn<VentaPedido, List<String>> col) {
                    return new TableCell<VentaPedido, List<String>>() {

                        private final ComboBox<String> comboDetalle = new ComboBox<>();

                        @Override
                        protected void updateItem(List<String> detalle, boolean empty) {
                            super.updateItem(detalle, empty);

                            if (empty || detalle == null || detalle.isEmpty()) {
                                setGraphic(null);
                                return;
                            }

                            // cargar la lista dentro del combobox
                            comboDetalle.setItems(FXCollections.observableArrayList(detalle));

                            comboDetalle.setPromptText("Ver detalle (" + detalle.size() + ")");
                            comboDetalle.getSelectionModel().clearSelection();

                            setGraphic(comboDetalle);
                        }
                    };
                }
            });

            tvPedido.setItems(listaPedidos);

        } catch (DaoException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            util.Alertas.mostrarError(e.getMessage());
        }
    }
    
    // set service
    public void setCajaService(CajaService servicio){
        this.cajaService = servicio;
    }
}
