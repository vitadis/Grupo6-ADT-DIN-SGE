/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import dao.ComprasDao;
import dao.ProductoDao;
import dao.ProveedorDao;
import dao.implement.ComprasDaoImpl;
import dao.implement.ProductoDaoImp;
import dao.implement.ProveedorDaoImpl;
import exceptions.DaoException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Compra;
import model.LineaCompra;
import model.Producto;
import model.Proveedor_GestionStock;

/**
 * Controlador de la ventana {@code registrarCompra.fxml}.
 *
 * Sirve para registrar una compra a mano (botón «Registrar compra») y para crear una compra a
 * partir de la lista sugerida (botón «Crear compra con esta lista»), en cuyo caso
 * {@link ComprasController} llama antes a {@link #setLineasSugeridas(int, List)}.
 * Las líneas se guardan en memoria en {@link #lineas} hasta que se pulsa Guardar.
 *
 * @author anazk
 */
public class RegistrarCompraController implements Initializable {

    /**
     * DAO para guardar la compra.
     */
    private final ComprasDao comprasDao = ComprasDaoImpl.getInstance();
    /**
     * DAO para cargar los proveedores en el ComboBox.
     */
    private final ProveedorDao proveedorDao = ProveedorDaoImpl.getInstance();
    /**
     * DAO para cargar los productos en el ComboBox.
     */
    private final ProductoDao productoDao = ProductoDaoImp.getInstance();

    /**
     * Líneas que se han añadido a la compra.
     */
    private final List<LineaCompra> lineas = new ArrayList<>();
    /**
     * Identificador del usuario que registra la compra.
     */
    private int usuarioId;
    /**
     * Indica si se ha guardado la compra antes de cerrar la ventana.
     */
    private boolean guardado = false;
    /**
     * Base imponible actual (suma de subtotales), redondeada a 2 decimales.
     */
    private double base;
    /**
     * IVA total actual, redondeado a 2 decimales.
     */
    private double totalIva;

    /**
     * Título de la ventana.
     */
    @FXML
    private Label lblTitulo;
    /**
     * Proveedor de la compra (solo proveedores activos).
     */
    @FXML
    private ComboBox<Proveedor_GestionStock> cbProveedor;
    /**
     * Fecha de la compra; por defecto, hoy.
     */
    @FXML
    private DatePicker dpFecha;
    /**
     * Número de factura (obligatorio y no repetido).
     */
    @FXML
    private TextField txtNumeroFactura;
    /**
     * Producto que se va a añadir (solo productos activos).
     */
    @FXML
    private ComboBox<Producto> cbProducto;
    /**
     * Cantidad del producto que se va a añadir.
     */
    @FXML
    private TextField txtCantidad;
    /**
     * Precio por unidad sin IVA del producto que se va a añadir.
     */
    @FXML
    private TextField txtPrecioUnidad;
    /**
     * Añade el producto, la cantidad y el precio como una línea nueva.
     */
    @FXML
    private Button btnAnadirLinea;
    /**
     * Scroll de la tabla de líneas.
     */
    @FXML
    private ScrollPane scrollLineas;
    /**
     * Contenedor donde se pintan las filas de las líneas.
     */
    @FXML
    private VBox contenedorLineas;
    /**
     * Muestra la base imponible.
     */
    @FXML
    private Label lblBase;
    /**
     * Muestra el IVA total.
     */
    @FXML
    private Label lblTotalIva;
    /**
     * Muestra el importe total.
     */
    @FXML
    private Label lblTotal;
    /**
     * Muestra los errores de validación o de la base de datos.
     */
    @FXML
    private Label lblError;
    /**
     * Cierra la ventana sin guardar.
     */
    @FXML
    private Button btnCancelar;
    /**
     * Valida y guarda la compra.
     */
    @FXML
    private Button btnGuardar;

    /**
     * Constructor vacío. Lo usa JavaFX al cargar {@code registrarCompra.fxml}.
     */
    public RegistrarCompraController() {
    }

    /**
     * Pone la fecha de hoy, configura cómo se muestran los productos en su ComboBox
     * y carga los proveedores y productos activos.
     *
     * @param url ubicación del FXML (no se usa)
     * @param rb recursos de idioma (no se usa)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dpFecha.setValue(LocalDate.now());
        cbProducto.setConverter(new StringConverter<Producto>() {
            @Override
            public String toString(Producto p) {
                if (p == null) {
                    return "";
                }
                return p.getNombre();
            }

            @Override
            public Producto fromString(String texto) {
                return null;
            }
        });

        try {
            for (Proveedor_GestionStock p : proveedorDao.obtenerProveedores()) {
                if (p.isActivo()) {
                    cbProveedor.getItems().add(p);
                }
            }
            for (Producto p : productoDao.listarProductos()) {
                if (p.isActivo()) {
                    cbProducto.getItems().add(p);
                }
            }
        } catch (DaoException e) {
            lblError.setText(e.getMessage());
        }

        pintarLineas();
    }

    /**
     * Indica qué usuario registra la compra.
     *
     * @param usuarioId identificador del usuario con la sesión iniciada
     */
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Rellena la ventana con una compra de la lista sugerida: selecciona el proveedor
     * y añade las líneas.
     *
     * @param proveedorId identificador del proveedor habitual de esos productos
     * @param sugeridas líneas sugeridas que se van a comprar
     */
    public void setLineasSugeridas(int proveedorId, List<LineaCompra> sugeridas) {
        lblTitulo.setText("NUEVA COMPRA DESDE LA LISTA");

        for (Proveedor_GestionStock p : cbProveedor.getItems()) {
            if (p.getId() == proveedorId) {
                cbProveedor.setValue(p);
            }
        }

        lineas.clear();
        lineas.addAll(sugeridas);
        pintarLineas();
    }

    /**
     * Indica si el usuario ha guardado la compra.
     *
     * @return {@code true} si se guardó en la base de datos
     */
    public boolean isGuardado() {
        return guardado;
    }

    /**
     * Valida el producto, la cantidad y el precio y añade una línea a la compra.
     * Si el producto ya estaba en la compra, se sustituye su línea.
     *
     * @param event evento del botón Añadir
     */
    @FXML
    private void onAnadirLinea(ActionEvent event) {
        lblError.setText("");

        Producto producto = cbProducto.getValue();
        if (producto == null) {
            lblError.setText("Selecciona un producto.");
            return;
        }

        double cantidad;
        double precio;
        try {
            cantidad = Double.parseDouble(txtCantidad.getText().trim().replace(',', '.'));
            precio = Double.parseDouble(txtPrecioUnidad.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            lblError.setText("La cantidad y el precio deben ser números.");
            return;
        }

        if (cantidad <= 0 || precio < 0) {
            lblError.setText("La cantidad debe ser mayor que 0 y el precio no puede ser negativo.");
            return;
        }

        for (int i = 0; i < lineas.size(); i++) {
            if (lineas.get(i).getProducto_id() == producto.getId()) {
                lineas.remove(i);
                break;
            }
        }

        lineas.add(new LineaCompra(producto.getId(), producto.getNombre(), cantidad, precio,
                producto.getIva(), Math.round(cantidad * precio * 100) / 100.0));

        cbProducto.setValue(null);
        txtCantidad.clear();
        txtPrecioUnidad.clear();
        pintarLineas();
    }

    /**
     * Valida los datos, crea la {@link Compra} con sus líneas y totales y la guarda.
     * Si todo va bien cierra la ventana; si no, muestra el error en {@link #lblError}.
     *
     * @param event evento del botón Guardar compra
     */
    @FXML
    private void onGuardar(ActionEvent event) {
        lblError.setText("");

        Proveedor_GestionStock proveedor = cbProveedor.getValue();
        String factura = txtNumeroFactura.getText().trim();

        if (proveedor == null || dpFecha.getValue() == null || factura.isEmpty()) {
            lblError.setText("El proveedor, la fecha y el número de factura son obligatorios.");
            return;
        }
        if (lineas.isEmpty()) {
            lblError.setText("Añade al menos un producto.");
            return;
        }

        Compra compra = new Compra();
        compra.setProveedor_id(proveedor.getId());
        compra.setUsuario_id(usuarioId);
        compra.setFecha(dpFecha.getValue().toString());
        compra.setNumero_factura(factura);
        compra.setDetalle(new ArrayList<>(lineas));
        compra.setBase(base);
        compra.setTotal_iva(totalIva);
        compra.setTotal(Math.round((base + totalIva) * 100) / 100.0);

        try {
            comprasDao.insertarCompra(compra);
            guardado = true;
            cerrar(event);
        } catch (DaoException e) {
            lblError.setText(e.getMessage());
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        }
    }

    /**
     * Cierra la ventana sin guardar.
     *
     * @param event evento del botón Cancelar
     */
    @FXML
    private void onCancelar(ActionEvent event) {
        cerrar(event);
    }

    /**
     * Vuelve a pintar la tabla de líneas y actualiza los totales.
     * Si no hay líneas, muestra un mensaje.
     */
    private void pintarLineas() {
        contenedorLineas.getChildren().clear();

        for (LineaCompra l : lineas) {
            contenedorLineas.getChildren().add(crearFilaLinea(l));
        }

        if (lineas.isEmpty()) {
            Label vacio = new Label("Todavía no hay productos en la compra.");
            vacio.getStyleClass().add("texto-secundario");
            contenedorLineas.getChildren().add(vacio);
        }

        actualizarTotales();
    }

    /**
     * Calcula la base y el IVA sumando las líneas, los guarda en {@link #base} y
     * {@link #totalIva} y los muestra en sus etiquetas.
     */
    private void actualizarTotales() {
        base = 0;
        totalIva = 0;
        for (LineaCompra l : lineas) {
            base += l.getSubtotal();
            totalIva += l.getSubtotal() * l.getIva();
        }
        base = Math.round(base * 100) / 100.0;
        totalIva = Math.round(totalIva * 100) / 100.0;

        lblBase.setText(String.format("%.2f €", base));
        lblTotalIva.setText(String.format("%.2f €", totalIva));
        lblTotal.setText(String.format("%.2f €", base + totalIva));
    }

    /**
     * Crea la fila de una línea con su botón para quitarla.
     *
     * @param l línea que se va a mostrar
     * @return la fila lista para añadir a {@link #contenedorLineas}
     */
    private HBox crearFilaLinea(final LineaCompra l) {
        Button btnQuitar = new Button("✕");
        btnQuitar.getStyleClass().add("btn-secundario");
        btnQuitar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                lineas.remove(l);
                pintarLineas();
            }
        });

        HBox fila = new HBox(
                celda(l.getNombre(), 220),
                celda(String.valueOf(l.getCantidad()), 80),
                celda(String.format("%.2f €", l.getPrecio_unidad()), 100),
                celda(Math.round(l.getIva() * 100) + " %", 60),
                celda(String.format("%.2f €", l.getSubtotal()), 100),
                btnQuitar
        );
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.getStyleClass().add("compra-fila");
        return fila;
    }

    /**
     * Crea una etiqueta con un ancho fijo, para que las columnas queden alineadas con la cabecera.
     *
     * @param texto texto de la celda
     * @param ancho ancho en píxeles
     * @return la etiqueta creada
     */
    private Label celda(String texto, double ancho) {
        Label lbl = new Label(texto);
        lbl.setPrefWidth(ancho);
        return lbl;
    }

    /**
     * Cierra la ventana a la que pertenece el botón pulsado.
     *
     * @param event evento del botón pulsado
     */
    private void cerrar(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
