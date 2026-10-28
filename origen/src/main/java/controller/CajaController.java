package controller;

import exceptions.DaoException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import model.CategoriaProducto;
import model.LineaPedido;
import model.Producto;
import model.VentaPedido;
import model.Usuario;
import services.ProductoService;
import util.Alertas;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Toggle;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class CajaController implements UsuarioImplementacion {

    private static final String RUTA_TARJETA = "/view/caja/productoComponent.fxml";
    private static final String RUTA_LINEA_PEDIDO = "/view/caja/productoPedidoComponent.fxml";

    @FXML
    private ToggleGroup categoriasGroup;
    @FXML
    private FlowPane contenedorCategorias;
    @FXML
    private FlowPane contenedorProductos;
    @FXML
    private RadioButton rbTodo;

    private final ProductoService productoService = new ProductoService();
    private List<Producto> productosVenta = new ArrayList<>();
    private final VentaPedido pedido = new VentaPedido();

    private Usuario usuario;

    //contenedorLineasPedido
    @FXML
    private Label lblSubtitulo;
    @FXML
    private VBox panelPedido;
    @FXML
    private Label lblPedidoActual;
    @FXML
    private VBox contenedorLineasPedido;
    @FXML
    private VBox resumenPedido;
    @FXML
    private Label lblNumProductos;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblAyuda;

    @FXML
    private void initialize() {
        rbTodo.setUserData(null);

        categoriasGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(
                    ObservableValue<? extends Toggle> observable,
                    Toggle anterior,
                    Toggle nuevo) {

                mostrarProductos();
            }
        });

        cargarProductos();
        actualizarPedido();
    }

    // --------------------- Categorías ---------------------
    /**
     * Crea un RadioButton solo por las categorías de model.CategoriaProducto
     * que tienen productos de venta (se llama cuando ya se cargaron los
     * productos).
     */
    private void cargarCategorias() {
        contenedorCategorias.getChildren().removeIf(nodo -> nodo != rbTodo);

        for (CategoriaProducto categoria : productoService.categoriasDeVenta(productosVenta)) {
            RadioButton rb = new RadioButton(categoria.toString());
            rb.setToggleGroup(categoriasGroup);
            rb.setUserData(categoria);
            rb.getStyleClass().add("chip-categoria");
            contenedorCategorias.getChildren().add(rb);
        }
    }

    /**
     * Categoría seleccionada vacio si es todo -> se usa para el filtrado segun
     * la categoria
     */
    public CategoriaProducto getCategoriaSeleccionada() {
        Toggle seleccionado = categoriasGroup.getSelectedToggle();
        if (seleccionado == null || seleccionado.getUserData() == null) {
            return null;
        }
        return (CategoriaProducto) seleccionado.getUserData();
    }

    // --------------------- Productos ---------------------
    private void cargarProductos() {
        try {
            productosVenta = productoService.listarProductosVenta();
            cargarCategorias();
            mostrarProductos();
        } catch (DaoException e) {
            Alertas.mostrarError("No se pudieron cargar los productos: " + e.getMessage());
        }
    }

    /**
     * Pinta una tarjeta por cada producto de la categoría seleccionada.
     */
    private void mostrarProductos() {
        contenedorProductos.getChildren().clear();

        List<Producto> filtrados = productoService.filtrarPorCategoria(
                productosVenta, getCategoriaSeleccionada());

        if (filtrados.isEmpty()) {
            Label vacio = new Label("No hay productos en esta categoría.");
            vacio.getStyleClass().add("texto-secundario");
            contenedorProductos.getChildren().add(vacio);
            return;
        }

        for (Producto p : filtrados) {
            try {
                contenedorProductos.getChildren().add(crearTarjeta(p));
            } catch (IOException ex) {
                Alertas.mostrarError("No se pudo cargar la tarjeta de producto: " + ex.getMessage());
                return;
            }
        }
    }

    // creo la tarjeta con los elementos
    private Node crearTarjeta(Producto producto) throws IOException {
        URL ubicacion = getClass().getResource(RUTA_TARJETA);
        if (ubicacion == null) {
            throw new IOException("No se encuentra el FXML de la tarjeta: " + RUTA_TARJETA);
        }

        FXMLLoader loader = new FXMLLoader(ubicacion);
        Node tarjeta = loader.load();

        ProductoComponentController controller = loader.getController();
        controller.setProducto(producto, this::anadirAlPedido);

        return tarjeta;
    }

    /**
     * Se ejecuta al pulsar "Añadir" en una tarjeta.
     */
    private void anadirAlPedido(Producto producto) {
        pedido.agregar(producto);
        actualizarPedido();
    }

    // --------------------- Pedido ---------------------
    /**
     * Repinta el panel del pedido a partir del modelo: una línea (componente
     * FXML) por cada producto distinto, con su cantidad, más total y contador.
     */
    private void actualizarPedido() {
        contenedorLineasPedido.getChildren().clear();

        for (LineaPedido linea : pedido.lineas()) {
            try {
                contenedorLineasPedido.getChildren().add(crearLineaPedido(linea));
            } catch (IOException ex) {
                Alertas.mostrarError("No se pudo cargar la línea del pedido: " + ex.getMessage());
                return;
            }
        }

        lblNumProductos.setText(pedido.numProductos() + " productos");
        lblTotal.setText(String.format("%.2f €", pedido.totalConIva()));

        boolean vacio = pedido.estaVacio();
        lblAyuda.setVisible(vacio);
        lblAyuda.setManaged(vacio);
    }

    private Node crearLineaPedido(LineaPedido linea) throws IOException {
        URL ubicacion = getClass().getResource(RUTA_LINEA_PEDIDO);
        if (ubicacion == null) {
            throw new IOException("No se encuentra el FXML de la línea: " + RUTA_LINEA_PEDIDO);
        }

        FXMLLoader loader = new FXMLLoader(ubicacion);
        Node nodo = loader.load();

        ProductoPedidoComponentController controller = loader.getController();
        controller.setLinea(linea, this::sumarUnidad, this::restarUnidad, this::quitarLinea);

        return nodo;
    }

    private void sumarUnidad(Producto producto) {
        pedido.agregar(producto);
        actualizarPedido();
    }

    private void restarUnidad(Producto producto) {
        pedido.restar(producto);
        actualizarPedido();
    }

    private void quitarLinea(Producto producto) {
        pedido.quitar(producto);
        actualizarPedido();
    }

    // --------------------- handlers, javafx ---------------------
    @FXML
    private void onHistorialVentas() {
    }

    @FXML
    private void onCancelar() {
        if (pedido.estaVacio()) {
            return;
        }
        util.Alertas.mostrarConfirmacion("Confirmación", "¿Quieres cancelar el pedido?", this::limpiarPedido);
    }

    private void limpiarPedido() {
        pedido.limpiar();
        actualizarPedido();
    }

    @FXML
    private void onCobrar() {
        if (pedido.estaVacio()) {
            return;
        }

        try {
            controller.InstanciarEscena.createNewStage("/view/caja/confirmarPagoDialog.fxml", "ConfirmarPago", this::abrirPago);
        } catch (IOException ex) {
            System.getLogger(CajaController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

    }

    // dialog metodo de pago
    private void abrirPago(Object controller) {
        try {
            ConfirmarPagoDialogController controllerDialog = (ConfirmarPagoDialogController) controller;
            controllerDialog.setPedido(pedido);
        } catch (Exception e) {
            System.out.println("Error con el dialog perfil: " + e.getMessage());
        }
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
