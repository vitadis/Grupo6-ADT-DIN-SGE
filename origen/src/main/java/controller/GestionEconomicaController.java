/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controlador de la pantalla «Gestión económica» ({@code gestionEconomicaView.fxml}).
 *
 * @author anazk
 */
public class GestionEconomicaController implements Initializable {

    // ------------------------------- Barra superior -------------------------------

    /**
     * Buscador general.
     */
    @FXML
    private TextField txtBuscar;
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

    /**
     * Constructor vacío. Lo usa JavaFX al cargar {@code gestionEconomicaView.fxml}.
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
    }

}
