/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

public class LoginControllerTest extends ApplicationTest {

    // carga de scena
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/loginView.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Test si es visible todos los botones
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

    // Usuario correcto
    @Test
    public void testLoginCorrecto() {
        // obtengo el label
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("admin");
        clickOn("#txtContrasena").write("1234");
        clickOn(".login-button");
        assertEquals("", lblError.getText());
    }

    // Campos vacios
    @Test
    public void testLogCamposVacios() {
        // obtengo el label
        Label lblError = obtenerLabelError();

        clickOn(".login-button");
        assertEquals("Completa todos los campos obligatorios.", lblError.getText());
    }

    // Usuario incorrecto
    @Test
    public void testLoginIncorrecto() {
        // obtengo el label
        Label lblError = obtenerLabelError();

        clickOn("#txtUsuario").write("admin");
        clickOn("#txtContrasena").write("a");
        clickOn(".login-button");
        assertEquals("El usuario no existe o las credenciales son incorrectas.", lblError.getText());
    }

    @AfterEach
    public void tearDown() throws TimeoutException {
        FxToolkit.hideStage(); // cerrar la ventana
        release(new KeyCode[]{}); // soltar teclas que hayan quedado pulsadas
        release(new MouseButton[]{}); // soltar botones del ratón
    }

    // metodos auxiliares
    // obtengo el label, porque se modifica solo si existe error, y si no pasa nada se mantiene vacio
    private Label obtenerLabelError() {
        return lookup(".login-error").queryAs(Label.class);
    }
}
