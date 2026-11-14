package controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
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
import util.AppLogger;
import util.InterpretePermisos;

/**
 * FXML Controller class para la navegación principal entre módulos.
 *
 * @author Joel
 */
public class NavController {

    // ---- Variables FXML ----
    @FXML
    private StackPane contenido;
    @FXML
    private VBox vbNav;

    // ---- Atributos y mapas de módulos ----
    private static final Map<String, String> ICONOS = new LinkedHashMap<>();
    private final AppLogger logger = AppLogger.getInstance();

    static {
        ICONOS.put("Inicio", "/img/icon_home.png");
        ICONOS.put("Caja y pedidos", "/img/icon_caja.png");
        ICONOS.put("Gestión económica", "/img/icon_euro.png");
        ICONOS.put("Gestión stock", "/img/icon_box.png");
        ICONOS.put("Compras y proveedores", "/img/icon_proveedores.png");
        ICONOS.put("Administración", "/img/icon_administracion.png");
    }

    private static final Map<String, String> VISTAS = Map.of(
            "Inicio", "/view/inicio/inicioView.fxml",
            "Caja y pedidos", "/view/caja/cajaView.fxml",
            "Gestión económica", "/view/economia/gestionEconomicaView.fxml",
            "Gestión stock", "/view/gestionStock/gestionStock.fxml",
            "Compras y proveedores", "/view/compra/CompraView.fxml",
            "Administración", "/view/admin/administracionView.fxml"
    );

    private final ToggleGroup grupoNav = new ToggleGroup();
    private final Map<String, Node> vistasCargadas = new LinkedHashMap<>();
    private final Map<String, Object> controladores = new LinkedHashMap<>();

    // ---- Usuario ----
    private Usuario usuario;

    /**
     * Devuelve el usuario autenticado en la sesión actual.
     *
     * @return usuario autenticado
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Asigna el usuario autenticado al controlador y construye el menú de
     * navegación según sus permisos.
     *
     * @param usuario usuario autenticado
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            logger.setUser(usuario.getUsername());
        }
        construirNavegacion();
    }

    /**
     * Inicializa el controlador de navegación.
     *
     * @param url ubicación utilizada para resolver rutas relativas
     * @param rb recursos utilizados para localizar el objeto raíz
     */
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
    }

    // ---- Métodos FXML ----
    /**
     * Abre la ventana modal de consulta del perfil del usuario actual.
     *
     * @param event evento de acción del botón
     * @throws IOException si no se puede cargar la vista del perfil
     */
    @FXML
    private void abrirPerfil(ActionEvent event) throws IOException {
        InstanciarEscena.createNewStage("/view/admin/usuarioView.fxml", "Perfil de usuario", this::dialogPerfil);
    }

    /**
     * Configura el controlador del diálogo de usuario en modo solo lectura para
     * mostrar el perfil.
     *
     * @param controller instancia del controlador del diálogo
     */
    private void dialogPerfil(Object controller) {
        try {
            DialogUsuarioController controllerDialog = (DialogUsuarioController) controller;
            controllerDialog.setUsuario(usuario);
            controllerDialog.setSoloLectura();
            logger.info("Consulta de perfil del usuario: " + (usuario != null ? usuario.getUsername() : "desconocido"));
        } catch (Exception e) {
            Alertas.mostrarError("Error con el dialog perfil: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al abrir perfil de usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Solicita confirmación al usuario, registra el cierre de sesión y vuelve a
     * la pantalla de login.
     *
     * @param event evento de acción del botón
     */
    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alertas.mostrarConfirmacion("Confirmación", "¿Seguro que quieres cerrar sesión?", () -> {
            try {
                Button btnCS = (Button) event.getSource();
                Stage stage = (Stage) btnCS.getScene().getWindow();

                logger.info("Cierre de sesión del usuario: " + (usuario != null ? usuario.getUsername() : "desconocido"));
                InstanciarEscena.cambiarVista(stage, "/view/loginView.fxml", "Inicio Sesión");
            } catch (IOException ex) {
                Alertas.mostrarError("No se pudo cerrar sesión: " + ex.getMessage());
                logger.log(Level.SEVERE, "Error al cerrar sesión: " + ex.getMessage(), ex);
            }
        });
    }

    // ---- Métodos auxiliares ----
    /**
     * Construye los botones del menú lateral y precarga las vistas de los
     * módulos permitidos para el usuario.
     */
    private void construirNavegacion() {
        vbNav.getChildren().clear();
        contenido.getChildren().clear();
        vistasCargadas.clear();
        controladores.clear();

        List<String> sinAcceso = InterpretePermisos.apartadosSinAcceso(usuario.getPermisos());

        ICONOS.forEach((nombre, icono) -> {
            if (sinAcceso.contains(nombre)) {
                return;
            }

            vbNav.getChildren().add(crearBoton(nombre, icono));

            String ruta = VISTAS.get(nombre);
            if (ruta != null) {
                cargarVista(nombre, ruta);
            }
        });

        seleccionarVistaInicial();
    }

    /**
     * Crea un botón de navegación lateral con su icono y evento de selección
     * asociados.
     *
     * @param nombre nombre visible del módulo
     * @param rutaIcono ruta del recurso gráfico del icono
     * @return botón configurado para la barra lateral
     */
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

    /**
     * Carga el archivo FXML de un módulo, inyecta el usuario en su controlador
     * si corresponde y lo añade al contenedor.
     *
     * @param nombre nombre identificador del módulo
     * @param ruta ruta al archivo FXML de la vista
     */
    private void cargarVista(String nombre, String ruta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            Node vista = loader.load();

            Object controller = loader.getController();
            controladores.put(nombre, controller);
            if (controller instanceof UsuarioImplementacion) {
                ((UsuarioImplementacion) controller).setUsuario(usuario);
            }

            vista.setVisible(false);
            vista.setManaged(false);
            contenido.getChildren().add(vista);
            vistasCargadas.put(nombre, vista);
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo cargar " + ruta + ": " + e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar la vista '" + ruta + "': " + e.getMessage(), e);
        }
    }

    /**
     * Selecciona y muestra automáticamente el primer módulo disponible para el
     * usuario.
     */
    private void seleccionarVistaInicial() {
        if (vbNav.getChildren().isEmpty()) {
            Alertas.mostrarError("El usuario no tiene acceso a ningún apartado.");
            logger.log(Level.WARNING, "El usuario '" + (usuario != null ? usuario.getUsername() : "desconocido") + "' no tiene acceso a ningún apartado.");
            return;
        }

        ToggleButton inicial = botones().stream()
                .filter(b -> vistasCargadas.containsKey(b.getText()))
                .findFirst()
                .orElse(botones().get(0));

        navegar(inicial);
    }

    /**
     * Activa el botón seleccionado, muestra su vista asociada y actualiza el
     * título de la ventana.
     *
     * @param elegido botón de navegación pulsado
     */
    private void navegar(ToggleButton elegido) {
        elegido.setSelected(true);
        mostrarVista(elegido.getText());
        cambiarTitulo(elegido.getText());
        logger.info("Navegación al módulo: " + elegido.getText());
    }

    /**
     * Hace visible únicamente la vista del módulo indicado y solicita refrescar
     * sus datos si lo soporta.
     *
     * @param nombre nombre del módulo a mostrar
     */
    private void mostrarVista(String nombre) {
        for (Map.Entry<String, Node> entrada : vistasCargadas.entrySet()) {
            boolean activa = entrada.getKey().equals(nombre);
            entrada.getValue().setVisible(activa);
            entrada.getValue().setManaged(activa);
        }

        Object controller = controladores.get(nombre);
        if (controller instanceof ActualizarDatos) {
            ((ActualizarDatos) controller).actualizarDatos();
        }
    }

    /**
     * Devuelve la lista de botones de navegación añadidos al menú lateral.
     *
     * @return lista de {@link ToggleButton} del menú
     */
    private List<ToggleButton> botones() {
        return vbNav.getChildren().stream()
                .map(n -> (ToggleButton) n)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza el título de la ventana principal con el nombre del módulo
     * activo.
     *
     * @param titulo nuevo título de la ventana
     */
    private void cambiarTitulo(String titulo) {
        if (contenido.getScene() != null && contenido.getScene().getWindow() instanceof Stage) {
            ((Stage) contenido.getScene().getWindow()).setTitle(titulo);
        }
    }
}
