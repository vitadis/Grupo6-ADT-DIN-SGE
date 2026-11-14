package controller;

import java.net.URL;
import java.util.function.Consumer;
import java.util.logging.Level;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Producto;
import util.AppLogger;

/**
 * FXML Controller class para la tarjeta visual de un producto en caja.
 *
 * @author Joel
 */
public class ProductoComponentController {

    // ---- Componentes FXML ----
    @FXML
    private ImageView imgProducto;
    @FXML
    private Label lblStock;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblCategoria;
    @FXML
    private Label lblPrecio;
    @FXML
    private Button btnAnadir;

    // ---- Estado y callbacks ----
    private Producto producto;
    private Consumer<Producto> alAnadir;
    private final AppLogger logger = AppLogger.getInstance();

    /**
     * Configura la tarjeta con los datos del producto, su imagen, estado de
     * stock y acción al añadir.
     *
     * @param producto producto a mostrar en la tarjeta
     * @param alAnadir callback ejecutado al pulsar el botón de añadir
     */
    public void setProducto(
            Producto producto,
            Consumer<Producto> alAnadir) {

        if (producto == null) {
            logger.log(Level.WARNING, "Intento de inicializar tarjeta con producto nulo.");
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo."
            );
        }

        this.producto = producto;
        this.alAnadir = alAnadir;

        lblNombre.setText(producto.getNombre());

        lblCategoria.setText(
                producto.getCategoria().getEtiqueta()
        );

        lblPrecio.setText(
                String.format("%.2f €", producto.productoIvaAplicado())
        );

        cargarImagen(producto.getImagen());
        actualizarStock();
    }

    // ---- Métodos auxiliares ----
    /**
     * Carga la imagen del producto desde los recursos de la aplicación si la
     * ruta es válida.
     *
     * @param rutaImagen ruta interna del recurso de imagen
     */
    private void cargarImagen(String rutaImagen) {

        imgProducto.setImage(null);

        if (rutaImagen == null || rutaImagen.isBlank()) {
            return;
        }

        URL url = getClass().getResource(rutaImagen);

        if (url != null) {
            imgProducto.setImage(
                    new Image(url.toExternalForm(), true)
            );
        } else {
            logger.log(Level.WARNING, "No se encontró la imagen del producto: " + rutaImagen);
        }
    }

    /**
     * Actualiza la etiqueta, el estilo CSS y la disponibilidad del botón según
     * el stock actual.
     */
    private void actualizarStock() {

        if (producto.getStock_actual() <= 0) {

            lblStock.setText("Agotado");
            lblStock.getStyleClass().setAll("estado-agotado");
            btnAnadir.setDisable(true);

        } else if (producto.tieneStockBajo()) {

            lblStock.setText("Stock bajo");
            lblStock.getStyleClass().setAll("estado-bajo");
            btnAnadir.setDisable(false);

        } else {

            lblStock.setText("En stock");
            lblStock.getStyleClass().setAll("estado-ok");
            btnAnadir.setDisable(false);
        }
    }

    // ---- Métodos FXML ----
    /**
     * Comprueba disponibilidad de stock y notifica al controlador de caja para
     * añadir el producto al pedido.
     */
    @FXML
    private void anadir() {

        if (producto == null || alAnadir == null) {
            logger.log(Level.WARNING, "Intento de añadir producto sin producto o callback configurado.");
            return;
        }

        if (producto.getStock_actual() <= 0) {
            logger.log(Level.WARNING, "Intento de añadir producto sin stock: " + producto.getNombre());
            actualizarStock();
            return;
        }

        alAnadir.accept(producto);
    }
}
