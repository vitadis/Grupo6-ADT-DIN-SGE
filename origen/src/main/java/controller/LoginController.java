package controller;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Usuario;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class LoginController implements Initializable {

    // --------------------- Variables FXML ---------------------
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private TextField txtContrasenaVisible;
    @FXML
    private Label lblMensajeError;

    // --------------------- Atributos ---------------------
    private Usuario usuario;

    // --------------------- Inicialización ---------------------
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtContrasenaVisible.textProperty().bindBidirectional(txtContrasena.textProperty());
    }

    // --------------------- Usuario ---------------------
    public Usuario getUsuario() {
        return usuario;
    }

    // --------------------- Métodos FXML ---------------------
    @FXML
    private void alternarContrasena(ActionEvent event) {
        boolean mostrando = txtContrasenaVisible.isVisible();

        txtContrasenaVisible.setVisible(!mostrando);
        txtContrasenaVisible.setManaged(!mostrando);
        txtContrasena.setVisible(mostrando);
        txtContrasena.setManaged(mostrando);

        Button boton = (Button) event.getSource();
        boton.setText(mostrando ? "Mostrar" : "Ocultar");

        TextField activo = mostrando ? txtContrasena : txtContrasenaVisible;
        activo.requestFocus();
        activo.positionCaret(activo.getText().length());
    }

    @FXML
    private void validar(ActionEvent event) {
        String nombre = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        if (nombre.isEmpty() || contrasena.isEmpty()) {
            lblMensajeError.setText("Completa todos los campos obligatorios.");
            return;
        }

        // TODO: validar con el nosql
        if ("usuario".equals(nombre) && "1234".equals(contrasena)) {
            lblMensajeError.setText("");
            crearUsuarioPrueba();
            abrirNav();
        } else {
            lblMensajeError.setText("Usuario o contraseña incorrectos.");
        }
    }

    // --------------------- Métodos auxiliares ---------------------
    private void abrirNav() {
        try {
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            NavController navController = InstanciarEscena.cambiarVista(stage, "/view/navView.fxml", "Inicio");
            navController.setUsuario(usuario);
        } catch (IOException e) {
            lblMensajeError.setText("No se ha podido cargar la pantalla principal.");
            e.printStackTrace();
        }
    }

    private void crearUsuarioPrueba() {
        usuario = new Usuario();
        usuario.setId("1");

        List<String> permisos = new LinkedList<>();
        permisos.add("total");
        permisos.add("");
        permisos.add("");
        permisos.add("");
        permisos.add("");
        usuario.setPermisos(permisos);
    }
}
