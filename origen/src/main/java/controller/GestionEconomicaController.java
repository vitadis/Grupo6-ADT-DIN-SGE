/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import model.EstadoPedido;
import model.Movimiento;
import model.Proveedor;
import model.Transaccion;
import model.Usuario;
import model.VentaPedido;
import services.ComprasService;
import services.MovimientoService;
import services.PedidoService;
import util.Alertas;

/**
 * Controlador de la pantalla «Gestión económica»
 * ({@code gestionEconomicaView.fxml}).
 *
 * @author anazk
 */
public class GestionEconomicaController implements Initializable, UsuarioImplementacion {

    // ------------------------------- Barra superior -------------------------------
    /**
     * Nombre y rol del usuario.
     */
    @FXML
    private Label lblUsuario;
    /**
     * Iniciales del usuario en el avatar.
     */
    @FXML
    private Label lblAvatar;

    // ------------------------------- Botones de la cabecera -------------------------------
    /**
     * Abre el balance mensual.
     */
    @FXML
    private Button btnBalanceMensual;
    /**
     * Abre la ventana para registrar un movimiento.
     */
    @FXML
    private Button btnRegistrarMovimiento;

    // ------------------------------- Tarjetas de resumen -------------------------------
    /**
     * Título de la tarjeta de ingresos.
     */
    @FXML
    private Label lblTituloIngresos;
    /**
     * Variación de los ingresos respecto al mes anterior.
     */
    @FXML
    private Label lblVariacionIngresos;
    /**
     * Total de ingresos.
     */
    @FXML
    private Label lblIngresos;
    /**
     * Título de la tarjeta de gastos.
     */
    @FXML
    private Label lblTituloGastos;
    /**
     * Variación de los gastos respecto al mes anterior.
     */
    @FXML
    private Label lblVariacionGastos;
    /**
     * Total de gastos.
     */
    @FXML
    private Label lblGastos;
    /**
     * Margen de beneficio en porcentaje.
     */
    @FXML
    private Label lblMargen;
    /**
     * Beneficio estimado (ingresos - gastos).
     */
    @FXML
    private Label lblBeneficio;

    // ------------------------------- Filtros -------------------------------
    /**
     * Fecha de inicio del filtro.
     */
    @FXML
    private DatePicker dpDesde;
    /**
     * Fecha de fin del filtro.
     */
    @FXML
    private DatePicker dpHasta;
    /**
     * Filtro por tipo (ingreso o gasto).
     */
    @FXML
    private ComboBox<String> cbTipo;
    /**
     * Filtro por categoría.
     */
    @FXML
    private ComboBox<String> cbCategoria;
    /**
     * Búsqueda por concepto.
     */
    @FXML
    private TextField txtConcepto;
    /**
     * Quita todos los filtros.
     */
    @FXML
    private Button btnLimpiarFiltros;

    // ------------------------------- Tabla de movimientos -------------------------------
    /**
     * Scroll de la tabla de movimientos.
     */
    @FXML
    private ScrollPane scrollMovimientos;
    /**
     * Contenedor donde se pinta una fila (HBox) por movimiento.
     */
    @FXML
    private VBox contenedorMovimientos;
    /**
     * Texto «Mostrando X de Y movimientos».
     */
    @FXML
    private Label lblContador;
    /**
     * Va a la página anterior.
     */
    @FXML
    private Button btnPaginaAnterior;
    /**
     * Página actual.
     */
    @FXML
    private Label lblPagina;
    /**
     * Va a la página siguiente.
     */
    @FXML
    private Button btnPaginaSiguiente;

    // ------------------------------- Gráfico y aviso -------------------------------
    /**
     * Gráfico de barras con el gasto de cada mes.
     */
    @FXML
    private BarChart<String, Number> graficoGastos;
    /**
     * Eje X del gráfico: los meses.
     */
    @FXML
    private CategoryAxis ejeMeses;
    /**
     * Eje Y del gráfico: el importe en euros.
     */
    @FXML
    private NumberAxis ejeImporte;
    /**
     * Texto de la tarjeta de aviso.
     */
    @FXML
    private Label lblTextoAviso;
    /**
     * Abre los gastos por categoría.
     */
    @FXML
    private Button btnRevisarGastos;

    private final ComprasService comprasService = new ComprasService();
    private final PedidoService pedidoService = new PedidoService();
    /**
     * Service de los movimientos (gastos e ingresos que no son compras ni ventas).
     */
    private final MovimientoService movimientoService = new MovimientoService();

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
     * Compras leídas de la base de datos al abrir la pantalla (los gastos).
     */
    private List<Compra> compras = new ArrayList<>();
    /**
     * Pedidos leídos de la base de datos al abrir la pantalla (los ingresos).
     */
    private List<VentaPedido> pedidos = new ArrayList<>();
    /**
     * Movimientos leídos de la base de datos al abrir la pantalla (luz, nóminas, alquiler...).
     */
    private List<Movimiento> movimientos = new ArrayList<>();
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
     * Se ejecuta al cargar la vista.
     *
     * @param url ubicación del FXML (no se usa)
     * @param rb recursos de idioma (no se usa)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbTipo.getItems().addAll("Todos", "Ingreso", "Gasto");

        cargarDatos();
        cargarGrafico();
        cargarResumen(YearMonth.now());
        cargarTransacciones();
        pintarTabla(todas);
    }

    /**
     * Recibe el usuario con la sesión iniciada y pone su nombre y sus iniciales en la barra
     * superior. Lo llama {@link NavController} al cargar la vista.
     *
     * @param usuario usuario que ha iniciado sesión
     */
    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null && usuario.getUsername() != null) {
            lblUsuario.setText(usuario.getUsername());
            String nombre = usuario.getUsername();
            lblAvatar.setText(nombre.substring(0, Math.min(2, nombre.length())).toUpperCase());
        }
    }

    // ------------------------------- Registrar movimiento -------------------------------

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
                            ventana.setDatos(usuarioId, movimientos);
                        }
                    });

            if (c.isGuardado()) {
                recargar();
            }
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo abrir la ventana de registrar movimiento.");
        }
    }

    /**
     * Vuelve a leer los datos y repinta las tarjetas, el gráfico y la tabla, respetando los
     * filtros que estén puestos.
     */
    private void recargar() {
        cargarDatos();
        cargarGrafico();
        cargarResumen(YearMonth.now());
        cargarTransacciones();
        onFiltrar(null);
    }

    /**
     * Lee las compras, los pedidos y los movimientos una sola vez. Se leen por separado para
     * que, si falla uno, la pantalla siga mostrando lo de los otros.
     */
    private void cargarDatos() {
        try {
            compras = comprasService.listarCompras();
        } catch (DaoException e) {
            Alertas.mostrarError("No se pudieron leer las compras: " + e.getMessage());
        }

        try {
            pedidos = pedidoService.listaPedidos();
        } catch (DaoException e) {
            Alertas.mostrarError("No se pudieron leer las ventas: " + e.getMessage());
        }

        try {
            movimientos = movimientoService.listarMovimientos();
        } catch (DaoException e) {
            Alertas.mostrarError("No se pudieron leer los movimientos: " + e.getMessage());
        }
    }

    private void cargarGrafico() {
        Map<YearMonth, Double> gastoPorMes = new LinkedHashMap<>();
        YearMonth actual = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            gastoPorMes.put(actual.minusMonths(i), 0.0);
        }
        for (Compra c : compras) {
            YearMonth mes = YearMonth.from(LocalDate.parse(c.getFecha()));
            if (gastoPorMes.containsKey(mes)) {
                gastoPorMes.put(mes, gastoPorMes.get(mes) + c.getTotal());
            }
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

    private double sumarIngresos(YearMonth mes) {
        double suma = 0;
        for (VentaPedido p : pedidos) {
            YearMonth mesPedido = YearMonth.from(LocalDate.parse(p.getFecha()));
            if (mesPedido.equals(mes) && p.getEstado() == EstadoPedido.COBRADO) {
                suma += p.getTotal();
            }
        }
        for (Movimiento m : movimientos) {
            YearMonth mesMovimiento = YearMonth.from(LocalDate.parse(m.getFecha()));
            if (mesMovimiento.equals(mes) && m.esIngreso()) {
                suma += m.getImporte();
            }
        }
        return suma;
    }

    private double sumarGastos(YearMonth mes) {
        double suma = 0;
        for (Compra c : compras) {
            YearMonth mesCompra = YearMonth.from(LocalDate.parse(c.getFecha()));
            if (mesCompra.equals(mes)) {
                suma += c.getTotal();
            }
        }
        for (Movimiento m : movimientos) {
            YearMonth mesMovimiento = YearMonth.from(LocalDate.parse(m.getFecha()));
            if (mesMovimiento.equals(mes) && !m.esIngreso()) {
                suma += m.getImporte();
            }
        }
        return suma;
    }

    private void cargarResumen(YearMonth mes) {
        double ingresos = sumarIngresos(mes);
        double gastos = sumarGastos(mes);
        double beneficio = ingresos - gastos;

        lblIngresos.setText(String.format("%.2f €", ingresos));
        lblGastos.setText(String.format("%.2f €", gastos));
        lblBeneficio.setText(String.format("%.2f €", beneficio));

    }

    // ------------------------------- Tabla de movimientos -------------------------------

    /**
     * Monta la lista {@link #todas} con los gastos (una fila por compra) y los ingresos (una
     * fila por día con la suma de los pedidos cobrados), ordenada de la más nueva a la más
     * vieja. También rellena el ComboBox de categorías con las que aparecen.
     */
    private void cargarTransacciones() {
        todas.clear();
        Map<Integer, String> nombresProveedor = new HashMap<>();
        try {
            for (Proveedor p : comprasService.listarProveedores()) {
                nombresProveedor.put(p.getId(), p.getNombre());
            }
        } catch (DaoException e) {
        }
        for (Compra c : compras) {
            String proveedor = nombresProveedor.get(c.getProveedor_id());
            if (proveedor == null) {
                proveedor = "Proveedor " + c.getProveedor_id();
            }
            todas.add(new Transaccion(LocalDate.parse(c.getFecha()), proveedor + " · " + c.getNumero_factura(),
                    "Compras", false, c.getTotal()));
        }
        Map<LocalDate, Double> ventasPorDia = new TreeMap<>();
        for (VentaPedido p : pedidos) {
            if (p.getEstado() == EstadoPedido.COBRADO) {
                LocalDate dia = LocalDate.parse(p.getFecha());
                if (ventasPorDia.containsKey(dia)) {
                    ventasPorDia.put(dia, ventasPorDia.get(dia) + p.getTotal());
                } else {
                    ventasPorDia.put(dia, p.getTotal());
                }
            }
        }
        for (LocalDate dia : ventasPorDia.keySet()) {
            double total = Math.round(ventasPorDia.get(dia) * 100) / 100.0;
            todas.add(new Transaccion(dia, "Ventas del día (TPV)", "Ventas", true, total));
        }
        // movimientos: una fila por movimiento (luz, nóminas, alquiler...)
        for (Movimiento m : movimientos) {
            todas.add(new Transaccion(LocalDate.parse(m.getFecha()), m.getConcepto(), m.getCategoria(),
                    m.esIngreso(), m.getImporte()));
        }
        Collections.sort(todas, new Comparator<Transaccion>() {
            @Override
            public int compare(Transaccion a, Transaccion b) {
                return b.getFecha().compareTo(a.getFecha());
            }
        });
        cbCategoria.getItems().clear();
        cbCategoria.getItems().add("Todas");
        for (Transaccion t : todas) {
            if (!cbCategoria.getItems().contains(t.getCategoria())) {
                cbCategoria.getItems().add(t.getCategoria());
            }
        }
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
                celda(t.getConcepto(), 220),
                celda(t.getCategoria(), 110),
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

    // ------------------------------- Filtros -------------------------------

    /**
     * Vuelve a pintar la tabla solo con las transacciones que cumplen todos los filtros.
     * Lo llaman las fechas, los dos ComboBox y el campo de concepto al cambiar.
     *
     * @param event evento del filtro que ha cambiado
     */
    @FXML
    private void onFiltrar(Event event) {
        LocalDate desde = dpDesde.getValue();       // null si está vacío
        LocalDate hasta = dpHasta.getValue();
        String tipo = cbTipo.getValue();
        String categoria = cbCategoria.getValue();
        String texto = txtConcepto.getText().trim().toLowerCase();

        List<Transaccion> filtradas = new ArrayList<>();
        for (Transaccion t : todas) {               // atributo con TODAS las transacciones
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
