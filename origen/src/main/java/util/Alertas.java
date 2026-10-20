/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.function.Consumer;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Joel
 */
public class Alertas {

    public static void mostrarError(String mensaje) {
        Alert alerta = mostrarMensaje(Alert.AlertType.ERROR, "Error", mensaje);
        alerta.showAndWait();
    }

    public static void mostrarInfo(String mensaje) {
        Alert alerta = mostrarMensaje(Alert.AlertType.INFORMATION, "Información", mensaje);
        alerta.showAndWait();
    }

    private static Alert mostrarMensaje(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        return alerta;
    }

    public static void mostrarConfirmacion(String mensaje, Consumer consumible) {
        Alert alerta = mostrarMensaje(Alert.AlertType.CONFIRMATION, mensaje, mensaje);
        alerta.showAndWait();
        if (alerta.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
            consumible.accept(mensaje);
        }
    }

}
