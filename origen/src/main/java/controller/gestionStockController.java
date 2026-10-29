package controller;

import dao.ProductoDao;
import dao.implement.ProductoDaoImp;
import exceptions.DaoException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
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
import org.bson.Document;

/**
 *
 * @author Hodei.Torres
 */
public class GestionStockController implements Initializable {

    @FXML private Label cantProduc;
    @FXML private Label precioStock;
    @FXML private Label cantStockBajo;
    @FXML private Label cantAgotados;
    @FXML private Label cantCaducar;
    @FXML private ComboBox<String> categoriaDisplay;
    @FXML private ComboBox<String> estadoDisplay;
    @FXML private TextField txtBuscar;

    @FXML private TableView<FilaStock> tablaStock;
    @FXML private TableColumn<FilaStock, String> colProducto, colValor, colEstado, colPrecio;
    @FXML private TableColumn<FilaStock, CategoriaProducto> colCategoria;
    @FXML private TableColumn<FilaStock, Number> colExistencia, colMinimo;

    @FXML private GridPane gridInfoStock;
    @FXML private GridPane gridFiltroStock;
    @FXML private ToggleButton soloAlertas;

    private final ProductoDao productoDao = ProductoDaoImp.getInstance();

    // Productos tal cual vienen del DAO (se usan para el resumen)
    private final ObservableList<Producto> productos = FXCollections.observableArrayList();

    // Filas que muestra la tabla (una por producto)
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
                    .bind(tablaStock.widthProperty().subtract(15).multiply(peso / total));
        }

        // Se enlazan las columnas con los getters de FilaStock
        colProducto.setCellValueFactory(new PropertyValueFactory<FilaStock, String>("nombre"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<FilaStock, CategoriaProducto>("categoria"));
        colExistencia.setCellValueFactory(new PropertyValueFactory<FilaStock, Number>("existencia"));
        colMinimo.setCellValueFactory(new PropertyValueFactory<FilaStock, Number>("minimo"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<FilaStock, String>("precio"));
        colValor.setCellValueFactory(new PropertyValueFactory<FilaStock, String>("valor"));
        colEstado.setCellValueFactory(new PropertyValueFactory<FilaStock, String>("estado"));

        // Opciones de los combos
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

    private void cargarProductos() {
        try {
            productos.setAll(productoDao.listarProductos());

            filas.clear();
            for (Producto p : productos) {
                filas.add(new FilaStock(p));
            }

            actualizarResumen();
        } catch (DaoException e) {
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

    private double valorStock(Producto p) {
        return p.getStock_actual() * p.getPrecio_venta();
    }

    private LocalDate fechaDeLote(Document lote) {
        Object f = lote.get(Producto.LOTE_FECHA_CADUCIDAD);
        if (f instanceof String) {
            return LocalDate.parse((String) f);
        }
        if (f instanceof Date) {
            return ((Date) f).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return null;
    }

    private boolean caducaEnSieteDias(Producto p) {
        LocalDate hoy = LocalDate.now();
        LocalDate limite = hoy.plusDays(7);

        for (Document lote : p.getLotes()) {
            LocalDate f = fechaDeLote(lote);
            if (f != null && !f.isBefore(hoy) && !f.isAfter(limite)) {
                return true;
            }
        }
        return false;
    }

    private void actualizarResumen() {
        double valorTotal = 0;
        int agotados = 0;
        int stockBajo = 0;
        int caducan = 0;

        for (Producto p : productos) {
            valorTotal += valorStock(p);
            if (p.getStock_actual() == 0) {
                agotados++;
            }
            if (p.tieneStockBajo()) {
                stockBajo++;
            }
            if (caducaEnSieteDias(p)) {
                caducan++;
            }
        }

        cantProduc.setText(String.valueOf(productos.size()));
        precioStock.setText(String.format("%.2f €", valorTotal));
        cantAgotados.setText(String.valueOf(agotados));
        cantStockBajo.setText(String.valueOf(stockBajo));
        cantCaducar.setText(String.valueOf(caducan));
    }

    @FXML
    private void registrarPerdida(ActionEvent event) {
        try {
            RegistrarPerdidaController c = InstanciarEscena.createNewStage("/view/gestionStock/registrarPerdida.fxml", "Registrar Perdida",  null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cargarProductos();
    }

    @FXML
    private void añadirProducto(ActionEvent event) {
        try {
            AñadirProductoController c = InstanciarEscena.createNewStage("/view/gestionStock/añadirProducto.fxml", "Añadir Producto", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cargarProductos();
    }
}