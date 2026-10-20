package controller;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Usuario;
import util.Alertas;
import util.InterpretePermisos;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class NavController {

    // --------------------- Variables FXML ---------------------
    @FXML
    private StackPane contenido;
    @FXML
    private VBox vbContenido;

    // --------------------- Atributos ---------------------
    private static final Map<String, String> VISTAS = Map.of(
            //"Inicio", "/view/inicio/inicioView.fxml",
            "Caja y pedidos", "/view/caja/cajaView.fxml"
    //"Gestión económica", "/view/gestion/gestionView.fxml",
    //"Gestión stock", "/view/stock/stockView.fxml",
    //"Compras y proveedores", "/view/compras/comprasView.fxml",
    //"Análisis e informes", "/view/informes/informesView.fxml",
    //"Administración", "/view/admin/adminView.fxml"
    );

    private final Map<String, Node> vistasCargadas = new LinkedHashMap<>();

    private Usuario usuario;

    // --------------------- Usuario ---------------------
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        cargarVistas();
        
    }

    @FXML
    private void inizialize(URL location, ResourceBundle resources) {
        // consultar al nosql, para ver los datos del usuario que inicio sesión
        
        
        

    }

    // --------------------- Métodos FXML ---------------------
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

        Alertas.mostrarConfirmacion("¿Seguro que quieres cerrar sesión?", e -> {
            try {
                Button btnCS = (Button) event.getSource();
                Stage stage = (Stage) btnCS.getScene().getWindow();

                InstanciarEscena.cambiarVista(stage, "/view/loginView.fxml", "Inicio Sesión");
            } catch (IOException ex) {
                Alertas.mostrarError("No se pudo cerrar sesión: " + ex.getMessage());
            }
        });

    }

    // --------------------- Métodos auxiliares ---------------------
    private void cargarVistas() {
        List<String> permisos = usuario.getPermisos();
        List<String> sinAcceso = InterpretePermisos.apartadosSinAcceso(permisos);

        if (!sinAcceso.isEmpty()) {
            vbContenido.getChildren().removeIf(nodo
                    -> nodo instanceof ToggleButton
                    && sinAcceso.contains(((ToggleButton) nodo).getText()));
        }

        botones().forEach(boton -> {
            String ruta = VISTAS.get(boton.getText());
            if (ruta == null) {
                return;
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
                Node vista = loader.load();

                Object controller = loader.getController();
                if (controller instanceof UsuarioImplementacion) {
                    ((UsuarioImplementacion) controller).setUsuario(usuario);
                }

                vista.setVisible(false);
                vista.setManaged(false);
                contenido.getChildren().add(vista);
                vistasCargadas.put(boton.getText(), vista);
            } catch (IOException e) {
                Alertas.mostrarError("No se pudo cargar " + ruta + ": " + e.getMessage());
            }
        });

        seleccionarVistaInicial();
    }

    private void seleccionarVistaInicial() {
        if (vistasCargadas.isEmpty()) {
            Alertas.mostrarError("El usuario no tiene acceso a ningún apartado.");
            return;
        }

        String nombre = vistasCargadas.containsKey("Inicio")
                ? "Inicio"
                : vistasCargadas.keySet().iterator().next();

        botones().stream()
                .filter(b -> b.getText().equals(nombre))
                .findFirst()
                .ifPresent(b -> navegar(b));
    }

    private void navegar(ToggleButton elegido) {
        botones().forEach(tb -> tb.setSelected(tb == elegido));
        mostrarVista(elegido.getText());
    }

    private void mostrarVista(String nombre) {
        vistasCargadas.forEach((clave, vista) -> {
            boolean activa = clave.equals(nombre);
            vista.setVisible(activa);
            vista.setManaged(activa);
        });
    }

    private List<ToggleButton> botones() {
        return vbContenido.getChildren().stream()
                .filter(n -> n instanceof ToggleButton)
                .map(n -> (ToggleButton) n)
                .collect(Collectors.toList());
    }
}
