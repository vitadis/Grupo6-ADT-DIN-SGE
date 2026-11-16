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
import util.AppLogger;

/**
 * FXML Controller class para la visualización del historial de ventas.
 *
 * @author Joel
 */
public class HistorialController implements Initializable {

    // ---- Servicios y estado ----
    private CajaService cajaService;
    private final AppLogger logger = AppLogger.getInstance();

    // ---- Tabla de pedidos ----
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
    private TableColumn<VentaPedido, List<String>> tcDetalle;
    @FXML
    private TableColumn<VentaPedido, Double> tcTotal;

    /**
     * Inicializa el controlador de la vista de historial.
     *
     * @param url ubicación utilizada para resolver rutas relativas
     * @param rb recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    // ---- Mapeo de detalle ----
    /**
     * Formatea una línea de pedido mostrando el nombre del producto y su
     * cantidad.
     *
     * @param linea línea de pedido a formatear
     * @return texto con el nombre y cantidad del producto
     */
    private String getString(LineaPedido linea) {
        return linea.getNombre() + " x" + linea.getCantidad();
    }

    /**
     * Transforma la lista de líneas de un pedido en una lista de descripciones
     * textuales.
     *
     * @param pedido pedido del que extraer el detalle
     * @return lista de textos formateados o lista vacía si no tiene detalle
     */
    private List<String> mapearDetalle(VentaPedido pedido) {
        List<LineaPedido> lineas = pedido.getDetalle();
        if (lineas == null) {
            return List.of();
        }
        return lineas.stream()
                .map(this::getString)
                .collect(Collectors.toList());
    }

    // ---- Carga de tabla ----
    /**
     * Obtiene los pedidos desde el servicio, configura las columnas y puebla la
     * tabla.
     */
    private void cargarTabla() {
        if (cajaService == null) {
            logger.log(Level.WARNING, "Intento de cargar historial sin servicio de caja inicializado.");
            return;
        }

        try {
            List<VentaPedido> pedidos = cajaService.listaPedidos();
            ObservableList<VentaPedido> listaPedidos = FXCollections.observableArrayList(pedidos);

            tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
            tcUsuarioId.setCellValueFactory(new PropertyValueFactory<>("usuario_id"));
            tcFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
            tcEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
            tcMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodo_pago"));
            tcTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

            tcDetalle.setCellValueFactory(new Callback<CellDataFeatures<VentaPedido, List<String>>, ObservableValue<List<String>>>() {
                @Override
                public ObservableValue<List<String>> call(CellDataFeatures<VentaPedido, List<String>> cell) {
                    return new SimpleObjectProperty<>(mapearDetalle(cell.getValue()));
                }
            });

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

                            comboDetalle.setItems(FXCollections.observableArrayList(detalle));

                            comboDetalle.setPromptText("Ver detalle (" + detalle.size() + ")");
                            comboDetalle.getSelectionModel().clearSelection();

                            setGraphic(comboDetalle);
                        }
                    };
                }
            });

            tvPedido.setItems(listaPedidos);
            logger.info("Historial de pedidos cargado: " + pedidos.size() + " registros.");

        } catch (DaoException e) {
            logger.log(Level.SEVERE, "Error al cargar el historial de pedidos: " + e.getMessage(), e);
            util.Alertas.mostrarError(e.getMessage());
        }
    }

    /**
     * Asigna el servicio de caja y dispara la carga de datos en la tabla.
     *
     * @param servicio instancia de {@link CajaService}
     */
    public void setCajaService(CajaService servicio) {
        this.cajaService = servicio;
        cargarTabla();
    }
}
