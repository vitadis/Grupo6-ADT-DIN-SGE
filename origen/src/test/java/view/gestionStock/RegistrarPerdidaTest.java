package view.gestionStock;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Producto;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 *
 * @author Hodei.Torres
 */
public class RegistrarPerdidaTest extends ApplicationTest {

    private Stage ventana;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/gestionStock/registrarPerdida.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
        stage.toFront();
        this.ventana = stage;
    }

    @SuppressWarnings("unchecked")
    private ComboBox<Producto> combo() {
        return lookup("#comboProducto").queryAs(ComboBox.class);
    }

    private String leerYCerrarAlerta() {
        WaitForAsyncUtils.waitForFxEvents();
        DialogPane dialogo = lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(dialogo, "Debería haberse mostrado una alerta");

        String content = dialogo.getContentText() != null ? dialogo.getContentText() : "";
        String header = dialogo.getHeaderText() != null ? dialogo.getHeaderText() : "";
        String textoCompleto = content + " " + header;

        Button btnOk = (Button) dialogo.lookupButton(ButtonType.OK);
        if (btnOk != null) {
            clickOn(btnOk);
        } else {
            clickOn(".dialog-pane .button");
        }

        WaitForAsyncUtils.waitForFxEvents();
        return textoCompleto.trim();
    }

    private void guardarConCantidad(String cantidad) {
        TextField txt = lookup("#txtCantidad").queryAs(TextField.class);
        interact(txt::clear);
        if (!cantidad.isEmpty()) {
            clickOn(txt).write(cantidad);
        }
        clickOn("Guardar");
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void elComboSeCargaConProductos() {
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(combo().getItems().isEmpty(), "El combo debería tener productos");
    }

    @Test
    void ElPrimerProductoVieneSeleccionado() {
        WaitForAsyncUtils.waitForFxEvents();
        assertNotNull(combo().getValue());
    }

    @Test
    void elComboMuestraElStockDisponible() {
        WaitForAsyncUtils.waitForFxEvents();
        Producto p = combo().getValue();
        assertNotNull(p, "Debe haber un producto seleccionado");
        String texto = combo().getConverter().toString(p);
        assertTrue(texto.contains(p.getNombre()));
        assertTrue(texto.contains("(stock: " + p.getStock_actual() + ")"));
    }

    @Test
    void laNotaEmpiezaVacia() {
        TextArea nota = lookup("#txtNota").queryAs(TextArea.class);
        assertTrue(nota.getText() == null || nota.getText().isEmpty());
    }

    @Test
    void unaCantidadVaciaMuestraError() {
        guardarConCantidad("");
        String texto = leerYCerrarAlerta();
        assertTrue(texto.toLowerCase().contains("entero"), "Mensaje inesperado: " + texto);
        assertTrue(ventana.isShowing(), "La ventana no debe cerrarse si hay error");
    }

    @Test
    void unaCantidadConLetrasMuestraError() {
        guardarConCantidad("abc");
        String texto = leerYCerrarAlerta();
        assertTrue(texto.toLowerCase().contains("entero"), "Mensaje inesperado: " + texto);
        assertTrue(ventana.isShowing());
    }

    @Test
    void unaCantidadCeroMuestraAviso() {
        guardarConCantidad("0");
        String texto = leerYCerrarAlerta();
        assertTrue(texto.toLowerCase().contains("mayor que cero"), "Mensaje inesperado: " + texto);
        assertTrue(ventana.isShowing());
    }

    @Test
    void unaCantidadNegativaMuestraAviso() {
        guardarConCantidad("-3");
        String texto = leerYCerrarAlerta();
        assertTrue(texto.toLowerCase().contains("mayor que cero"), "Mensaje inesperado: " + texto);
        assertTrue(ventana.isShowing());
    }

    @Test
    void unaCantidadMayorQueElStockMuestraAviso() {
        int excesiva = combo().getValue().getStock_actual() + 1;
        guardarConCantidad(String.valueOf(excesiva));
        String texto = leerYCerrarAlerta();
        assertTrue(texto.contains("Solo hay"), "Mensaje inesperado: " + texto);
        assertTrue(ventana.isShowing());
    }

    @Test
    void cancelarCierraLaVentana() {
        clickOn("Cancelar");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(ventana.isShowing());
    }
}