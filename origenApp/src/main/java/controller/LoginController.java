/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
/**
 * FXML Controller class
 *
 * @author Joel
 */
public class LoginController implements Initializable {


    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtContrasena;
    @FXML
    private TextField txtContrasenaVisible;
    @FXML
    private Label lblMensajeError;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // sobrepongo ambos elementos
        txtContrasenaVisible.textProperty().bindBidirectional(txtContrasena.textProperty());
    }

    @FXML
    private void alternarContrasena(ActionEvent event) {
        boolean mostrando = txtContrasenaVisible.isVisible();

        txtContrasenaVisible.setVisible(!mostrando);
        txtContrasenaVisible.setManaged(!mostrando);
        txtContrasena.setVisible(mostrando);
        txtContrasena.setManaged(mostrando);

        // compruebo el texto
        Button boton = (Button) event.getSource();
        boton.setText(mostrando ? "Mostrar" : "Ocultar");

        TextField activo = mostrando ? txtContrasena : txtContrasenaVisible;
        activo.requestFocus();
        activo.positionCaret(activo.getText().length());
    }

    @FXML
    private void validar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            lblMensajeError.setText("Completa todos los campos obligatorios.");
            return;
        }

        if ("usuario".equals(usuario) && "1234".equals(contrasena)) {
            lblMensajeError.setText("");
            System.out.println("Login correcto");
            // Aquí cargo la vista
        } else {
            lblMensajeError.setText("Usuario o contraseña incorrectos.");
        }
    }

}
