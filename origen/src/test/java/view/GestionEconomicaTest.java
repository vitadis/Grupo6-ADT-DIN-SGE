package view;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import controller.UsuarioImplementacion;
import dao.PedidoDao;
import dao.implement.ComprasDaoImpl;
import dao.implement.MovimientoDaoImpl;
import dao.implement.PedidoDaoImp;
import dao.implement.ProveedorDaoImpl;
import exceptions.DaoException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Compra;
import model.EstadoPedido;
import model.Movimiento;
import model.Usuario;
import model.VentaPedido;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import services.MovimientoService;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.LabeledMatchers.hasText;

/**
 * Tests de la pantalla «Gestión económica» ({@code gestionEconomicaView.fxml}).
 *
 * Prueba la pantalla: tarjetas, gráfico, tabla y filtros. Del diálogo «Registrar movimiento»
 * solo comprueba que se abre y que, al guardar desde él, la pantalla se actualiza; lo que pasa
 * dentro del diálogo está en {@link RegistrarMovimientoTest}.
 *
 * Usa la base de datos {@code origen} (MongoDB tiene que estar arrancado). Los valores esperados
 * se calculan leyendo la base de datos con un {@link MovimientoService}, así que no dependen de
 * datos concretos. El movimiento que se guarda lleva concepto {@code TEST-...} y se borra al
 * acabar.
 *
 * Si las ventas no se pueden leer (por ejemplo, pedidos guardados en minúsculas), la pantalla
 * avisa con un {@code Alert} al cargarse; el test lo cierra para que no se quede esperando.
 *
 * @author anazk
 */
public class GestionEconomicaTest extends ApplicationTest {

    /**
     * Dirección de MongoDB, para borrar lo que deja el test.
     */
    private static final String CONEXION = "mongodb://localhost:27017";
    /**
     * Base de datos que usa la aplicación.
     */
    private static final String BASE_DATOS = "origen";

    /**
     * Ventana de la pantalla, para distinguirla del diálogo que se abre encima.
     */
    private Stage stagePrincipal;
    /**
     * Service con los DAOs de MongoDB, para calcular lo que debería enseñar la pantalla.
     */
    private MovimientoService servicio;

    /**
     * Constructor vacío. Lo usa JUnit para crear el test.
     */
    public GestionEconomicaTest() {
    }

    /**
     * Abre la pantalla maximizada y le pasa un usuario, como hace {@code NavController}, para que
     * los movimientos se puedan guardar. Antes deja preparado el cierre de los avisos que pueda
     * sacar al cargarse.
     *
     * @param stage ventana que da TestFX
     * @throws IOException si no se encuentra el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        stagePrincipal = stage;
        servicio = new MovimientoService(MovimientoDaoImpl.getInstance(), ComprasDaoImpl.getInstance(),
                (PedidoDao) PedidoDaoImp.getInstance(), ProveedorDaoImpl.getInstance());

        cerrarAvisosAlCargar(5);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/economia/gestionEconomicaView.fxml"));
        Parent root = loader.load();
        Usuario usuario = new Usuario();
        usuario.setId(1);
        usuario.setUsername("test");
        ((UsuarioImplementacion) loader.getController()).setUsuario(usuario);
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Se ven las tarjetas, el gráfico, la tabla, los filtros y el botón principal.
     */
    @Test
    public void testVisibilidadInicial() {
        verifyThat("#btnRegistrarMovimiento", isVisible());
        verifyThat("#lblIngresos", isVisible());
        verifyThat("#lblGastos", isVisible());
        verifyThat("#lblBeneficio", isVisible());
        verifyThat("#graficoGastos", isVisible());
        verifyThat("#contenedorMovimientos", isVisible());
        verifyThat("#lblContador", isVisible());
        verifyThat("#btnLimpiarFiltros", isVisible());
    }

    /**
     * El combo de tipo ofrece «Todos», «Ingreso» y «Gasto».
     */
    @Test
    public void testOpcionesDelFiltroDeTipo() {
        assertEquals(List.of("Todos", "Ingreso", "Gasto"), combo("#cbTipo").getItems());
    }

    /**
     * La tarjeta de gastos es la suma de las compras y de los movimientos de gasto del mes.
     */
    @Test
    public void testTarjetaDeGastos() {
        assertEquals(euros(gastosDelMes()), texto("#lblGastos"));
    }

    /**
     * La tarjeta de ingresos es la suma de las ventas cobradas y de los movimientos de ingreso
     * del mes.
     */
    @Test
    public void testTarjetaDeIngresos() {
        assertEquals(euros(ingresosDelMes()), texto("#lblIngresos"));
    }

    /**
     * El beneficio es ingresos menos gastos.
     */
    @Test
    public void testTarjetaDeBeneficio() {
        assertEquals(euros(ingresosDelMes() - gastosDelMes()), texto("#lblBeneficio"));
    }

    /**
     * El gráfico tiene una sola serie con los seis últimos meses, en orden y acabando en el mes
     * actual, sin barras negativas.
     */
    @Test
    public void testGraficoTieneLosSeisUltimosMeses() {
        BarChart<String, Number> grafico = grafico();
        assertEquals(1, grafico.getData().size());
        List<XYChart.Data<String, Number>> barras = grafico.getData().get(0).getData();
        assertEquals(6, barras.size());

        YearMonth mes = YearMonth.now().minusMonths(5);
        for (XYChart.Data<String, Number> barra : barras) {
            assertEquals(nombreMes(mes), barra.getXValue());
            assertTrue(barra.getYValue().doubleValue() >= 0, "Barra negativa en " + barra.getXValue());
            mes = mes.plusMonths(1);
        }
    }

    /**
     * La barra del mes actual coincide con la tarjeta de gastos.
     */
    @Test
    public void testUltimaBarraEsElGastoDelMes() {
        List<XYChart.Data<String, Number>> barras = grafico().getData().get(0).getData();
        double ultima = barras.get(barras.size() - 1).getYValue().doubleValue();
        assertEquals(gastosDelMes(), ultima, 0.01);
    }

    /**
     * Sin filtros se ven todos los movimientos: una fila por compra, una por día con ventas
     * cobradas y una por movimiento.
     */
    @Test
    public void testSinFiltrosSeVenTodos() {
        int total = totalEsperado();
        assertEquals("Mostrando " + total + " de " + total + " movimientos", texto("#lblContador"));
        assertEquals(total, filas().size());
    }

    /**
     * Cada fila tiene fecha, concepto, categoría, tipo e importe.
     */
    @Test
    public void testCadaFilaTieneSusCincoColumnas() {
        assumeTrue(!filas().isEmpty(), "No hay movimientos en la BD");
        for (HBox fila : filas()) {
            assertEquals(5, fila.getChildren().size());
            assertTrue(celda(fila, 4).endsWith("€"), "El importe no acaba en €");
        }
    }

    /**
     * Filtrar por «Gasto» y por «Ingreso» reparte todas las filas entre los dos tipos.
     */
    @Test
    public void testFiltroDeTipoReparteLasFilas() {
        int total = totalEsperado();

        elegir("#cbTipo", "Gasto");
        int gastos = filas().size();
        for (HBox fila : filas()) {
            assertTrue(celda(fila, 4).startsWith("−"), "Con «Gasto» solo deben salir gastos");
        }

        elegir("#cbTipo", "Ingreso");
        int ingresos = filas().size();

        assertEquals(total, gastos + ingresos);
    }

    /**
     * Filtrar por la categoría «Compras» deja tantas filas como compras hay.
     */
    @Test
    public void testFiltroDeCategoriaCompras() {
        int compras = servicio.listarCompras().size();
        assumeTrue(compras > 0, "No hay compras en la BD");

        elegir("#cbCategoria", "Compras");

        assertEquals(compras, filas().size());
        for (HBox fila : filas()) {
            assertEquals("Compras", celda(fila, 2));
        }
    }

    /**
     * Un concepto que no existe deja la tabla vacía con su mensaje.
     */
    @Test
    public void testConceptoQueNoExisteNoDejaFilas() {
        activarVentana();
        clickOn("#txtConcepto").write("zzzz");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(filas().isEmpty());
        assertFalse(lookup(hasText("No hay movimientos con estos filtros.")).queryAll().isEmpty());
    }

    /**
     * Con un rango de fechas solo quedan filas de esas fechas.
     */
    @Test
    public void testFiltroDeFechas() {
        final LocalDate desde = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        final LocalDate hasta = desde.plusDays(14);
        interact(new Runnable() {
            @Override
            public void run() {
                lookup("#dpDesde").queryAs(DatePicker.class).setValue(desde);
                lookup("#dpHasta").queryAs(DatePicker.class).setValue(hasta);
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        for (HBox fila : filas()) {
            String[] partes = celda(fila, 0).split("/");
            LocalDate dia = LocalDate.of(desde.getYear(), Integer.parseInt(partes[1]), Integer.parseInt(partes[0]));
            assertFalse(dia.isBefore(desde) || dia.isAfter(hasta), "Fila fuera del rango: " + dia);
        }
    }

    /**
     * «Limpiar filtros» vacía todos los filtros y vuelve a enseñar todas las filas.
     */
    @Test
    public void testLimpiarFiltros() {
        int total = totalEsperado();
        elegir("#cbTipo", "Gasto");
        clickOn("#txtConcepto").write("zzzz");

        clickOn("#btnLimpiarFiltros");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(total, filas().size());
        assertNull(combo("#cbTipo").getValue());
        assertEquals("", lookup("#txtConcepto").queryAs(TextField.class).getText());
    }

    /**
     * El botón «Registrar movimiento» abre el diálogo.
     */
    @Test
    public void testRegistrarMovimientoAbreElDialogo() {
        activarVentana();
        int ventanas = listWindows().size();

        clickOn("#btnRegistrarMovimiento");
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(listWindows().size() > ventanas, "No se abre ninguna ventana");
        assertNotNull(from(rootDialogo()).lookup("#txtImporte").query(), "No es el diálogo de movimiento");
    }

    /**
     * Al guardar un movimiento desde el diálogo, la pantalla se recarga y tiene una fila más.
     * Al acabar, {@link #tearDown()} borra el movimiento.
     */
    @Test
    public void testGuardarDesdeElDialogoActualizaLaPantalla() {
        int antes = filas().size();
        activarVentana();
        clickOn("#btnRegistrarMovimiento");
        WaitForAsyncUtils.waitForFxEvents();
        Parent dialogo = rootDialogo();

        clickOn(from(dialogo).lookup("#txtConcepto").queryAs(TextField.class)).write("TEST-" + System.currentTimeMillis());
        clickOn(from(dialogo).lookup("#cbCategoria").queryAs(ComboBox.class).getEditor()).write("Otros");
        clickOn(from(dialogo).lookup("#txtImporte").queryAs(TextField.class)).write("5");
        clickOn(from(dialogo).lookup("#btnGuardar").queryButton());
        WaitForAsyncUtils.waitForFxEvents();
        cerrarAvisos();

        assertEquals(antes + 1, filas().size(), "La pantalla no se ha actualizado");
    }

    /**
     * Cierra el diálogo si se ha quedado abierto, borra los movimientos de prueba, oculta la
     * ventana y suelta el teclado y el ratón.
     *
     * @throws TimeoutException si TestFX no consigue ocultar la ventana
     */
    @AfterEach
    public void tearDown() throws TimeoutException {
        cerrarVentanasExtra();
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
     * Deja preparado el cierre de los {@code Alert} que la pantalla saca mientras se carga. El
     * aviso usa {@code showAndWait()}, que procesa las tareas pendientes de JavaFX mientras
     * espera: así esta tarea lo cierra en cuanto aparece.
     *
     * @param intentos cuántas veces más se vuelve a comprobar, por si hay varios avisos
     */
    private void cerrarAvisosAlCargar(final int intentos) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Window ventana : new ArrayList<>(Window.getWindows())) {
                    if (ventana != stagePrincipal && ventana.getScene() != null
                            && ventana.getScene().getRoot().lookup(".dialog-pane") != null) {
                        ventana.hide();
                    }
                }
                if (intentos > 0) {
                    cerrarAvisosAlCargar(intentos - 1);
                }
            }
        });
    }

    /**
     * Cierra los {@code Alert} que estén abiertos, por ejemplo el aviso de las ventas que sale
     * al recargar la pantalla, y espera a que la pantalla termine de recargarse.
     */
    private void cerrarAvisos() {
        for (int i = 0; i < 5; i++) {
            interact(new Runnable() {
                @Override
                public void run() {
                    for (Window ventana : new ArrayList<>(Window.getWindows())) {
                        if (ventana != stagePrincipal && ventana.getScene() != null
                                && ventana.getScene().getRoot().lookup(".dialog-pane") != null) {
                            ventana.hide();
                        }
                    }
                }
            });
            WaitForAsyncUtils.waitForFxEvents();
        }
    }

    /**
     * Calcula los gastos del mes actual igual que la pantalla: compras más movimientos de gasto.
     *
     * @return los gastos del mes
     */
    private double gastosDelMes() {
        YearMonth actual = YearMonth.now();
        double suma = 0;
        for (Compra c : servicio.listarCompras()) {
            if (YearMonth.from(LocalDate.parse(c.getFecha())).equals(actual)) {
                suma += c.getTotal();
            }
        }
        for (Movimiento m : servicio.listarMovimientos()) {
            if (!m.esIngreso() && YearMonth.from(LocalDate.parse(m.getFecha())).equals(actual)) {
                suma += m.getImporte();
            }
        }
        return suma;
    }

    /**
     * Calcula los ingresos del mes actual igual que la pantalla: ventas cobradas más movimientos
     * de ingreso. Si las ventas no se pueden leer, cuentan como 0, como en la pantalla.
     *
     * @return los ingresos del mes
     */
    private double ingresosDelMes() {
        YearMonth actual = YearMonth.now();
        double suma = 0;
        for (VentaPedido p : ventas()) {
            if (p.getEstado() == EstadoPedido.COBRADO && YearMonth.from(LocalDate.parse(p.getFecha())).equals(actual)) {
                suma += p.getTotal();
            }
        }
        for (Movimiento m : servicio.listarMovimientos()) {
            if (m.esIngreso() && YearMonth.from(LocalDate.parse(m.getFecha())).equals(actual)) {
                suma += m.getImporte();
            }
        }
        return suma;
    }

    /**
     * Calcula cuántas filas debe tener la tabla sin filtros: una por compra, una por día con
     * ventas cobradas y una por movimiento.
     *
     * @return el número de filas
     */
    private int totalEsperado() {
        Set<String> diasConVentas = new HashSet<>();
        for (VentaPedido p : ventas()) {
            if (p.getEstado() == EstadoPedido.COBRADO) {
                diasConVentas.add(p.getFecha());
            }
        }
        return servicio.listarCompras().size() + diasConVentas.size() + servicio.listarMovimientos().size();
    }

    /**
     * Lee las ventas; si no se pueden leer devuelve una lista vacía, como hace la pantalla.
     *
     * @return las ventas, o una lista vacía
     */
    private List<VentaPedido> ventas() {
        try {
            return servicio.listarVentas();
        } catch (DaoException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Devuelve el nombre corto de un mes igual que lo escribe el gráfico.
     *
     * @param mes mes
     * @return el nombre, por ejemplo {@code "nov"}
     */
    private String nombreMes(YearMonth mes) {
        return mes.getMonth().getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es")).replace(".", "");
    }

    /**
     * Da formato a un importe igual que las tarjetas, por ejemplo {@code 5365,00 €}.
     *
     * @param importe importe
     * @return el texto con dos decimales y el símbolo del euro
     */
    private String euros(double importe) {
        return String.format("%.2f €", importe);
    }

    /**
     * Devuelve el gráfico de gastos.
     *
     * @return el gráfico
     */
    @SuppressWarnings("unchecked")
    private BarChart<String, Number> grafico() {
        return lookup("#graficoGastos").queryAs(BarChart.class);
    }

    /**
     * Devuelve uno de los ComboBox de los filtros.
     *
     * @param id selector del ComboBox
     * @return el ComboBox
     */
    @SuppressWarnings("unchecked")
    private ComboBox<String> combo(String id) {
        return lookup(id).queryAs(ComboBox.class);
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

    /**
     * Devuelve las filas de la tabla, sin el mensaje de «no hay movimientos».
     *
     * @return las filas
     */
    private List<HBox> filas() {
        List<HBox> filas = new ArrayList<>();
        for (Node nodo : lookup("#contenedorMovimientos").queryAs(VBox.class).getChildren()) {
            if (nodo instanceof HBox) {
                filas.add((HBox) nodo);
            }
        }
        return filas;
    }

    /**
     * Devuelve el texto de una celda de una fila. La columna del tipo es una etiqueta dentro de
     * otra caja, por eso se busca el primer Label.
     *
     * @param fila fila de la tabla
     * @param columna posición de la celda
     * @return su texto
     */
    private String celda(HBox fila, int columna) {
        Node nodo = fila.getChildren().get(columna);
        if (nodo instanceof HBox) {
            nodo = ((HBox) nodo).getChildren().get(0);
        }
        return ((Label) nodo).getText();
    }

    /**
     * Abre un ComboBox de los filtros y hace clic en la opción con ese texto.
     *
     * @param id selector del ComboBox
     * @param texto texto de la opción
     */
    private void elegir(String id, String texto) {
        activarVentana();
        clickOn(id);
        WaitForAsyncUtils.waitForFxEvents();
        for (Node nodo : lookup(".list-cell").queryAll()) {
            if (nodo instanceof ListCell && nodo.getScene().getWindow() != stagePrincipal
                    && texto.equals(((ListCell<?>) nodo).getText())) {
                clickOn(nodo);
                WaitForAsyncUtils.waitForFxEvents();
                return;
            }
        }
        fail("No se encuentra la opción «" + texto + "» en " + id);
    }

    /**
     * Hace un clic en el título para activar la ventana. En Windows el primer clic en una
     * ventana sin foco solo la activa y no llega al control.
     */
    private void activarVentana() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn(lookup(hasText("Gestión económica")).queryLabeled());
        WaitForAsyncUtils.waitForFxEvents();
    }

    /**
     * Devuelve el contenido del diálogo que se ha abierto encima de la pantalla.
     *
     * @return la raíz del diálogo
     */
    private Parent rootDialogo() {
        for (Window ventana : listWindows()) {
            if (ventana != stagePrincipal) {
                return ventana.getScene().getRoot();
            }
        }
        fail("No se ha abierto ningún diálogo");
        return null;
    }

    /**
     * Cierra sin guardar las ventanas que se hayan quedado abiertas encima de la pantalla.
     */
    private void cerrarVentanasExtra() {
        interact(new Runnable() {
            @Override
            public void run() {
                for (Window ventana : new ArrayList<>(listWindows())) {
                    if (ventana != stagePrincipal) {
                        ventana.hide();
                    }
                }
            }
        });
    }
}
