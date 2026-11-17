package controller;

import exceptions.DaoException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.TreeMap;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Compra;
import model.Movimiento;
import model.ProcedenciaMovimiento;
import model.Proveedor;
import model.Transaccion;
import model.Usuario;
import dao.implement.ComprasDaoImpl;
import dao.implement.MovimientoDaoImpl;
import dao.implement.ProveedorDaoImpl;
import services.MovimientoService;
import util.Alertas;
import util.AppLogger;

/**
 * Controlador de la pantalla «Gestión económica»
 * ({@code gestionEconomicaView.fxml}).
 *
 * @author anazk
 */
public class GestionEconomicaController implements Initializable, UsuarioImplementacion, ActualizarDatos {

    private final AppLogger logger = AppLogger.getInstance();

    @FXML
    private Button btnRegistrarMovimiento;

    @FXML
    private Label lblTituloIngresos;
    @FXML
    private Label lblVariacionIngresos;
    @FXML
    private Label lblIngresos;
    @FXML
    private Label lblTituloGastos;
    @FXML
    private Label lblVariacionGastos;
    @FXML
    private Label lblGastos;
    @FXML
    private Label lblMargen;
    @FXML
    private Label lblBeneficio;

    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private ComboBox<String> cbCategoria;
    @FXML
    private TextField txtConcepto;
    @FXML
    private Button btnLimpiarFiltros;

    @FXML
    private ScrollPane scrollMovimientos;
    @FXML
    private VBox contenedorMovimientos;
    @FXML
    private Label lblContador;
    @FXML
    private Button btnPaginaAnterior;
    @FXML
    private Label lblPagina;
    @FXML
    private Button btnPaginaSiguiente;

    @FXML
    private BarChart<String, Number> graficoGastos;
    @FXML
    private CategoryAxis ejeMeses;
    @FXML
    private NumberAxis ejeImporte;

    /**
     * Único service de la pantalla: registros económicos, compras y proveedores. Se le pasan los
     * DAOs de MongoDB. La ventana «Registrar movimiento» crea el suyo y carga sus datos sola.
     */
    private final MovimientoService movimientoService = new MovimientoService(
            MovimientoDaoImpl.getInstance(), ComprasDaoImpl.getInstance(), ProveedorDaoImpl.getInstance());

    /**
     * Formato en el que se muestran las fechas en la tabla (dd/MM).
     */
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM");
    /**
     * Todas las transacciones (ingresos y gastos), de la más nueva a la más vieja. Los filtros
     * trabajan sobre esta lista.
     */
    private final List<Transaccion> todas = new ArrayList<>();
    /**
     * Registros de {@code registros_economicos} leídos al abrir la pantalla. Las tarjetas, el
     * gráfico y la tabla salen solo de aquí.
     */
    private List<Movimiento> movimientos = new ArrayList<>();
    /**
     * Compras por identificador, solo para escribir el concepto de los registros que vienen de
     * una compra (proveedor y factura).
     */
    private final Map<Integer, Compra> compras = new HashMap<>();
    /**
     * Usuario con la sesión iniciada; lo pasa {@link NavController}.
     */
    private Usuario usuario;

    /**
     * Constructor vacío. Lo usa JavaFX al cargar
     * {@code gestionEconomicaView.fxml}.
     */
    public GestionEconomicaController() {
    }

    /**
     * Rellena el filtro de tipo y carga los datos con {@link #actualizarDatos()}.
     *
     * @param url ubicación del FXML (no se usa)
     * @param rb recursos de idioma (no se usa)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipo.getItems().addAll("Todos", "Ingreso", "Gasto");

        actualizarDatos();
    }

    /**
     * Recibe el usuario con la sesión iniciada; se usa como {@code usuario_id} de los
     * movimientos que se registran. Lo llama {@link NavController} al cargar la vista.
     *
     * @param usuario usuario que ha iniciado sesión
     */
    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            logger.setUser(usuario.getUsername());
        }
    }

    /**
     * Abre la ventana de registrar movimiento y, si se guarda, vuelve a cargar la pantalla.
     *
     * @param event evento del botón Registrar movimiento
     */
    @FXML
    private void onRegistrarMovimiento(ActionEvent event) {
        try {
            RegistrarMovimientoController c = InstanciarEscena.createNewStage(
                    "/view/economia/registrarMovimiento.fxml", "Registrar movimiento",
                    new Consumer<RegistrarMovimientoController>() {
                        @Override
                        public void accept(RegistrarMovimientoController ventana) {
                            int usuarioId = 0;
                            if (usuario != null && usuario.getId() != null) {
                                usuarioId = usuario.getId();
                            }
                            ventana.setUsuarioId(usuarioId);
                        }
                    });

            if (c.isGuardado()) {
                actualizarDatos();
            }
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo abrir la ventana de registrar movimiento.");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Vuelve a leer los datos y repinta las tarjetas, el gráfico y la tabla, respetando los
     * filtros que estén puestos. Lo llama {@link NavController} cada vez que se entra en
     * «Gestión económica» (así se ven las compras y ventas hechas en otras pantallas) y
     * también se usa después de registrar un movimiento.
     */
    @Override
    public void actualizarDatos() {
        cargarDatos();
        cargarGrafico();
        cargarResumen(YearMonth.now());
        cargarTransacciones();
        onFiltrar(null);
    }

    /**
     * Lee los registros económicos y las compras una sola vez. Se leen por separado para que,
     * si fallan las compras, la pantalla siga mostrando los importes.
     */
    private void cargarDatos() {
        try {
            movimientos = movimientoService.listarMovimientos();
        } catch (DaoException e) {
            Alertas.mostrarError("No se pudieron leer los registros económicos: " + e.getMessage());
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        compras.clear();
        try {
            for (Compra c : movimientoService.listarCompras()) {
                compras.put(c.getId(), c);
            }
        } catch (DaoException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Pinta en el gráfico el gasto de cada uno de los seis últimos meses.
     */
    private void cargarGrafico() {
        Map<YearMonth, Double> gastoPorMes = new LinkedHashMap<>();
        YearMonth actual = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            gastoPorMes.put(actual.minusMonths(i), 0.0);
        }
        for (Movimiento m : movimientos) {
            YearMonth mes = YearMonth.from(LocalDate.parse(m.getFecha()));
            if (!m.esIngreso() && gastoPorMes.containsKey(mes)) {
                gastoPorMes.put(mes, gastoPorMes.get(mes) + m.getImporte());
            }
        }

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Gastos");
        for (YearMonth mes : gastoPorMes.keySet()) {
            String nombre = mes.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es")).replace(".", "");
            serie.getData().add(new XYChart.Data<String, Number>(nombre, gastoPorMes.get(mes)));
        }

        graficoGastos.getData().clear();
        graficoGastos.getData().add(serie);
    }

    /**
     * Suma los ingresos o los gastos de un mes.
     *
     * @param mes mes que se suma
     * @param ingresos {@code true} para sumar ingresos, {@code false} para gastos
     * @return la suma
     */
    private double sumar(YearMonth mes, boolean ingresos) {
        double suma = 0;
        for (Movimiento m : movimientos) {
            YearMonth mesMovimiento = YearMonth.from(LocalDate.parse(m.getFecha()));
            if (mesMovimiento.equals(mes) && m.esIngreso() == ingresos) {
                suma += m.getImporte();
            }
        }
        return suma;
    }

    /**
     * Rellena las tarjetas de ingresos, gastos y beneficio de un mes.
     *
     * @param mes mes que se muestra
     */
    private void cargarResumen(YearMonth mes) {
        double ingresos = sumar(mes, true);
        double gastos = sumar(mes, false);
        double beneficio = ingresos - gastos;

        lblIngresos.setText(String.format("%.2f €", ingresos));
        lblGastos.setText(String.format("%.2f €", gastos));
        lblBeneficio.setText(String.format("%.2f €", beneficio));
    }

    /**
     * Monta la lista {@link #todas} a partir de los registros económicos, ordenada de la más
     * nueva a la más vieja. Los registros de pedidos se agrupan en una fila por día («Ventas del
     * día (TPV)»); los de compras llevan como concepto el proveedor y la factura. También rellena
     * el ComboBox de categorías con las que aparecen.
     */
    private void cargarTransacciones() {
        todas.clear();
        Map<Integer, String> nombresProveedor = new HashMap<>();
        try {
            for (Proveedor p : movimientoService.listarProveedores()) {
                nombresProveedor.put(p.getId(), p.getNombre());
            }
        } catch (DaoException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        Map<LocalDate, Double> ventasPorDia = new TreeMap<>();
        for (Movimiento m : movimientos) {
            LocalDate dia = LocalDate.parse(m.getFecha());
            ProcedenciaMovimiento origen = m.getOrigen();
            if (origen != null && ProcedenciaMovimiento.PEDIDOS.equals(origen.getColeccion())) {
                if (ventasPorDia.containsKey(dia)) {
                    ventasPorDia.put(dia, ventasPorDia.get(dia) + m.getImporte());
                } else {
                    ventasPorDia.put(dia, m.getImporte());
                }
            } else {
                todas.add(new Transaccion(dia, concepto(m, nombresProveedor), m.getCategoria(),
                        m.esIngreso(), m.getImporte()));
            }
        }
        for (LocalDate dia : ventasPorDia.keySet()) {
            double total = Math.round(ventasPorDia.get(dia) * 100) / 100.0;
            todas.add(new Transaccion(dia, "Ventas del día (TPV)", "Ventas", true, total));
        }

        // Los registros llegan del más viejo al más nuevo; se da la vuelta antes de ordenar por
        // fecha para que, dentro del mismo día, el último registrado también quede arriba.
        Collections.reverse(todas);
        Collections.sort(todas, new Comparator<Transaccion>() {
            @Override
            public int compare(Transaccion a, Transaccion b) {
                return b.getFecha().compareTo(a.getFecha());
            }
        });
        String elegida = cbCategoria.getValue();
        cbCategoria.getItems().clear();
        cbCategoria.getItems().add("Todas");
        for (Transaccion t : todas) {
            if (!cbCategoria.getItems().contains(t.getCategoria())) {
                cbCategoria.getItems().add(t.getCategoria());
            }
        }
        if (elegida != null && cbCategoria.getItems().contains(elegida)) {
            cbCategoria.setValue(elegida);
        }
    }

    /**
     * Devuelve el concepto que se enseña de un registro. Si viene de una compra es
     * «proveedor · factura»; si no, su propio concepto.
     *
     * @param m registro económico
     * @param nombresProveedor nombre de cada proveedor por su identificador
     * @return el concepto
     */
    private String concepto(Movimiento m, Map<Integer, String> nombresProveedor) {
        ProcedenciaMovimiento origen = m.getOrigen();
        if (origen == null || !ProcedenciaMovimiento.COMPRAS.equals(origen.getColeccion())) {
            return m.getConcepto();
        }
        Compra c = compras.get(origen.getProcedencia_id());
        if (c == null) {
            return "Compra " + origen.getProcedencia_id();
        }
        String proveedor = nombresProveedor.get(c.getProveedor_id());
        if (proveedor == null) {
            proveedor = "Proveedor " + c.getProveedor_id();
        }
        return proveedor + " · " + c.getNumero_factura();
    }

    /**
     * Pinta en la tabla las transacciones recibidas y actualiza el contador.
     *
     * @param lista transacciones que se muestran
     */
    private void pintarTabla(List<Transaccion> lista) {
        contenedorMovimientos.getChildren().clear();
        for (Transaccion t : lista) {
            contenedorMovimientos.getChildren().add(crearFila(t));
        }

        if (lista.isEmpty()) {
            Label vacio = new Label("No hay movimientos con estos filtros.");
            vacio.getStyleClass().add("texto-secundario");
            contenedorMovimientos.getChildren().add(vacio);
        }

        lblContador.setText("Mostrando " + lista.size() + " de " + todas.size() + " movimientos");
        scrollMovimientos.setVvalue(0);
    }

    /**
     * Crea la fila de una transacción con las mismas columnas que la cabecera de la tabla.
     *
     * @param t transacción que se pinta
     * @return la fila
     */
    private HBox crearFila(Transaccion t) {
        Label etiquetaTipo;
        Label importe;
        if (t.isIngreso()) {
            etiquetaTipo = new Label("✓ Ingreso");
            etiquetaTipo.getStyleClass().add("estado-ok");
            importe = celda(String.format("+ %,.2f €", t.getImporte()), 110);
            importe.getStyleClass().add("texto-ok");
        } else {
            etiquetaTipo = new Label("Gasto");
            etiquetaTipo.getStyleClass().add("gestion-estado-neutro");
            importe = celda(String.format("− %,.2f €", t.getImporte()), 110);
        }
        importe.setAlignment(Pos.CENTER_RIGHT);
        HBox columnaTipo = new HBox(etiquetaTipo);
        columnaTipo.setPrefWidth(100);
        columnaTipo.setMinWidth(100);

        HBox fila = new HBox(
                celda(t.getFecha().format(FORMATO_FECHA), 80),
                celda(t.getConcepto(), 300),
                celda(t.getCategoria(), 150),
                columnaTipo,
                importe
        );
        fila.getStyleClass().add("compra-fila");
        return fila;
    }

    /**
     * Crea una celda de la tabla: un Label con un ancho fijo, igual que la columna de la cabecera.
     *
     * @param texto texto de la celda
     * @param ancho ancho de la columna
     * @return la celda
     */
    private Label celda(String texto, double ancho) {
        Label l = new Label(texto);
        l.setPrefWidth(ancho);
        l.setMinWidth(ancho);
        return l;
    }

    /**
     * Vuelve a pintar la tabla solo con las transacciones que cumplen todos los filtros.
     * Lo llaman las fechas, los dos ComboBox y el campo de concepto al cambiar.
     *
     * @param event evento del filtro que ha cambiado
     */
    @FXML
    private void onFiltrar(Event event) {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        String tipo = cbTipo.getValue();
        String categoria = cbCategoria.getValue();
        String texto = txtConcepto.getText().trim().toLowerCase();

        List<Transaccion> filtradas = new ArrayList<>();
        for (Transaccion t : todas) {
            if (desde != null && t.getFecha().isBefore(desde)) {
                continue;
            }
            if (hasta != null && t.getFecha().isAfter(hasta)) {
                continue;
            }
            if ("Ingreso".equals(tipo) && !t.isIngreso()) {
                continue;
            }
            if ("Gasto".equals(tipo) && t.isIngreso()) {
                continue;
            }
            if (categoria != null && !"Todas".equals(categoria) && !categoria.equals(t.getCategoria())) {
                continue;
            }
            if (!texto.isEmpty() && !t.getConcepto().toLowerCase().contains(texto)) {
                continue;
            }
            filtradas.add(t);
        }
        pintarTabla(filtradas);
    }

    /**
     * Vacía todos los filtros y vuelve a mostrar todas las transacciones.
     *
     * @param event evento del botón Limpiar filtros
     */
    @FXML
    private void onLimpiarFiltros(ActionEvent event) {
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        cbTipo.setValue(null);
        cbCategoria.setValue(null);
        txtConcepto.clear();
        pintarTabla(todas);
    }
}
