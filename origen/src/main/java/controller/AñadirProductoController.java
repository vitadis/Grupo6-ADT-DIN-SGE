package controller;

import exceptions.DaoException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import model.CategoriaProducto;
import model.Producto;
import model.Proveedor;
import model.TipoProducto;
import services.GestionStockService;
import static util.Alertas.mostrarError;
import util.AppLogger;

/**
 *
 * @author Hodei.Torres
 */
public class AñadirProductoController implements Initializable {

    @FXML
    private CheckBox chkNuevo;
    @FXML
    private ComboBox<Producto> comboExistente;
    @FXML
    private TextField txtProducto;
    @FXML
    private GridPane panelNuevo;
    @FXML
    private ComboBox<TipoProducto> comboTipo;
    @FXML
    private ComboBox<CategoriaProducto> comboCategoria;
    @FXML
    private ComboBox<Proveedor> comboProveedor;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtIva;
    @FXML
    private TextField txtStockMinimo;
    @FXML
    private Label lblCantidad;
    @FXML
    private TextField txtCantidad;
    @FXML
    private DatePicker dateCaducidad;

    private final GestionStockService stockService = new GestionStockService();
    private final AppLogger logger = AppLogger.getInstance();

    private boolean guardado = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboExistente.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto p) {
                return p == null ? "" : p.getNombre();
            }

            @Override
            public Producto fromString(String texto) {
                return null;
            }
        });

        comboTipo.setItems(FXCollections.observableArrayList(TipoProducto.values()));

        try {
            comboExistente.setItems(FXCollections.observableArrayList(stockService.listarProductos()));
            comboProveedor.setItems(FXCollections.observableArrayList(stockService.listarProveedoresActivos()));
        } catch (DaoException e) {
            logger.log(Level.SEVERE, "Error al cargar productos y proveedores en añadir producto", e);
            mostrarError(e.getMessage());
        }

        modoCambiado();
    }

    public void setProductoInicial(Producto p) {
        if (p == null) {
            return;
        }
        for (Producto existente : comboExistente.getItems()) {
            if (existente.getId() == p.getId()) {
                comboExistente.setValue(existente);
                break;
            }
        }
    }

    @FXML
    private void modoCambiado() {
        boolean nuevo = chkNuevo.isSelected();

        mostrar(comboExistente, !nuevo);
        mostrar(txtProducto, nuevo);
        mostrar(panelNuevo, nuevo);

        lblCantidad.setText(nuevo ? "Cantidad inicial:" : "Cantidad a añadir:");

        if (txtProducto.getScene() != null) {
            txtProducto.getScene().getWindow().sizeToScene();
        }
    }

    @FXML
    private void tipoSeleccionado() {
        comboCategoria.getSelectionModel().clearSelection();
        comboCategoria.setItems(FXCollections.observableArrayList(
                stockService.categoriasDeTipo(comboTipo.getValue())));
    }

    @FXML
    private void guardar() {
        try {
            if (chkNuevo.isSelected()) {
                guardarNuevo();
            } else {
                guardarExistente();
            }
            guardado = true;
            cerrar();
        } catch (IllegalArgumentException e) {
            logger.warning("Validación rechazada al añadir producto: " + e.getMessage());
            mostrarError(e.getMessage());
        } catch (DaoException e) {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error al guardar producto: " + e.getMessage(), e);
            String causa = (e.getCause() != null) ? "\n" + e.getCause().getMessage() : "";
            mostrarError(e.getMessage() + causa);
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    public boolean isGuardado() {
        return guardado;
    }

    private void guardarExistente() throws DaoException {
        Producto elegido = comboExistente.getValue();
        int cantidad = leerEntero(txtCantidad, "La cantidad");
        LocalDate caducidad = dateCaducidad.getValue();

        stockService.anadirStock(elegido, cantidad, caducidad);
        logger.info("Entrada de stock: " + elegido.getNombre() + " (+" + cantidad
                + ", caduca " + caducidad + ")");
    }

    private void guardarNuevo() throws DaoException {
        double precio = leerDecimal(txtPrecio, "El precio");
        double iva = leerDecimal(txtIva, "El IVA");
        int stockMinimo = leerEntero(txtStockMinimo, "El stock mínimo");
        int cantidad = leerEntero(txtCantidad, "La cantidad inicial");

        Producto p = stockService.crearProducto(txtProducto.getText(), comboTipo.getValue(),
                comboCategoria.getValue(), comboProveedor.getValue(), precio, iva,
                stockMinimo, cantidad, dateCaducidad.getValue());
        logger.info("Producto nuevo creado: " + p.getNombre()
                + " (categoría " + p.getCategoria() + ", stock inicial " + p.getStock_actual() + ")");
    }

    private void mostrar(Node nodo, boolean visible) {
        nodo.setVisible(visible);
        nodo.setManaged(visible);
    }

    private double leerDecimal(TextField campo, String nombre) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException(nombre + " es obligatorio.");
        }
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombre + " debe ser un número.");
        }
    }

    private int leerEntero(TextField campo, String nombre) {
        String texto = campo.getText() == null ? "" : campo.getText().trim();
        if (texto.isEmpty()) {
            throw new IllegalArgumentException(nombre + " es obligatorio.");
        }
        try {
            int valor = Integer.parseInt(texto);
            if (valor < 0) {
                throw new IllegalArgumentException(nombre + " no puede ser negativo.");
            }
            return valor;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nombre + " debe ser un número entero.");
        }
    }

    private void cerrar() {
        txtProducto.getScene().getWindow().hide();
    }
}