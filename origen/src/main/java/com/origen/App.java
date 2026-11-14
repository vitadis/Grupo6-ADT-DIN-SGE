package com.origen;

import controller.InstanciarEscena;
import javafx.application.Application;
import util.AppLogger;

/**
 * JavaFX App
 */
public class App {
    public static void main(String[] args) {
        Application.launch(InstanciarEscena.class, args);
        //MongoDBConnectionTest.mani(args);
        AppLogger log = AppLogger.getInstance();
        
        log.leer().forEach(System.out::println);
    }
    
}