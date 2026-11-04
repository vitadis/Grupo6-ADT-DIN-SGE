package controller;

import dao.GestionStockDao;
import dao.implement.GestionStockDaoImp;
import exceptions.DaoException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import static util.Alertas.mostrarError;

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

    private final GestionStockDao stockDao = GestionStockDaoImp.getInstance();

    private List<Producto> productosExistentes = new ArrayList<>();
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
            productosExistentes = stockDao.listarProductos();
            comboExistente.setItems(FXCollections.observableArrayList(productosExistentes));
            comboProveedor.setItems(
                    FXCollections.observableArrayList(stockDao.listarActivos()));
        } catch (DaoException e) {
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

    // ============================ EVENTOS ====================================
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
        cargarCategorias(comboTipo.getValue());
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
            mostrarError(e.getMessage());
        } catch (DaoException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    public boolean isGuardado() {
        return guardado;
    }

    // ============================ GUARDADO ===================================
    private void guardarExistente() throws DaoException {
        Producto elegido = comboExistente.getValue();
        if (elegido == null) {
            throw new IllegalArgumentException("Selecciona un producto.");
        }

        int cantidad = leerEntero(txtCantidad, "La cantidad");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }

        LocalDate caducidad = dateCaducidad.getValue();
        if (caducidad == null) {
            throw new IllegalArgumentException("Indica la fecha de caducidad.");
        }

        stockDao.anadirStock(elegido.getId(), cantidad, caducidad.toString());
    }

    private void guardarNuevo() throws DaoException {
        Producto producto = construirProducto();
        stockDao.insertarProducto(producto);
    }

    private Producto construirProducto() {
        String nombre = txtProducto.getText() == null ? "" : txtProducto.getText().trim();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        for (Producto existente : productosExistentes) {
            if (existente.getNombre().equalsIgnoreCase(nombre)) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con ese nombre. Desmarca \"Es un producto nuevo\" para añadirle stock.");
            }
        }

        TipoProducto tipo = comboTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona un tipo.");
        }

        CategoriaProducto categoria = comboCategoria.getValue();
        if (categoria == null) {
            throw new IllegalArgumentException("Selecciona una categoría.");
        }

        Proveedor proveedor = comboProveedor.getValue();
        if (proveedor == null) {
            throw new IllegalArgumentException("Selecciona un proveedor.");
        }

        double precio = leerDecimal(txtPrecio, "El precio");
        double ivaPorcentaje = leerDecimal(txtIva, "El IVA");
        if (ivaPorcentaje < 0 || ivaPorcentaje > 100) {
            throw new IllegalArgumentException("El IVA debe estar entre 0 y 100.");
        }

        int stockMinimo = leerEntero(txtStockMinimo, "El stock mínimo");
        int cantidad = leerEntero(txtCantidad, "La cantidad inicial");

        LocalDate caducidad = dateCaducidad.getValue();
        if (cantidad > 0 && caducidad == null) {
            throw new IllegalArgumentException(
                    "Indica la fecha de caducidad del stock inicial.");
        }

        List<Integer> proveedores = new ArrayList<>();
        proveedores.add(proveedor.getId());

        // El tipo antes que la categoría (el setter las compara)
        Producto p = new Producto();
        p.setNombre(nombre);
        p.setTipo(tipo);
        p.setCategoria(categoria);
        p.setPrecio_venta(precio);
        p.setIva(ivaPorcentaje / 100.0);
        p.setStock_minimo(stockMinimo);
        p.setStock_actual(cantidad);
        p.setProveedores_id(proveedores);
        p.setActivo(true);

        if (cantidad > 0) {
            p.agregarLote(cantidad, caducidad.toString());
        }

        return p;
    }

    // ============================ UTILIDADES =================================
    private void cargarCategorias(TipoProducto tipo) {
        comboCategoria.getSelectionModel().clearSelection();

        List<CategoriaProducto> filtradas = new ArrayList<>();
        if (tipo != null) {
            for (CategoriaProducto c : CategoriaProducto.values()) {
                if (c.getTipo() == tipo) {
                    filtradas.add(c);
                }
            }
        }
        comboCategoria.setItems(FXCollections.observableArrayList(filtradas));
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
