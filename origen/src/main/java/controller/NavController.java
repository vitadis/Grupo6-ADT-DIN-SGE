package controller;

import java.io.IOException;
import java.io.InputStream;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private VBox vbNav;

    // --------------------- Atributos ---------------------
    // map de los iconos de los modulos
    private static final Map<String, String> ICONOS = new LinkedHashMap<>();

    static {
        ICONOS.put("Inicio", "/img/icon_home.png");
        ICONOS.put("Caja y pedidos", "/img/icon_caja.png");
        ICONOS.put("Gestión económica", "/img/icon_euro.png");
        ICONOS.put("Gestión stock", "/img/icon_box.png");
        ICONOS.put("Compras y proveedores", "/img/icon_proveedores.png");
        ICONOS.put("Análisis e informes", "/img/icon_analisis.png");
        ICONOS.put("Administración", "/img/icon_administracion.png");
    }

    // map de vistas
    private static final Map<String, String> VISTAS = Map.of(
            //"Inicio", "/view/inicio/inicioView.fxml",
            "Caja y pedidos", "/view/caja/cajaView.fxml",
            "Gestión económica", "/view/compra/CompraView.fxml",
            "Gestión stock", "/view/gestionStock/gestionStock.fxml"
    //"Compras y proveedores", "/view/compras/comprasView.fxml",
    //"Análisis e informes", "/view/informes/informesView.fxml",
    //"Administración", "/view/admin/adminView.fxml"
    );

    // ToogleGrup para el nava
    private final ToggleGroup grupoNav = new ToggleGroup();
    // Vistas cargadas (lo vinculo con el modulo)
    private final Map<String, Node> vistasCargadas = new LinkedHashMap<>();

    // --------------------- Usuario ---------------------
    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        construirNavegacion();
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        //construirNavegacion();
    }

    // --------------------- Métodos FXML ---------------------
    @FXML
    private void abrirPerfil(ActionEvent event) throws IOException {
        InstanciarEscena.createNewStage("/view/perfilView.fxml", "Perfil de usuario", this::dialogPerfil);
    }

    // metodo
    private void dialogPerfil(Object controller) {
        try {
            DialogPerfilController controllerDialog = (DialogPerfilController) controller;
            controllerDialog.setusuario(usuario);
        } catch (Exception e) {
            System.out.println("Error con el dialog perfil: " + e.getMessage());
        }

    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alertas.mostrarConfirmacion("Confirmación", "¿Seguro que quieres cerrar sesión?", () -> {
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
    /**
     * Crea un ToggleButton (y carga su vista) solo para los módulos a los que
     * el usuario tiene acceso.
     */
    private void construirNavegacion() {
        vbNav.getChildren().clear();
        contenido.getChildren().clear();
        vistasCargadas.clear();

        List<String> sinAcceso = InterpretePermisos.apartadosSinAcceso(usuario.getPermisos());

        ICONOS.forEach((nombre, icono) -> {
            if (sinAcceso.contains(nombre)) {
                return; // sin acceso no se crea el botón
            }

            vbNav.getChildren().add(crearBoton(nombre, icono));

            String ruta = VISTAS.get(nombre);
            if (ruta != null) {
                cargarVista(nombre, ruta);
            }
        });

        seleccionarVistaInicial();
    }

    private ToggleButton crearBoton(String nombre, String rutaIcono) {
        ToggleButton boton = new ToggleButton(nombre);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.getStyleClass().add("nav-item");
        boton.setToggleGroup(grupoNav);

        InputStream recurso = getClass().getResourceAsStream(rutaIcono);
        if (recurso != null) {
            ImageView icono = new ImageView(new Image(recurso));
            icono.setFitWidth(24);
            icono.setFitHeight(24);
            icono.setPreserveRatio(true);
            boton.setGraphic(icono);
        }

        boton.setOnAction(e -> navegar(boton));
        return boton;
    }

    private void cargarVista(String nombre, String ruta) {
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
            vistasCargadas.put(nombre, vista);
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo cargar " + ruta + ": " + e.getMessage());
        }
    }

    private void seleccionarVistaInicial() {
        if (vbNav.getChildren().isEmpty()) {
            Alertas.mostrarError("El usuario no tiene acceso a ningún apartado.");
            return;
        }

        ToggleButton inicial = botones().stream()
                .filter(b -> vistasCargadas.containsKey(b.getText()))
                .findFirst()
                .orElse(botones().get(0));

        navegar(inicial);
    }

    private void navegar(ToggleButton elegido) {
        elegido.setSelected(true);
        mostrarVista(elegido.getText());
        cambiarTitulo(elegido.getText());
    }

    private void mostrarVista(String nombre) {
        vistasCargadas.forEach((clave, vista) -> {
            boolean activa = clave.equals(nombre);
            vista.setVisible(activa);
            vista.setManaged(activa);
        });
    }

    private List<ToggleButton> botones() {
        return vbNav.getChildren().stream()
                .map(n -> (ToggleButton) n)
                .collect(Collectors.toList());
    }

    private void cambiarTitulo(String titulo) {
        if (contenido.getScene() != null && contenido.getScene().getWindow() instanceof Stage) {
            ((Stage) contenido.getScene().getWindow()).setTitle(titulo);
        }
    }
}
