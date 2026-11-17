package view;

import controller.EditarProveedorController;
import dao.implement.ComprasDaoImpl;
import dao.implement.MovimientoDaoImpl;
import dao.implement.ProductoDaoImp;
import dao.implement.ProveedorDaoImpl;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.Proveedor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import services.ComprasService;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests del diálogo de proveedor ({@code editarProveedor.fxml}), abierto solo, en modo alta y en
 * modo edición.
 *
 * Necesita MongoDB arrancado y con algún proveedor. El diálogo se abre con datos reales: se le
 * pasa el service igual que hace {@code ComprasController}, y el modo edición se prueba con el
 * primer proveedor de la base de datos. Solo prueba la vista (carga, modos y mensajes de error):
 * ningún test llega a guardar.
 *
 * @author anazk
 */
public class EditarProveedorTest extends ApplicationTest {

    /**
     * Ventana del diálogo.
     */
    private Stage stage;
    /**
     * Controller del diálogo, para pasarlo a modo edición como hace {@code ComprasController}.
     */
    private EditarProveedorController controlador;
    /**
     * Service que se le pasa al diálogo, como hace {@code ComprasController}.
     */
    private ComprasService comprasService;

    /**
     * Constructor vacío. Lo usa JUnit para crear el test.
     */
    public EditarProveedorTest() {
    }

    /**
     * Abre el diálogo en modo alta, con los estilos de la aplicación, y le pasa el service.
     *
     * @param stage ventana que da TestFX
     * @throws IOException si no se encuentra el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        comprasService = new ComprasService(ComprasDaoImpl.getInstance(), ProveedorDaoImpl.getInstance(),
                ProductoDaoImp.getInstance(), MovimientoDaoImpl.getInstance());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/compra/editarProveedor.fxml"));
        Parent root = loader.load();
        controlador = loader.getController();
        controlador.setComprasService(comprasService);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * En modo alta: título de alta, campos vacíos, activo marcado y ningún día marcado.
     */
    @Test
    public void testEstadoInicialEnModoAlta() {
        assertEquals("NUEVO PROVEEDOR", texto("#lblTitulo"));
        assertEquals("", campo("#txtNombre").getText());
        assertEquals("", campo("#txtNifCif").getText());
        assertTrue(check("#chkActivo").isSelected(), "Un proveedor nuevo empieza activo");
        for (String dia : new String[]{"#chkLunes", "#chkMartes", "#chkMiercoles", "#chkJueves",
            "#chkViernes", "#chkSabado", "#chkDomingo"}) {
            assertFalse(check(dia).isSelected(), dia + " no debería venir marcado");
        }
    }

    /**
     * Guardar sin datos da error y la ventana sigue abierta.
     */
    @Test
    public void testGuardarVacioDaError() {
        clickOn("#btnGuardar");

        assertEquals("El nombre del proveedor es obligatorio.", texto("#lblError"));
        assertTrue(stage.isShowing(), "Con error la ventana no se cierra");
    }

    /**
     * Guardar sin NIF/CIF da error.
     */
    @Test
    public void testSinNifDaError() {
        clickOn("#txtNombre").write("Distribuciones Prueba");

        clickOn("#btnGuardar");

        assertEquals("El NIF/CIF es obligatorio.", texto("#lblError"));
    }

    /**
     * Un plazo de entrega con letras da error.
     */
    @Test
    public void testPlazoConLetrasDaError() {
        rellenarObligatorios();
        clickOn("#txtPlazoEntrega").write("abc");

        clickOn("#btnGuardar");

        assertEquals("El plazo de entrega debe ser un número entero.", texto("#lblError"));
    }

    /**
     * Un plazo de entrega negativo da el error de {@code Proveedor.validar()}.
     */
    @Test
    public void testPlazoNegativoDaError() {
        rellenarObligatorios();
        clickOn("#txtPlazoEntrega").write("-2");

        clickOn("#btnGuardar");

        assertEquals("El plazo de entrega no puede ser negativo.", texto("#lblError"));
    }

    /**
     * Un correo sin {@code @} da el error de {@code Contacto.validar()}.
     */
    @Test
    public void testEmailSinArrobaDaError() {
        rellenarObligatorios();
        clickOn("#txtEmail").write("pedidos.prueba.es");

        clickOn("#btnGuardar");

        assertEquals("El correo electrónico no es válido.", texto("#lblError"));
    }

    /**
     * Un teléfono con letras da el error de {@code Contacto.validar()}.
     */
    @Test
    public void testTelefonoConLetrasDaError() {
        rellenarObligatorios();
        clickOn("#txtTelefono").write("944 abc");

        clickOn("#btnGuardar");

        assertEquals("El teléfono solo puede tener números, espacios y '+'.", texto("#lblError"));
    }

    /**
     * Cancelar cierra la ventana sin guardar.
     */
    @Test
    public void testCancelarCierraSinGuardar() {
        rellenarObligatorios();

        clickOn("#btnCancelar");
        WaitForAsyncUtils.waitForFxEvents();

        assertFalse(controlador.isGuardado());
        assertFalse(stage.isShowing());
    }

    /**
     * En modo edición, el título cambia y los campos y los días vienen con los datos del primer
     * proveedor de la base de datos.
     */
    @Test
    public void testModoEdicionRellenaLosCampos() {
        List<Proveedor> proveedores = comprasService.listarProveedores();
        assumeTrue(!proveedores.isEmpty(), "No hay proveedores en la BD");
        Proveedor p = proveedores.get(0);
        editar(p);

        assertEquals("EDITAR PROVEEDOR", texto("#lblTitulo"));
        assertEquals(p.getNombre(), campo("#txtNombre").getText());
        assertEquals(p.getNif_cif(), campo("#txtNifCif").getText());
        assertEquals(String.valueOf(p.getPlazo_entrega()), campo("#txtPlazoEntrega").getText());
        assertEquals(p.isActivo(), check("#chkActivo").isSelected());
        assertEquals(p.getDias_laborales().contains("lunes"), check("#chkLunes").isSelected());
        if (p.getContacto() != null) {
            assertEquals(p.getContacto().getEmail(), campo("#txtEmail").getText());
        }
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
     * Escribe el nombre y el NIF, que son los campos obligatorios.
     */
    private void rellenarObligatorios() {
        clickOn("#txtNombre").write("Distribuciones Prueba");
        clickOn("#txtNifCif").write("B12345678");
    }

    /**
     * Devuelve el texto de un Label.
     *
     * @param id selector del Label, por ejemplo {@code "#lblError"}
     * @return su texto
     */
    private String texto(String id) {
        return lookup(id).queryAs(Label.class).getText();
    }

    /**
     * Devuelve un campo de texto del diálogo.
     *
     * @param id selector del campo
     * @return el campo
     */
    private TextField campo(String id) {
        return lookup(id).queryAs(TextField.class);
    }

    /**
     * Devuelve un CheckBox del diálogo.
     *
     * @param id selector del CheckBox
     * @return el CheckBox
     */
    private CheckBox check(String id) {
        return lookup(id).queryAs(CheckBox.class);
    }

    /**
     * Pasa el diálogo a modo edición, como hace {@code ComprasController} al pulsar «Editar».
     *
     * @param proveedor proveedor que se edita
     */
    private void editar(final Proveedor proveedor) {
        interact(new Runnable() {
            @Override
            public void run() {
                controlador.setProveedor(proveedor);
            }
        });
    }
}
