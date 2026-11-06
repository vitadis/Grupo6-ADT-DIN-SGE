package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
 * Controller de la pantalla de administración.
 *
 * @author Joel
 */
public class AdministracionController {

    // Clicks necesarios sobre una fila para abrir su dialog (1 = un click, 2 = doble click)
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

    // ---- Logger ----
    @FXML
    private ListView<RegistroLog> listaLogger;

    private final AdministracionService servicio = AdministracionService.getInstance();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    // Usuario que ha iniciado sesión (quien administra) y usuario de la fila elegida en la tabla
    private Usuario usuarioSesion;
    private Usuario usuarioSeleccionado;

    // Debe llamarse desde donde se navega a esta pantalla, pasando al usuario logueado
    public void setUsuarioSesion(Usuario usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    @FXML
    private void initialize() {
        configurarTabla();
        configurarContadorPermisos();
        configurarMenuLogger();

        // Al cambiar de pestaña se actualiza la cabecera y se recarga lo dinámico
        tabPaneAdmin.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> obs, Tab anterior, Tab actual) {
                actualizarCabecera(actual);
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

    // Tabla de usuarios
    private void configurarTabla() {
        tablaUsuarios.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tablaUsuarios.setItems(listaUsuarios);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        colPermisos.setCellValueFactory(new PropertyValueFactory<>("permisos"));

        // Permisos en comboBox desplegable
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

    // Captura el click sobre cualquier celda de la fila y abre el dialog de ese usuario
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

    private void cargarUsuarios() {
        try {
            listaUsuarios.setAll(servicio.listarUsuarios());
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    // Permisos (colección "permisos" -> lista "todos")
    private void configurarContadorPermisos() {
        contenedorPermisos.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> cambio) {
                lblContadorPermisos.setText(contenedorPermisos.getChildren().size() + " permisos");
            }
        });
    }

    private void cargarPermisos() {
        try {
            contenedorPermisos.getChildren().clear();
            for (String permiso : servicio.listarPermisos()) {
                contenedorPermisos.getChildren().add(new Label(permiso));
            }
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    // Logger (se recarga cada vez que se entra en la pestaña)
    private void cargarLogger() {
        listaLogger.getItems().setAll(AppLogger.getInstance().leer());
    }

    // Click derecho sobre el historial -> "Copiar" (copia las líneas seleccionadas)
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

    // Cabecera dinámica
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
            hayAccion = false; // Los permisos solo se consultan
        } else if (tab == tabLogger) {
            lblTitulo.setText("Logger del sistema");
            hayAccion = false; // El logger solo muestra el historial
        } else if (tab == tabConfiguracion) {
            lblTitulo.setText("Configuración del sistema");
            hayAccion = false; // Configuración no tiene acción principal
        }
        btnAccionPrincipal.setVisible(hayAccion);
        btnAccionPrincipal.setManaged(hayAccion);
    }

    // Botones (solo imprimen lo que harán)
    @FXML
    private void onAccionPrincipal() {
        Tab tab = tabPaneAdmin.getSelectionModel().getSelectedItem();
        if (tab == tabUsuarios) {
            abrirAltaUsuario();
        }
    }

    // Alta de usuario: el mismo dialog de usuario, en modo alta
    private void abrirAltaUsuario() {
        try {
            InstanciarEscena.createNewStage("/view/admin/usuarioView.fxml", "Alta de usuario", this::dialogAlta);
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo abrir el dialog de alta.");
        }
    }

    private void dialogAlta(Object controller) {
        try {
            DialogUsuarioController dialog = (DialogUsuarioController) controller;
            dialog.setAcciones(
                    usuario -> accionCrear(dialog, usuario),
                    usuario -> accionModificar(dialog, usuario),
                    usuario -> accionEliminar(dialog, usuario));
            dialog.setPermisosDisponibles(servicio.listarPermisos());
            dialog.setModoAlta();
        } catch (Exception e) {
            Alertas.mostrarError("Error con el dialog de alta: " + e.getMessage());
        }
    }

    @FXML
    private void onCerrarSesion(ActionEvent event) {
        Alertas.mostrarConfirmacion("Confirmación", "¿Seguro que quieres cerrar sesión?", () -> {
            try {
                Button btnCS = (Button) event.getSource();
                Stage stage = (Stage) btnCS.getScene().getWindow();

                InstanciarEscena.cambiarVista(stage, "/view/loginView.fxml", "Inicio Sesión");
            } catch (IOException ex) {
                Alertas.mostrarError("No se pudo cerrar sesión: " + ex.getMessage());
            }
        });
    }

    // Dialog del usuario elegido en la tabla
    private void abrirDialogUsuario() {
        if (usuarioSeleccionado == null) {
            return;
        }

        try {
            InstanciarEscena.createNewStage("/view/admin/usuarioView.fxml", "Información de usuario", this::dialogUsuario);
        } catch (IOException e) {
            Alertas.mostrarError("No se pudo abrir el dialog de usuario.");
        }
    }

    // verifica el usuario de la fila y se lo entrega al dialog
    private void dialogUsuario(Object controller) {
        try {
            DialogUsuarioController dialog = (DialogUsuarioController) controller;
            dialog.setAcciones(
                    usuario -> accionCrear(dialog, usuario),
                    usuario -> accionModificar(dialog, usuario),
                    usuario -> accionEliminar(dialog, usuario));

            // El usuario de la fila cumple las reglas del modelo
            usuarioSeleccionado.validar();

            dialog.setPermisosDisponibles(servicio.listarPermisos());
            dialog.setUsuario(usuarioSeleccionado);
        } catch (Exception e) {
            Alertas.mostrarError("Error con el dialog de usuario: " + e.getMessage());
        }
    }

    // Acciones de los botones del dialog (negocio, validaciones y log)
    private void accionCrear(DialogUsuarioController dialog, Usuario nuevo) {
        try {
            Empleado empleado = nuevo.getEmpleado();
            empleado.setFecha_alta(LocalDate.now());
            empleado.validar();
            nuevo.setUsername(generarUsername(empleado));

            servicio.altaUsuario(nuevo);
            cargarUsuarios();
            dialog.cerrar();
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    private void accionModificar(DialogUsuarioController dialog, Usuario usuario) {
        try {
            // El usuario puede no tener empleado asociado
            if (usuario.getEmpleado() != null) {
                usuario.getEmpleado().validar();
            }
            servicio.modificarUsuario(usuario);

            cargarUsuarios();
            dialog.cerrar();
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

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
            cargarUsuarios();
            dialog.cerrar();
        } catch (RuntimeException e) {
            Alertas.mostrarError(e.getMessage());
        }
    }

    // Username "nombre.apellido" con la primera palabra de cada uno
    private String generarUsername(Empleado empleado) {
        String nombre = empleado.getNombre().trim().split("\\s+")[0];
        String apellido = empleado.getApellido().trim().split("\\s+")[0];
        return (nombre + "." + apellido).toLowerCase();
    }
}
