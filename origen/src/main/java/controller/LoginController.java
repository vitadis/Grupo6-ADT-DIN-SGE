package controller;

import dao.UsuarioDao;
import dao.implement.UsuarioDaoImp;
import exceptions.DaoException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
import util.AppLogger;

/**
 * FXML Controller class para la pantalla de inicio de sesión.
 *
 * @author Joel
 */
public class LoginController implements Initializable {

    // ---- Variables FXML ----
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private TextField txtContrasenaVisible;
    @FXML
    private Label lblMensajeError;

    // ---- Atributos ----
    private Usuario usuario;
    private final AppLogger logger = AppLogger.getInstance();
    private LoginService loginService;

    // ---- Inicialización ----
    /**
     * Enlaza los campos de contraseña visible y oculta e inicializa el servicio
     * de autenticación.
     *
     * @param url ubicación utilizada para resolver rutas relativas
     * @param rb recursos utilizados para localizar el objeto raíz
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtContrasenaVisible.textProperty()
                .bindBidirectional(txtContrasena.textProperty());

        UsuarioDao usuarioDao = UsuarioDaoImp.getInstance();
        loginService = new LoginService(usuarioDao);
    }

    // ---- Usuario ----
    /**
     * Devuelve el usuario autenticado tras iniciar sesión.
     *
     * @return usuario autenticado o null si no ha iniciado sesión
     */
    public Usuario getUsuario() {
        return usuario;
    }

    // ---- Métodos FXML ----
    /**
     * Alterna la visibilidad entre el campo de contraseña oculto y el de texto
     * plano.
     *
     * @param event evento de acción del botón
     */
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

    /**
     * Valida las credenciales introducidas, registra el inicio de sesión y abre
     * la vista principal.
     *
     * @param event evento de acción del botón de acceso
     */
    @FXML
    private void validar(ActionEvent event) {
        String usernameIntentado = txtUsuario.getText();
        try {
            usuario = loginService.iniciarSesion(
                    usernameIntentado,
                    txtContrasena.getText()
            );

            lblMensajeError.setText("");
            logger.setUser(usuario.getUsername());
            logger.info("Inicio de sesión exitoso.");

            abrirNav();

        } catch (IllegalArgumentException e) {
            lblMensajeError.setText(e.getMessage());
            logger.log(Level.WARNING, "Intento de inicio de sesión fallido para '" + usernameIntentado + "': " + e.getMessage());

        } catch (DaoException e) {
            lblMensajeError.setText(
                    "No se ha podido iniciar sesión."
            );
            logger.log(Level.SEVERE, "Error de acceso a datos durante el login: " + e.getMessage(), e);
        }
    }

    // ---- Métodos auxiliares ----
    /**
     * Cambia la escena actual a la pantalla principal de navegación y le asigna
     * el usuario autenticado.
     */
    private void abrirNav() {
        try {
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            NavController navController = InstanciarEscena.cambiarVista(stage, "/view/navView.fxml", "Inicio");
            navController.setUsuario(usuario);
        } catch (Exception e) {
            lblMensajeError.setText("No se ha podido cargar la pantalla principal.");
            logger.log(Level.SEVERE, "Error al cargar la vista principal tras el login: " + e.getMessage(), e);
        }
    }
}
