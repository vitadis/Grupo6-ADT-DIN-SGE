package controller;

import java.util.function.Consumer;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.LineaPedido;
import model.Producto;
import util.AppLogger;

/**
 * FXML Controller class para cada línea de producto dentro del resumen del
 * pedido.
 *
 * @author Joel
 */
public class ProductoPedidoComponentController {

    // ---- Componentes FXML ----
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

    // ---- Estado y callbacks ----
    private Producto producto;
    private Consumer<Producto> alSumar;
    private Consumer<Producto> alRestar;
    private Consumer<Producto> alQuitar;
    private final AppLogger logger = AppLogger.getInstance();

    /**
     * Configura los datos visuales de la línea de pedido y asigna las acciones
     * de cantidad.
     *
     * @param linea línea de pedido a representar
     * @param alSumar callback ejecutado al incrementar una unidad
     * @param alRestar callback ejecutado al reducir una unidad
     * @param alQuitar callback ejecutado al eliminar la línea completa
     */
    public void setLinea(LineaPedido linea,
            Consumer<Producto> alSumar,
            Consumer<Producto> alRestar,
            Consumer<Producto> alQuitar) {
        if (linea == null || linea.producto() == null) {
            logger.log(Level.WARNING, "Intento de inicializar línea de pedido con datos nulos.");
            return;
        }

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

    // ---- Métodos FXML ----
    /**
     * Notifica al controlador de caja que debe sumarse una unidad del producto.
     */
    @FXML
    private void sumar() {
        if (alSumar == null || producto == null) {
            logger.log(Level.WARNING, "Intento de sumar unidad sin producto o callback configurado.");
            return;
        }
        alSumar.accept(producto);
    }

    /**
     * Notifica al controlador de caja que debe restarse una unidad del
     * producto.
     */
    @FXML
    private void restar() {
        if (alRestar == null || producto == null) {
            logger.log(Level.WARNING, "Intento de restar unidad sin producto o callback configurado.");
            return;
        }
        alRestar.accept(producto);
    }

    /**
     * Notifica al controlador de caja que debe eliminarse por completo esta
     * línea del pedido.
     */
    @FXML
    private void quitar() {
        if (alQuitar == null || producto == null) {
            logger.log(Level.WARNING, "Intento de quitar línea sin producto o callback configurado.");
            return;
        }
        alQuitar.accept(producto);
    }
}
