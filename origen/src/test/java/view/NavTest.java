package view;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import util.InterpretePermisos;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * TEST CLASS NavTest
 *
 * @author Joel
 */
public class NavTest extends ApplicationTest {

    // ---------------------- Tipos de permisos ----------------------
    private static final Map<String, List<String>> PERMISOS = new LinkedHashMap<>();

    // inserts de permisos
    static {
        PERMISOS.put("total", List.of("total"));
        PERMISOS.put("sinPermisos", List.of());
        PERMISOS.put("soloInicio", List.of("inicio"));
        PERMISOS.put("soloCaja", List.of("caja"));
        PERMISOS.put("inicioYCaja", List.of("inicio", "caja"));
        PERMISOS.put("subpermisoGestion", List.of("gestion:ingresosGastos"));
        PERMISOS.put("stockYCompras", List.of("stock", "compras"));
        PERMISOS.put("soloAdministracion", List.of("administracion"));
        PERMISOS.put("ejemploMasDeUno", List.of("caja", "gestion:ingresosGastos", "inicio"));
    }

    // modulos existentes
    private static final List<String> MODULOS_MENU = List.of(
            "Inicio",
            "Caja y pedidos",
            "Gestión económica",
            "Gestión stock",
            "Compras y proveedores",
            "Administración");

    private Stage stagePrincipal;
    private controller.NavController controller;

    /**
     * Iniciamos el stage
     *
     * @param stage
     * @throws java.io.IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        stagePrincipal = stage;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/navView.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    // ---------------------- Visibilidad de nodos (sin usuario) ----------------------
    @Test
    public void testVisibilidadSidebar() {
        verifyThat("#vbContenido", isVisible());
    }

    @Test
    public void testVisibilidadContenedorNav() {
        verifyThat("#vbNav", isVisible());
    }

    @Test
    public void testVisibilidadBtnPerfil() {
        verifyThat(lookup(hasText("Perfil")).queryButton(), isVisible());
    }

    @Test
    public void testVisibilidadBtnCerrarSesion() {
        verifyThat(lookup(hasText("Cerrar sesión")).queryButton(), isVisible());
    }

    @Test
    public void testNavVacioSinUsuario() {
        assertTrue(lookup("#vbNav").queryAs(VBox.class).getChildren().isEmpty());
    }

    @Test
    public void testContenidoVacioSinUsuario() {
        assertTrue(lookup("#contenido").queryAs(StackPane.class).getChildren().isEmpty());
    }

    @Test
    public void testBotonesInferioresTienenIconoYEstilo() {
        Button perfil = lookup(hasText("Perfil")).queryButton();
        Button cerrar = lookup(hasText("Cerrar sesión")).queryButton();
        assertNotNull(perfil.getGraphic());
        assertNotNull(cerrar.getGraphic());
        assertTrue(perfil.getStyleClass().contains("nav-item"));
        assertTrue(cerrar.getStyleClass().contains("nav-item"));
    }

    // ---------------------- Módulos según permisos ----------------------
    @Test
    public void testPermisosTotal() {
        probarCaso("total");
    }

    @Test
    public void testPermisosSinPermisos() {
        probarCaso("sinPermisos");
    }

    @Test
    public void testPermisosSoloInicio() {
        probarCaso("soloInicio");
    }

    @Test
    public void testPermisosSoloCaja() {
        probarCaso("soloCaja");
    }

    @Test
    public void testPermisosInicioYCaja() {
        probarCaso("inicioYCaja");
    }

    @Test
    public void testPermisosSubpermisoGestion() {
        probarCaso("subpermisoGestion");
    }

    @Test
    public void testPermisosStockYCompras() {
        probarCaso("stockYCompras");
    }

    @Test
    public void testPermisosSoloAdministracion() {
        probarCaso("soloAdministracion");
    }

    @Test
    public void testPermisosVariosModulos() {
        probarCaso("ejemploMasDeUno");
    }

    // ---------------------- Navegacion entre modulos ----------------------
    @Test
    public void testNavegarPorTodosLosModulosConPermisoTotal() {
        cargarUsuario(PERMISOS.get("total"));
        List<ToggleButton> botones = obtenerBotones();
        assertEquals(MODULOS_MENU.size(), botones.size());

        for (int i = 0; i < botones.size(); i++) {
            clickOn(botones.get(i));
            WaitForAsyncUtils.waitForFxEvents();
            verificarVistaActiva(i, botones.get(i).getText());
        }
    }

    @Test
    public void testNavegarPorLosModulosDeUnUsuarioConPocosPermisos() {
        cargarUsuario(PERMISOS.get("ejemploMasDeUno"));
        List<ToggleButton> botones = obtenerBotones();
        assertFalse(botones.isEmpty());

        for (int i = botones.size() - 1; i >= 0; i--) {
            clickOn(botones.get(i));
            WaitForAsyncUtils.waitForFxEvents();
            verificarVistaActiva(i, botones.get(i).getText());
        }
    }

    // ---------------------- Metodos auxiliares -------------------------
    /**
     * Comprueba un caso del mapa CASOS usando InterpretePermisos para saber qué
     * módulos deben cargarse.
     */
    private void probarCaso(String nombreCaso) {
        List<String> permisos = PERMISOS.get(nombreCaso);
        assertNotNull(permisos, "Caso inexistente: " + nombreCaso);

        // el interprete decide que cargar
        List<String> sinAcceso = InterpretePermisos.apartadosSinAcceso(permisos);
        List<String> esperados = MODULOS_MENU.stream()
                .filter(m -> !sinAcceso.contains(m))
                .collect(Collectors.toList());

        cargarUsuario(permisos);

        List<ToggleButton> botones = obtenerBotones();
        StackPane contenido = lookup("#contenido").queryAs(StackPane.class);

        // Comparamos lo esperado con la lista de toggleButton
        List<String> textos = botones.stream().map(ToggleButton::getText).collect(Collectors.toList());
        assertEquals(esperados, textos, "Botones del nav incorrectos en el caso " + nombreCaso);

        // Compruebo los apartados sin acceso, si estan ocultos 
        for (String oculto : sinAcceso) {
            assertFalse(textos.contains(oculto), "'" + oculto + "' no debería aparecer en " + nombreCaso);
        }

        // Comprobacion de los iconos
        for (ToggleButton boton : botones) {
            assertInstanceOf(ImageView.class, boton.getGraphic(),
                    "Sin icono: " + boton.getText());
            Image imagen = ((ImageView) boton.getGraphic()).getImage();
            assertNotNull(imagen, "Imagen nula: " + boton.getText());
            assertFalse(imagen.isError(), "Icono con error: " + boton.getText());
        }

        // comprobacion del tamaño de modulos cargados
        assertEquals(esperados.size(), contenido.getChildren().size(),
                "Módulos cargados incorrectos en el caso " + nombreCaso);

        if (esperados.isEmpty()) {
            // lista de las ventanas aviertas tiene que ser mayor a 1
            assertTrue(listWindows().size() > 1,
                    "Debería mostrarse el aviso de que no hay acceso a ningún apartado");
            return;
        }

        // sin errores ya que solo esta una ventana abierta
        assertEquals(1, listWindows().size(),
                "Se abrió algún aviso/error al cargar los módulos en el caso " + nombreCaso);

        // por defecto esta abierto el primer modulo
        verificarVistaActiva(0, esperados.get(0));
    }

    private void verificarVistaActiva(int indice, String nombreModulo) {
        List<ToggleButton> botones = obtenerBotones();
        StackPane contenido = lookup("#contenido").queryAs(StackPane.class);

        for (int i = 0; i < contenido.getChildren().size(); i++) {
            Node vista = contenido.getChildren().get(i);
            boolean activa = i == indice;
            assertEquals(activa, vista.isVisible(),
                    "Visibilidad incorrecta de la vista " + i + " (activa: " + indice + ")");
            assertEquals(activa, vista.isManaged(),
                    "Managed incorrecto de la vista " + i + " (activa: " + indice + ")");
            assertEquals(activa, botones.get(i).isSelected(),
                    "Botón seleccionado incorrecto: " + botones.get(i).getText());
        }
        assertEquals(nombreModulo, stagePrincipal.getTitle());
    }

    @AfterEach
    public void tearDown() throws TimeoutException {
        cerrarVentanasExtra();
        FxToolkit.hideStage(); // cerrar la ventana
        release(new KeyCode[]{}); // soltar teclas que hayan quedado pulsadas
        release(new MouseButton[]{}); // soltar botones del ratón
    }

    // ---------------------- Métodos auxiliares ----------------------
    // asigna un usuario simulado con esos permisos al nav
    private void cargarUsuario(List<String> permisos) {
        Usuario usuario = new Usuario() {
            @Override
            public List<String> getPermisos() {
                return permisos;
            }

            @Override
            public String getUsername() {
                return "tester";
            }
        };

        // ignora las alerta modal del usuarios
        Platform.runLater(() -> controller.setUsuario(usuario));
        WaitForAsyncUtils.waitForFxEvents();
    }

    private List<ToggleButton> obtenerBotones() {
        return lookup("#vbNav").queryAs(VBox.class).getChildren().stream()
                .map(n -> (ToggleButton) n)
                .collect(Collectors.toList());
    }

    // cierra dialogs/alertas que hayan quedado abiertos
    private void cerrarVentanasExtra() {
        interact(() -> listWindows().stream()
                .filter(w -> w != stagePrincipal)
                .collect(Collectors.toList())
                .forEach(Window::hide));
    }
}
