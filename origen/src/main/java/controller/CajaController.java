package controller;

import dao.implement.PedidoDaoImp;
import dao.implement.ProductoDaoImp;
import exceptions.DaoException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import model.CategoriaProducto;
import model.EstadoPedido;
import model.LineaPedido;
import model.Producto;
import model.Usuario;
import model.VentaPedido;
import services.CajaService;
import util.Alertas;
import util.AppLogger;

/**
 * FXML Controller class del módulo Caja
 *
 * @author Joel
 */
public class CajaController implements UsuarioImplementacion {

    private static final String RUTA_TARJETA = "/view/caja/productoComponent.fxml";
    private static final String RUTA_LINEA_PEDIDO = "/view/caja/productoPedidoComponent.fxml";

    // ---- Categorías y catálogo ----
    @FXML
    private ToggleGroup categoriasGroup;
    @FXML
    private FlowPane contenedorCategorias;
    @FXML
    private FlowPane contenedorProductos;
    @FXML
    private RadioButton rbTodo;

    // ---- Panel de pedido ----
    @FXML
    private VBox contenedorLineasPedido;
    @FXML
    private Label lblNumProductos;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblAyuda;

    // ---- Servicios y estado ----
    private final CajaService cajaService = new CajaService(ProductoDaoImp.getInstance(), PedidoDaoImp.getInstance());
    private List<Producto> productosVenta = new ArrayList<>();
    private final VentaPedido pedido = new VentaPedido();
    private final AppLogger logger = AppLogger.getInstance();
    private Usuario usuario;

    /**
     * Inicializa el filtro de categorías, carga los productos disponibles y
     * actualiza la vista del pedido.
     */
    @FXML
    private void initialize() {
        rbTodo.setUserData(null);

        categoriasGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(
                    ObservableValue<? extends Toggle> observable,
                    Toggle anterior,
                    Toggle nuevo) {
                CategoriaProducto cat = getCategoriaSeleccionada();
                logger.info("Filtro de categoría cambiado a: " + (cat != null ? cat.toString() : "Todo"));
                mostrarProductos();
            }
        });

        cargarProductos();
        actualizarPedido();
    }

    // ---- Categorías ----
    /**
     * Crea un RadioButton por cada categoría que disponga de productos en
     * venta.
     */
    private void cargarCategorias() {
        contenedorCategorias.getChildren().removeIf(nodo -> nodo != rbTodo);

        for (CategoriaProducto categoria : cajaService.categoriasDeVenta(productosVenta)) {
            RadioButton rb = new RadioButton(categoria.toString());
            rb.setToggleGroup(categoriasGroup);
            rb.setUserData(categoria);
            rb.getStyleClass().add("chip-categoria");
            contenedorCategorias.getChildren().add(rb);
        }
    }

    /**
     * Obtiene la categoría seleccionada actualmente o null si está marcada la
     * opción general.
     *
     * @return categoría seleccionada o null para mostrar todas
     */
    public CategoriaProducto getCategoriaSeleccionada() {
        Toggle seleccionado = categoriasGroup.getSelectedToggle();
        if (seleccionado == null || seleccionado.getUserData() == null) {
            return null;
        }
        return (CategoriaProducto) seleccionado.getUserData();
    }

    // ---- Productos ----
    /**
     * Carga los productos de venta desde el servicio y actualiza las categorías
     * y tarjetas visuales.
     */
    private void cargarProductos() {
        try {
            productosVenta = cajaService.listarProductosVenta();
            cargarCategorias();
            mostrarProductos();
        } catch (DaoException e) {
            logger.log(Level.SEVERE, "Error al cargar productos de venta: " + e.getMessage(), e);
            Alertas.mostrarError("No se pudieron cargar los productos: " + e.getMessage());
        }
    }

    /**
     * Renderiza las tarjetas de productos correspondientes a la categoría
     * seleccionada.
     */
    private void mostrarProductos() {
        contenedorProductos.getChildren().clear();

        List<Producto> filtrados = cajaService.filtrarPorCategoria(
                productosVenta, getCategoriaSeleccionada());

        if (filtrados.isEmpty()) {
            Label vacio = new Label("No hay productos en esta categoría.");
            vacio.getStyleClass().add("texto-secundario");
            contenedorProductos.getChildren().add(vacio);
            logger.log(Level.WARNING, "No se encontraron productos para la categoría seleccionada.");
            return;
        }

        for (Producto p : filtrados) {
            try {
                contenedorProductos.getChildren().add(crearTarjeta(p));
            } catch (IOException ex) {
                Alertas.mostrarError("No se pudo cargar la tarjeta de producto: " + ex.getMessage());
                logger.log(Level.SEVERE, "Error al cargar tarjeta de producto '" + p.getNombre() + "': " + ex.getMessage(), ex);
                return;
            }
        }
    }

    /**
     * Carga el componente FXML de tarjeta para un producto y enlaza su acción
     * de añadir al pedido.
     *
     * @param producto producto a representar en la tarjeta
     * @return nodo visual de la tarjeta cargada
     * @throws IOException si no se encuentra o no puede cargarse el archivo
     * FXML
     */
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
     * Añade una unidad del producto al pedido actual y refresca el panel de
     * resumen.
     *
     * @param producto producto seleccionado
     */
    private void anadirAlPedido(Producto producto) {
        pedido.agregar(producto);
        actualizarPedido();
        logger.info("Producto añadido al pedido: " + producto.getNombre());
    }

    // ---- Pedido ----
    /**
     * Repinta las líneas del pedido actual, el contador de productos y el
     * importe total con IVA.
     */
    private void actualizarPedido() {
        contenedorLineasPedido.getChildren().clear();

        for (LineaPedido linea : pedido.lineas()) {
            try {
                contenedorLineasPedido.getChildren().add(crearLineaPedido(linea));
            } catch (IOException ex) {
                Alertas.mostrarError("No se pudo cargar la línea del pedido: " + ex.getMessage());
                logger.log(Level.SEVERE, "Error al cargar línea de pedido: " + ex.getMessage(), ex);
                return;
            }
        }

        lblNumProductos.setText(pedido.numProductos() + " productos");
        lblTotal.setText(String.format("%.2f €", pedido.totalConIva()));

        boolean vacio = pedido.estaVacio();
        lblAyuda.setVisible(vacio);
        lblAyuda.setManaged(vacio);
    }

    /**
     * Carga el componente FXML de una línea de pedido y configura sus controles
     * de cantidad.
     *
     * @param linea línea de pedido a renderizar
     * @return nodo visual de la línea cargada
     * @throws IOException si no se encuentra o no puede cargarse el archivo
     * FXML
     */
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

    /**
     * Incrementa en una unidad la cantidad de un producto dentro del pedido.
     *
     * @param producto producto a incrementar
     */
    private void sumarUnidad(Producto producto) {
        pedido.agregar(producto);
        actualizarPedido();
        logger.info("Unidad sumada en pedido: " + producto.getNombre());
    }

    /**
     * Reduce en una unidad la cantidad de un producto dentro del pedido.
     *
     * @param producto producto a decrementar
     */
    private void restarUnidad(Producto producto) {
        pedido.restar(producto);
        actualizarPedido();
        logger.info("Unidad restada en pedido: " + producto.getNombre());
    }

    /**
     * Elimina por completo la línea asociada a un producto del pedido actual.
     *
     * @param producto producto a retirar del pedido
     */
    private void quitarLinea(Producto producto) {
        pedido.quitar(producto);
        actualizarPedido();
        logger.info("Línea eliminada del pedido: " + producto.getNombre());
    }

    // ---- Métodos FXML ----
    /**
     * Abre la ventana modal con el historial de ventas realizadas.
     */
    @FXML
    private void onHistorialVentas() {
        try {
            controller.InstanciarEscena.createNewStage("/view/caja/historialView.fxml", "Historial de Ventas", (c) -> {
                HistorialController controlador = (HistorialController) c;
                controlador.setCajaService(cajaService);
            });
            logger.info("Consulta del historial de ventas.");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error al abrir el historial de ventas: " + ex.getMessage(), ex);
            Alertas.mostrarError("No se pudo abrir el historial de ventas: " + ex.getMessage());
        }
    }

    /**
     * Solicita confirmación al usuario para cancelar y vaciar el pedido en
     * curso.
     */
    @FXML
    private void onCancelar() {
        if (pedido.estaVacio()) {
            logger.log(Level.WARNING, "Intento de cancelar un pedido vacío.");
            return;
        }
        util.Alertas.mostrarConfirmacion("Confirmación", "¿Quieres cancelar el pedido?", this::limpiarPedido);
    }

    /**
     * Vacía las líneas del pedido actual y actualiza la interfaz.
     */
    private void limpiarPedido() {
        pedido.limpiar();
        actualizarPedido();
        logger.info("Pedido actual cancelado y limpiado.");
    }

    /**
     * Abre el diálogo de confirmación de pago si el pedido contiene productos.
     */
    @FXML
    private void onCobrar() {
        if (pedido.estaVacio()) {
            logger.log(Level.WARNING, "Intento de cobrar un pedido vacío.");
            return;
        }
        try {
            controller.InstanciarEscena.createNewStage("/view/caja/confirmarPagoDialog.fxml", "ConfirmarPago", this::abrirPago);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error al abrir el diálogo de pago: " + ex.getMessage(), ex);
            Alertas.mostrarError("No se pudo abrir el diálogo de pago: " + ex.getMessage());
        }
    }

    /**
     * Configura el controlador del diálogo de pago asignando el pedido y el
     * callback de cobro.
     *
     * @param controller instancia del controlador del diálogo de pago
     */
    private void abrirPago(Object controller) {
        try {
            ConfirmarPagoDialogController controllerDialog = (ConfirmarPagoDialogController) controller;
            controllerDialog.setPedido(pedido);
            controllerDialog.setAlPagar(this::agregarPedido);
            logger.info("Diálogo de cobro abierto para pedido de " + pedido.numProductos() + " productos.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al inicializar el diálogo de pago: " + e.getMessage(), e);
        }
    }

    /**
     * Registra el pedido cobrado en la base de datos, actualiza el stock y
     * reinicia la caja.
     *
     * @param pedido pedido confirmado para guardar
     */
    private void agregarPedido(VentaPedido pedido) {
        if (pedido.estaVacio()) {
            return;
        }
        pedido.setUsuario_id(usuario.getId());
        pedido.setId(100);

        LocalDate fecha = LocalDate.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        pedido.setFecha(fecha.format(formato));
        pedido.setEstado(EstadoPedido.COBRADO);

        try {
            cajaService.realizarPedido(pedido);
            logger.info("Pedido cobrado y registrado correctamente. Total: " + String.format("%.2f €", pedido.totalConIva()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al registrar el pedido en base de datos: " + e.getMessage(), e);
            Alertas.mostrarError("No se pudo registrar el pedido: " + e.getMessage());
            return;
        }

        actualizarStock(pedido);
        pedido.limpiar();
        actualizarPedido();
        cargarProductos();
    }

    /**
     * Descuenta del inventario el stock de cada producto incluido en el pedido
     * cobrado.
     *
     * @param pedido pedido cuyas líneas determinan el ajuste de stock
     */
    private void actualizarStock(VentaPedido pedido) {
        if (pedido.estaVacio()) {
            return;
        }
        for (LineaPedido lp : pedido.getDetalle()) {
            try {
                cajaService.modificarProducto(lp.productoModificadoStock());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error al actualizar stock del producto '" + lp.getNombre() + "': " + e.getMessage(), e);
            }
        }
        logger.info("Stock actualizado tras finalizar el pedido.");
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            logger.setUser(usuario.getUsername());
        }
    }
}
