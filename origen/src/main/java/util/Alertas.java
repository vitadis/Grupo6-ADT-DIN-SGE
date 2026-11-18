/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Utilidades para mostrar ventanas de alerta de JavaFX (error, información y confirmación).
 *
 * @author Joel
 */
public class Alertas {

    private Alertas() {
    }

    /**
     * Muestra una alerta de error y espera a que el usuario la cierre.
     *
     * @param mensaje texto del error
     */
    public static void mostrarError(String mensaje) {
        Alert alerta = mostrarMensaje(Alert.AlertType.ERROR, "Error", mensaje);
        alerta.showAndWait();
    }

    /**
     * Muestra una alerta informativa y espera a que el usuario la cierre.
     *
     * @param mensaje texto informativo
     */
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

    /**
     * Muestra una alerta de confirmación y, si el usuario pulsa Aceptar, ejecuta la acción.
     *
     * @param titulo título de la ventana
     * @param mensaje pregunta que se muestra
     * @param consumible acción que se ejecuta si el usuario confirma
     */
    public static void mostrarConfirmacion(String titulo,String mensaje, Runnable consumible) {
        Alert alerta = mostrarMensaje(Alert.AlertType.CONFIRMATION, titulo, mensaje);
        if (alerta.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
            consumible.run();
        }
    }

}
