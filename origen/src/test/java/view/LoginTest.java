package view;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isInvisible;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

/**
 * TEST CLASS LoginTest
 *
 * @author Joel
 */
public class LoginTest extends ApplicationTest {

    /**
     * Iniciamos el stage
     *
     * @param stage
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/loginView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // ---------------------- Visibilidad Inicial de los nodos ----------------------
    @Test
    public void testVisibilidadBtnLogin() {
        verifyThat(".login-button", isVisible());
    }

    @Test
    public void testVisibilidadBtnMostrar() {
        verifyThat(".show-button", isVisible());
    }

    @Test
    public void testVisibilidadTxtUsuario() {
        verifyThat("#txtUsuario", isVisible());
    }

    @Test
    public void testVisibilidadTxtContrasena() {
        verifyThat("#txtContrasena", isVisible());
    }

    @Test
    public void testVisibilidadLblError() {
        verifyThat("#lblMensajeError", isVisible());
    }

    @Test
    public void testTxtContrasenaVisibleOcultoInicialmente() {
        verifyThat("#txtContrasenaVisible", isInvisible());
    }

    @Test
    public void testTextoInicialBtnMostrar() {
        Button boton = lookup(".show-button").queryAs(Button.class);
        assertEquals("Mostrar", boton.getText());
    }

    @Test
    public void testLblErrorVacioInicialmente() {
        assertEquals("", obtenerLabelError().getText());
    }

    // ---------------------- Mostrar / ocultar contraseña ----------------------
    @Test
    public void testMostrarContrasena() {
        clickOn(".show-button");

        PasswordField pass = lookup("#txtContrasena").queryAs(PasswordField.class);
        TextField visible = lookup("#txtContrasenaVisible").queryAs(TextField.class);
        Button boton = lookup(".show-button").queryAs(Button.class);

        assertTrue(visible.isVisible());
        assertTrue(visible.isManaged());
        assertFalse(pass.isVisible());
        assertFalse(pass.isManaged());
        assertEquals("Ocultar", boton.getText());
    }

    @Test
    public void testOcultarContrasena() {
        clickOn(".show-button"); // mostrar
        clickOn(".show-button"); // ocultar

        PasswordField pass = lookup("#txtContrasena").queryAs(PasswordField.class);
        TextField visible = lookup("#txtContrasenaVisible").queryAs(TextField.class);
        Button boton = lookup(".show-button").queryAs(Button.class);

        assertTrue(pass.isVisible());
        assertTrue(pass.isManaged());
        assertFalse(visible.isVisible());
        assertFalse(visible.isManaged());
        assertEquals("Mostrar", boton.getText());
    }

    @Test
    public void testContrasenaVisibleMuestraElMismoTexto() {
        clickOn("#txtContrasena").write("1234");
        clickOn(".show-button");

        TextField visible = lookup("#txtContrasenaVisible").queryAs(TextField.class);
        assertEquals("1234", visible.getText());
    }

    @Test
    public void testEscribirEnContrasenaVisibleActualizaContrasena() {
        clickOn(".show-button");
        clickOn("#txtContrasenaVisible").write("abcd");

        PasswordField pass = lookup("#txtContrasena").queryAs(PasswordField.class);
        assertEquals("abcd", pass.getText());
    }

    @Test
    public void testLoginIncorrectoConContrasenaVisible() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("admin");
        clickOn(".show-button");
        clickOn("#txtContrasenaVisible").write("a");
        clickOn(".login-button");
        assertEquals("El usuario no existe o las credenciales son incorrectas.", lblError.getText());
    }

    // ---------------------- Validaciones de campos ----------------------
    // Campos vacios
    @Test
    public void testLogCamposVacios() {
        Label lblError = obtenerLabelError();

        clickOn(".login-button");
        assertEquals("Completa todos los campos obligatorios.", lblError.getText());
    }

    // Solo usuario
    @Test
    public void testLogSoloUsuario() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("admin");
        clickOn(".login-button");
        assertEquals("Completa todos los campos obligatorios.", lblError.getText());
    }

    // Solo contraseña
    @Test
    public void testLogSoloContrasena() {
        Label lblError = obtenerLabelError();

        clickOn("#txtContrasena").write("1234");
        clickOn(".login-button");
        assertEquals("Completa todos los campos obligatorios.", lblError.getText());
    }

    // Usuario solo con espacios
    @Test
    public void testLogUsuarioSoloEspacios() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("   ");
        clickOn("#txtContrasena").write("1234");
        clickOn(".login-button");
        assertEquals("Completa todos los campos obligatorios.", lblError.getText());
    }

    // Enter con campos vacios (defaultButton)
    @Test
    public void testLogEnterConCamposVacios() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertEquals("Completa todos los campos obligatorios.", lblError.getText());
    }

    // ---------------------- Credenciales (requieren BD) ----------------------
    // Usuario correcto
    @Test
    public void testLoginCorrecto() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("admin");
        clickOn("#txtContrasena").write("1234");
        clickOn(".login-button");
        assertEquals("", lblError.getText());
    }

    // Contraseña incorrecta
    @Test
    public void testLoginIncorrecto() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("admin");
        clickOn("#txtContrasena").write("a");
        clickOn(".login-button");
        assertEquals("El usuario no existe o las credenciales son incorrectas.", lblError.getText());
    }

    // Usuario inexistente
    @Test
    public void testLoginUsuarioInexistente() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("usuarioInexistente");
        clickOn("#txtContrasena").write("1234");
        clickOn(".login-button");
        assertEquals("El usuario no existe o las credenciales son incorrectas.", lblError.getText());
    }

    // Usuario y contraseña incorrectos
    @Test
    public void testLoginUsuarioYContrasenaIncorrectos() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("xxx");
        clickOn("#txtContrasena").write("yyy");
        clickOn(".login-button");
        assertEquals("El usuario no existe o las credenciales son incorrectas.", lblError.getText());
    }

    // Login con Enter
    @Test
    public void testLoginIncorrectoConEnter() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("admin");
        clickOn("#txtContrasena").write("a");
        press(KeyCode.ENTER).release(KeyCode.ENTER);
        assertEquals("El usuario no existe o las credenciales son incorrectas.", lblError.getText());
    }

    // Se mantiene el error tras reintentar con campos vacios
    @Test
    public void testMensajeErrorCambiaEntreIntentos() {
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("admin");
        clickOn("#txtContrasena").write("a");
        clickOn(".login-button");
        assertEquals("El usuario no existe o las credenciales son incorrectas.", lblError.getText());

        clickOn("#txtUsuario").eraseText(5);
        clickOn(".login-button");
        assertEquals("Completa todos los campos obligatorios.", lblError.getText());
    }

    @AfterEach
    public void tearDown() throws TimeoutException {
        FxToolkit.hideStage(); // cerrar la ventana
        release(new KeyCode[]{}); // soltar teclas que hayan quedado pulsadas
        release(new MouseButton[]{}); // soltar botones del ratón
    }

    // ---------------------- Métodos auxiliares ----------------------
    // obtengo el label, porque solo se modifica si existe error; si no, se mantiene vacío
    private Label obtenerLabelError() {
        return lookup(".login-error").queryAs(Label.class);
    }
}
