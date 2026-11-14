package controller;

import exceptions.DaoException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import model.CategoriaProducto;
import model.FilaStock;
import model.Producto;
import model.Usuario;
import services.GestionStockService;
import util.AppLogger;

/**
 *
 * @author Hodei.Torres
 */
public class GestionStockController implements Initializable, UsuarioImplementacion, ActualizarDatos {

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
    private ComboBox<String> categoriaDisplay;
    @FXML
    private ComboBox<String> estadoDisplay;
    @FXML
    private TextField txtBuscar;

    @FXML
    private TableView<FilaStock> tablaStock;
    @FXML
    private TableColumn<FilaStock, String> colProducto, colValor, colEstado, colPrecio;
    @FXML
    private TableColumn<FilaStock, CategoriaProducto> colCategoria;
    @FXML
    private TableColumn<FilaStock, Number> colExistencia, colMinimo;

    @FXML
    private GridPane gridInfoStock;
    @FXML
    private GridPane gridFiltroStock;
    @FXML
    private ToggleButton soloAlertas;

    private Usuario usuario;

    private final GestionStockService stockService = new GestionStockService();
    private final AppLogger logger = AppLogger.getInstance();

    // Productos tal cual vienen del service (se usan para el resumen)
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    private final ObservableList<FilaStock> filas = FXCollections.observableArrayList();
    private final FilteredList<FilaStock> filasFiltradas = new FilteredList<FilaStock>(filas, new Predicate<FilaStock>() {
        @Override
        public boolean test(FilaStock f) {
            return true;
        }
    });

    // Un único listener para los cuatro filtros
    private final ChangeListener<Object> alCambiarFiltro = new ChangeListener<Object>() {
        @Override
        public void changed(ObservableValue<? extends Object> obs, Object antes, Object ahora) {
            aplicarFiltros();
        }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Ajustes de ancho de las columnas
        int n = tablaStock.getColumns().size();
        double total = n - 1 + 2.0;

        for (int i = 0; i < n; i++) {
            double peso = (i == 0) ? 2.0 : 1.0;
            tablaStock.getColumns().get(i).prefWidthProperty()
                    .bind(tablaStock.widthProperty().subtract(20).multiply(peso / total));
        }

        // Se enlazan las columnas con los getters de FilaStock
        colProducto.setCellValueFactory(new PropertyValueFactory<FilaStock, String>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<FilaStock, CategoriaProducto>("categoria"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<FilaStock, Number>("existencia"));
        colMinimo.setCellValueFactory(new PropertyValueFactory<FilaStock, Number>("minimo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<FilaStock, String>("precio"));
        colValor.setCellValueFactory(new PropertyValueFactory<FilaStock, String>("valor"));
        colEstado.setCellValueFactory(new PropertyValueFactory<FilaStock, String>("estado"));

        categoriaDisplay.getItems().add("Todas");
        Set<String> etiquetas = new LinkedHashSet<String>();
        for (CategoriaProducto c : CategoriaProducto.values()) {
            etiquetas.add(c.getEtiqueta());
        }
        categoriaDisplay.getItems().addAll(etiquetas);
        categoriaDisplay.setValue("Todas");

        estadoDisplay.getItems().addAll("Todos", "OK", "Stock bajo", "Agotado");
        estadoDisplay.setValue("Todos");

        // Cualquier cambio en los filtros recalcula la tabla
        txtBuscar.textProperty().addListener(alCambiarFiltro);
        categoriaDisplay.valueProperty().addListener(alCambiarFiltro);
        estadoDisplay.valueProperty().addListener(alCambiarFiltro);
        soloAlertas.selectedProperty().addListener(alCambiarFiltro);

        // SortedList para mantener el ordenar por columnas
        SortedList<FilaStock> ordenadas = new SortedList<FilaStock>(filasFiltradas);
        ordenadas.comparatorProperty().bind(tablaStock.comparatorProperty());
        tablaStock.setItems(ordenadas);

        cargarProductos();
    }

    @Override
    public void actualizarDatos() {
        cargarProductos();
    }

    private void cargarProductos() {
        try {
            productos.setAll(stockService.listarProductos());

            filas.clear();
            for (Producto p : productos) {
                filas.add(new FilaStock(p));
            }

            actualizarResumen();
        } catch (DaoException e) {
            logger.log(Level.SEVERE, "Error al cargar productos", e);
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    private void aplicarFiltros() {
        final String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        final String categoria = categoriaDisplay.getValue();
        final String estado = estadoDisplay.getValue();
        final boolean soloAl = soloAlertas.isSelected();

        filasFiltradas.setPredicate(new Predicate<FilaStock>() {
            @Override
            public boolean test(FilaStock f) {
                // Buscador: el nombre contiene el texto escrito
                if (!texto.isEmpty() && !f.getNombre().toLowerCase().contains(texto)) {
                    return false;
                }
                // Categoría
                if (categoria != null && !categoria.equals("Todas")
                        && !categoria.equals(f.getCategoria().getEtiqueta())) {
                    return false;
                }
                // Estado
                if (estado != null && !estado.equals("Todos")
                        && !estado.equals(f.getEstado())) {
                    return false;
                }
                // Solo alertas: agotado o stock bajo
                if (soloAl && !(f.getEstado().equals("Agotado") || f.getEstado().equals("Stock bajo"))) {
                    return false;
                }
                return true;
            }
        });
    }

    private void actualizarResumen() {
        cantProduc.setText(String.valueOf(productos.size()));
        precioStock.setText(String.format("%.2f €", stockService.valorTotal(productos)));
        cantAgotados.setText(String.valueOf(stockService.contarAgotados(productos)));
        cantStockBajo.setText(String.valueOf(stockService.contarStockBajo(productos)));
        cantCaducar.setText(String.valueOf(stockService.contarCaducan(productos, 7)));
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @FXML
    private void registrarPerdida(ActionEvent event) {
        try {
            InstanciarEscena.createNewStage("/view/gestionStock/registrarPerdida.fxml", "Registrar Perdida", null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "No se pudo abrir la ventana de registrar pérdida", e);
        }
        cargarProductos();
    }

    @FXML
    private void añadirProducto(ActionEvent event) {
        try {
            InstanciarEscena.createNewStage("/view/gestionStock/añadirProducto.fxml", "Añadir Producto", null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "No se pudo abrir la ventana de añadir producto", e);
        }
        cargarProductos();
    }
}
