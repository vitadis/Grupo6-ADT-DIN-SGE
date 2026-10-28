package controller;

import dao.implement.UsuarioDaoImp;
import exceptions.DaoException;
import java.io.IOException;
import java.net.URL;
import dao.UsuarioDao;
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
import services.LoginService;

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

    private LoginService loginService;

    // --------------------- Inicialización ---------------------
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtContrasenaVisible.textProperty()
                .bindBidirectional(txtContrasena.textProperty());

        UsuarioDao usuarioDao = UsuarioDaoImp.getInstance();
        loginService = new LoginService(usuarioDao);
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
        try {
            usuario = loginService.iniciarSesion(
                    txtUsuario.getText(),
                    txtContrasena.getText()
            );

            lblMensajeError.setText("");
            abrirNav();

        } catch (IllegalArgumentException e) {
            lblMensajeError.setText(e.getMessage());

        } catch (DaoException e) {
            lblMensajeError.setText(
                    "No se ha podido iniciar sesión."
            );
            e.printStackTrace();
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
}
