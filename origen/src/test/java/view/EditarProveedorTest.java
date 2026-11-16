package view;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import controller.EditarProveedorController;
import dao.ProductoDao;
import dao.implement.ComprasDaoImpl;
import dao.implement.ProductoDaoImp;
import dao.implement.ProveedorDaoImpl;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.Proveedor;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import services.ComprasService;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests del diálogo de proveedor ({@code editarProveedor.fxml}), abierto solo, en modo alta y en
 * modo edición.
 *
 * Usa la base de datos {@code origen} (MongoDB tiene que estar arrancado). Los proveedores que da
 * de alta llevan NIF {@code TEST...} y se borran al acabar cada test; el que se edita vuelve a
 * quedar como estaba. Así la base de datos no cambia.
 *
 * @author anazk
 */
public class EditarProveedorTest extends ApplicationTest {

    /**
     * Dirección de MongoDB, para borrar y restaurar lo que tocan los tests.
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
     * Controller del diálogo, para pasarlo a modo edición como hace {@code ComprasController}.
     */
    private EditarProveedorController controlador;
    /**
     * Service con los DAOs de MongoDB; también se usa para comprobar lo guardado.
     */
    private ComprasService servicio;
    /**
     * Documento del proveedor tal cual estaba antes de editarlo, para dejarlo idéntico al acabar.
     */
    private Document proveedorOriginal;

    /**
     * Constructor vacío. Lo usa JUnit para crear el test.
     */
    public EditarProveedorTest() {
    }

    /**
     * Abre el diálogo en modo alta y le pasa el service con los DAOs de MongoDB.
     *
     * @param stage ventana que da TestFX
     * @throws IOException si no se encuentra el FXML
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        servicio = new ComprasService(ComprasDaoImpl.getInstance(), ProveedorDaoImpl.getInstance(),
                (ProductoDao) ProductoDaoImp.getInstance());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/compra/editarProveedor.fxml"));
        Parent root = loader.load();
        controlador = loader.getController();
        controlador.setComprasService(servicio);

        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * En modo alta: título de alta, campos vacíos, activo marcado y ningún día marcado.
     */
    @Test
    public void testEstadoInicialEnModoAlta() {
        assertEquals("NUEVO PROVEEDOR", texto("#lblTitulo"));
        assertEquals("", campo("#txtNombre").getText());
        assertEquals("", campo("#txtNifCif").getText());
        assertTrue(check("#chkActivo").isSelected(), "Un proveedor nuevo empieza activo");
        for (String dia : new String[]{"#chkLunes", "#chkMartes", "#chkMiercoles", "#chkJueves",
            "#chkViernes", "#chkSabado", "#chkDomingo"}) {
            assertFalse(check(dia).isSelected(), dia + " no debería venir marcado");
        }
    }

    /**
     * Guardar sin datos da error, no guarda nada y la ventana sigue abierta.
     */
    @Test
    public void testGuardarVacioDaError() {
        int antes = servicio.listarProveedores().size();

        clickOn("#btnGuardar");

        assertEquals("El nombre del proveedor es obligatorio.", texto("#lblError"));
        assertEquals(antes, servicio.listarProveedores().size());
        assertTrue(stage.isShowing(), "Con error la ventana no se cierra");
    }

    /**
     * Guardar sin NIF/CIF da error.
     */
    @Test
    public void testSinNifDaError() {
        clickOn("#txtNombre").write("Distribuciones Prueba");

        clickOn("#btnGuardar");

        assertEquals("El NIF/CIF es obligatorio.", texto("#lblError"));
    }

    /**
     * Un plazo de entrega con letras da error.
     */
    @Test
    public void testPlazoConLetrasDaError() {
        rellenarObligatorios(nifDePrueba());
        clickOn("#txtPlazoEntrega").write("abc");

        clickOn("#btnGuardar");

        assertEquals("El plazo de entrega debe ser un número entero.", texto("#lblError"));
    }

    /**
     * Un plazo de entrega negativo da el error de {@code Proveedor.validar()}.
     */
    @Test
    public void testPlazoNegativoDaError() {
        rellenarObligatorios(nifDePrueba());
        clickOn("#txtPlazoEntrega").write("-2");

        clickOn("#btnGuardar");

        assertEquals("El plazo de entrega no puede ser negativo.", texto("#lblError"));
    }

    /**
     * Un correo sin {@code @} da el error de {@code Contacto.validar()}.
     */
    @Test
    public void testEmailSinArrobaDaError() {
        rellenarObligatorios(nifDePrueba());
        clickOn("#txtEmail").write("pedidos.prueba.es");

        clickOn("#btnGuardar");

        assertEquals("El correo electrónico no es válido.", texto("#lblError"));
    }

    /**
     * Un teléfono con letras da el error de {@code Contacto.validar()}.
     */
    @Test
    public void testTelefonoConLetrasDaError() {
        rellenarObligatorios(nifDePrueba());
        clickOn("#txtTelefono").write("944 abc");

        clickOn("#btnGuardar");

        assertEquals("El teléfono solo puede tener números, espacios y '+'.", texto("#lblError"));
    }

    /**
     * Cancelar cierra la ventana sin dar de alta el proveedor.
     */
    @Test
    public void testCancelarCierraSinGuardar() {
        String nif = nifDePrueba();
        rellenarObligatorios(nif);

        clickOn("#btnCancelar");
        WaitForAsyncUtils.waitForFxEvents();

        assertNull(buscarPorNif(nif), "Cancelar no debe guardar");
        assertFalse(controlador.isGuardado());
        assertFalse(stage.isShowing());
    }

    /**
     * Un alta correcta guarda el proveedor en la base de datos con sus datos y sus días, y
     * cierra la ventana. Al acabar, {@link #tearDown()} lo borra.
     */
    @Test
    public void testAltaCorrecta() {
        String nif = nifDePrueba();
        rellenarObligatorios(nif);
        clickOn("#txtPlazoEntrega").write("3");
        clickOn("#chkLunes");
        clickOn("#chkMiercoles");
        clickOn("#txtPersona").write("Ane");
        clickOn("#txtTelefono").write("+34 944 000 000");
        clickOn("#txtEmail").write("pedidos@prueba.es");

        clickOn("#btnGuardar");
        WaitForAsyncUtils.waitForFxEvents();

        Proveedor p = buscarPorNif(nif);
        assertNotNull(p, "No se ha dado de alta en la BD");
        assertEquals("Distribuciones Prueba", p.getNombre());
        assertEquals(3, p.getPlazo_entrega());
        assertEquals(List.of("lunes", "miercoles"), p.getDias_laborales());
        assertTrue(p.isActivo());
        assertEquals("pedidos@prueba.es", p.getContacto().getEmail());
        assertTrue(controlador.isGuardado());
        assertFalse(stage.isShowing(), "Al guardar se cierra la ventana");
    }

    /**
     * Pulsar Enter guarda, porque «Guardar» es el botón por defecto.
     */
    @Test
    public void testEnterGuardaPorqueEsElBotonPorDefecto() {
        String nif = nifDePrueba();
        rellenarObligatorios(nif);

        type(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(buscarPorNif(nif), "Enter debería guardar");
    }

    /**
     * En modo edición, el título cambia y los campos vienen con los datos del proveedor.
     */
    @Test
    public void testModoEdicionRellenaLosCampos() {
        Proveedor p = primerProveedor();
        editar(p);

        assertEquals("EDITAR PROVEEDOR", texto("#lblTitulo"));
        assertEquals(p.getNombre(), campo("#txtNombre").getText());
        assertEquals(p.getNif_cif(), campo("#txtNifCif").getText());
        assertEquals(String.valueOf(p.getPlazo_entrega()), campo("#txtPlazoEntrega").getText());
        assertEquals(p.isActivo(), check("#chkActivo").isSelected());
    }

    /**
     * Al editar se actualiza el mismo proveedor (no se da de alta otro) y se guarda el cambio.
     * Al acabar, {@link #tearDown()} lo deja como estaba.
     */
    @Test
    public void testEditarActualizaElMismoProveedor() {
        Proveedor p = primerProveedor();
        proveedorOriginal = documentoProveedor(p.getId());
        int antes = servicio.listarProveedores().size();
        String nombreNuevo = p.getNombre() + " TEST";
        editar(p);

        clickOn("#txtNombre");
        push(KeyCode.CONTROL, KeyCode.A);
        write(nombreNuevo);
        clickOn("#btnGuardar");
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(antes, servicio.listarProveedores().size(), "Editar no debe dar de alta otro");
        assertEquals(nombreNuevo, buscarPorId(p.getId()).getNombre(), "No se ha guardado el cambio");
    }

    /**
     * Deja la base de datos como estaba, oculta la ventana y suelta el teclado y el ratón.
     *
     * @throws TimeoutException si TestFX no consigue ocultar la ventana
     */
    @AfterEach
    public void tearDown() throws TimeoutException {
        borrarProveedoresDePrueba();
        restaurarProveedorEditado();
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    /**
     * Borra los proveedores con NIF {@code TEST...}. Se hace con el driver porque el DAO no tiene
     * método para borrar.
     */
    private void borrarProveedoresDePrueba() {
        try (MongoClient cliente = MongoClients.create(CONEXION)) {
            cliente.getDatabase(BASE_DATOS).getCollection("proveedores")
                    .deleteMany(Filters.regex("nif_cif", "^TEST"));
        }
    }

    /**
     * Vuelve a poner el proveedor editado exactamente como estaba, con su documento original.
     */
    private void restaurarProveedorEditado() {
        if (proveedorOriginal == null) {
            return;
        }
        try (MongoClient cliente = MongoClients.create(CONEXION)) {
            cliente.getDatabase(BASE_DATOS).getCollection("proveedores")
                    .replaceOne(Filters.eq("_id", proveedorOriginal.get("_id")), proveedorOriginal);
        }
        proveedorOriginal = null;
    }

    /**
     * Lee el documento de un proveedor tal cual está en MongoDB.
     *
     * @param id identificador del proveedor
     * @return el documento
     */
    private Document documentoProveedor(int id) {
        try (MongoClient cliente = MongoClients.create(CONEXION)) {
            return cliente.getDatabase(BASE_DATOS).getCollection("proveedores")
                    .find(Filters.eq("_id", id)).first();
        }
    }

    /**
     * Devuelve el texto de un Label.
     *
     * @param id selector del Label, por ejemplo {@code "#lblError"}
     * @return su texto
     */
    private String texto(String id) {
        return lookup(id).queryAs(Label.class).getText();
    }

    /**
     * Devuelve un campo de texto del diálogo.
     *
     * @param id selector del campo
     * @return el campo
     */
    private TextField campo(String id) {
        return lookup(id).queryAs(TextField.class);
    }

    /**
     * Devuelve un CheckBox del diálogo.
     *
     * @param id selector del CheckBox
     * @return el CheckBox
     */
    private CheckBox check(String id) {
        return lookup(id).queryAs(CheckBox.class);
    }

    /**
     * Crea un NIF que no existe y que la limpieza reconoce.
     *
     * @return un NIF {@code TEST...}
     */
    private String nifDePrueba() {
        return "TEST" + System.currentTimeMillis();
    }

    /**
     * Escribe el nombre y el NIF, que son los campos obligatorios.
     *
     * @param nif NIF que se escribe
     */
    private void rellenarObligatorios(String nif) {
        clickOn("#txtNombre").write("Distribuciones Prueba");
        clickOn("#txtNifCif").write(nif);
    }

    /**
     * Devuelve el primer proveedor de la base de datos. Si no hay ninguno, el test se salta.
     *
     * @return el proveedor
     */
    private Proveedor primerProveedor() {
        List<Proveedor> todos = servicio.listarProveedores();
        assumeTrue(!todos.isEmpty(), "No hay proveedores en la BD");
        return todos.get(0);
    }

    /**
     * Busca en la base de datos un proveedor por su NIF.
     *
     * @param nif NIF del proveedor
     * @return el proveedor, o {@code null} si no está
     */
    private Proveedor buscarPorNif(String nif) {
        for (Proveedor p : servicio.listarProveedores()) {
            if (nif.equals(p.getNif_cif())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca en la base de datos un proveedor por su identificador.
     *
     * @param id identificador del proveedor
     * @return el proveedor
     */
    private Proveedor buscarPorId(int id) {
        for (Proveedor p : servicio.listarProveedores()) {
            if (p.getId() == id) {
                return p;
            }
        }
        fail("No existe el proveedor " + id);
        return null;
    }

    /**
     * Pasa el diálogo a modo edición, como hace {@code ComprasController} al pulsar «Editar».
     *
     * @param proveedor proveedor que se edita
     */
    private void editar(final Proveedor proveedor) {
        interact(new Runnable() {
            @Override
            public void run() {
                controlador.setProveedor(proveedor);
            }
        });
    }
}
