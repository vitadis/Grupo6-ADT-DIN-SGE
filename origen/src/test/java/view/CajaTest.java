package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * TEST CLASS CajaTest
 *
 * @author Joel
 */
public class CajaTest extends ApplicationTest {

    private static final double MARGEN_ERROR = 0.011;

    private Stage stagePrincipal;

    /**
     * Iniciamos el stage
     *
     * @param stage
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        stagePrincipal = stage;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/caja/cajaView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    // ---------------------- cajaView ----------------------
    // ---------------------- Visibilidad Inicial de los nodos ----------------------
    @Test
    public void testVisibilidadBtnHistorial() {
        verifyThat(".btn-historial", isVisible());
    }

    @Test
    public void testVisibilidadBtnCancelar() {
        verifyThat(".btn-cancelar", isVisible());
    }

    @Test
    public void testVisibilidadBtnCobrar() {
        verifyThat(".btn-cobrar", isVisible());
    }

    @Test
    public void testVisibilidadChipTodo() {
        verifyThat("#rbTodo", isVisible());
    }

    @Test
    public void testVisibilidadContenedorProductos() {
        verifyThat("#contenedorProductos", isVisible());
    }

    @Test
    public void testVisibilidadContenedorCategorias() {
        verifyThat("#contenedorCategorias", isVisible());
    }

    @Test
    public void testVisibilidadContenedorLineasPedido() {
        verifyThat("#contenedorLineasPedido", isVisible());
    }

    @Test
    public void testVisibilidadLblTotal() {
        verifyThat("#lblTotal", isVisible());
    }

    @Test
    public void testVisibilidadLblNumProductos() {
        verifyThat("#lblNumProductos", isVisible());
    }

    @Test
    public void testVisibilidadLblAyuda() {
        verifyThat("#lblAyuda", isVisible());
    }

    // ---------------------- Textos de los botones ----------------------
    @Test
    public void testTextoBotones() {
        assertEquals("Historial de ventas", lookup(".btn-historial").queryButton().getText());
        assertEquals("Cancelar", lookup(".btn-cancelar").queryButton().getText());
        assertEquals("Cobrar", lookup(".btn-cobrar").queryButton().getText());
    }

    // ---------------------- Estado inicial ----------------------
    @Test
    public void testChipTodoSeleccionadoPorDefecto() {
        assertTrue(lookup("#rbTodo").queryAs(RadioButton.class).isSelected());
    }

    @Test
    public void testPedidoVacioInicialmente() {
        assertTrue(lineasPedido().getChildren().isEmpty());
        assertEquals("0 productos", obtenerLabel("#lblNumProductos").getText());
        assertTrue(obtenerLabel("#lblTotal").getText().startsWith("0"));
        assertTrue(obtenerLabel("#lblTotal").getText().endsWith("€"));
    }

    @Test
    public void testAyudaVisibleConPedidoVacio() {
        Label ayuda = obtenerLabel("#lblAyuda");
        assertTrue(ayuda.isVisible());
        assertTrue(ayuda.isManaged());
    }

    @Test
    public void testContenedorProductosNoVacio() {
        // tiene datos porque hago consulta a la db, si no hay datos salta un mensaje
        FlowPane productos = lookup("#contenedorProductos").queryAs(FlowPane.class);
        assertFalse(productos.getChildren().isEmpty());
    }

    // ---------------------- Categorias ----------------------
    @Test
    public void testClickEnCadaCategoriaLaSeleccionaYMuestraContenido() {
        FlowPane categorias = lookup("#contenedorCategorias").queryAs(FlowPane.class);
        FlowPane productos = lookup("#contenedorProductos").queryAs(FlowPane.class);

        List<Node> chips = new ArrayList<>(categorias.getChildren());

        for (Node nodo : chips) {
            if (nodo instanceof RadioButton) {
                RadioButton chip = (RadioButton) nodo;
                clic(chip);
                assertTrue(chip.isSelected(), "No se selecciona la categoría " + chip.getText());
                assertFalse(productos.getChildren().isEmpty());
            }
        }
    }

    @Test
    public void testVolverATodo() {
        FlowPane categorias = lookup("#contenedorCategorias").queryAs(FlowPane.class);
        assumeTrue(categorias.getChildren().size() > 1, "No hay categorías con productos");

        clic(categorias.getChildren().get(1));
        clic(lookup("#rbTodo").query());
        assertTrue(lookup("#rbTodo").queryAs(RadioButton.class).isSelected());
    }

    // ---------------------- Botones con pedido vacio ----------------------
    // Cancelar con el pedido vacio
    @Test
    public void testCancelarConPedidoVacioNoAbreNada() {
        int ventanas = listWindows().size();

        clic(lookup(".btn-cancelar").query());
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(ventanas, listWindows().size());
    }

    // Cobrar con el pedido vacio
    @Test
    public void testCobrarConPedidoVacioNoAbreNada() {
        int ventanas = listWindows().size();

        clic(lookup(".btn-cobrar").query());
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(ventanas, listWindows().size());
    }

    // ---------------------- Añadir productos al pedido ----------------------
    // Para estos test necesito tener la base de datos creada
    // Añadir un producto
    @Test
    public void testAnadirProductoActualizaPedido() {
        anadirPrimerProducto();

        assertEquals(1, lineasPedido().getChildren().size());
        assertEquals("1 productos", obtenerLabel("#lblNumProductos").getText());
        assertFalse(obtenerLabel("#lblAyuda").isVisible());
        assertFalse(obtenerLabel("#lblAyuda").isManaged());
        assertFalse(obtenerLabel("#lblTotal").getText().startsWith("0,00"));
    }

    // Añadir el mismo producto dos veces
    @Test
    public void testAnadirDosVecesMismoProductoMantieneUnaLinea() {
        anadirPrimerProducto();
        anadirPrimerProducto();

        assertEquals(1, lineasPedido().getChildren().size());
        assertEquals("2 productos", obtenerLabel("#lblNumProductos").getText());
    }

    // Añadir dos productos distintos
    @Test
    public void testAnadirDosProductosDistintosCreaDosLineas() {
        Node tarjeta0 = obtenerTarjeta(0);
        Node tarjeta1 = obtenerTarjeta(1);
        assumeTrue(!nombreDe(tarjeta0).equals(nombreDe(tarjeta1)), "Las dos primeras tarjetas son el mismo producto");

        anadirDesde(tarjeta0);
        anadirDesde(tarjeta1);

        assertEquals(2, lineasPedido().getChildren().size());
        assertEquals("2 productos", obtenerLabel("#lblNumProductos").getText());

        double suma = 0;
        for (Node linea : lineasPedido().getChildren()) {
            suma += importe(label(linea, "#lblSubtotal").getText());
        }
        assertEquals(suma, importe(obtenerLabel("#lblTotal").getText()), MARGEN_ERROR);
    }

    // ---------------------- Datos de la tarjeta del producto ----------------------
    @Test
    public void testTarjetaProductoMuestraSusDatos() {
        Node tarjeta = obtenerTarjeta(0);

        String nombre = label(tarjeta, "#lblNombre").getText();
        assertFalse(nombre == null || nombre.isBlank(), "Nombre vacío");
        assertNotEquals("Nombre del producto", nombre, "Nombre sin cargar (texto por defecto del FXML)");

        String categoria = label(tarjeta, "#lblCategoria").getText();
        assertFalse(categoria == null || categoria.isBlank(), "Categoría vacía");
        assertNotEquals("Categoría", categoria, "Categoría sin cargar (texto por defecto del FXML)");

        assertTrue(label(tarjeta, "#lblPrecio").getText().endsWith("€"), "El precio no acaba en €");

        String stock = label(tarjeta, "#lblStock").getText();
        assertFalse(stock == null || stock.isBlank(), "Estado de stock vacío");

        ImageView imagen = from(tarjeta).lookup("#imgProducto").queryAs(ImageView.class);
        assertNotNull(imagen, "No existe imgProducto");
        assertTrue(imagen.isVisible());
        if (imagen.getImage() != null) {
            assertFalse(imagen.getImage().isError(), "La imagen del producto tiene error");
        }
    }

    // ---------------------- Datos de la linea del pedido ----------------------
    @Test
    public void testLineaPedidoMuestraDatosDelProducto() {
        Node tarjeta = obtenerTarjeta(0);
        String nombre = nombreDe(tarjeta);
        double precio = importe(label(tarjeta, "#lblPrecio").getText());

        anadirDesde(tarjeta);

        Node linea = primeraLinea();
        assertEquals(nombre, label(linea, "#lblNombre").getText());
        assertEquals(precio, importe(label(linea, "#lblPrecioUnidad").getText()), MARGEN_ERROR);
        assertTrue(label(linea, "#lblPrecioUnidad").getText().contains("€"));
        assertEquals("1", label(linea, "#lblCantidad").getText());
        assertEquals(precio, importe(label(linea, "#lblSubtotal").getText()), MARGEN_ERROR);
        assertEquals(precio, importe(obtenerLabel("#lblTotal").getText()), MARGEN_ERROR);
    }

    // Comprobar que estan los botones de la linea
    @Test
    public void testBotonesDeLaLineaEstanPresentes() {
        anadirPrimerProducto();
        Node linea = primeraLinea();

        assertTrue(boton(linea, "+").isVisible());
        assertTrue(boton(linea, "-").isVisible());
        assertTrue(boton(linea, "✕").isVisible());
    }

    // Sumar una unidad
    @Test
    public void testSumarIncrementaCantidadSubtotalYTotal() {
        anadirPrimerProducto();
        double subtotalAntes = importe(label(primeraLinea(), "#lblSubtotal").getText());

        clic(boton(primeraLinea(), "+"));
        WaitForAsyncUtils.waitForFxEvents();

        Node linea = primeraLinea();
        assertEquals("2", label(linea, "#lblCantidad").getText());
        assertEquals(subtotalAntes * 2, importe(label(linea, "#lblSubtotal").getText()), MARGEN_ERROR);
        assertEquals(subtotalAntes * 2, importe(obtenerLabel("#lblTotal").getText()), MARGEN_ERROR);
        assertEquals("2 productos", obtenerLabel("#lblNumProductos").getText());
        assertEquals(1, lineasPedido().getChildren().size());
    }

    // Restar una unidad
    @Test
    public void testRestarDecrementaCantidadSubtotalYTotal() {
        anadirPrimerProducto();
        double subtotalUnaUnidad = importe(label(primeraLinea(), "#lblSubtotal").getText());

        clic(boton(primeraLinea(), "+"));
        WaitForAsyncUtils.waitForFxEvents();
        clic(boton(primeraLinea(), "-"));
        WaitForAsyncUtils.waitForFxEvents();

        Node linea = primeraLinea();
        assertEquals("1", label(linea, "#lblCantidad").getText());
        assertEquals(subtotalUnaUnidad, importe(label(linea, "#lblSubtotal").getText()), MARGEN_ERROR);
        assertEquals(subtotalUnaUnidad, importe(obtenerLabel("#lblTotal").getText()), MARGEN_ERROR);
        assertEquals("1 productos", obtenerLabel("#lblNumProductos").getText());
        assertEquals(1, lineasPedido().getChildren().size());
    }

    // Quitar producto
    @Test
    public void testQuitarEliminaLaLineaYVuelveAlEstadoVacio() {
        anadirPrimerProducto();

        clic(boton(primeraLinea(), "✕"));
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(lineasPedido().getChildren().isEmpty());
        assertEquals("0 productos", obtenerLabel("#lblNumProductos").getText());
        assertTrue(obtenerLabel("#lblTotal").getText().startsWith("0"));
        assertTrue(obtenerLabel("#lblAyuda").isVisible());
        assertTrue(obtenerLabel("#lblAyuda").isManaged());
    }

    @Test
    public void testQuitarUnaLineaConservaLaOtra() {
        Node tarjeta0 = obtenerTarjeta(0);
        Node tarjeta1 = obtenerTarjeta(1);
        assumeTrue(!nombreDe(tarjeta0).equals(nombreDe(tarjeta1)), "Las dos primeras tarjetas son el mismo producto");

        anadirDesde(tarjeta0);
        anadirDesde(tarjeta1);
        assertEquals(2, lineasPedido().getChildren().size());

        clic(boton(primeraLinea(), "✕"));
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(1, lineasPedido().getChildren().size());
        assertEquals(nombreDe(tarjeta1), label(primeraLinea(), "#lblNombre").getText());
        assertEquals("1 productos", obtenerLabel("#lblNumProductos").getText());
        assertFalse(obtenerLabel("#lblAyuda").isVisible());
    }

    // ---------------------- Dialogo de pago ----------------------
    @Test
    public void testCobrarConPedidoAbreDialog() {
        anadirPrimerProducto();
        int ventanas = listWindows().size();

        clic(lookup(".btn-cobrar").query());
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > ventanas, "Cobrar no abre el dialog de pago");
    }

    @Test
    public void testDialogoPagoMuestraTextosYTotalDelPedido() {
        anadirPrimerProducto();
        double totalPedido = importe(obtenerLabel("#lblTotal").getText());

        Parent dialogo = abrirDialogoCobrar();

        assertFalse(from(dialogo).lookup(hasText("Total a pagar")).queryAll().isEmpty(), "Falta 'Total a pagar'");
        assertFalse(from(dialogo).lookup(hasText("Método de pago:")).queryAll().isEmpty(), "Falta 'Método de pago:'");

        Label lblTotalDialogo = from(dialogo).lookup("#lblTotal").queryAs(Label.class);
        assertEquals(totalPedido, importe(lblTotalDialogo.getText()), MARGEN_ERROR,
                "El total del dialog no coincide con el del pedido");
    }

    @Test
    public void testDialogoPagoTieneMetodosDePago() {
        anadirPrimerProducto();

        Parent dialogo = abrirDialogoCobrar();

        ComboBox<?> combo = from(dialogo).lookup("#cmbMetodoPago").queryAs(ComboBox.class);
        assertNotNull(combo, "No existe cmbMetodoPago");
        assertFalse(combo.getItems().isEmpty(), "El combo de métodos de pago está vacío");
    }

    @Test
    public void testDialogoPagoBotones() {
        anadirPrimerProducto();

        Parent dialogo = abrirDialogoCobrar();

        Button volver = from(dialogo).lookup("#btnVolver").queryAs(Button.class);
        Button pagar = from(dialogo).lookup("#btnPagar").queryAs(Button.class);
        assertEquals("Volver", volver.getText());
        assertEquals("Pagar", pagar.getText());
        assertTrue(volver.isCancelButton(), "Volver debería ser el botón de cancelar");
        assertTrue(pagar.isDefaultButton(), "Pagar debería ser el botón por defecto");
    }

    // Volver sin perder el pedido
    @Test
    public void testVolverCierraDialogoPagoYConservaElPedido() {
        anadirPrimerProducto();
        int ventanasAntes = listWindows().size();

        Parent dialogo = abrirDialogoCobrar();
        assertTrue(listWindows().size() > ventanasAntes);

        clic(from(dialogo).lookup("#btnVolver").queryAs(Button.class));
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(ventanasAntes, listWindows().size(), "Volver no cierra el dialog");
        assertEquals(1, lineasPedido().getChildren().size(), "El pedido no debería haberse tocado");
        assertEquals("1 productos", obtenerLabel("#lblNumProductos").getText());
    }

    // ---------------------- Cancelar pedido ----------------------
    // Confirmar la cancelacion
    @Test
    public void testCancelarConPedidoPideConfirmacion() {
        anadirPrimerProducto();
        int ventanas = listWindows().size();

        clic(lookup(".btn-cancelar").query());
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > ventanas, "Cancelar no pide confirmación");
    }

    // ---------------------- Historial de ventas ----------------------
    // Abrir historial
    @Test
    public void testHistorialAbreVentana() {
        int ventanas = listWindows().size();

        clic(lookup(".btn-historial").query());
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > ventanas, "El historial no abre ninguna ventana");
    }

    @Test
    public void testHistorialMuestraTituloYTablaConSusColumnas() {
        clic(lookup(".btn-historial").query());
        WaitForAsyncUtils.waitForFxEvents();
        Parent historial = obtenerRootDialogo();

        assertFalse(from(historial).lookup(hasText("Historial de Pedidos")).queryAll().isEmpty(),
                "Falta el título 'Historial de Pedidos'");

        TableView<?> tabla = from(historial).lookup("#tvPedido").queryAs(TableView.class);
        assertNotNull(tabla, "No existe la tabla tvPedido");
        assertTrue(tabla.isVisible());

        String[] esperadas = {"id", "usuario_id", "fecha", "estado", "metodo_pago", "detalle", "total"};
        assertEquals(esperadas.length, tabla.getColumns().size(), "Número de columnas incorrecto");
        for (int i = 0; i < esperadas.length; i++) {
            assertEquals(esperadas[i], tabla.getColumns().get(i).getText(), "Columna " + i + " incorrecta");
        }
    }

    @AfterEach
    public void tearDown() throws TimeoutException {
        cerrarVentanasExtra();
        FxToolkit.hideStage(); // cerrar la ventana
        release(new KeyCode[]{}); // soltar teclas que hayan quedado pulsadas
        release(new MouseButton[]{}); // soltar botones del ratón
    }

    // ---------------------- Métodos auxiliares ----------------------
    // obtengo el label por el selector
    private Label obtenerLabel(String selector) {
        return lookup(selector).queryAs(Label.class);
    }

    // obtengo el contenedor de las lineas del pedido
    private VBox lineasPedido() {
        return lookup("#contenedorLineasPedido").queryAs(VBox.class);
    }

    // obtengo la primera linea del pedido
    private Node primeraLinea() {
        return lineasPedido().getChildren().get(0);
    }

    private Label label(Node padre, String id) {
        Label l = from(padre).lookup(id).queryAs(Label.class);
        assertNotNull(l, "No se encuentra " + id);
        return l;
    }

    private Button boton(Node padre, String texto) {
        Button b = from(padre).lookup(hasText(texto)).queryButton();
        assertNotNull(b, "No se encuentra el botón '" + texto + "'");
        return b;
    }

    private String nombreDe(Node tarjeta) {
        return label(tarjeta, "#lblNombre").getText();
    }

    // obtengo una tarjeta segun su posicion
    private Node obtenerTarjeta(int indice) {
        FlowPane productos = lookup("#contenedorProductos").queryAs(FlowPane.class);
        assumeTrue(productos.getChildren().size() > indice,
                "No hay suficientes productos de venta en la BD");
        Node tarjeta = productos.getChildren().get(indice);
        assumeTrue(!from(tarjeta).lookup(hasText("Añadir")).queryAll().isEmpty(),
                "La posición " + indice + " no es una tarjeta de producto");
        return tarjeta;
    }

    // añado el producto de la tarjeta
    private void anadirDesde(Node tarjeta) {
        clic(boton(tarjeta, "Añadir"));
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void anadirPrimerProducto() {
        anadirDesde(obtenerTarjeta(0));
    }

    // abro el dialogo de pago
    private Parent abrirDialogoCobrar() {
        clic(lookup(".btn-cobrar").query());
        WaitForAsyncUtils.waitForFxEvents();
        return obtenerRootDialogo();
    }

    // obtengo la ventana que se ha abierto
    private Parent obtenerRootDialogo() {
        for (Window ventana : listWindows()) {
            if (ventana != stagePrincipal) {
                return ventana.getScene().getRoot();
            }
        }
        fail("No se ha abierto ningún dialog");
        return null;
    }

    // convierto el precio a double para poder compararlo
    private double importe(String texto) {
        String numero = texto.split("€")[0].trim();
        numero = numero.replace(".", "").replace(",", ".");
        return Double.parseDouble(numero);
    }

    // cierro las ventanas extra que se hayan quedado abiertas
    private void cerrarVentanasExtra() {
        interact(() -> {
            for (Window ventana : new ArrayList<>(listWindows())) {
                if (ventana != stagePrincipal) {
                    ventana.hide();
                }
            }
        });
    }

    // ---------------------- Clics ----------------------
    private void clic(Node nodo) {
        clickOn(nodo);
    }
}
