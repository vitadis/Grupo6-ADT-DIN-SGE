package controller;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import util.InterpretePermisos;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class NavController implements Initializable {

    // --------------------- Variables Fxml ---------------------
    @FXML
    private StackPane contenido;
    @FXML
    private VBox vbContenido;

    private static final Map<String, String> VISTAS = Map.of(
            //"Inicio", "/view/inicio/inicioView.fxml",
            "Caja y pedidos", "/view/caja/cajaView.fxml"
    //"Gestión económica", "/view/gestion/gestionView.fxml",
    //"Gestión stock", "/view/stock/stockView.fxml",
    //"Compras y proveedores", "/view/compras/comprasView.fxml",
    //"Análisis e informes", "/view/informes/informesView.fxml",
    //"Administración", "/view/admin/adminView.fxml"
    );

    // Map con las vistas cargadas -> simplementa lo hago visible
    private final Map<String, Node> vistasCargadas = new LinkedHashMap<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // modificar el usuario y tener un get
        List<String> permisos = List.of("caja", "stock", "gestion.ingresosGastos", "administracion", "inicio");

        // ocultar botones sin acceso
        List<String> sinAcceso = InterpretePermisos.apartadosSinAcceso(permisos);
        if (!sinAcceso.isEmpty()) {
            vbContenido.getChildren().removeIf(nodo
                    -> nodo instanceof ToggleButton && sinAcceso.contains(((ToggleButton) nodo).getText())
            );
        }

        // cargar la vista de los apartados con acceso
        vbContenido.getChildren().stream()
                .filter(n -> n instanceof ToggleButton)
                .map(n -> (ToggleButton) n)
                .forEach(boton -> {
                    String ruta = VISTAS.get(boton.getText());
                    if (ruta == null) {
                        return;
                    }
                    try {
                        Node vista = FXMLLoader.load(getClass().getResource(ruta));
                        vista.setVisible(false);
                        vista.setManaged(false);
                        contenido.getChildren().add(vista);
                        vistasCargadas.put(boton.getText(), vista);
                    } catch (IOException e) {
                        InstanciarEscena.mostrarError("No se pudo cargar " + ruta + ": " + e.getMessage());
                    }
                });

        // mostrar la primera vista displonible, que es inicio
        vbContenido.getChildren().stream()
                .filter(n -> n instanceof ToggleButton)
                .map(n -> (ToggleButton) n)
                .findFirst()
                .ifPresent(this::navegar);
    }

    @FXML
    private void irInicio(ActionEvent event) {
        navegar((ToggleButton) event.getSource());
    }

    @FXML
    private void irCaja(ActionEvent event) {
        navegar((ToggleButton) event.getSource());
    }

    @FXML
    private void irEconomia(ActionEvent event) {
        navegar((ToggleButton) event.getSource());
    }

    @FXML
    private void irStock(ActionEvent event) {
        navegar((ToggleButton) event.getSource());
    }

    @FXML
    private void irCompras(ActionEvent event) {
        navegar((ToggleButton) event.getSource());
    }

    @FXML
    private void irAnalisis(ActionEvent event) {
        navegar((ToggleButton) event.getSource());
    }

    @FXML
    private void irAdmin(ActionEvent event) {
        navegar((ToggleButton) event.getSource());
    }

    @FXML
    private void abrirPerfil(ActionEvent event) {

    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        try {
            Button btnCS = (Button) event.getSource();
            Stage stage = (Stage) btnCS.getScene().getWindow();

            Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
            alerta.initOwner(stage);
            alerta.setTitle("Cerrar sesión");
            alerta.setHeaderText(null);
            alerta.setContentText("¿Seguro que quieres cerrar sesión?");

            if (alerta.showAndWait().filter(r -> r == ButtonType.OK).isPresent()) {
                InstanciarEscena.cambiarVista(stage, "/view/loginView.fxml", "Inicio Sesión");
            }
        } catch (IOException ex) {
            InstanciarEscena.mostrarError("No se pudo cerrar sesión: " + ex.getMessage());
        }
    }

    // metodos auxiliares
    /**
     * Marca el botón elegido y muestra su vista, ocultando las demás.
     */
    private void navegar(ToggleButton elegido) {
        seleccionarOpcion(elegido);

        String texto = elegido.getText();
        vistasCargadas.forEach((clave, vista) -> {
            boolean activa = clave.equals(texto);
            vista.setVisible(activa);
            vista.setManaged(activa);
        });
    }

    private void seleccionarOpcion(ToggleButton elegido) {
        vbContenido.getChildren().stream()
                .filter(n -> n instanceof ToggleButton)
                .map(n -> (ToggleButton) n)
                .forEach(tb -> tb.setSelected(tb == elegido));
    }
}
