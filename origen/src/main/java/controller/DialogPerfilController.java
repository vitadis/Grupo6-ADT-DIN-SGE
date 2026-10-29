package controller;

import dao.implement.UsuarioDaoImp;
import exceptions.DaoException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import model.Empleado;
import model.Usuario;
import services.UsuarioService;

public class DialogPerfilController implements Initializable {

    private Usuario usuario;
    private UsuarioService usuarioService;

    @FXML
    private Label lblNombreUsuario;

    @FXML
    private Label lblRol;

    @FXML
    private Label lblMensaje;

    @FXML
    private GridPane gridPaneContenedor;

    @FXML
    private ComboBox<String> cbPermisos;

    @FXML
    private TextField tfContrasena;

    private TextField tfEmail;
    private TextField tfTelefono;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblMensaje.setText("");
        usuarioService = new UsuarioService(UsuarioDaoImp.getInstance());
    }

    // Recibir el usuario
    public void setusuario(Usuario user) {

        if (user == null) {
            lblMensaje.setText("El usuario no puede ser nulo.");
            return;
        }

        usuario = user;
        cargarDatosUsuario();
    }

    // Cargar los datos del usuario
    private void cargarDatosUsuario() {

        lblNombreUsuario.setText(usuario.getUsername());
        lblRol.setText(usuario.getRol());

        tfContrasena.clear();

        ObservableList<String> permisos
                = FXCollections.observableArrayList(usuario.getPermisos());

        cbPermisos.setItems(permisos);

        cargarDatosEmpleado(usuario.getEmpleado());
    }

    // Cargar los datos del empleado
    private void cargarDatosEmpleado(Empleado empleado) {

        if (empleado == null) {
            return;
        }

        gridPaneContenedor.add(
                new Label("Nombre:"), 0, 4);

        gridPaneContenedor.add(
                new Label(empleado.getNombre()), 1, 4);

        gridPaneContenedor.add(
                new Label("Email:"), 0, 5);

        tfEmail = new TextField(empleado.getEmail());
        gridPaneContenedor.add(tfEmail, 1, 5);

        gridPaneContenedor.add(
                new Label("Teléfono:"), 0, 6);

        tfTelefono = new TextField(empleado.getTelefono());
        gridPaneContenedor.add(tfTelefono, 1, 6);

        gridPaneContenedor.add(
                new Label("Fecha alta:"), 0, 7);

        gridPaneContenedor.add(
                new Label(empleado.getFecha_alta().toString()),
                1, 7);
    }

    // Actualizar los cambios
    @FXML
    private void onActualizarDatos(ActionEvent event) {

        if (usuario == null) {
            lblMensaje.setText("No hay ningún usuario cargado.");
            return;
        }

        if (!hayCambios()) {
            lblMensaje.setText("No has realizado ningún cambio.");
            return;
        }

        try {
            Empleado empleado = usuario.getEmpleado();

            if (empleado != null) {
                empleado.setEmail(tfEmail.getText());
                empleado.setTelefono(tfTelefono.getText());
            }

            String nuevaContrasena = tfContrasena.getText();

            if (!nuevaContrasena.isBlank()) {
                usuario.setPassword_hash(nuevaContrasena);
            }

            usuarioService.modificarPerfil(usuario);
            lblMensaje.setText("Perfil actualizado correctamente.");
            tfContrasena.clear();

        } catch (IllegalArgumentException e) {
            lblMensaje.setText(e.getMessage());

        } catch (DaoException e) {
            lblMensaje.setText("Error al actualizar el perfil.");
        }
    }

    // Comprobar si hay cambios en los campos editables
    private boolean hayCambios() {

        Empleado empleado = usuario.getEmpleado();

        boolean cambioContrasena
                = tfContrasena.getText() != null
                && !tfContrasena.getText().isBlank();

        boolean cambioEmail
                = empleado != null
                && !Objects.equals(
                        empleado.getEmail(), tfEmail.getText());

        boolean cambioTelefono
                = empleado != null
                && !Objects.equals(
                        empleado.getTelefono(), tfTelefono.getText());

        return cambioContrasena || cambioEmail || cambioTelefono;
    }

    // Cerrar el diálogo
    @FXML
    private void onCerrar(ActionEvent event) {

        Button boton = (Button) event.getSource();
        Stage stage = (Stage) boton.getScene().getWindow();

        stage.close();
    }
}
