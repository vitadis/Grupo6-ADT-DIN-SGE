package controller;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Empleado;
import model.Usuario;

/**
 * FXML Controller class para el diálogo de alta, consulta y edición de
 * usuarios.
 *
 * @author Joel
 */
public class DialogUsuarioController {

    // ---- Estado y callbacks ----
    private Usuario usuarioGestionado;
    private List<String> permisosCatalogo = new ArrayList<String>();
    private final Set<String> permisosSeleccionados = new LinkedHashSet<String>();
    private final List<String> permisosDisponibles = new ArrayList<String>();

    private Consumer<Usuario> alCrear;
    private Consumer<Usuario> alModificar;
    private Consumer<Usuario> alEliminar;

    // ---- Datos de usuario ----
    @FXML
    private TextField tfId;
    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private TextField tfRol;
    @FXML
    private MenuButton mbPermisos;
    @FXML
    private CheckBox chkActivo;
    @FXML
    private TextField tfCreadoEn;

    // ---- Datos de empleado ----
    @FXML
    private VBox panelEmpleado;
    @FXML
    private TextField tfEmpleadoNombre;
    @FXML
    private TextField tfEmpleadoApellido;
    @FXML
    private TextField tfEmpleadoDni;
    @FXML
    private TextField tfEmpleadoPuesto;
    @FXML
    private TextField tfEmpleadoEmail;
    @FXML
    private TextField tfEmpleadoTelefono;
    @FXML
    private TextField tfEmpleadoFechaAlta;
    @FXML
    private Label lblSinEmpleado;

    // ---- Botones ----
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnAlta;
    @FXML
    private Button btnCancelar;

    /**
     * Inicializa el diálogo ocultando por defecto los botones de modo alta.
     */
    @FXML
    private void initialize() {
        mostrarBotones(false);
    }

    /**
     * Asigna los callbacks ejecutados al crear, modificar o eliminar un
     * usuario.
     *
     * @param alCrear acción al dar de alta
     * @param alModificar acción al guardar cambios
     * @param alEliminar acción al eliminar
     */
    public void setAcciones(Consumer<Usuario> alCrear,
            Consumer<Usuario> alModificar,
            Consumer<Usuario> alEliminar) {
        this.alCrear = alCrear;
        this.alModificar = alModificar;
        this.alEliminar = alEliminar;
    }

    /**
     * Define el catálogo de permisos disponibles que se mostrarán en el
     * selector.
     *
     * @param permisos lista de permisos disponibles
     */
    public void setPermisosDisponibles(List<String> permisos) {
        this.permisosCatalogo = permisos != null ? permisos : new ArrayList<String>();
    }

    private void mostrarBotones(boolean modoAlta) {
        ponerVisible(btnModificar, !modoAlta);
        ponerVisible(btnEliminar, !modoAlta);
        ponerVisible(btnAlta, modoAlta);
        ponerVisible(btnCancelar, modoAlta);
    }

    private void ponerVisible(Node nodo, boolean visible) {
        if (nodo != null) {
            nodo.setVisible(visible);
            nodo.setManaged(visible);
        }
    }

    /**
     * Carga en el formulario los datos de un usuario existente en modo edición.
     *
     * @param usuario usuario a consultar o editar
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioGestionado = usuario;

        tfUsername.setEditable(false);
        tfUsername.setPromptText("");
        tfEmpleadoNombre.setEditable(false);
        tfEmpleadoApellido.setEditable(false);
        tfEmpleadoDni.setEditable(false);

        tfId.setText(String.valueOf(usuario.getId()));
        tfUsername.setText(usuario.getUsername());
        pfPassword.setText(usuario.getPassword_hash());
        tfRol.setText(usuario.getRol());
        chkActivo.setSelected(usuario.isActivo());
        tfCreadoEn.setText(String.valueOf(usuario.getCreado_en()));

        cargarPermisos(usuario);
        cargarEmpleado(usuario.getEmpleado());
    }

    /**
     * Prepara el formulario con campos vacíos y editables para dar de alta un
     * nuevo usuario.
     */
    public void setModoAlta() {
        this.usuarioGestionado = null;
        mostrarBotones(true);

        tfId.clear();
        tfId.setDisable(true);
        tfCreadoEn.clear();
        tfCreadoEn.setDisable(true);

        tfUsername.clear();
        tfUsername.setDisable(true);
        tfUsername.setPromptText("Se genera automáticamente");

        pfPassword.clear();
        tfRol.clear();
        chkActivo.setSelected(true);

        cargarPermisos(new Usuario());

        panelEmpleado.setVisible(true);
        panelEmpleado.setManaged(true);
        lblSinEmpleado.setVisible(false);
        lblSinEmpleado.setManaged(false);

        tfEmpleadoNombre.clear();
        tfEmpleadoNombre.setEditable(true);
        tfEmpleadoApellido.clear();
        tfEmpleadoApellido.setEditable(true);
        tfEmpleadoDni.clear();
        tfEmpleadoDni.setEditable(true);
        tfEmpleadoPuesto.clear();
        tfEmpleadoPuesto.setEditable(true);
        tfEmpleadoEmail.clear();
        tfEmpleadoEmail.setEditable(true);
        tfEmpleadoTelefono.clear();
        tfEmpleadoTelefono.setEditable(true);
        tfEmpleadoFechaAlta.clear();
        tfEmpleadoFechaAlta.setDisable(true);
    }

    /**
     * Cierra la ventana modal actual.
     */
    public void cerrar() {
        ((Stage) tfId.getScene().getWindow()).close();
    }

    // ---- Permisos ----
    private void cargarPermisos(Usuario usuario) {
        permisosSeleccionados.clear();
        if (usuario.getPermisos() != null) {
            permisosSeleccionados.addAll(usuario.getPermisos());
        }

        permisosDisponibles.clear();
        permisosDisponibles.addAll(permisosCatalogo);
        for (String permiso : permisosSeleccionados) {
            if (!permisosDisponibles.contains(permiso)) {
                permisosDisponibles.add(permiso);
            }
        }

        mbPermisos.getItems().clear();
        for (final String permiso : permisosDisponibles) {
            final CheckBox check = new CheckBox(permiso);
            check.setSelected(permisosSeleccionados.contains(permiso));
            check.setMaxWidth(Double.MAX_VALUE);
            check.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent evento) {
                    if (check.isSelected()) {
                        permisosSeleccionados.add(permiso);
                    } else {
                        permisosSeleccionados.remove(permiso);
                    }
                    actualizarResumenPermisos();
                }
            });

            CustomMenuItem item = new CustomMenuItem(check);
            item.setHideOnClick(false);
            mbPermisos.getItems().add(item);
        }
        actualizarResumenPermisos();
    }

    private void actualizarResumenPermisos() {
        mbPermisos.setText(permisosSeleccionados.size() + " permisos seleccionados");
    }

    private List<String> permisosElegidos() {
        List<String> elegidos = new ArrayList<String>();
        for (String permiso : permisosDisponibles) {
            if (permisosSeleccionados.contains(permiso)) {
                elegidos.add(permiso);
            }
        }
        return elegidos;
    }

    // ---- Empleado ----
    private void cargarEmpleado(Empleado empleado) {
        boolean hayEmpleado = empleado != null;

        panelEmpleado.setVisible(hayEmpleado);
        panelEmpleado.setManaged(hayEmpleado);
        lblSinEmpleado.setVisible(!hayEmpleado);
        lblSinEmpleado.setManaged(!hayEmpleado);

        if (!hayEmpleado) {
            return;
        }

        tfEmpleadoNombre.setText(empleado.getNombre());
        tfEmpleadoApellido.setText(empleado.getApellido());
        tfEmpleadoDni.setText(empleado.getDni());
        tfEmpleadoPuesto.setText(empleado.getPuesto());
        tfEmpleadoEmail.setText(empleado.getEmail());
        tfEmpleadoTelefono.setText(empleado.getTelefono());
        tfEmpleadoFechaAlta.setText(String.valueOf(empleado.getFecha_alta()));
    }

    private Usuario leerAltaFormulario() {
        Empleado empleado = new Empleado();
        empleado.setNombre(tfEmpleadoNombre.getText().trim());
        empleado.setApellido(tfEmpleadoApellido.getText().trim());
        empleado.setDni(tfEmpleadoDni.getText().trim());
        empleado.setPuesto(tfEmpleadoPuesto.getText().trim());
        empleado.setEmail(tfEmpleadoEmail.getText().trim());
        empleado.setTelefono(tfEmpleadoTelefono.getText().trim());

        Usuario nuevo = new Usuario();
        nuevo.setPassword_hash(pfPassword.getText());
        nuevo.setRol(tfRol.getText().trim());
        nuevo.setActivo(chkActivo.isSelected());
        nuevo.setPermisos(permisosElegidos());
        nuevo.setEmpleado(empleado);
        return nuevo;
    }

    private Usuario construirUsuarioEditado() {
        Usuario editado = new Usuario();
        editado.setId(usuarioGestionado.getId());
        editado.setUsername(usuarioGestionado.getUsername());
        editado.setCreado_en(usuarioGestionado.getCreado_en());
        editado.setPassword_hash(pfPassword.getText());
        editado.setRol(tfRol.getText().trim());
        editado.setActivo(chkActivo.isSelected());
        editado.setPermisos(permisosElegidos());

        Empleado empOriginal = usuarioGestionado.getEmpleado();
        if (empOriginal != null) {
            Empleado empEditado = new Empleado();
            empEditado.setNombre(empOriginal.getNombre());
            empEditado.setApellido(empOriginal.getApellido());
            empEditado.setDni(empOriginal.getDni());
            empEditado.setFecha_alta(empOriginal.getFecha_alta());
            empEditado.setPuesto(tfEmpleadoPuesto.getText().trim());
            empEditado.setEmail(tfEmpleadoEmail.getText().trim());
            empEditado.setTelefono(tfEmpleadoTelefono.getText().trim());
            editado.setEmpleado(empEditado);
        }
        return editado;
    }

    // ---- Métodos FXML ----
    @FXML
    private void onAlta(ActionEvent event) {
        if (alCrear != null) {
            alCrear.accept(leerAltaFormulario());
        }
    }

    @FXML
    private void onModificar(ActionEvent event) {
        if (alModificar != null) {
            alModificar.accept(construirUsuarioEditado());
        }
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        if (alEliminar != null) {
            alEliminar.accept(usuarioGestionado);
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        cerrar();
    }

    // set solo lectura
    public void setSoloLectura() {
        ponerVisible(btnModificar, false);
        ponerVisible(btnEliminar, false);
        ponerVisible(btnAlta, false);
        ponerVisible(btnCancelar, false);

        ponerVisible(tfId, false);
        ponerVisible(tfCreadoEn, false);
        ponerVisible(chkActivo, false);
        ponerVisible(lblSinEmpleado, false);

        pfPassword.setEditable(false);
        tfRol.setEditable(false);
        tfEmpleadoPuesto.setEditable(false);
        tfEmpleadoEmail.setEditable(false);
        tfEmpleadoTelefono.setEditable(false);
        mbPermisos.setDisable(true);
    }
}
