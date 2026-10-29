package controller;

import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.MetodoPago;
import model.VentaPedido;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class ConfirmarPagoDialogController {

    @FXML
    private Label lblTotal;
    @FXML
    private ComboBox<MetodoPago> cmbMetodoPago;

    private VentaPedido venta;
    private Consumer<VentaPedido> alPagar;
    private MetodoPago metodoPagado;

    public ConfirmarPagoDialogController() {

    }

    @FXML
    private void initialize() {
        cmbMetodoPago.setItems(FXCollections.observableArrayList(MetodoPago.values()));
        cmbMetodoPago.getSelectionModel().selectFirst();
    }

    public void setPedido(VentaPedido venta) {
        this.venta = venta;
        // Total con IVA, que es lo que paga el cliente
        lblTotal.setText(String.format("%.2f €", venta.totalConIva()));
    }

    public void setAlPagar(Consumer<VentaPedido> realizarPago) {
        alPagar = realizarPago;
    }

    public MetodoPago getMetodoPagado() {
        return metodoPagado;
    }

    @FXML
    private void onPagar() {
        metodoPagado = cmbMetodoPago.getValue();
        
        // cambio el metodo de pago
        venta.setMetodo_pago(cmbMetodoPago.getValue());

        // De momento solo imprime
        System.out.println(venta);
        alPagar.accept(venta);
        
        System.out.println(venta);
        cerrar();
    }

    @FXML
    private void volver() {
        cerrar();
    }

    private void cerrar() {
        ((Stage) lblTotal.getScene().getWindow()).close();
    }
}
