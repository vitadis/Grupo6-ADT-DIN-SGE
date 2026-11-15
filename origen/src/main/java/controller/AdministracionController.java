package controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import model.Empleado;
import model.RegistroLog;
import model.Usuario;
import services.AdministracionService;
import util.Alertas;
import util.AppLogger;

/**
 * FXML Controller class del módulo Administración
 *
 * @author Joel
 */
public class AdministracionController implements UsuarioImplementacion {

    private static final int CLICKS_PARA_ABRIR = 2;

    // ---- Cabecera ----
    @FXML
    private Label lblRuta;
    @FXML
    private Label lblTitulo;
    @FXML
    private Button btnAccionPrincipal;

    // ---- Pestañas ----
    @FXML
    private TabPane tabPaneAdmin;
    @FXML
    private Tab tabUsuarios;
    @FXML
    private Tab tabPermisos;
    @FXML
    private Tab tabLogger;
    @FXML
    private Tab tabConfiguracion;

    // ---- Usuarios y empleados ----
    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, Integer> colId;
    @FXML
    private TableColumn<Usuario, String> colUsername;
    @FXML
    private TableColumn<Usuario, String> colRol;
    @FXML
    private TableColumn<Usuario, List<String>> colPermisos;

    // ---- Permisos ----
    @FXML
    private VBox contenedorPermisos;
    @FXML
    private Label lblContadorPermisos;

    // ---- List Logger ----
    @FXML
    private ListView<RegistroLog> listaLogger;

    // ---- Servicios y estado ----
    private final AdministracionService servicio = AdministracionService.getInstance();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();
    private final AppLogger logger = AppLogger.getInstance();
    private Usuario usuarioSeleccionado;
    private Usuario usuario;

    /**
     * Inicializa los componentes visuales, los listeners de pestañas y carga la
     * lista inicial de usuarios.
     */
    @FXML
    private void initialize() {
        configurarTabla();
        configurarContadorPermisos();
        configurarMenuLogger();

        tabPaneAdmin.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> obs, Tab anterior, Tab actual) {
                actualizarCabecera(actual);
                if (actual != null) {
                    logger.log(Level.INFO, "Cambio a pestaña: " + actual.getText()); // <-- AQUÍ
                }
                if (actual == tabPermisos) {
                    cargarPermisos();
                } else if (actual == tabLogger) {
                    cargarLogger();
                }
            }
        });
        actualizarCabecera(tabPaneAdmin.getSelectionModel().getSelectedItem());

        cargarUsuarios();
    }

    /**
     * Enlaza las columnas de la tabla de usuarios, renderiza los permisos en un
     * ComboBox y activa el clic por fila.
     */
    private void configurarTabla() {
        tablaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tablaUsuarios.setItems(listaUsuarios);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        colPermisos.setCellValueFactory(new PropertyValueFactory<>("permisos"));

        colPermisos.setCellFactory(new Callback<TableColumn<Usuario, List<String>>, TableCell<Usuario, List<String>>>() {
            @Override
            public TableCell<Usuario, List<String>> call(TableColumn<Usuario, List<String>> columna) {
                return new TableCell<Usuario, List<String>>() {
                    private final ComboBox<String> comboPermisos = new ComboBox<String>();

                    {
                        comboPermisos.setMaxWidth(Double.MAX_VALUE);
                    }

                    @Override
                    protected void updateItem(List<String> permisos, boolean vacio) {
                        super.updateItem(permisos, vacio);
                        if (vacio || permisos == null) {
                            setGraphic(null);
                        } else {
                            comboPermisos.setItems(FXCollections.observableArrayList(permisos));
                            comboPermisos.setValue(null);
                            comboPermisos.setPromptText(permisos.isEmpty() ? "Sin permisos" : permisos.size() + " permisos");
                            setGraphic(comboPermisos);
                        }
                    }
                };
            }
        });

        configurarClickFila();
    }

    /**
     * Detecta el doble clic principal sobre una fila no vacía y abre el diálogo
     * de detalle del usuario.
     */
    private void configurarClickFila() {
        tablaUsuarios.setRowFactory(new Callback<TableView<Usuario>, TableRow<Usuario>>() {
            @Override
            public TableRow<Usuario> call(TableView<Usuario> tabla) {
                final TableRow<Usuario> fila = new TableRow<>();
                fila.setOnMouseClicked((MouseEvent evento) -> {
                    if (!fila.isEmpty()
                            && evento.getButton() == MouseButton.PRIMARY
                            && evento.getClickCount() == CLICKS_PARA_ABRIR) {
                        usuarioSeleccionado = fila.getItem();
                        abrirDialogUsuario();
                    }
                });
                return fila;
            }
        });
    }

    /**
     * Obtiene los usuarios desde el servicio y actualiza la lista observable de
     * la tabla.
     */
    private void cargarUsuarios() {
        try {
            listaUsuarios.setAll(servicio.listarUsuarios());
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar usuarios: " + e.getMessage(), e);
        }
    }

    /**
     * Escucha los cambios en el contenedor de permisos para actualizar el
     * contador en pantalla.
     */
    private void configurarContadorPermisos() {
        contenedorPermisos.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> cambio) {
                lblContadorPermisos.setText(contenedorPermisos.getChildren().size() + " permisos");
            }
        });
    }

    /**
     * Limpia el contenedor y carga como etiquetas todos los permisos
     * disponibles en el sistema.
     */
    private void cargarPermisos() {
        try {
            contenedorPermisos.getChildren().clear();
            for (String permiso : servicio.listarPermisos()) {
                contenedorPermisos.getChildren().add(new Label(permiso));
            }
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar permisos: " + e.getMessage(), e);
        }
    }

    /**
     * Recarga los registros del historial del sistema en la lista visual.
     */
    private void cargarLogger() {
        try {
            listaLogger.getItems().setAll(logger.leer());
        } catch (RuntimeException e) {
            Alertas.mostrarError("No se pudo leer el historial de logs.");
            logger.log(Level.SEVERE, "Error al leer registros del logger: " + e.getMessage(), e);
        }
    }

    /**
     * Habilita la selección múltiple en el logger y añade un menú contextual
     * para copiar líneas al portapapeles.
     */
    private void configurarMenuLogger() {
        listaLogger.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final MenuItem copiar = new MenuItem("Copiar");
        copiar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evento) {
                StringBuilder texto = new StringBuilder();
                for (RegistroLog registro : listaLogger.getSelectionModel().getSelectedItems()) {
                    if (texto.length() > 0) {
                        texto.append(System.lineSeparator());
                    }
                    texto.append(String.valueOf(registro));
                }
                ClipboardContent contenido = new ClipboardContent();
                contenido.putString(texto.toString());
                Clipboard.getSystemClipboard().setContent(contenido);
            }
        });

        ContextMenu menu = new ContextMenu(copiar);
        menu.setOnShowing(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent evento) {
                copiar.setDisable(listaLogger.getSelectionModel().getSelectedItems().isEmpty());
            }
        });
        listaLogger.setContextMenu(menu);
    }

    /**
     * Actualiza la ruta, el título y la visibilidad del botón principal según
     * la pestaña seleccionada.
     */
    private void actualizarCabecera(Tab tab) {
        if (tab == null) {
            return;
        }
        lblRuta.setText("Administración > " + tab.getText());

        boolean hayAccion = true;
        if (tab == tabUsuarios) {
            lblTitulo.setText("Usuarios y empleados");
            btnAccionPrincipal.setText("Alta usuario");
        } else if (tab == tabPermisos) {
            lblTitulo.setText("Permisos");
            hayAccion = false;
        } else if (tab == tabLogger) {
            lblTitulo.setText("Logger del sistema");
            hayAccion = false;
        } else if (tab == tabConfiguracion) {
            lblTitulo.setText("Configuración del sistema");
            hayAccion = false;
        }
        btnAccionPrincipal.setVisible(hayAccion);
        btnAccionPrincipal.setManaged(hayAccion);
    }

    // ---------------- Methods FXML ----------------
    /**
     * Ejecuta la acción principal correspondiente a la pestaña activa.
     */
    @FXML
    private void onAccionPrincipal() {
        Tab tab = tabPaneAdmin.getSelectionModel().getSelectedItem();
        if (tab == tabUsuarios) {
            abrirAltaUsuario();
        }
    }

    /**
     * Limpia la selección previa y abre una nueva ventana modal con el
     * formulario de alta de usuario.
     */
    private void abrirAltaUsuario() {
        usuarioSeleccionado = null;
        tablaUsuarios.getSelectionModel().clearSelection();
        try {
            InstanciarEscena.createNewStage("/view/admin/usuarioView.fxml", "Alta de usuario", this::dialogAlta);
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo abrir el dialog de alta.");
            logger.log(Level.SEVERE, "Error al abrir vista de alta de usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Configura el controlador del diálogo en modo alta limpiando datos previos
     * y asignando callbacks.
     *
     * @param controller instancia del controlador del diálogo
     */
    private void dialogAlta(Object controller) {
        try {
            DialogUsuarioController dialog = (DialogUsuarioController) controller;
            dialog.setAcciones(
                    u -> accionCrear(dialog, u),
                    u -> accionModificar(dialog, u),
                    u -> accionEliminar(dialog, u));
            dialog.setPermisosDisponibles(servicio.listarPermisos());
            dialog.setModoAlta();
        } catch (Exception e) {
            Alertas.mostrarError("Error con el dialog de alta: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al inicializar dialog de alta: " + e.getMessage(), e);
        }
    }

    /**
     * Solicita confirmación al usuario y redirige a la vista de inicio de
     * sesión.
     *
     * @param event evento de acción del botón
     */
    @FXML
    private void onCerrarSesion(ActionEvent event) {
        Alertas.mostrarConfirmacion("Confirmación", "¿Seguro que quieres cerrar sesión?", () -> {
            try {
                Button btnCS = (Button) event.getSource();
                Stage stage = (Stage) btnCS.getScene().getWindow();

                logger.log(Level.INFO, "Cierre de sesión del usuario: " + (usuario != null ? usuario.getUsername() : "desconocido"));
                InstanciarEscena.cambiarVista(stage, "/view/loginView.fxml", "Inicio Sesión");
            } catch (IOException ex) {
                Alertas.mostrarError("No se pudo cerrar sesión: " + ex.getMessage());
                logger.log(Level.SEVERE, "Error al cerrar sesión: " + ex.getMessage(), ex);
            }
        });
    }

    /**
     * Abre la ventana modal con la información del usuario seleccionado en la
     * tabla.
     */
    private void abrirDialogUsuario() {
        if (usuarioSeleccionado == null) {
            return;
        }

        try {
            InstanciarEscena.createNewStage("/view/admin/usuarioView.fxml", "Información de usuario", this::dialogUsuario);
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo abrir el dialog de usuario.");
            logger.log(Level.SEVERE, "Error al abrir vista de usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Carga el usuario seleccionado en el controlador del diálogo de edición.
     *
     * @param controller instancia del controlador del diálogo
     */
    private void dialogUsuario(Object controller) {
        try {
            DialogUsuarioController dialog = (DialogUsuarioController) controller;
            dialog.setAcciones(
                    u -> accionCrear(dialog, u),
                    u -> accionModificar(dialog, u),
                    u -> accionEliminar(dialog, u));

            dialog.setPermisosDisponibles(servicio.listarPermisos());
            dialog.setUsuario(usuarioSeleccionado);
            logger.log(Level.INFO, "Consulta de ficha del usuario: " + usuarioSeleccionado.getUsername());
        } catch (Exception e) {
            Alertas.mostrarError("Error con el dialog de usuario: " + e.getMessage());
            logger.log(Level.SEVERE, "Error al cargar datos en dialog de usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Genera el username si hay empleado asociado, delega el alta al servicio y
     * refresca la tabla.
     *
     * @param dialog controlador del diálogo activo
     * @param nuevo usuario a dar de alta
     */
    private void accionCrear(DialogUsuarioController dialog, Usuario nuevo) {
        try {
            if (nuevo != null && nuevo.getEmpleado() != null) {
                nuevo.setUsername(generarUsername(nuevo.getEmpleado()));
            }

            servicio.altaUsuario(nuevo);
            logger.log(Level.INFO, "Usuario creado: " + nuevo.getUsername() + " (Rol: " + nuevo.getRol() + ")");
            cargarUsuarios();
            dialog.cerrar();
        } catch (IllegalArgumentException e) {
            Alertas.mostrarError(e.getMessage());
            logger.log(Level.WARNING, "Validación fallida al crear usuario: " + e.getMessage());
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
            logger.log(Level.SEVERE, "Error al crear usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Delega la validación y actualización del usuario al servicio y refresca
     * la tabla.
     *
     * @param dialog controlador del diálogo activo
     * @param usuario usuario con los cambios aplicados
     */
    private void accionModificar(DialogUsuarioController dialog, Usuario usuario) {
        try {
            servicio.modificarUsuario(usuario);
            logger.log(Level.INFO, "Usuario modificado: " + usuario.getUsername() + " (Rol: " + usuario.getRol() + ")");

            cargarUsuarios();
            dialog.cerrar();
        } catch (IllegalArgumentException e) {
            Alertas.mostrarError(e.getMessage());
            logger.log(Level.WARNING, "Validación o modificación rechazada para '" + usuario.getUsername() + "': " + e.getMessage());
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
            logger.log(Level.SEVERE, "Error al modificar usuario '" + usuario.getUsername() + "': " + e.getMessage(), e);
        }
    }

    /**
     * Pide confirmación, delega la eliminación al servicio y recarga la tabla.
     *
     * @param dialog controlador del diálogo activo
     * @param usuario usuario a eliminar
     */
    private void accionEliminar(DialogUsuarioController dialog, Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar usuario");
        confirmacion.setHeaderText("¿Eliminar a '" + usuario.getUsername() + "'?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> respuesta = confirmacion.showAndWait();
        if (!respuesta.isPresent() || respuesta.get() != ButtonType.OK) {
            return;
        }

        try {
            servicio.eliminarUsuario(usuario);
            logger.log(Level.INFO, "Usuario eliminado: " + usuario.getUsername());
            cargarUsuarios();
            dialog.cerrar();
        } catch (IllegalArgumentException e) {
            Alertas.mostrarError(e.getMessage());
            logger.log(Level.WARNING, "Validación fallida al eliminar usuario: " + e.getMessage());
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
            logger.log(Level.SEVERE, "Error al eliminar usuario '" + usuario.getUsername() + "': " + e.getMessage(), e);
        }
    }

    /**
     * Construye el nombre de usuario en minúsculas uniendo el primer nombre y
     * el primer apellido con un punto.
     *
     * @param empleado empleado del que se extraen nombre y apellido
     * @return username generado en formato nombre.apellido
     */
    private String generarUsername(Empleado empleado) {
        String nombre = empleado.getNombre().trim().split("\\s+")[0];
        String apellido = empleado.getApellido().trim().split("\\s+")[0];
        return (nombre + "." + apellido).toLowerCase();
    }

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            logger.setUser(usuario.getUsername());
        }
    }
}
