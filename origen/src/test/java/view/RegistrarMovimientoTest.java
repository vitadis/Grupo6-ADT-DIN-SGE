package view;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import controller.RegistrarMovimientoController;
import dao.PedidoDao;
import dao.implement.ComprasDaoImpl;
import dao.implement.MovimientoDaoImpl;
import dao.implement.PedidoDaoImp;
import dao.implement.ProveedorDaoImpl;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Movimiento;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import services.MovimientoService;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests del diálogo «Registrar movimiento» ({@code registrarMovimiento.fxml}), abierto solo.
 *
 * Usa la base de datos {@code origen} (MongoDB tiene que estar arrancado). Al diálogo se le pasan
 * el service y los movimientos que hay, como hace {@code GestionEconomicaController}. Los
 * movimientos que guarda llevan concepto {@code TEST-...} y se borran al acabar cada test.
 *
 * @author anazk
 */
public class RegistrarMovimientoTest extends ApplicationTest {

    /**
     * Dirección de MongoDB, para borrar lo que dejan los tests.
     */
    private static final String CONEXION = "mongodb://localhost:27017";
    /**
     * Base de datos que usa la aplicación.
     */
    private static final String BASE_DATOS = "origen";

    /**
     * Ventana del diálogo.
     */
    private Stage stage;
    /**
     * Controller del diálogo.
     */
    private RegistrarMovimientoController controlador;
    /**
     * Service con los DAOs de MongoDB; también se usa para comprobar lo guardado.
     */
    private MovimientoService servicio;

    /**
     * Constructor vacío. Lo usa JUnit para crear el test.
     */
    public RegistrarMovimientoTest() {
    }

    /**
     * Abre el diálogo y le pasa el service y los movimientos que hay, como hace
     * {@code GestionEconomicaController}.
     *
     * @param stage ventana que da TestFX
     * @throws IOException si no se encuentra el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        servicio = new MovimientoService(MovimientoDaoImpl.getInstance(), ComprasDaoImpl.getInstance(),
                (PedidoDao) PedidoDaoImp.getInstance(), ProveedorDaoImpl.getInstance());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/economia/registrarMovimiento.fxml"));
        Parent root = loader.load();
        controlador = loader.getController();
        controlador.setMovimientoService(servicio);
        controlador.setDatos(1, servicio.listarMovimientos());

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Al abrir: «Gasto» marcado, fecha de hoy, sin aviso ni errores.
     */
    @Test
    public void testEstadoInicial() {
        assertTrue(toggle("#tgGasto").isSelected());
        assertFalse(toggle("#tgIngreso").isSelected());
        assertEquals(LocalDate.now(), lookup("#dpFecha").queryAs(DatePicker.class).getValue());
        assertFalse(avisoVisible());
        assertEquals("", texto("#lblError"));
    }

    /**
     * El desplegable trae las categorías fijas y también las de los movimientos que ya hay.
     */
    @Test
    public void testCategoriasDelDesplegable() {
        assertTrue(categorias().getItems().contains("Suministros"));
        assertTrue(categorias().getItems().contains("Personal"));
        for (Movimiento m : servicio.listarMovimientos()) {
            assertTrue(categorias().getItems().contains(m.getCategoria()), "Falta la categoría " + m.getCategoria());
        }
    }

    /**
     * Sin concepto da el error de {@code Movimiento.validar()}.
     */
    @Test
    public void testSinConceptoDaError() {
        escribirCategoria("Otros");
        clickOn("#txtImporte").write("10");

        clickOn("#btnGuardar");

        assertEquals("El concepto es obligatorio.", texto("#lblError"));
        assertTrue(stage.isShowing(), "Con error la ventana no se cierra");
    }

    /**
     * Sin categoría da error.
     */
    @Test
    public void testSinCategoriaDaError() {
        clickOn("#txtConcepto").write(conceptoDePrueba());
        clickOn("#txtImporte").write("10");

        clickOn("#btnGuardar");

        assertEquals("La categoría es obligatoria.", texto("#lblError"));
    }

    /**
     * Un importe con letras da error.
     */
    @Test
    public void testImporteConLetrasDaError() {
        rellenar(conceptoDePrueba(), "Otros", "abc");

        clickOn("#btnGuardar");

        assertEquals("El importe debe ser un número.", texto("#lblError"));
    }

    /**
     * Un importe 0 da error.
     */
    @Test
    public void testImporteCeroDaError() {
        rellenar(conceptoDePrueba(), "Otros", "0");

        clickOn("#btnGuardar");

        assertEquals("El importe debe ser mayor que 0.", texto("#lblError"));
    }

    /**
     * Si se desmarca el tipo, da error.
     */
    @Test
    public void testSinTipoDaError() {
        rellenar(conceptoDePrueba(), "Otros", "10");
        clickOn("#tgGasto");

        clickOn("#btnGuardar");

        assertEquals("El tipo debe ser ingreso o gasto.", texto("#lblError"));
    }

    /**
     * Un gasto mayor que la media de su categoría enseña el aviso con el nombre de la categoría.
     */
    @Test
    public void testAvisoSiElGastoSuperaLaMedia() {
        assumeTrue(hayGastosDe("Suministros"), "No hay gastos de Suministros en la BD");

        rellenar(conceptoDePrueba(), "Suministros", "99999");

        assertTrue(avisoVisible());
        assertTrue(texto("#lblAviso").contains("«Suministros»"));
    }

    /**
     * Un gasto por debajo de la media no enseña el aviso.
     */
    @Test
    public void testSinAvisoSiElGastoEsBajo() {
        assumeTrue(hayGastosDe("Suministros"), "No hay gastos de Suministros en la BD");

        rellenar(conceptoDePrueba(), "Suministros", "1");

        assertFalse(avisoVisible());
    }

    /**
     * Un ingreso nunca enseña el aviso, aunque el importe sea alto.
     */
    @Test
    public void testUnIngresoNoTieneAviso() {
        assumeTrue(hayGastosDe("Suministros"), "No hay gastos de Suministros en la BD");
        clickOn("#tgIngreso");

        rellenar(conceptoDePrueba(), "Suministros", "99999");

        assertFalse(avisoVisible());
    }

    /**
     * Guardar un gasto correcto lo mete en la base de datos con sus datos y cierra la ventana.
     * El importe acepta la coma decimal. Al acabar, {@link #tearDown()} lo borra.
     */
    @Test
    public void testGuardarGasto() {
        String concepto = conceptoDePrueba();
        rellenar(concepto, "Otros", "12,5");
        clickOn("#txtObservaciones").write("Prueba");

        clickOn("#btnGuardar");
        WaitForAsyncUtils.waitForFxEvents();

        Movimiento m = buscar(concepto);
        assertNotNull(m, "No se ha guardado en la BD");
        assertEquals("gasto", m.getTipo());
        assertEquals("Otros", m.getCategoria());
        assertEquals(12.5, m.getImporte(), 0.001);
        assertEquals(LocalDate.now().toString(), m.getFecha());
        assertEquals(1, m.getUsuario_id());
        assertEquals("Prueba", m.getObservaciones());
        assertTrue(controlador.isGuardado());
        assertFalse(stage.isShowing(), "Al guardar se cierra la ventana");
    }

    /**
     * Guardar con «Ingreso» marcado lo guarda como ingreso.
     */
    @Test
    public void testGuardarIngreso() {
        String concepto = conceptoDePrueba();
        clickOn("#tgIngreso");
        rellenar(concepto, "Eventos", "300");

        clickOn("#btnGuardar");
        WaitForAsyncUtils.waitForFxEvents();

        Movimiento m = buscar(concepto);
        assertNotNull(m, "No se ha guardado en la BD");
        assertEquals("ingreso", m.getTipo());
    }

    /**
     * Cancelar cierra la ventana sin guardar.
     */
    @Test
    public void testCancelarCierraSinGuardar() {
        String concepto = conceptoDePrueba();
        rellenar(concepto, "Otros", "10");

        clickOn("#btnCancelar");
        WaitForAsyncUtils.waitForFxEvents();

        assertNull(buscar(concepto), "Cancelar no debe guardar");
        assertFalse(controlador.isGuardado());
        assertFalse(stage.isShowing());
    }

    /**
     * Borra los movimientos de prueba, oculta la ventana y suelta el teclado y el ratón.
     *
     * @throws TimeoutException si TestFX no consigue ocultar la ventana
     */
    @AfterEach
    public void tearDown() throws TimeoutException {
        borrarMovimientosDePrueba();
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /**
     * Borra los movimientos con concepto {@code TEST-...}. Se hace con el driver porque el DAO
     * no tiene método para borrar.
     */
    private void borrarMovimientosDePrueba() {
        try (MongoClient cliente = MongoClients.create(CONEXION)) {
            cliente.getDatabase(BASE_DATOS).getCollection("movimientos")
                    .deleteMany(Filters.regex("concepto", "^TEST-"));
        }
    }

    /**
     * Escribe el concepto, la categoría y el importe.
     *
     * @param concepto concepto del movimiento
     * @param categoria categoría
     * @param importe texto del importe
     */
    private void rellenar(String concepto, String categoria, String importe) {
        clickOn("#txtConcepto").write(concepto);
        escribirCategoria(categoria);
        clickOn("#txtImporte").write(importe);
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Escribe la categoría en el desplegable, que es editable.
     *
     * @param categoria categoría
     */
    private void escribirCategoria(String categoria) {
        clickOn(categorias().getEditor()).write(categoria);
    }

    /**
     * Crea un concepto que no existe y que la limpieza reconoce.
     *
     * @return un concepto {@code TEST-...}
     */
    private String conceptoDePrueba() {
        return "TEST-" + System.currentTimeMillis();
    }

    /**
     * Busca en la base de datos un movimiento por su concepto.
     *
     * @param concepto concepto del movimiento
     * @return el movimiento, o {@code null} si no está
     */
    private Movimiento buscar(String concepto) {
        for (Movimiento m : servicio.listarMovimientos()) {
            if (concepto.equals(m.getConcepto())) {
                return m;
            }
        }
        return null;
    }

    /**
     * Indica si en la base de datos hay gastos de una categoría, para poder calcular su media.
     *
     * @param categoria categoría
     * @return {@code true} si hay al menos uno
     */
    private boolean hayGastosDe(String categoria) {
        for (Movimiento m : servicio.listarMovimientos()) {
            if (!m.esIngreso() && categoria.equalsIgnoreCase(m.getCategoria())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Indica si se ve la caja ámbar del aviso.
     *
     * @return {@code true} si está visible
     */
    private boolean avisoVisible() {
        return lookup("#cajaAviso").queryAs(VBox.class).isVisible();
    }

    /**
     * Devuelve el desplegable de categorías.
     *
     * @return el ComboBox
     */
    @SuppressWarnings("unchecked")
    private ComboBox<String> categorias() {
        return lookup("#cbCategoria").queryAs(ComboBox.class);
    }

    /**
     * Devuelve uno de los dos botones de tipo.
     *
     * @param id selector del botón
     * @return el ToggleButton
     */
    private ToggleButton toggle(String id) {
        return lookup(id).queryAs(ToggleButton.class);
    }

    /**
     * Devuelve el texto de un Label.
     *
     * @param id selector del Label
     * @return su texto
     */
    private String texto(String id) {
        return lookup(id).queryAs(Label.class).getText();
    }
}
