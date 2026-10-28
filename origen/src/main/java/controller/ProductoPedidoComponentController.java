package controller;

import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.LineaPedido;
import model.Producto;

/**
 * Controller de una línea del pedido (ProductoPedidoComponent.fxml).
 *
 * @author Joel
 */
public class ProductoPedidoComponentController {

    @FXML
    private Label lblNombre;
    @FXML
    private Label lblPrecioUnidad;
    @FXML
    private Label lblCantidad;
    @FXML
    private Label lblSubtotal;
    @FXML
    private Button btnMas;

    private Producto producto;
    private Consumer<Producto> alSumar;
    private Consumer<Producto> alRestar;
    private Consumer<Producto> alQuitar;

    public void setLinea(LineaPedido linea,
            Consumer<Producto> alSumar,
            Consumer<Producto> alRestar,
            Consumer<Producto> alQuitar) {
        this.producto = linea.producto();
        this.alSumar = alSumar;
        this.alRestar = alRestar;
        this.alQuitar = alQuitar;

        lblNombre.setText(producto.getNombre());
        lblPrecioUnidad.setText(String.format("%.2f € / ud.", linea.precioUnidad()));
        lblCantidad.setText(String.valueOf(linea.getCantidad()));
        lblSubtotal.setText(String.format("%.2f €", linea.subtotalConIva()));
        btnMas.setDisable(!linea.puedeIncrementar());
    }

    @FXML
    private void sumar() {
        if (alSumar != null) {
            alSumar.accept(producto);
        }
    }

    // Con cantidad 1, restar elimina la línea (lo gestiona VentaPedido)
    @FXML
    private void restar() {
        if (alRestar != null) {
            alRestar.accept(producto);
        }
    }

    @FXML
    private void quitar() {
        if (alQuitar != null) {
            alQuitar.accept(producto);
        }
    }
}
