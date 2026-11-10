package controller;

import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import model.FilaStock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Test de la pantalla Gestión stock. Es un test de integración: usa la base
 * de datos real (mongodb://localhost:27017, base "origen"), así que Mongo debe
 * estar arrancado y con los productos de prueba cargados.
 *
 * @author Hodei.Torres
 */
public class GestionStockTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/gestionStock/gestionStock.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        stage.toFront();
    }

    @SuppressWarnings("unchecked")
    private TableView<FilaStock> tabla() {
        return lookup("#tablaStock").queryAs(TableView.class);
    }

    private List<FilaStock> filas() {
        WaitForAsyncUtils.waitForFxEvents();
        return tabla().getItems();
    }

    @Test
    void laTablaSeCargaConProductos() {
        assertNotNull(tabla());
        assertFalse(filas().isEmpty(), "La tabla debería tener productos");
    }

    @Test
    void elContadorDeProductosCoincideConLaTabla() {
        Label cantProduc = lookup("#cantProduc").queryAs(Label.class);
        assertEquals(String.valueOf(filas().size()), cantProduc.getText());
    }

    @Test
    void lasTarjetasDeResumenTienenValor() {
        assertTrue(lookup("#precioStock").queryAs(Label.class).getText().endsWith("€"));
        assertNotNull(lookup("#cantAgotados").queryAs(Label.class).getText());
        assertNotNull(lookup("#cantStockBajo").queryAs(Label.class).getText());
        assertNotNull(lookup("#cantCaducar").queryAs(Label.class).getText());
    }

    @Test
    void losFiltrosEmpiezanEnTodos() {
        ComboBox<?> categoria = lookup("#categoriaDisplay").queryAs(ComboBox.class);
        ComboBox<?> estado = lookup("#estadoDisplay").queryAs(ComboBox.class);
        assertEquals("Todas", categoria.getValue());
        assertEquals("Todos", estado.getValue());
    }

    @Test
    void elBuscadorFiltraPorNombre() {
        int total = filas().size();

        clickOn("#txtBuscar").write("coca");
        List<FilaStock> resultado = filas();

        assertFalse(resultado.isEmpty(), "Debería haber productos con 'coca'");
        assertTrue(resultado.size() <= total);
        for (FilaStock f : resultado) {
            assertTrue(f.getNombre().toLowerCase().contains("coca"),
                    "Fila que no coincide: " + f.getNombre());
        }
    }

    @Test
    void unBuscadorSinCoincidenciasDejaLaTablaVacia() {
        clickOn("#txtBuscar").write("zzzz_no_existe");
        assertTrue(filas().isEmpty());
    }

    @Test
    void alBorrarElBuscadorVuelvenTodosLosProductos() {
        int total = filas().size();

        TextField buscar = lookup("#txtBuscar").queryAs(TextField.class);
        clickOn(buscar).write("coca");
        interact(buscar::clear);

        assertEquals(total, filas().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void elFiltroDeCategoriaSoloMuestraEsaCategoria() {
        ComboBox<String> comboCategoria = lookup("#categoriaDisplay").queryAs(ComboBox.class);
        
        // Seleccionamos "Bebidas" directamente en el ComboBox dentro del hilo de JavaFX
        interact(() -> comboCategoria.getSelectionModel().select("Bebidas"));
        WaitForAsyncUtils.waitForFxEvents();

        List<FilaStock> resultado = filas();

        assertFalse(resultado.isEmpty(), "Debería haber bebidas");
        for (FilaStock f : resultado) {
            assertEquals("Bebidas", f.getCategoria().getEtiqueta());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void elFiltroDeEstadoAgotadoSoloMuestraAgotados() {
        ComboBox<String> comboEstado = lookup("#estadoDisplay").queryAs(ComboBox.class);
        
        // Igual para el combo de estado: evitamos que clickOn("Agotado") pinche en la tabla
        interact(() -> comboEstado.getSelectionModel().select("Agotado"));
        WaitForAsyncUtils.waitForFxEvents();

        for (FilaStock f : filas()) {
            assertEquals("Agotado", f.getEstado());
        }
    }

    @Test
    void soloAlertasMuestraAgotadosOStockBajo() {
        ToggleButton soloAlertas = lookup("#soloAlertas").queryAs(ToggleButton.class);
        clickOn(soloAlertas);
        assertTrue(soloAlertas.isSelected());

        for (FilaStock f : filas()) {
            assertTrue(f.getEstado().equals("Agotado") || f.getEstado().equals("Stock bajo"),
                    "Estado inesperado: " + f.getEstado());
        }
    }

    @Test
    void elBotonRegistrarPerdidaAbreSuVentana() {
        clickOn("Registrar pérdida");
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(lookup("#comboProducto").tryQuery().orElse(null),
                "Debería abrirse la ventana de registrar pérdida");

        clickOn("Cancelar");
    }

    @Test
    void elBotonAnadirAbreSuVentana() {
        clickOn("Añadir stock");
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(lookup("#chkNuevo").tryQuery().orElse(null),
                "Debería abrirse la ventana de añadir producto");

        clickOn("Cancelar");
    }
}