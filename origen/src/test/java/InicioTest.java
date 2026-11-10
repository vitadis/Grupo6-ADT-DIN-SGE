
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

/**
 *
 * @author Hodei.Torres
 */
public class InicioTest extends ApplicationTest {

    @Start
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/inicio/inicioView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1100, 700);
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        stage.setScene(scene);
        stage.show();
    }

    @Test
    void lasTarjetasSeRellenan() {
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(lookup("#gastoMes").queryAs(Label.class).getText().endsWith("€"));
        assertTrue(lookup("#cantIngreso").queryAs(Label.class).getText().endsWith("€"));
        assertTrue(lookup("#beneficioAprox").queryAs(Label.class).getText().endsWith("€"));
        assertNotNull(lookup("#cantStockBajo").queryAs(Label.class).getText());
    }

    @Test
    void elContadorDeAlertasTieneTexto() {
        Label contador = lookup("#lblContadorAlertas").queryAs(Label.class);
        assertTrue(contador.getText().contains("agotados"));
    }

    @Test
    void elContadorDeComprasTieneTexto() {
        Label contador = lookup("#lblContador").queryAs(Label.class);
        assertTrue(contador.getText().startsWith("Mostrando"));
    }

    @Test
    void laTarjetaDeStockBajoCoincideConElPanelDeAlertas() {
        Label tarjeta = lookup("#cantStockBajo").queryAs(Label.class);
        Label resumen = lookup("#lblContadorAlertas").queryAs(Label.class);

        String[] partes = resumen.getText().split(" ");
        int agotados = Integer.parseInt(partes[0]);
        int bajos = Integer.parseInt(partes[3]);

        assertEquals(String.valueOf(agotados + bajos), tarjeta.getText());
    }

    @Test
    void elBotonHistorialAbreSuVentana() {
        clickOn("#historialVentas");
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(lookup("#tvPedido").tryQuery().orElse(null), "Debería abrirse la ventana del historial de ventas");

        press(javafx.scene.input.KeyCode.ESCAPE).release(javafx.scene.input.KeyCode.ESCAPE);
    }

    @Test
    void elBotonRegistrarPerdidaAbreSuVentana() {
        clickOn("#registrarPerdida");
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(lookup("#comboProducto").tryQuery().orElse(null), "Deberia abrirse la ventana del historial de ventas");

        clickOn("Cancelar");

    }

    @Test
    void elBotonAñadirProductoAbreSuVentana() {
        clickOn("#anadirProducto");
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(lookup("#chkNuevo").tryQuery().orElse(null), "Debería abrirse la ventana de añadir producto");

        clickOn("Cancelar");

    }

    @Test
    void elBotonRegistrarCompraAbreSuVentana() {
        clickOn("#registrarCompra");
        WaitForAsyncUtils.waitForFxEvents();

        assertNotNull(lookup("#contenedorLineas").tryQuery().orElse(null), "Deberia abrirse la ventana del historial de ventas");

        clickOn("#btnCancelar");

    }
}
