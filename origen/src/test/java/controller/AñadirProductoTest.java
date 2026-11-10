package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.CategoriaProducto;
import model.Producto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

/**
 * Test de la ventana Añadir producto. Necesita Mongo arrancado y con
 * productos y proveedores activos. Solo prueba carga, modos y validaciones:
 * ningún test llega a guardar.
 */
public class AñadirProductoTest extends ApplicationTest {

    private Stage ventana;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/gestionStock/añadirProducto.fxml"));
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
    private <T> ComboBox<T> combo(String id) {
        return lookup(id).queryAs(ComboBox.class);
    }

    private TextField campo(String id) {
        return lookup(id).queryAs(TextField.class);
    }

    private void escribir(String id, String texto) {
        clickOn(id);
        push(KeyCode.CONTROL, KeyCode.A);
        if (texto.isEmpty()) {
            type(KeyCode.BACK_SPACE);
        } else {
            write(texto);
        }
    }

    private void elegirPrimero(String id) {
        clickOn(id);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void marcarNuevo() {
        clickOn("#chkNuevo");
        WaitForAsyncUtils.waitForFxEvents();
    }

    private String leerYCerrarAlerta() {
        WaitForAsyncUtils.waitForFxEvents();
        DialogPane dialogo = lookup(".dialog-pane").queryAs(DialogPane.class);
        assertNotNull(dialogo, "Debería haberse mostrado una alerta");
        String texto = dialogo.getContentText();
        type(KeyCode.ENTER);
        WaitForAsyncUtils.waitForFxEvents();
        return texto;
    }

    private void guardarCheck(String fragmento) {
        clickOn("Guardar");
        String texto = leerYCerrarAlerta();
        assertTrue(texto.contains(fragmento),
                "Se esperaba '" + fragmento + "' y salió: " + texto);
        assertTrue(ventana.isShowing(), "La ventana no debe cerrarse si hay error");
    }

    private void rellenarNumerosValidos() {
        escribir("#txtPrecio", "2.5");
        escribir("#txtIva", "21");
        escribir("#txtStockMinimo", "0");
        escribir("#txtCantidad", "0");
    }

    @Test
    void empiezaEnModoProductoExistente() {
        assertFalse(lookup("#chkNuevo").queryAs(CheckBox.class).isSelected());
        assertTrue(lookup("#comboExistente").query().isVisible());
        assertFalse(lookup("#txtProducto").query().isVisible());
        assertFalse(lookup("#panelNuevo").queryAs(GridPane.class).isVisible());
        assertEquals("Cantidad a añadir:", lookup("#lblCantidad").queryAs(Label.class).getText());
    }

    @Test
    void elComboDeProductosSeCargaYNoHayNadaSeleccionado() {
        ComboBox<Producto> existente = combo("#comboExistente");
        assertFalse(existente.getItems().isEmpty(), "Debería haber productos");
        assertEquals(null, existente.getValue());
    }

    @Test
    void losValoresPorDefectoSonLosEsperados() {
        assertEquals("21", campo("#txtIva").getText());
        assertEquals("0", campo("#txtStockMinimo").getText());
    }

    @Test
    void marcarNuevoMuestraElFormularioCompleto() {
        marcarNuevo();

        assertFalse(lookup("#comboExistente").query().isVisible());
        assertTrue(lookup("#txtProducto").query().isVisible());
        assertTrue(lookup("#panelNuevo").query().isVisible());
        assertEquals("Cantidad inicial:", lookup("#lblCantidad").queryAs(Label.class).getText());
    }

    @Test
    void desmarcarNuevoVuelveAlModoExistente() {
        marcarNuevo();
        marcarNuevo();

        assertTrue(lookup("#comboExistente").query().isVisible());
        assertFalse(lookup("#txtProducto").query().isVisible());
        assertFalse(lookup("#panelNuevo").query().isVisible());
        assertEquals("Cantidad a añadir:", lookup("#lblCantidad").queryAs(Label.class).getText());
    }

    @Test
    void elComboDeTipoTieneOpciones() {
        marcarNuevo();
        assertFalse(combo("#comboTipo").getItems().isEmpty());
    }

    @Test
    void elComboDeProveedoresSeCarga() {
        marcarNuevo();
        assertFalse(combo("#comboProveedor").getItems().isEmpty(),
                "Debería haber proveedores activos");
    }

    @Test
    void alElegirUnTipoSoloSalenSusCategorias() {
        marcarNuevo();
        elegirPrimero("#comboTipo");

        ComboBox<CategoriaProducto> categorias = combo("#comboCategoria");
        assertFalse(categorias.getItems().isEmpty(), "Debería haber categorías del tipo elegido");
        Object tipo = combo("#comboTipo").getValue();
        for (CategoriaProducto c : categorias.getItems()) {
            assertEquals(tipo, c.getTipo(), "Categoría de otro tipo: " + c);
        }
    }

    @Test
    void sinProductoSeleccionadoMuestraError() {
        escribir("#txtCantidad", "5");
        guardarCheck("Selecciona un producto");
    }

    @Test
    void cantidadVaciaMuestraError() {
        elegirPrimero("#comboExistente");
        escribir("#txtCantidad", "");
        guardarCheck("obligatorio");
    }

    @Test
    void cantidadConLetrasMuestraError() {
        elegirPrimero("#comboExistente");
        escribir("#txtCantidad", "abc");
        guardarCheck("número entero");
    }

    @Test
    void cantidadNegativaMuestraError() {
        elegirPrimero("#comboExistente");
        escribir("#txtCantidad", "-2");
        guardarCheck("negativo");
    }

    @Test
    void cantidadCeroMuestraError() {
        elegirPrimero("#comboExistente");
        escribir("#txtCantidad", "0");
        guardarCheck("mayor que cero");
    }

    @Test
    void sinFechaDeCaducidadMuestraError() {
        elegirPrimero("#comboExistente");
        escribir("#txtCantidad", "5");
        guardarCheck("fecha de caducidad");
    }

    @Test
    void nuevoSinPrecioMuestraError() {
        marcarNuevo();
        escribir("#txtPrecio", "");
        guardarCheck("precio");
    }

    @Test
    void nuevoConPrecioConLetrasMuestraError() {
        marcarNuevo();
        escribir("#txtPrecio", "abc");
        guardarCheck("número");
    }

    @Test
    void nuevoSinNombreMuestraError() {
        marcarNuevo();
        rellenarNumerosValidos();
        escribir("#txtProducto", "");
        guardarCheck("nombre");
    }

    @Test
    void nuevoConNombreRepetidoMuestraError() {
        ComboBox<Producto> existente = combo("#comboExistente");
        String repetido = existente.getItems().get(0).getNombre();

        marcarNuevo();
        rellenarNumerosValidos();
        escribir("#txtProducto", repetido);
        guardarCheck("Ya existe");
    }

    @Test
    void nuevoSinTipoMuestraError() {
        marcarNuevo();
        rellenarNumerosValidos();
        escribir("#txtProducto", "Producto de prueba test");
        guardarCheck("tipo");
    }

    @Test
    void nuevoSinCategoriaMuestraError() {
        marcarNuevo();
        rellenarNumerosValidos();
        escribir("#txtProducto", "Producto de prueba test");
        elegirPrimero("#comboTipo");
        guardarCheck("categoría");
    }

    @Test
    void nuevoSinProveedorMuestraError() {
        marcarNuevo();
        rellenarNumerosValidos();
        escribir("#txtProducto", "Producto de prueba test");
        elegirPrimero("#comboTipo");
        elegirPrimero("#comboCategoria");
        guardarCheck("proveedor");
    }

    @Test
    void nuevoConIvaFueraDeRangoMuestraError() {
        marcarNuevo();
        rellenarNumerosValidos();
        escribir("#txtIva", "150");
        escribir("#txtProducto", "Producto de prueba test");
        elegirPrimero("#comboTipo");
        elegirPrimero("#comboCategoria");
        elegirPrimero("#comboProveedor");
        guardarCheck("entre 0 y 100");
    }

    @Test
    void nuevoConStockInicialSinFechaMuestraError() {
        marcarNuevo();
        rellenarNumerosValidos();
        escribir("#txtCantidad", "5");
        escribir("#txtProducto", "Producto de prueba test");
        elegirPrimero("#comboTipo");
        elegirPrimero("#comboCategoria");
        elegirPrimero("#comboProveedor");
        guardarCheck("fecha de caducidad");
    }

    @Test
    void cancelarCierraLaVentana() {
        clickOn("Cancelar");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(ventana.isShowing());
    }
}