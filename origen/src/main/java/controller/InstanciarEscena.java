package controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class InstanciarEscena extends Application {

    public static <T> T cambiarVista(Stage stage, String ruta, String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(InstanciarEscena.class.getResource(ruta));
        Parent root = loader.load();
        if (stage.getScene() == null) {
            stage.setScene(new Scene(root));
        } else {
            stage.getScene().setRoot(root);
        }

        if (!stage.isShowing()) {
            stage.show();
        }
        stage.setMaximized(true);
        return loader.getController();
    }

    public static Stage abrirNuevaVentana(String ruta, String titulo) throws IOException {
        Stage stage = new Stage();
        cambiarVista(stage, ruta, titulo);
        return stage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Iniciar sesión");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    
}
