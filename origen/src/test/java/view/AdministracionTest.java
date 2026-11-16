package view;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * TEST CLASS AdministracionTest
 *
 * Tests básicos de la vista de administración y su diálogo de usuario.
 *
 * @author Joel
 */
public class AdministracionTest extends ApplicationTest {

    private static final String MSG_SIN_CAMBIOS = "No se ha realizado ninguna modificación.";
    private static final String MSG_USERNAME_DUPLICADO = "Ya existe un usuario con ese username.";

    private Stage stagePrincipal;

    /**
     * Iniciamos el stage
     *
     * @param stage
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage stage) throws java.io.IOException {
        stagePrincipal = stage;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/admin/administracionView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root, 1000, 700));
        stage.show();
    }

    // ---------------------- administracionView ----------------------
    // ---------------------- Visibilidad Inicial de los nodos ----------------------
    @Test
    public void testVisibilidadTabPane() {
        verifyThat("#tabPaneAdmin", isVisible());
    }

    @Test
    public void testVisibilidadTablaUsuarios() {
        verifyThat("#tablaUsuarios", isVisible());
    }

    @Test
    public void testVisibilidadBtnAccionPrincipal() {
        verifyThat("#btnAccionPrincipal", isVisible());
    }

    @Test
    public void testVisibilidadLblRuta() {
        verifyThat("#lblRuta", isVisible());
    }

    @Test
    public void testVisibilidadLblTitulo() {
        verifyThat("#lblTitulo", isVisible());
    }

    // ---------------------- Estado inicial ----------------------
    @Test
    public void testTextoPestanas() {
        assertEquals(4, tabPane().getTabs().size());
        assertEquals("Usuarios y empleados", tabPane().getTabs().get(0).getText());
        assertEquals("Permisos", tabPane().getTabs().get(1).getText());
        assertEquals("Logger", tabPane().getTabs().get(2).getText());
        assertEquals("Configuración del sistema", tabPane().getTabs().get(3).getText());
    }

    @Test
    public void testPestanaUsuariosSeleccionadaPorDefecto() {
        assertEquals(0, tabPane().getSelectionModel().getSelectedIndex());
    }

    @Test
    public void testCabeceraInicial() {
        verifyThat("#lblTitulo", hasText("Usuarios y empleados"));
        verifyThat("#lblRuta", hasText("Administración > Usuarios y empleados"));
        verifyThat("#btnAccionPrincipal", hasText("Alta usuario"));
    }

    @Test
    public void testColumnasTablaUsuarios() {
        TableView<Usuario> tabla = tablaUsuarios();
        assertEquals(4, tabla.getColumns().size());
        assertEquals("Id", tabla.getColumns().get(0).getText());
        assertEquals("Username", tabla.getColumns().get(1).getText());
        assertEquals("Rol", tabla.getColumns().get(2).getText());
        assertEquals("Permisos", tabla.getColumns().get(3).getText());
    }

    // ---------------------- Navegación entre pestañas ----------------------
    @Test
    public void testIrAPermisos() {
        irAPestana("Permisos");

        assertEquals(1, tabPane().getSelectionModel().getSelectedIndex());
        verifyThat("#lblTitulo", hasText("Permisos"));
        verifyThat("#lblRuta", hasText("Administración > Permisos"));
        verifyThat("#btnAccionPrincipal", isInvisible());
    }

    @Test
    public void testIrALogger() {
        irAPestana("Logger");

        assertEquals(2, tabPane().getSelectionModel().getSelectedIndex());
        verifyThat("#lblTitulo", hasText("Logger del sistema"));
        verifyThat("#lblRuta", hasText("Administración > Logger"));
        verifyThat("#listaLogger", isVisible());
        verifyThat("#btnAccionPrincipal", isInvisible());
    }

    @Test
    public void testIrAConfiguracion() {
        irAPestana("Configuración del sistema");

        assertEquals(3, tabPane().getSelectionModel().getSelectedIndex());
        verifyThat("#lblTitulo", hasText("Configuración del sistema"));
        verifyThat("#lblRuta", hasText("Administración > Configuración del sistema"));
        verifyThat("#btnAccionPrincipal", isInvisible());
    }

    @Test
    public void testVolverAUsuarios() {
        irAPestana("Logger");
        irAPestana("Usuarios y empleados");

        assertEquals(0, tabPane().getSelectionModel().getSelectedIndex());
        verifyThat("#lblTitulo", hasText("Usuarios y empleados"));
        verifyThat("#btnAccionPrincipal", isVisible());
    }

    // ---------------------- Cerrar sesión ----------------------
    @Test
    public void testCerrarSesionPideConfirmacionYCancelarSeQuedaEnLaVista() {
        irAPestana("Configuración del sistema");
        int ventanas = listWindows().size();

        clickOn("Cerrar sesión");
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(listWindows().size() > ventanas, "Cerrar sesión no pide confirmación");

        cerrarAlerta(alerta()); // cierro sin aceptar
        verifyThat("#tabPaneAdmin", isVisible());
    }

    // ---------------------- DIALOG: ALTA DE USUARIO ----------------------
    // Para estos test necesito tener la base de datos creada
    @Test
    public void testAltaUsuarioAbreDialog() {
        int ventanas = listWindows().size();

        clickOn("#btnAccionPrincipal");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > ventanas, "Alta usuario no abre el dialog");
    }

    @Test
    public void testAltaBotonesVisibles() {
        abrirDialogAlta();

        verifyThat("#btnAlta", isVisible());
        verifyThat("#btnCancelar", isVisible());
        verifyThat("#btnModificar", isInvisible());
        verifyThat("#btnEliminar", isInvisible());
    }

    @Test
    public void testAltaCamposAutomaticosDeshabilitados() {
        abrirDialogAlta();

        verifyThat("#tfId", isDisabled());
        verifyThat("#tfCreadoEn", isDisabled());
        verifyThat("#tfUsername", isDisabled());
        verifyThat("#tfEmpleadoFechaAlta", isDisabled());
        assertEquals("Se genera automáticamente", campo("#tfUsername").getPromptText());
    }

    @Test
    public void testAltaEstadoInicialDelFormulario() {
        abrirDialogAlta();

        assertTrue(lookup("#chkActivo").queryAs(CheckBox.class).isSelected());
        verifyThat("#mbPermisos", hasText("0 permisos seleccionados"));
        verifyThat("#panelEmpleado", isVisible());
        verifyThat("#lblSinEmpleado", isInvisible());
        assertTrue(campo("#tfEmpleadoNombre").isEditable());
        assertTrue(campo("#tfEmpleadoApellido").isEditable());
        assertTrue(campo("#tfEmpleadoDni").isEditable());
    }

    @Test
    public void testAltaCancelarCierraDialog() {
        int ventanas = listWindows().size();
        abrirDialogAlta();

        clickOn("#btnCancelar");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(ventanas, listWindows().size(), "Cancelar no cierra el dialog");
    }

    // ---------------------- DIALOG: ERRORES DEL SERVICE EN EL ALTA ----------------------
    /**
     * Alta con el formulario vacio.
     */
    @Test
    public void testAltaConFormularioVacioMuestraErrorYNoCierraDialog() {
        abrirDialogAlta();

        clickOn("#btnAlta");
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane error = alerta();
        assertFalse(textoAlerta(error).isBlank(), "El error no muestra ningún mensaje");
        cerrarAlerta(error);

        verifyThat("#btnAlta", isVisible()); // el dialog sigue abierto
    }

    /**
     * Alta con un username que ya existe.
     */
    @Test
    public void testAltaConUsernameExistenteMuestraError() {
        Usuario existente = primerUsuarioConEmpleado();
        abrirDialogAlta();

        rellenar("#tfRol", "CAJERO");
        rellenar("#pfPassword", "Pass1234!");
        rellenar("#tfEmpleadoNombre", existente.getEmpleado().getNombre());
        rellenar("#tfEmpleadoApellido", existente.getEmpleado().getApellido());
        rellenar("#tfEmpleadoDni", "12345678Z");
        rellenar("#tfEmpleadoPuesto", "Cajero");
        rellenar("#tfEmpleadoEmail", "prueba@empresa.com");
        rellenar("#tfEmpleadoTelefono", "600111222");

        clickOn("#btnAlta");
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane error = alerta();
        assertTrue(textoAlerta(error).contains(MSG_USERNAME_DUPLICADO),
                "Mensaje inesperado: " + textoAlerta(error));
        cerrarAlerta(error);

        verifyThat("#btnAlta", isVisible()); // el dialog sigue abierto
    }

    // ---------------------- DIALOG: EDICION DE USUARIO ----------------------
    @Test
    public void testDobleClicEnFilaAbreDialog() {
        int ventanas = listWindows().size();

        abrirDialogUsuario();

        assertTrue(listWindows().size() > ventanas, "El doble clic no abre el dialog");
    }

    @Test
    public void testEdicionBotonesVisibles() {
        abrirDialogUsuario();

        verifyThat("#btnModificar", isVisible());
        verifyThat("#btnEliminar", isVisible());
        verifyThat("#btnAlta", isInvisible());
        verifyThat("#btnCancelar", isInvisible());
    }

    @Test
    public void testEdicionMuestraDatosDeLaFilaSeleccionada() {
        Usuario usuario = tablaUsuarios().getItems().get(0);
        abrirDialogUsuario();

        assertEquals(String.valueOf(usuario.getId()), campo("#tfId").getText());
        assertEquals(usuario.getUsername(), campo("#tfUsername").getText());
        assertEquals(usuario.getRol(), campo("#tfRol").getText());
        assertEquals(usuario.isActivo(), lookup("#chkActivo").queryAs(CheckBox.class).isSelected());
    }

    @Test
    public void testEdicionUsernameNoEditable() {
        abrirDialogUsuario();

        assertFalse(campo("#tfUsername").isEditable());
    }

    @Test
    public void testEdicionPanelEmpleadoSegunTengaEmpleado() {
        Usuario usuario = tablaUsuarios().getItems().get(0);
        abrirDialogUsuario();

        if (usuario.getEmpleado() != null) {
            verifyThat("#panelEmpleado", isVisible());
            verifyThat("#lblSinEmpleado", isInvisible());
            assertEquals(usuario.getEmpleado().getNombre(), campo("#tfEmpleadoNombre").getText());
            assertFalse(campo("#tfEmpleadoNombre").isEditable());
        } else {
            verifyThat("#panelEmpleado", isInvisible());
            verifyThat("#lblSinEmpleado", isVisible());
        }
    }

    /**
     * Modificar sin cambios.
     */
    @Test
    public void testModificarSinCambiosMuestraError() {
        abrirDialogUsuario();

        clickOn("#btnModificar");
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane error = alerta();
        assertTrue(textoAlerta(error).contains(MSG_SIN_CAMBIOS),
                "Mensaje inesperado: " + textoAlerta(error));
        cerrarAlerta(error);

        verifyThat("#btnModificar", isVisible()); // el dialog sigue abierto
    }

    /**
     * Eliminar y cancelar la confirmacion.
     */
    @Test
    public void testEliminarPideConfirmacionYCancelarNoCierraDialog() {
        abrirDialogUsuario();

        clickOn("#btnEliminar");
        WaitForAsyncUtils.waitForFxEvents();

        DialogPane confirmacion = alerta();
        assertTrue(textoAlerta(confirmacion).contains("Eliminar")
                || textoAlerta(confirmacion).contains("eliminar"),
                "No parece la confirmación de eliminar: " + textoAlerta(confirmacion));
        cerrarAlerta(confirmacion); // cierro sin aceptar

        verifyThat("#btnEliminar", isVisible());
    }

    @AfterEach
    public void tearDown() throws TimeoutException {
        cerrarVentanasExtra();
        FxToolkit.hideStage(); // cerrar la ventana
        release(new KeyCode[]{}); // soltar teclas que hayan quedado pulsadas
        release(new MouseButton[]{}); // soltar botones del ratón
    }

    // ---------------------- Métodos auxiliares ----------------------
    /**
     * Obtengo el TabPane.
     */
    private TabPane tabPane() {
        return lookup("#tabPaneAdmin").queryAs(TabPane.class);
    }

    /**
     * Obtengo la tabla de usuarios.
     */
    private TableView<Usuario> tablaUsuarios() {
        return lookup("#tablaUsuarios").queryTableView();
    }

    /**
     * Obtengo un TextField (o PasswordField) por el selector.
     */
    private TextField campo(String selector) {
        return lookup(selector).queryAs(TextField.class);
    }

    /**
     * Escribo en un campo sin hacer clic.
     */
    private void rellenar(String selector, String texto) {
        interact(() -> campo(selector).setText(texto));
    }

    /**
     * Voy a la pestaña con ese texto.
     */
    private void irAPestana(String texto) {
        Node cabecera = lookup(".tab-label").match(hasText(texto)).query();
        assertNotNull(cabecera, "No se encuentra la pestaña '" + texto + "'");
        clickOn(cabecera);
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Abro el dialog de alta con el boton principal.
     */
    private Parent abrirDialogAlta() {
        clickOn("#btnAccionPrincipal");
        WaitForAsyncUtils.waitForFxEvents();
        return obtenerRootDialogo();
    }

    /**
     * Abro el dialog del primer usuario con doble clic.
     */
    private Parent abrirDialogUsuario() {
        assumeTrue(!tablaUsuarios().getItems().isEmpty(), "No hay usuarios en la BD");
        Node fila = lookup(".table-row-cell").nth(0).query();
        Node celdaUsername = from(fila).lookup(".table-cell").nth(1).query();
        doubleClickOn(celdaUsername);
        WaitForAsyncUtils.waitForFxEvents();
        return obtenerRootDialogo();
    }

    /**
     * Obtengo el primer usuario con empleado.
     */
    private Usuario primerUsuarioConEmpleado() {
        for (Usuario u : tablaUsuarios().getItems()) {
            if (u.getEmpleado() != null) {
                return u;
            }
        }
        assumeTrue(false, "No hay ningún usuario con empleado en la BD");
        return null;
    }

    /**
     * Obtengo el Alert que se ha abierto.
     */
    private DialogPane alerta() {
        verifyThat(".dialog-pane", isVisible());
        return lookup(".dialog-pane").queryAs(DialogPane.class);
    }

    /**
     * Obtengo el texto del Alert.
     */
    private String textoAlerta(DialogPane dialogo) {
        String cabecera = dialogo.getHeaderText() != null ? dialogo.getHeaderText() : "";
        String contenido = dialogo.getContentText() != null ? dialogo.getContentText() : "";
        return cabecera + " " + contenido;
    }

    /**
     * Cierro el Alert.
     */
    private void cerrarAlerta(DialogPane dialogo) {
        interact(() -> dialogo.getScene().getWindow().hide());
    }

    /**
     * Obtengo la ventana que se ha abierto.
     */
    private Parent obtenerRootDialogo() {
        for (Window ventana : listWindows()) {
            if (ventana != stagePrincipal) {
                return ventana.getScene().getRoot();
            }
        }
        fail("No se ha abierto ningún dialog");
        return null;
    }

    /**
     * Cierro las ventanas extra que se hayan quedado abiertas.
     */
    private void cerrarVentanasExtra() {
        interact(() -> {
            for (Window ventana : new ArrayList<>(listWindows())) {
                if (ventana != stagePrincipal) {
                    ventana.hide();
                }
            }
        });
    }
}
