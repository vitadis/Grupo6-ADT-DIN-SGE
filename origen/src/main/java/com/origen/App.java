package com.origen;

import controller.InstanciarEscena;
import javafx.application.Application;
import util.AppLogger;

/**
 * Punto de entrada de la aplicación JavaFX.
 */
public class App {

    private App() {
    }

    /**
     * Arranca la aplicación JavaFX y, al cerrarla, muestra por consola el contenido del log.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        Application.launch(InstanciarEscena.class, args);        
    }
    
}
