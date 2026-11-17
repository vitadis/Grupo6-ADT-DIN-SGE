package view;

import controller.RegistrarCompraController;
import dao.implement.ComprasDaoImpl;
import dao.implement.MovimientoDaoImpl;
import dao.implement.ProductoDaoImp;
import dao.implement.ProveedorDaoImpl;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.LineaCompra;
import model.Producto;
import model.Proveedor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import services.ComprasService;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Tests del diálogo «Registrar compra» ({@code registrarCompra.fxml}), abierto solo.
 *
 * Necesita MongoDB arrancado y con proveedores y productos activos. El diálogo se abre con datos
 * reales: se le pasa el service igual que hace {@code ComprasController}, y los proveedores y
 * productos de los tests se cogen de sus combos. Solo prueba la vista (carga, líneas, totales y
 * mensajes de error): ningún test llega a guardar.
 *
 * @author anazk
 */
public class RegistrarCompraTest extends ApplicationTest {

    /**
     * Ventana del diálogo.
     */
    private Stage stage;
    /**
     * Controller del diálogo, para pasarle datos como hace {@code ComprasController}.
     */
    private RegistrarCompraController controlador;

    /**
     * Constructor vacío. Lo usa JUnit para crear el test.
     */
    public RegistrarCompraTest() {
    }

    /**
     * Abre el diálogo con los estilos de la aplicación y le pasa el service, como hace
     * {@code ComprasController}.
     *
     * @param stage ventana que da TestFX
     * @throws IOException si no se encuentra el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/compra/registrarCompra.fxml"));
        Parent root = loader.load();
        controlador = loader.getController();
        controlador.setComprasService(new ComprasService(ComprasDaoImpl.getInstance(),
                ProveedorDaoImpl.getInstance(), ProductoDaoImp.getInstance(), MovimientoDaoImpl.getInstance()));
        controlador.setUsuarioId(1);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Al abrir: título, fecha de hoy, combos cargados, lista vacía, totales a cero y sin errores.
     */
    @Test
    public void testEstadoInicial() {
        assertEquals("REGISTRAR COMPRA", texto("#lblTitulo"));
        assertEquals(LocalDate.now(), lookup("#dpFecha").queryAs(DatePicker.class).getValue());
        assertFalse(combo("#cbProveedor").getItems().isEmpty(), "El combo de proveedores no se ha cargado");
        assertFalse(combo("#cbProducto").getItems().isEmpty(), "El combo de productos no se ha cargado");
        assertEquals("Todavía no hay productos en la compra.", ((Label) lineas().getChildren().get(0)).getText());
        assertEquals("0,00 €", texto("#lblBase"));
        assertEquals("0,00 €", texto("#lblTotalIva"));
        assertEquals("0,00 €", texto("#lblTotal"));
        assertEquals("", texto("#lblError"));
    }

    /**
     * Añadir sin elegir producto da error.
     */
    @Test
    public void testAnadirSinProductoDaError() {
        clickOn("#btnAnadirLinea");

        assertEquals("Selecciona un producto.", texto("#lblError"));
    }

    /**
     * Una cantidad con letras da error.
     */
    @Test
    public void testCantidadConLetrasDaError() {
        anadirLinea(producto(0), "abc", "2,50");

        assertEquals("La cantidad y el precio deben ser números.", texto("#lblError"));
    }

    /**
     * Una cantidad 0 da el error de {@code LineaCompra.validar()}.
     */
    @Test
    public void testCantidadCeroDaError() {
        anadirLinea(producto(0), "0", "2,50");

        assertEquals("La cantidad debe ser mayor que 0.", texto("#lblError"));
    }

    /**
     * Al añadir 4 unidades a 2,50 € la base es 10,00 € y el IVA y el total salen con el IVA del
     * producto; además los campos se vacían.
     */
    @Test
    public void testAnadirLineaCalculaLosTotales() {
        Producto p = producto(0);
        anadirLinea(p, "4", "2,50");

        assertEquals(1, lineas().getChildren().size());
        assertEquals(p.getNombre(), celda(lineas().getChildren().get(0), 0));
        assertEquals(euros(10.0), texto("#lblBase"));
        assertEquals(euros(10.0 * p.getIva()), texto("#lblTotalIva"));
        assertEquals(euros(10.0 + 10.0 * p.getIva()), texto("#lblTotal"));
        assertEquals("", lookup("#txtCantidad").queryAs(TextField.class).getText(), "Los campos se vacían");
    }

    /**
     * Añadir otra vez el mismo producto sustituye su línea en vez de crear otra.
     */
    @Test
    public void testElMismoProductoSustituyeSuLinea() {
        anadirLinea(producto(0), "4", "2,50");
        anadirLinea(producto(0), "6", "2,50");

        assertEquals(1, lineas().getChildren().size(), "No debe haber dos líneas del mismo producto");
        assertEquals(euros(15.0), texto("#lblBase"));
    }

    /**
     * Dos productos distintos son dos líneas y la base es la suma de las dos.
     */
    @Test
    public void testDosProductosDistintosSonDosLineas() {
        anadirLinea(producto(0), "4", "2,50");
        anadirLinea(producto(1), "1", "8");

        assertEquals(2, lineas().getChildren().size());
        assertEquals(euros(18.0), texto("#lblBase"));
    }

    /**
     * El botón ✕ quita la línea y la compra vuelve a estar vacía.
     */
    @Test
    public void testQuitarLineaVuelveAlEstadoVacio() {
        anadirLinea(producto(0), "4", "2,50");

        clickOn(from(lineas()).lookup(hasText("✕")).queryButton());
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals("Todavía no hay productos en la compra.", ((Label) lineas().getChildren().get(0)).getText());
        assertEquals("0,00 €", texto("#lblTotal"));
    }

    /**
     * Guardar sin proveedor da error y la ventana sigue abierta.
     */
    @Test
    public void testGuardarSinProveedorDaError() {
        anadirLinea(producto(0), "4", "2,50");
        clickOn("#txtNumeroFactura").write("F-PRUEBA");

        clickOn("#btnGuardar");

        assertEquals("El proveedor es obligatorio.", texto("#lblError"));
        assertTrue(stage.isShowing(), "Con error la ventana no se cierra");
    }

    /**
     * Guardar sin número de factura da error.
     */
    @Test
    public void testGuardarSinFacturaDaError() {
        elegir("#cbProveedor", proveedor(0).getNombre());
        anadirLinea(producto(0), "4", "2,50");

        clickOn("#btnGuardar");

        assertEquals("El número de factura es obligatorio.", texto("#lblError"));
    }

    /**
     * Guardar sin ninguna línea da error.
     */
    @Test
    public void testGuardarSinLineasDaError() {
        elegir("#cbProveedor", proveedor(0).getNombre());
        clickOn("#txtNumeroFactura").write("F-PRUEBA");

        clickOn("#btnGuardar");

        assertEquals("Añade al menos un producto.", texto("#lblError"));
    }

    /**
     * Cancelar cierra la ventana sin guardar la compra.
     */
    @Test
    public void testCancelarCierraSinGuardar() {
        elegir("#cbProveedor", proveedor(0).getNombre());
        clickOn("#txtNumeroFactura").write("F-PRUEBA");
        anadirLinea(producto(0), "4", "2,50");

        clickOn("#btnCancelar");
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(controlador.isGuardado());
        assertFalse(stage.isShowing());
    }

    /**
     * Abierta desde la lista sugerida, la compra viene con su título, el proveedor elegido y las
     * líneas sugeridas.
     */
    @Test
    public void testLineasSugeridasRellenanLaCompra() {
        final Proveedor prov = proveedor(1);
        Producto prod = producto(1);
        final List<LineaCompra> sugeridas = new ArrayList<>();
        sugeridas.add(new LineaCompra(prod.getId(), prod.getNombre(), 3, 8.0, prod.getIva(), 24.0));

        interact(new Runnable() {
            @Override
            public void run() {
                controlador.setLineasSugeridas(prov.getId(), sugeridas);
            }
        });

        assertEquals("NUEVA COMPRA DESDE LA LISTA", texto("#lblTitulo"));
        assertEquals(prov.getId(), ((Proveedor) combo("#cbProveedor").getValue()).getId());
        assertEquals(1, lineas().getChildren().size());
        assertEquals(prod.getNombre(), celda(lineas().getChildren().get(0), 0));
        assertEquals(euros(24.0), texto("#lblBase"));
    }

    /**
     * Oculta la ventana y suelta el teclado y el ratón.
     *
     * @throws TimeoutException si TestFX no consigue ocultar la ventana
     */
    @AfterEach
    public void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /**
     * Devuelve el texto de un Label.
     *
     * @param id selector del Label, por ejemplo {@code "#lblTotal"}
     * @return su texto
     */
    private String texto(String id) {
        return lookup(id).queryAs(Label.class).getText();
    }

    /**
     * Devuelve el VBox donde se pintan las líneas de la compra.
     *
     * @return el contenedor de líneas
     */
    private VBox lineas() {
        return lookup("#contenedorLineas").queryAs(VBox.class);
    }

    /**
     * Devuelve uno de los ComboBox del diálogo.
     *
     * @param <T> tipo de las opciones
     * @param id selector del ComboBox
     * @return el ComboBox
     */
    @SuppressWarnings("unchecked")
    private <T> ComboBox<T> combo(String id) {
        return lookup(id).queryAs(ComboBox.class);
    }

    /**
     * Devuelve un proveedor del combo. Si no hay tantos, el test se salta.
     *
     * @param i posición en el combo
     * @return el proveedor
     */
    private Proveedor proveedor(int i) {
        assumeTrue(combo("#cbProveedor").getItems().size() > i, "No hay suficientes proveedores activos en la BD");
        return (Proveedor) combo("#cbProveedor").getItems().get(i);
    }

    /**
     * Devuelve un producto del combo. Si no hay tantos, el test se salta.
     *
     * @param i posición en el combo
     * @return el producto
     */
    private Producto producto(int i) {
        assumeTrue(combo("#cbProducto").getItems().size() > i, "No hay suficientes productos activos en la BD");
        return (Producto) combo("#cbProducto").getItems().get(i);
    }

    /**
     * Da formato a un importe igual que la ventana, por ejemplo {@code 10,00 €}.
     *
     * @param importe importe
     * @return el texto con dos decimales y el símbolo del euro
     */
    private String euros(double importe) {
        return String.format("%.2f €", Math.round(importe * 100) / 100.0);
    }

    /**
     * Devuelve el texto de una celda de una fila de líneas.
     *
     * @param fila fila de la compra
     * @param columna posición de la celda
     * @return su texto
     */
    private String celda(Node fila, int columna) {
        return ((Label) ((HBox) fila).getChildren().get(columna)).getText();
    }

    /**
     * Abre un ComboBox y hace clic en la opción con ese texto de la lista desplegable.
     *
     * @param id selector del ComboBox
     * @param texto texto de la opción
     */
    private void elegir(String id, String texto) {
        clickOn(id);
        WaitForAsyncUtils.waitForFxEvents();
        for (Node nodo : lookup(".list-cell").queryAll()) {
            if (nodo instanceof ListCell && nodo.getScene().getWindow() != stage
                    && texto.equals(((ListCell<?>) nodo).getText())) {
                clickOn(nodo);
                WaitForAsyncUtils.waitForFxEvents();
                return;
            }
        }
        fail("No se encuentra la opción «" + texto + "» en " + id);
    }

    /**
     * Elige un producto, escribe la cantidad y el precio y pulsa «+ Añadir».
     *
     * @param producto producto que se añade
     * @param cantidad texto de la cantidad
     * @param precio texto del precio por unidad
     */
    private void anadirLinea(Producto producto, String cantidad, String precio) {
        elegir("#cbProducto", producto.getNombre());
        clickOn("#txtCantidad").write(cantidad);
        clickOn("#txtPrecioUnidad").write(precio);
        clickOn("#btnAnadirLinea");
        WaitForAsyncUtils.waitForFxEvents();
    }
}
