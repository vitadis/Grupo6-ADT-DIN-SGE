/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

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

    public static void mostrarConfirmacion(String titulo,String mensaje, Runnable consumible) {
        Alert alerta = mostrarMensaje(Alert.AlertType.CONFIRMATION, titulo, mensaje);
        if (alerta.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
            consumible.run();
        }
    }

}
