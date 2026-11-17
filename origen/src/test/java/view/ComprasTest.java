package view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
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

/**
 * Tests de la pantalla «Compras y proveedores» ({@code CompraView.fxml}).
 *
 * Solo prueba la pantalla. De los diálogos solo comprueba que se abren: lo que pasa dentro de
 * ellos está en {@link RegistrarCompraTest} y {@link EditarProveedorTest}.
 *
 * Lee de la base de datos {@code origen} (MongoDB tiene que estar arrancado), pero no escribe
 * nada: ningún test pulsa «Guardar».
 *
 * @author anazk
 */
public class ComprasTest extends ApplicationTest {

    /**
     * Ventana de la pantalla, para distinguirla de los diálogos que se abren encima.
     */
    private Stage stagePrincipal;

    /**
     * Constructor vacío. Lo usa JUnit para crear el test.
     */
    public ComprasTest() {
    }

    /**
     * Abre la pantalla de compras maximizada.
     *
     * @param stage ventana que da TestFX
     * @throws IOException si no se encuentra el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        stagePrincipal = stage;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/compra/CompraView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Se ven el título, el subtítulo y el botón principal de la cabecera.
     */
    @Test
    public void testVisibilidadCabecera() {
        verifyThat("#lblTitulo", isVisible());
        verifyThat("#lblSubtitulo", isVisible());
        verifyThat("#btnAccionPrincipal", isVisible());
    }

    /**
     * Se ven la lista de compras, su contador y el panel de la lista sugerida.
     */
    @Test
    public void testVisibilidadPestanaCompras() {
        verifyThat("#tabPaneCompras", isVisible());
        verifyThat("#contenedorCompras", isVisible());
        verifyThat("#lblContador", isVisible());
        verifyThat("#panelSugerencias", isVisible());
        verifyThat("#btnCrearCompra", isVisible());
    }

    /**
     * Al abrir, la cabecera es la de la pestaña Compras.
     */
    @Test
    public void testCabeceraInicialEsDeCompras() {
        assertEquals("Compras", texto("#lblTitulo"));
        assertEquals("Compras registradas y lista de compra sugerida", texto("#lblSubtitulo"));
        assertEquals("Registrar compra", boton("#btnAccionPrincipal").getText());
    }

    /**
     * En la pestaña Proveedores cambian el título, el subtítulo y el botón principal.
     */
    @Test
    public void testPestanaProveedoresCambiaLaCabecera() {
        abrirPestana("Proveedores");

        assertEquals("Proveedores", texto("#lblTitulo"));
        assertEquals("Datos de contacto e historial de compras de cada proveedor", texto("#lblSubtitulo"));
        assertEquals("Nuevo proveedor", boton("#btnAccionPrincipal").getText());
    }

    /**
     * Al volver a la pestaña Compras, la cabecera vuelve a ser la de compras.
     */
    @Test
    public void testVolverAComprasRestauraLaCabecera() {
        abrirPestana("Proveedores");
        abrirPestana("Compras");

        assertEquals("Compras", texto("#lblTitulo"));
        assertEquals("Registrar compra", boton("#btnAccionPrincipal").getText());
    }

    /**
     * El contador dice tantas compras como filas hay en la lista.
     */
    @Test
    public void testContadorCoincideConLasFilas() {
        int filas = contenedor("#contenedorCompras").getChildren().size();
        assertEquals(filas + " compras", texto("#lblContador"));
    }

    /**
     * Cada fila tiene fecha, proveedor, factura, líneas e importe, y el importe acaba en €.
     */
    @Test
    public void testCadaCompraTieneSusCincoColumnas() {
        VBox compras = contenedor("#contenedorCompras");
        assumeTrue(!compras.getChildren().isEmpty(), "No hay compras en la BD");

        for (Node nodo : compras.getChildren()) {
            HBox fila = (HBox) nodo;
            assertEquals(5, fila.getChildren().size(), "Fecha, proveedor, factura, líneas e importe");
            String importe = ((Label) fila.getChildren().get(4)).getText();
            assertTrue(importe.endsWith("€"), "El importe no acaba en €: " + importe);
        }
    }

    /**
     * Sin sugerencias se avisa y no se puede crear la compra; con sugerencias se dice cuántas
     * hay y el botón está activo.
     */
    @Test
    public void testListaSugeridaYBotonCrearCompra() {
        int sugeridas = contenedor("#contenedorSugerencias").getChildren().size();

        if (sugeridas == 0) {
            assertEquals("No hay productos con stock bajo o agotados.", texto("#lblSubtituloSugerencias"));
            assertTrue(boton("#btnCrearCompra").isDisabled(), "Sin sugerencias no se puede crear la compra");
        } else {
            assertTrue(texto("#lblSubtituloSugerencias").startsWith("Generada con " + sugeridas));
            assertFalse(boton("#btnCrearCompra").isDisabled());
        }
    }

    /**
     * En la pestaña Compras, el botón principal abre el diálogo de registrar compra.
     */
    @Test
    public void testRegistrarCompraAbreElDialogo() {
        activarVentana();
        int ventanas = listWindows().size();

        clickOn("#btnAccionPrincipal");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > ventanas, "«Registrar compra» no abre ninguna ventana");
        assertNotNull(from(rootDialogo()).lookup("#cbProveedor").query(), "No es el diálogo de compra");
    }

    /**
     * Al entrar en Proveedores, la ficha muestra el primer proveedor de la lista.
     */
    @Test
    public void testHayProveedoresYSeSeleccionaElPrimero() {
        abrirPestana("Proveedores");
        VBox lista = contenedor("#contenedorListaProveedores");
        assumeTrue(!lista.getChildren().isEmpty(), "No hay proveedores en la BD");

        assertEquals(nombreDeFila(lista.getChildren().get(0)), texto("#lblNombreProveedor"));
    }

    /**
     * Al hacer clic en otro proveedor, la ficha cambia y solo esa fila queda marcada.
     */
    @Test
    public void testClicEnUnProveedorMuestraSuFicha() {
        abrirPestana("Proveedores");
        VBox lista = contenedor("#contenedorListaProveedores");
        assumeTrue(lista.getChildren().size() > 1, "Hacen falta al menos dos proveedores");

        Node segunda = lista.getChildren().get(1);
        clickOn(segunda);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(nombreDeFila(segunda), texto("#lblNombreProveedor"));
        assertTrue(segunda.getStyleClass().contains("proveedor-fila-seleccionada"), "La fila no se marca");
        assertFalse(lista.getChildren().get(0).getStyleClass().contains("proveedor-fila-seleccionada"),
                "La fila anterior se queda marcada");
    }

    /**
     * El subtítulo del historial dice tantas compras como filas tiene.
     */
    @Test
    public void testHistorialCoincideConSuContador() {
        abrirPestana("Proveedores");
        assumeTrue(!contenedor("#contenedorListaProveedores").getChildren().isEmpty(), "No hay proveedores en la BD");

        int filas = contenedor("#contenedorHistorialProveedor").getChildren().size();
        assertEquals(filas + " compras", texto("#lblSubtituloHistorial"));
    }

    /**
     * En la pestaña Proveedores, el botón principal abre el diálogo de proveedor.
     */
    @Test
    public void testNuevoProveedorAbreElDialogo() {
        abrirPestana("Proveedores");
        int ventanas = listWindows().size();

        clickOn("#btnAccionPrincipal");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > ventanas, "«Nuevo proveedor» no abre ninguna ventana");
        assertNotNull(from(rootDialogo()).lookup("#txtNifCif").query(), "No es el diálogo de proveedor");
    }

    /**
     * El botón «Editar» de la ficha abre el diálogo de proveedor.
     */
    @Test
    public void testEditarProveedorAbreElDialogo() {
        abrirPestana("Proveedores");
        assumeTrue(!contenedor("#contenedorListaProveedores").getChildren().isEmpty(), "No hay proveedores en la BD");
        int ventanas = listWindows().size();

        clickOn("#btnEditarProveedor");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > ventanas, "«Editar» no abre ninguna ventana");
    }

    /**
     * Cierra los diálogos que se hayan quedado abiertos, oculta la ventana y suelta el teclado y
     * el ratón para que no afecten al siguiente test.
     *
     * @throws TimeoutException si TestFX no consigue ocultar la ventana
     */
    @AfterEach
    public void tearDown() throws TimeoutException {
        cerrarVentanasExtra();
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /**
     * Devuelve el texto de un Label.
     *
     * @param id selector del Label, por ejemplo {@code "#lblTitulo"}
     * @return su texto
     */
    private String texto(String id) {
        return lookup(id).queryAs(Label.class).getText();
    }

    /**
     * Devuelve un botón de la pantalla.
     *
     * @param id selector del botón
     * @return el botón
     */
    private Button boton(String id) {
        return lookup(id).queryAs(Button.class);
    }

    /**
     * Devuelve uno de los VBox donde se pintan las filas.
     *
     * @param id selector del VBox
     * @return el VBox
     */
    private VBox contenedor(String id) {
        return lookup(id).queryAs(VBox.class);
    }

    /**
     * Devuelve el nombre de una fila de la lista de proveedores, que es su primera celda.
     *
     * @param fila fila de la lista
     * @return el nombre del proveedor
     */
    private String nombreDeFila(Node fila) {
        return ((Label) ((HBox) fila).getChildren().get(0)).getText();
    }

    /**
     * Hace clic en la cabecera de una pestaña y comprueba que queda seleccionada.
     *
     * @param nombre texto de la pestaña
     */
    private void abrirPestana(String nombre) {
        for (Node nodo : lookup(".tab-pane .tab-label").queryAll()) {
            if (nodo instanceof Label && nombre.equals(((Label) nodo).getText())) {
                clickOn(nodo);
                WaitForAsyncUtils.waitForFxEvents();
                assertEquals(nombre, lookup("#tabPaneCompras").queryAs(TabPane.class)
                        .getSelectionModel().getSelectedItem().getText());
                return;
            }
        }
        fail("No se encuentra la pestaña " + nombre);
    }

    /**
     * Hace un clic en el título para activar la ventana. En Windows el primer clic en una
     * ventana sin foco solo la activa y no llega al botón.
     */
    private void activarVentana() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#lblTitulo");
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Devuelve el contenido del diálogo que se ha abierto encima de la pantalla.
     *
     * @return la raíz del diálogo
     */
    private Parent rootDialogo() {
        for (Window ventana : listWindows()) {
            if (ventana != stagePrincipal) {
                return ventana.getScene().getRoot();
            }
        }
        fail("No se ha abierto ningún diálogo");
        return null;
    }

    /**
     * Cierra sin guardar los diálogos que se hayan quedado abiertos.
     */
    private void cerrarVentanasExtra() {
        interact(new Runnable() {
            @Override
            public void run() {
                for (Window ventana : new ArrayList<>(listWindows())) {
                    if (ventana != stagePrincipal) {
                        ventana.hide();
                    }
                }
            }
        });
    }
}
