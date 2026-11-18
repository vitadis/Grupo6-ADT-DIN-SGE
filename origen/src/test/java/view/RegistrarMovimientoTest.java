package view;

import controller.RegistrarMovimientoController;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del diálogo «Registrar movimiento» ({@code registrarMovimiento.fxml}), abierto solo.
 *
 * Necesita MongoDB arrancado y con alguna compra: el diálogo lee al abrirse los registros de
 * {@code registros_economicos}, y los tests del aviso usan la categoría de las compras
 * («Aprovisionamiento»), que siempre tiene gastos. Solo prueba la vista (carga, desplegable,
 * aviso y mensajes de error): ningún test llega a guardar.
 *
 * @author anazk
 */
public class RegistrarMovimientoTest extends ApplicationTest {

    /**
     * Ventana del diálogo.
     */
    private Stage stage;
    /**
     * Controller del diálogo.
     */
    private RegistrarMovimientoController controlador;
    /**
     * Categoría de los gastos de las compras, que siempre tiene registros anteriores.
     */
    private static final String CATEGORIA_CON_GASTOS = "Aprovisionamiento";

    /**
     * Constructor vacío. Lo usa JUnit para crear el test.
     */
    public RegistrarMovimientoTest() {
    }

    /**
     * Abre el diálogo con los estilos de la aplicación.
     *
     * @param stage ventana que da TestFX
     * @throws IOException si no se encuentra el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/economia/registrarMovimiento.fxml"));
        Parent root = loader.load();
        controlador = loader.getController();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Al abrir: «Gasto» marcado, fecha de hoy, sin aviso ni errores.
     */
    @Test
    public void testEstadoInicial() {
        assertTrue(toggle("#tgGasto").isSelected());
        assertFalse(toggle("#tgIngreso").isSelected());
        assertEquals(LocalDate.now(), lookup("#dpFecha").queryAs(DatePicker.class).getValue());
        assertFalse(avisoVisible());
        assertEquals("", texto("#lblError"));
    }

    /**
     * El desplegable trae las categorías fijas, pero no las de compras y pedidos
     * («Aprovisionamiento» y «Ventas»), que se apuntan solas.
     */
    @Test
    public void testCategoriasDelDesplegable() {
        assertTrue(categorias().getItems().contains("Suministros"));
        assertTrue(categorias().getItems().contains("Personal"));
        assertFalse(categorias().getItems().contains("Aprovisionamiento"));
        assertFalse(categorias().getItems().contains("Ventas"));
    }

    /**
     * Sin concepto da el error de {@code Movimiento.validar()}.
     */
    @Test
    public void testSinConceptoDaError() {
        clickOn(categorias().getEditor()).write("Otros");
        clickOn("#txtImporte").write("10");

        clickOn("#btnGuardar");

        assertEquals("El concepto es obligatorio.", texto("#lblError"));
        assertTrue(stage.isShowing(), "Con error la ventana no se cierra");
    }

    /**
     * Sin categoría da error.
     */
    @Test
    public void testSinCategoriaDaError() {
        clickOn("#txtConcepto").write("Prueba");
        clickOn("#txtImporte").write("10");

        clickOn("#btnGuardar");

        assertEquals("La categoría es obligatoria.", texto("#lblError"));
    }

    /**
     * Un importe con letras da error.
     */
    @Test
    public void testImporteConLetrasDaError() {
        rellenar("Prueba", "Otros", "abc");

        clickOn("#btnGuardar");

        assertEquals("El importe debe ser un número.", texto("#lblError"));
    }

    /**
     * Un importe 0 da error.
     */
    @Test
    public void testImporteCeroDaError() {
        rellenar("Prueba", "Otros", "0");

        clickOn("#btnGuardar");

        assertEquals("El importe debe ser mayor que 0.", texto("#lblError"));
    }

    /**
     * Si se desmarca el tipo, da error.
     */
    @Test
    public void testSinTipoDaError() {
        rellenar("Prueba", "Otros", "10");
        clickOn("#tgGasto");

        clickOn("#btnGuardar");

        assertEquals("El tipo debe ser ingreso o gasto.", texto("#lblError"));
    }

    /**
     * Un gasto muy alto en una categoría con gastos anteriores enseña el aviso con el nombre de
     * la categoría.
     */
    @Test
    public void testAvisoSiElGastoSuperaLaMedia() {
        rellenar("Prueba", CATEGORIA_CON_GASTOS, "99999");

        assertTrue(avisoVisible());
        assertTrue(texto("#lblAviso").contains("«" + CATEGORIA_CON_GASTOS + "»"));
    }

    /**
     * Un gasto muy bajo no enseña el aviso.
     */
    @Test
    public void testSinAvisoSiElGastoEsBajo() {
        rellenar("Prueba", CATEGORIA_CON_GASTOS, "0,01");

        assertFalse(avisoVisible());
    }

    /**
     * Un ingreso nunca enseña el aviso, aunque el importe sea alto.
     */
    @Test
    public void testUnIngresoNoTieneAviso() {
        clickOn("#tgIngreso");

        rellenar("Prueba", CATEGORIA_CON_GASTOS, "99999");

        assertFalse(avisoVisible());
    }

    /**
     * Cancelar cierra la ventana sin guardar.
     */
    @Test
    public void testCancelarCierraSinGuardar() {
        rellenar("Prueba", "Otros", "10");

        clickOn("#btnCancelar");
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(controlador.isGuardado());
        assertFalse(stage.isShowing());
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
     * Escribe el concepto, la categoría y el importe.
     *
     * @param concepto concepto del movimiento
     * @param categoria categoría
     * @param importe texto del importe
     */
    private void rellenar(String concepto, String categoria, String importe) {
        clickOn("#txtConcepto").write(concepto);
        clickOn(categorias().getEditor()).write(categoria);
        clickOn("#txtImporte").write(importe);
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Indica si se ve la caja ámbar del aviso.
     *
     * @return {@code true} si está visible
     */
    private boolean avisoVisible() {
        return lookup("#cajaAviso").queryAs(VBox.class).isVisible();
    }

    /**
     * Devuelve el desplegable de categorías.
     *
     * @return el ComboBox
     */
    @SuppressWarnings("unchecked")
    private ComboBox<String> categorias() {
        return lookup("#cbCategoria").queryAs(ComboBox.class);
    }

    /**
     * Devuelve uno de los dos botones de tipo.
     *
     * @param id selector del botón
     * @return el ToggleButton
     */
    private ToggleButton toggle(String id) {
        return lookup(id).queryAs(ToggleButton.class);
    }

    /**
     * Devuelve el texto de un Label.
     *
     * @param id selector del Label
     * @return su texto
     */
    private String texto(String id) {
        return lookup(id).queryAs(Label.class).getText();
    }
}
