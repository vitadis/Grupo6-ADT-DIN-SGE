/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import exceptions.DaoException;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Compra;
import model.LineaCompra;
import model.Producto;
import model.Proveedor;
import services.ComprasService;
import model.Usuario;
import util.Alertas;

/**
 * Controlador de la pantalla «Compras y proveedores» ({@code CompraView.fxml}).
 *
 * Tiene dos pestañas:
 * <ul>
 * <li><b>Compras:</b> listado de compras y lista de compra sugerida con los productos en stock bajo.</li>
 * <li><b>Proveedores:</b> lista de proveedores, su ficha y su historial de compras.</li>
 * </ul>
 * Abre las ventanas {@link RegistrarCompraController} y {@link EditarProveedorController}
 * y recarga los datos cuando se guarda algo.
 *
 * @author anazk
 */
public class ComprasController implements Initializable, UsuarioImplementacion {

    /**
     * Service de compras, proveedores y productos.
     */
    private final ComprasService comprasService = new ComprasService();
    /**
     * Proveedores por identificador, para sacar su nombre a partir de un {@code proveedor_id}.
     */
    private final Map<Integer, Proveedor> proveedores = new HashMap<>();
    /**
     * Formato en el que se muestran las fechas (dd/MM/yy).
     */
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yy");

    /**
     * Usuario con la sesión iniciada; lo pasa {@link NavController}.
     */
    private Usuario usuario;
    /**
     * Proveedor seleccionado en la pestaña Proveedores.
     */
    private Proveedor proveedorSeleccionado;

    /**
     * Líneas de la lista sugerida. Va en paralelo con {@link #proveedoresSugeridos} y {@link #checksSugeridos}.
     */
    private final List<LineaCompra> lineasSugeridas = new ArrayList<>();
    /**
     * Proveedor habitual de cada línea sugerida, en el mismo orden que {@link #lineasSugeridas}.
     */
    private final List<Integer> proveedoresSugeridos = new ArrayList<>();
    /**
     * CheckBox de cada tarjeta sugerida, en el mismo orden que {@link #lineasSugeridas}.
     */
    private final List<CheckBox> checksSugeridos = new ArrayList<>();

    /**
     * Campo de búsqueda de la barra superior.
     */
    @FXML
    private TextField txtBuscar;
    /**
     * Nombre del usuario en la barra superior.
     */
    @FXML
    private Label lblUsuario;
    /**
     * Iniciales del usuario en la barra superior.
     */
    @FXML
    private Label lblAvatar;
    /**
     * Título de la página.
     */
    @FXML
    private Label lblTitulo;
    /**
     * Descripción de la página.
     */
    @FXML
    private Label lblSubtitulo;
    /**
     * Vuelve a generar la lista sugerida. Solo se ve en la pestaña Compras.
     */
    @FXML
    private Button btnGenerarLista;
    /**
     * Scroll del listado de compras.
     */
    @FXML
    private ScrollPane scrollCompras;
    /**
     * Contenedor donde se pintan las filas de las compras.
     */
    @FXML
    private VBox contenedorCompras;
    /**
     * Número de compras del listado.
     */
    @FXML
    private Label lblContador;
    /**
     * Panel de la lista de compra sugerida.
     */
    @FXML
    private VBox panelSugerencias;
    /**
     * Título de la lista sugerida.
     */
    @FXML
    private Label lblTituloSugerencias;
    /**
     * Cuántos productos tiene la lista sugerida.
     */
    @FXML
    private Label lblSubtituloSugerencias;
    /**
     * Scroll de la lista sugerida.
     */
    @FXML
    private ScrollPane scrollSugerencias;
    /**
     * Contenedor donde se pintan las tarjetas sugeridas.
     */
    @FXML
    private VBox contenedorSugerencias;
    /**
     * Crea compras con los productos marcados de la lista sugerida.
     */
    @FXML
    private Button btnCrearCompra;
    /**
     * Migas de pan de la cabecera.
     */
    @FXML
    private Label lblRuta;
    /**
     * Contenedor de los botones de acción de la cabecera.
     */
    @FXML
    private HBox contenedorBotonesAccion;
    /**
     * «Registrar compra» o «Nuevo proveedor», según la pestaña.
     */
    @FXML
    private Button btnAccionPrincipal;
    /**
     * Pestañas de la pantalla.
     */
    @FXML
    private TabPane tabPaneCompras;
    /**
     * Pestaña Compras.
     */
    @FXML
    private Tab tabCompras;
    /**
     * Pestaña Proveedores.
     */
    @FXML
    private Tab tabProveedores;
    /**
     * Scroll de la lista de proveedores.
     */
    @FXML
    private ScrollPane scrollListaProveedores;
    /**
     * Contenedor donde se pintan las filas de los proveedores.
     */
    @FXML
    private VBox contenedorListaProveedores;
    /**
     * Círculo con las iniciales del proveedor seleccionado.
     */
    @FXML
    private StackPane avatarProveedor;
    /**
     * Iniciales del proveedor seleccionado.
     */
    @FXML
    private Label lblInicialesProveedor;
    /**
     * Nombre del proveedor seleccionado.
     */
    @FXML
    private Label lblNombreProveedor;
    /**
     * Persona, teléfono y correo del proveedor seleccionado.
     */
    @FXML
    private Label lblContactoProveedor;
    /**
     * Plazo de entrega y días laborales del proveedor seleccionado.
     */
    @FXML
    private Label lblCondicionesProveedor;
    /**
     * Abre la ventana para editar el proveedor seleccionado.
     */
    @FXML
    private Button btnEditarProveedor;
    /**
     * Número de compras del proveedor seleccionado.
     */
    @FXML
    private Label lblSubtituloHistorial;
    /**
     * Scroll del historial del proveedor.
     */
    @FXML
    private ScrollPane scrollHistorialProveedor;
    /**
     * Contenedor donde se pintan las compras del proveedor seleccionado.
     */
    @FXML
    private VBox contenedorHistorialProveedor;

    /**
     * Fila resaltada en la lista de proveedores, para quitarle el resaltado al cambiar.
     */
    private HBox filaProveedorSeleccionada;

    /**
     * Constructor vacío. Lo usa JavaFX al cargar {@code CompraView.fxml}.
     */
    public ComprasController() {
    }

    /**
     * Configura las pestañas y carga los proveedores, las compras, la lista de proveedores
     * y la lista sugerida. Si falla la base de datos, muestra un error.
     *
     * @param url ubicación del FXML (no se usa)
     * @param rb recursos de idioma (no se usa)
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarPestanas();
        try {
            cargarProveedores();
            cargarCompras();
            cargarListaProveedores();
            generarListaSugerida();
        } catch (DaoException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    /**
     * Recibe el usuario con la sesión iniciada. Lo llama {@link NavController} al cargar la vista.
     *
     * @param usuario usuario que ha iniciado sesión
     */
    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Escucha los cambios de pestaña para adaptar los botones de la cabecera.
     */
    private void configurarPestanas() {
        tabPaneCompras.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab anterior, Tab nueva) {
                actualizarBotonesAccion(nueva);
            }
        });
        actualizarBotonesAccion(tabPaneCompras.getSelectionModel().getSelectedItem());
    }

    /**
     * Adapta los botones de la cabecera a la pestaña: en Proveedores el botón principal
     * pasa a «Nuevo proveedor» y «Generar lista» solo se ve en Compras.
     *
     * @param pestana pestaña seleccionada
     */
    private void actualizarBotonesAccion(Tab pestana) {
        boolean enCompras = (pestana == tabCompras);
        btnGenerarLista.setVisible(enCompras);
        btnGenerarLista.setManaged(enCompras);

        if (pestana == tabProveedores) {
            btnAccionPrincipal.setText("Nuevo proveedor");
        } else {
            btnAccionPrincipal.setText("Registrar compra");
        }
    }

    /**
     * Vuelve a generar la lista sugerida.
     *
     * @param event evento del botón Generar lista de compra
     */
    @FXML
    private void onGenerarLista(ActionEvent event) {
        try {
            generarListaSugerida();
        } catch (DaoException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    /**
     * Abre la ventana de nuevo proveedor si está en la pestaña Proveedores y, si no,
     * la de registrar compra.
     *
     * @param event evento del botón principal
     */
    @FXML
    private void onAccionPrincipal(ActionEvent event) {
        if (tabPaneCompras.getSelectionModel().getSelectedItem() == tabProveedores) {
            abrirVentanaProveedor(null);
        } else {
            abrirVentanaCompra(0, null);
        }
    }

    /**
     * Abre la ventana de edición con el proveedor seleccionado.
     *
     * @param event evento del botón Editar
     */
    @FXML
    private void onEditarProveedor(ActionEvent event) {
        if (proveedorSeleccionado == null) {
            Alertas.mostrarError("Selecciona un proveedor de la lista.");
            return;
        }
        abrirVentanaProveedor(proveedorSeleccionado);
    }

    /**
     * Agrupa por proveedor los productos marcados de la lista sugerida y abre una ventana
     * de compra por cada proveedor, ya rellena.
     *
     * @param event evento del botón Crear compra con esta lista
     */
    @FXML
    private void onCrearCompraConLista(ActionEvent event) {
        Map<Integer, List<LineaCompra>> porProveedor = new LinkedHashMap<>();

        for (int i = 0; i < lineasSugeridas.size(); i++) {
            if (checksSugeridos.get(i).isSelected()) {
                Integer idProveedor = proveedoresSugeridos.get(i);
                if (!porProveedor.containsKey(idProveedor)) {
                    porProveedor.put(idProveedor, new ArrayList<LineaCompra>());
                }
                porProveedor.get(idProveedor).add(lineasSugeridas.get(i));
            }
        }

        if (porProveedor.isEmpty()) {
            Alertas.mostrarError("Marca al menos un producto de la lista.");
            return;
        }

        for (Integer idProveedor : porProveedor.keySet()) {
            abrirVentanaCompra(idProveedor, porProveedor.get(idProveedor));
        }
    }

    /**
     * Abre la ventana del proveedor y, si se guarda, recarga los datos.
     *
     * @param proveedor proveedor que se va a editar, o {@code null} para dar de alta uno nuevo
     */
    private void abrirVentanaProveedor(final Proveedor proveedor) {
        try {
            EditarProveedorController c = InstanciarEscena.createNewStage(
                    "/view/compra/editarProveedor.fxml", "Proveedor",
                    new Consumer<EditarProveedorController>() {
                        @Override
                        public void accept(EditarProveedorController ventana) {
                            if (proveedor != null) {
                                ventana.setProveedor(proveedor);
                            }
                        }
                    });

            if (c.isGuardado()) {
                cargarProveedores();
                cargarListaProveedores();
                cargarCompras();
            }
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo abrir la ventana del proveedor.");
        } catch (DaoException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    /**
     * Abre la ventana de registrar compra y, si se guarda, recarga las compras y el historial.
     *
     * @param idProveedor proveedor que se selecciona al abrir (solo se usa si hay líneas)
     * @param lineas líneas con las que se rellena, o {@code null} para empezar vacía
     */
    private void abrirVentanaCompra(final int idProveedor, final List<LineaCompra> lineas) {
        try {
            RegistrarCompraController c = InstanciarEscena.createNewStage(
                    "/view/compra/registrarCompra.fxml", "Registrar compra",
                    new Consumer<RegistrarCompraController>() {
                        @Override
                        public void accept(RegistrarCompraController ventana) {
                            if (usuario != null && usuario.getId() != null) {
                                ventana.setUsuarioId(usuario.getId());
                            }
                            if (lineas != null) {
                                ventana.setLineasSugeridas(idProveedor, lineas);
                            }
                        }
                    });

            if (c.isGuardado()) {
                cargarCompras();
                if (proveedorSeleccionado != null) {
                    cargarHistorialProveedor(proveedorSeleccionado);
                }
            }
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo abrir la ventana de la compra.");
        } catch (DaoException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    /**
     * Genera la lista de compra sugerida con los productos activos cuyo stock está por debajo
     * del mínimo. Para cada uno calcula la cantidad que falta hasta el mínimo, su proveedor
     * habitual (el primero de su lista) y el precio de su última compra.
     */
    private void generarListaSugerida() {
        Map<Integer, Double> ultimosPrecios = obtenerUltimosPrecios();

        contenedorSugerencias.getChildren().clear();
        lineasSugeridas.clear();
        proveedoresSugeridos.clear();
        checksSugeridos.clear();

        List<Producto> productos = comprasService.listarProductos();
        for (Producto p : productos) {
            if (p.isActivo() && p.getStock_actual() < p.getStock_minimo()) {
                int cantidad = p.getStock_minimo() - p.getStock_actual();

                double precio = 0;
                if (ultimosPrecios.containsKey(p.getId())) {
                    precio = ultimosPrecios.get(p.getId());
                }

                int idProveedor = 0;
                if (!p.getProveedores_id().isEmpty()) {
                    idProveedor = p.getProveedores_id().get(0);
                }

                lineasSugeridas.add(new LineaCompra(p.getId(), p.getNombre(), cantidad, precio,
                        p.getIva(), Math.round(cantidad * precio * 100) / 100.0));
                proveedoresSugeridos.add(idProveedor);
                // crearTarjetaSugerencia añade su CheckBox a checksSugeridos
                contenedorSugerencias.getChildren().add(crearTarjetaSugerencia(p));
            }
        }

        if (lineasSugeridas.isEmpty()) {
            lblSubtituloSugerencias.setText("No hay productos con stock bajo o agotados.");
        } else {
            lblSubtituloSugerencias.setText("Generada con " + lineasSugeridas.size()
                    + " productos en stock bajo o agotados.");
        }
        btnCrearCompra.setDisable(lineasSugeridas.isEmpty());
    }

    /**
     * Busca el último precio de compra de cada producto. Como las compras vienen ordenadas por
     * identificador, las más nuevas sustituyen a las antiguas.
     *
     * @return precio por unidad de cada producto, por su identificador
     */
    private Map<Integer, Double> obtenerUltimosPrecios() {
        Map<Integer, Double> precios = new HashMap<>();
        for (Compra c : comprasService.listarCompras()) {
            for (LineaCompra l : c.getDetalle()) {
                precios.put(l.getProducto_id(), l.getPrecio_unidad());
            }
        }
        return precios;
    }

    /**
     * Carga los proveedores en el mapa {@link #proveedores}.
     */
    private void cargarProveedores() {
        proveedores.clear();
        for (Proveedor p : comprasService.listarProveedores()) {
            proveedores.put(p.getId(), p);
        }
    }

    /**
     * Pinta todas las compras en la pestaña Compras y actualiza el contador.
     */
    private void cargarCompras() {
        List<Compra> compras = comprasService.listarCompras();

        contenedorCompras.getChildren().clear();
        for (Compra c : compras) {
            contenedorCompras.getChildren().add(crearFilaCompra(c));
        }

        lblContador.setText(compras.size() + " compras");
    }

    /**
     * Crea la fila de una compra con fecha, proveedor, factura, número de líneas e importe.
     *
     * @param c compra que se va a mostrar
     * @return la fila lista para añadir a {@link #contenedorCompras}
     */
    private HBox crearFilaCompra(Compra c) {
        Proveedor p = proveedores.get(c.getProveedor_id());
        String nombreProveedor = (p != null) ? p.getNombre() : "Desconocido"; //Operador ternario

        Label importe = celda(String.format("%.2f €", c.getTotal()), 110);
        importe.getStyleClass().add("compra-dato-destacado");

        HBox fila = new HBox(
                celda(LocalDate.parse(c.getFecha()).format(FORMATO_FECHA), 80),
                celda(nombreProveedor, 180),
                celda(c.getNumero_factura(), 120),
                celda(String.valueOf(c.getDetalle().size()), 70),
                importe
        );
        fila.getStyleClass().add("compra-fila");
        return fila;
    }

    /**
     * Crea una etiqueta con un ancho fijo, para que las columnas queden alineadas con la cabecera.
     *
     * @param texto texto de la celda
     * @param ancho ancho en píxeles
     * @return la etiqueta creada
     */
    private Label celda(String texto, double ancho) {
        Label lbl = new Label(texto);
        lbl.setPrefWidth(ancho);
        return lbl;
    }

    /**
     * Pinta la lista de proveedores y selecciona el que estaba seleccionado o, si no hay, el primero.
     */
    private void cargarListaProveedores() {
        contenedorListaProveedores.getChildren().clear();

        HBox primeraFila = null;
        Proveedor primerProveedor = null;
        HBox filaAnterior = null;
        Proveedor anterior = null;

        for (Proveedor p : proveedores.values()) {
            HBox fila = crearFilaProveedor(p);
            contenedorListaProveedores.getChildren().add(fila);

            if (primeraFila == null) {
                primeraFila = fila;
                primerProveedor = p;
            }
            if (proveedorSeleccionado != null && p.getId() == proveedorSeleccionado.getId()) {
                filaAnterior = fila;
                anterior = p;
            }
        }

        filaProveedorSeleccionada = null;
        if (anterior != null) {
            seleccionarProveedor(anterior, filaAnterior);
        } else if (primerProveedor != null) {
            seleccionarProveedor(primerProveedor, primeraFila);
        }
    }

    /**
     * Crea la fila de un proveedor. Al hacer clic en ella se selecciona.
     *
     * @param p proveedor que se va a mostrar
     * @return la fila lista para añadir a {@link #contenedorListaProveedores}
     */
    private HBox crearFilaProveedor(Proveedor p) {
        String estado = "Inactivo";
        if (p.isActivo()) {
            estado = "Activo";
        }

        HBox fila = new HBox(
                celda(p.getNombre(), 170),
                celda(p.getContacto().getTelefono(), 110),
                celda(estado, 80)
        );
        fila.getStyleClass().add("compra-fila");

        fila.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                seleccionarProveedor(p, fila);
            }
        });

        return fila;
    }

    /**
     * Resalta la fila del proveedor, rellena su ficha y carga su historial de compras.
     *
     * @param p proveedor seleccionado
     * @param fila fila del proveedor en la lista
     */
    private void seleccionarProveedor(Proveedor p, HBox fila) {
        if (filaProveedorSeleccionada != null) {
            filaProveedorSeleccionada.getStyleClass().remove("proveedor-fila-seleccionada");
        }
        fila.getStyleClass().add("proveedor-fila-seleccionada");
        filaProveedorSeleccionada = fila;
        proveedorSeleccionado = p;

        lblInicialesProveedor.setText(obtenerIniciales(p.getNombre()));
        lblNombreProveedor.setText(p.getNombre());
        lblContactoProveedor.setText(p.getContacto().getPersona() + " · "
                + p.getContacto().getTelefono() + " · " + p.getContacto().getEmail());
        lblCondicionesProveedor.setText("Entrega en " + p.getPlazo_entrega() + " días · "
                + String.join(", ", p.getDias_laborales()));

        cargarHistorialProveedor(p);
    }

    /**
     * Pinta las compras hechas a un proveedor.
     *
     * @param p proveedor del que se muestra el historial
     */
    private void cargarHistorialProveedor(Proveedor p) {
        List<Compra> compras = comprasService.listarComprasPorProveedor(p.getId());

        contenedorHistorialProveedor.getChildren().clear();
        for (Compra c : compras) {
            HBox fila = new HBox(
                    celda(LocalDate.parse(c.getFecha()).format(FORMATO_FECHA), 80),
                    celda(c.getNumero_factura(), 130),
                    celda(String.valueOf(c.getDetalle().size()), 70),
                    celda(String.format("%.2f €", c.getTotal()), 100)
            );
            fila.getStyleClass().add("compra-fila");
            contenedorHistorialProveedor.getChildren().add(fila);
        }

        lblSubtituloHistorial.setText(compras.size() + " compras");
    }

    /**
     * Saca las iniciales de las dos primeras palabras de un nombre, para el avatar.
     *
     * @param nombre nombre del proveedor
     * @return las iniciales en mayúsculas
     */
    private String obtenerIniciales(String nombre) {
        String[] palabras = nombre.split(" ");
        String iniciales = "";
        for (int i = 0; i < palabras.length && iniciales.length() < 2; i++) {
            if (!palabras[i].isEmpty()) {
                iniciales += palabras[i].charAt(0);
            }
        }
        return iniciales.toUpperCase();
    }

    /**
     * Crea la tarjeta de un producto de la lista sugerida, con su CheckBox, nombre, proveedor
     * habitual, cantidad a pedir y estado («Agotado» o «Stock bajo»).
     * El CheckBox se guarda en {@link #checksSugeridos}.
     *
     * @param p producto con stock bajo
     * @return la tarjeta lista para añadir a {@link #contenedorSugerencias}
     */
    private HBox crearTarjetaSugerencia(Producto p) {
        int cantidad = p.getStock_minimo() - p.getStock_actual();
        String nombreProveedor = "Sin proveedor";
        if (!p.getProveedores_id().isEmpty()) {
            Proveedor habitual = proveedores.get(p.getProveedores_id().get(0));
            if (habitual != null) {
                nombreProveedor = habitual.getNombre();
            }
        }
        Label lblNombre = new Label(p.getNombre());
        lblNombre.getStyleClass().add("compra-dato-destacado");

        Label lblProveedor = new Label(nombreProveedor);
        lblProveedor.getStyleClass().add("texto-secundario");

        VBox izquierda = new VBox(2, lblNombre, lblProveedor);

        Label lblCantidad = new Label(cantidad + " uds");
        lblCantidad.getStyleClass().add("compra-dato-destacado");

        Label lblEstado = new Label();
        if (p.getStock_actual() == 0) {
            lblEstado.setText("Agotado");
            lblEstado.getStyleClass().add("estado-error");
        } else {
            lblEstado.setText("Stock bajo");
            lblEstado.getStyleClass().add("estado-aviso");
        }

        VBox derecha = new VBox(4, lblCantidad, lblEstado);
        derecha.setAlignment(Pos.CENTER_RIGHT);
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        CheckBox chkIncluir = new CheckBox();
        chkIncluir.setSelected(true);
        checksSugeridos.add(chkIncluir);

        HBox tarjeta = new HBox(10, chkIncluir, izquierda, espacio, derecha);
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.getStyleClass().add("compra-fila");
        return tarjeta;
    }
}
