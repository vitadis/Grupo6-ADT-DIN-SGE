package controller;

import dao.ComprasDao;
import dao.GestionStockDao;
import dao.ProveedorDao;
import dao.implement.ComprasDaoImpl;
import dao.implement.GestionStockDaoImp;
import dao.implement.ProveedorDaoImpl;
import exceptions.DaoException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Compra;
import model.Producto;
import model.Proveedor;
import util.Alertas;
import util.AppLogger;

/**
 *
 * @author Hodei.Torres
 */
public class InicioController implements Initializable, ActualizarDatos {
        
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yy");
    private static final int MAX_COMPRAS = 8;

    @FXML private GridPane gridInfoStock;
    @FXML private Label cantIngreso;
    @FXML private Label gastoMes;
    @FXML private Label beneficioAprox;
    @FXML private Label cantStockBajo;
    @FXML private VBox contenedorCompras;
    @FXML private Label lblContador;
    @FXML private VBox contenedorAlertas;
    @FXML private Label lblContadorAlertas;
    @FXML private Button registrarCompra;
    @FXML private Button registrarPerdida;
    @FXML private Button historialVentas;
    @FXML private Button anadirProducto;

    private final ComprasDao comprasDao = ComprasDaoImpl.getInstance();
    private final GestionStockDao stockDao = GestionStockDaoImp.getInstance();
    private final ProveedorDao proveedorDao = ProveedorDaoImpl.getInstance();
    private final AppLogger logger = AppLogger.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarBotones();
        cargarDatos();
    }
    
    @Override
    public void actualizarDatos() {
        cargarDatos();
    }

    private void configurarBotones() {
        registrarPerdida.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                abrir("/view/gestionStock/registrarPerdida.fxml", "Registrar pérdida");
            }
        });
        anadirProducto.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                abrir("/view/gestionStock/añadirProducto.fxml", "Añadir producto");
            }
        });
        registrarCompra.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                abrir("/view/compra/registrarCompra.fxml", "Registrar compra");
            }
        });
    }

    private void abrir(String ruta, String titulo) {
        try {
            InstanciarEscena.createNewStage(ruta, titulo, null);
            cargarDatos();   
        } catch (Exception e) {
            e.printStackTrace();
            Alertas.mostrarError("No se pudo abrir la ventana: " + e.getMessage());
            logger.log(Level.SEVERE, "No se pudo abrir la ventana " + ruta, e);

        }
    }

    private void cargarDatos() {
        try {
            cargarAlertas();
            cargarCompras();
        } catch (DaoException e) {
            logger.log(Level.SEVERE, "Error al cargar los datos de Inicio", e);
            Alertas.mostrarError(e.getMessage());
        }
        cantIngreso.setText("-");
        beneficioAprox.setText("-");
    }

    private void cargarAlertas() throws DaoException {
        contenedorAlertas.getChildren().clear();

        List<Producto> productos = stockDao.listarProductos();
        int agotados = 0;
        int bajos = 0;

        for (Producto p : productos) {
            if (p.getStock_actual() == 0) {
                contenedorAlertas.getChildren().add(crearFilaAlerta(p, true));
                agotados++;
            }
        }
        for (Producto p : productos) {
            if (p.tieneStockBajo()) {
                contenedorAlertas.getChildren().add(crearFilaAlerta(p, false));
                bajos++;
            }
        }

        if (agotados + bajos == 0) {
            Label vacio = new Label("No hay alertas de stock.");
            vacio.getStyleClass().add("inicio-vacio");
            contenedorAlertas.getChildren().add(vacio);
        }

        cantStockBajo.setText(String.valueOf(agotados + bajos));
        lblContadorAlertas.setText(agotados + " agotados · " + bajos + " con stock bajo");
    }

    private HBox crearFilaAlerta(Producto p, boolean agotado) {
        Label nombre = new Label(p.getNombre());
        nombre.getStyleClass().add("compra-dato-destacado");

        Label detalle = new Label("Existencia " + p.getStock_actual() + " · mínimo " + p.getStock_minimo());
        detalle.getStyleClass().add("texto-secundario");

        VBox izquierda = new VBox(2, nombre, detalle);

        Label estado = new Label(agotado ? "Agotado" : "Stock bajo");
        estado.getStyleClass().add(agotado ? "estado-error" : "estado-aviso");

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        HBox fila = new HBox(8, izquierda, espacio, estado);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.getStyleClass().add("compra-fila");
        return fila;
    }


    private void cargarCompras() {
        Map<Integer, String> nombres = new HashMap<Integer, String>();
        for (Proveedor p : proveedorDao.obtenerProveedores()) {
            nombres.put(p.getId(), p.getNombre());
        }

        List<Compra> compras = comprasDao.obtenerCompras();
        LocalDate hoy = LocalDate.now();
        double gastos = 0;

        contenedorCompras.getChildren().clear();

        int mostradas = 0;
        for (int i = compras.size() - 1; i >= 0; i--) {
            Compra c = compras.get(i);
            LocalDate fecha = LocalDate.parse(c.getFecha());

            if (fecha.getYear() == hoy.getYear() && fecha.getMonth() == hoy.getMonth()) {
                gastos += c.getTotal();
            }

            if (mostradas < MAX_COMPRAS) {
                String proveedor = nombres.get(c.getProveedor_id());
                if (proveedor == null) {
                    proveedor = "Desconocido";
                }
                contenedorCompras.getChildren().add(crearFilaCompra(c, fecha, proveedor));
                mostradas++;
            }
        }

        if (compras.isEmpty()) {
            Label vacio = new Label("Todavía no hay compras.");
            vacio.getStyleClass().add("inicio-vacio");
            contenedorCompras.getChildren().add(vacio);
        }

        gastoMes.setText(String.format("%.2f €", gastos));
        lblContador.setText("Mostrando " + mostradas + " de " + compras.size() + " compras");
    }

    private HBox crearFilaCompra(Compra c, LocalDate fecha, String proveedor) {
        HBox fila = new HBox(
                celda(fecha.format(FORMATO_FECHA), 70),
                celda(proveedor, 130),
                celda(c.getNumero_factura(), 90),
                celda(String.valueOf(c.getDetalle().size()), 50),
                celda(String.format("%.2f €", c.getTotal()), 80));
        fila.getStyleClass().add("compra-fila");
        return fila;
    }

    private Label celda(String texto, double ancho) {
        Label lbl = new Label(texto);
        lbl.setPrefWidth(ancho);
        return lbl;
    }
}