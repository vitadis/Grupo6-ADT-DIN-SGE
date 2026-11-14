package controller;

import java.net.URL;
import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Producto;

public class ProductoComponentController {

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

    private Producto producto;
    private Consumer<Producto> alAnadir;

    /**
     * Configura la tarjeta con los datos del producto.
     */
    public void setProducto(
            Producto producto,
            Consumer<Producto> alAnadir) {

        if (producto == null) {
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

    /**
     * Carga la imagen desde los recursos de la aplicación.
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
        }
    }

    /**
     * Actualiza el estado visual del stock.
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

    /**
     * Solicita al controlador de caja que añada el producto.
     */
    @FXML
    private void anadir() {

        if (producto == null || alAnadir == null) {
            return;
        }

        if (producto.getStock_actual() <= 0) {
            actualizarStock();
            return;
        }

        alAnadir.accept(producto);
    }
}
