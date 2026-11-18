package controller;

import java.io.IOException;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Clase {@link Application} de JavaFX. Abre la ventana de inicio de sesión y ofrece métodos
 * para cargar vistas FXML en ventanas nuevas o existentes.
 */
public final class InstanciarEscena extends Application {

    /**
     * Constructor usado por JavaFX al lanzar la aplicación.
     */
    public InstanciarEscena() {
    }

    /**
     * Carga una vista FXML en el {@code Stage} indicado, lo muestra maximizado y le pone
     * el título.
     *
     * @param <T> tipo del controlador de la vista
     * @param stage ventana donde se carga la vista
     * @param ruta ruta del fichero FXML
     * @param titulo título de la ventana
     * @return el controlador de la vista cargada
     * @throws IOException si no se puede cargar el FXML
     */
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
        stage.setTitle(titulo);
        return loader.getController();
    }

    /**
     * Abre una ventana nueva con la vista FXML indicada.
     *
     * @param ruta ruta del fichero FXML
     * @param titulo título de la ventana
     * @return la ventana creada
     * @throws IOException si no se puede cargar el FXML
     */
    public static Stage abrirNuevaVentana(String ruta, String titulo) throws IOException {
        Stage stage = new Stage();
        cambiarVista(stage, ruta, titulo);
        return stage;
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/loginView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Image icon = new Image(getClass().getResourceAsStream("/img/icon_app.jpg"));
        stage.getIcons().add(icon);

        stage.setTitle("Iniciar sesión");
        stage.setScene(scene);
        stage.setMaximized(true);

        // Cerramos la conexión a la base de datos y cerramos el programa por completo
        stage.setOnCloseRequest(event -> {
            dao.implement.AccesoMongoDB.cerrarConexion();
            Platform.exit();
            System.exit(0);
        });

        stage.show();
    }

    /**
     * Abre la vista FXML en una ventana modal con los estilos de la aplicación y espera a que
     * se cierre.
     *
     * @param <T> tipo del controlador de la vista
     * @param ruta ruta del fichero FXML
     * @param nombreVentana título de la ventana
     * @param configurador acción para configurar el controlador antes de mostrar la ventana
     *        (puede ser {@code null})
     * @return el controlador de la vista, una vez cerrada la ventana
     * @throws IOException si no se puede cargar el FXML
     */
    public static <T> T createNewStage(String ruta, String nombreVentana, Consumer<T> configurador) throws IOException {
        FXMLLoader loader = new FXMLLoader(InstanciarEscena.class.getResource(ruta));
        Parent root = loader.load();

        T controller = loader.getController();
        if (configurador != null) {
            configurador.accept(controller);
        }

        Scene scene = new Scene(root);
        scene.getStylesheets().add(InstanciarEscena.class.getResource("/css/theme.css").toExternalForm());
        scene.getStylesheets().add(InstanciarEscena.class.getResource("/css/style.css").toExternalForm());

        Stage nuevaVentana = new Stage();
        nuevaVentana.setTitle(nombreVentana);
        nuevaVentana.initModality(Modality.APPLICATION_MODAL);
        nuevaVentana.setScene(scene);
        nuevaVentana.showAndWait();

        return controller;
    }

}
