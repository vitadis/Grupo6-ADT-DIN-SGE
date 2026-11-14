package controller;

import java.util.function.Consumer;
import java.util.logging.Level;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.MetodoPago;
import model.VentaPedido;
import util.AppLogger;

/**
 * FXML Controller class para el diálogo de confirmación de pago.
 *
 * @author Joel
 */
public class ConfirmarPagoDialogController {

    // ---- Componentes FXML ----
    @FXML
    private Label lblTotal;
    @FXML
    private ComboBox<MetodoPago> cmbMetodoPago;

    // ---- Estado y callbacks ----
    private VentaPedido venta;
    private Consumer<VentaPedido> alPagar;
    private MetodoPago metodoPagado;
    private final AppLogger logger = AppLogger.getInstance();

    public ConfirmarPagoDialogController() {
    }

    /**
     * Carga los métodos de pago disponibles en el selector y marca el primero
     * por defecto.
     */
    @FXML
    private void initialize() {
        cmbMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));
        cmbMetodoPago.getSelectionModel().selectFirst();
    }

    /**
     * Asigna el pedido a cobrar y muestra en pantalla su importe total con IVA.
     *
     * @param venta pedido pendiente de cobro
     */
    public void setPedido(VentaPedido venta) {
        this.venta = venta;
        if (venta != null) {
            lblTotal.setText(String.format("%.2f €", venta.totalConIva()));
        }
    }

    /**
     * Define la acción a ejecutar al confirmar el pago del pedido.
     *
     * @param realizarPago callback que procesa el pedido pagado
     */
    public void setAlPagar(Consumer<VentaPedido> realizarPago) {
        this.alPagar = realizarPago;
    }

    /**
     * Devuelve el método de pago seleccionado al confirmar el cobro.
     *
     * @return método de pago confirmado
     */
    public MetodoPago getMetodoPagado() {
        return metodoPagado;
    }

    // ---- Métodos FXML ----
    /**
     * Asigna el método de pago elegido al pedido, ejecuta el callback de cobro
     * y cierra el diálogo.
     */
    @FXML
    private void onPagar() {
        metodoPagado = cmbMetodoPago.getValue();

        if (venta == null || metodoPagado == null) {
            logger.log(Level.WARNING, "Intento de confirmar pago sin pedido o sin método de pago seleccionado.");
            return;
        }

        venta.setMetodo_pago(metodoPagado);
        logger.info("Pago confirmado con método: " + metodoPagado + " | Total: " + String.format("%.2f €", venta.totalConIva()));

        if (alPagar != null) {
            alPagar.accept(venta);
        }
        cerrar();
    }

    /**
     * Cancela la operación de cobro y cierra el diálogo.
     */
    @FXML
    private void volver() {
        logger.info("Confirmación de pago cancelada por el usuario.");
        cerrar();
    }

    /**
     * Cierra la ventana modal actual.
     */
    private void cerrar() {
        ((Stage) lblTotal.getScene().getWindow()).close();
    }
}
