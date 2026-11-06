package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
 * Dialog de usuario/empleado: solo muestra y recoge los datos.
 *
 * @author Joel
 */
public class DialogUsuarioController {

    // Usuario del dialog
    private Usuario usuarioGestionado;
    // Permisos que ofrece el dialog
    private List<String> permisosCatalogo = new ArrayList<String>();
    private final Set<String> permisosSeleccionados = new LinkedHashSet<String>();
    private final List<String> permisosDisponibles = new ArrayList<String>();

    // Un Consumer<Usuario> por botón
    private Consumer<Usuario> alCrear;
    private Consumer<Usuario> alModificar;
    private Consumer<Usuario> alEliminar;

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

    // Modificar/Eliminar en edición; Alta/Cancelar en alta
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnAlta;
    @FXML
    private Button btnCancelar;

    @FXML
    private void initialize() {
        mostrarBotones(false);
    }

    public void setAcciones(Consumer<Usuario> alCrear,
            Consumer<Usuario> alModificar,
            Consumer<Usuario> alEliminar) {
        this.alCrear = alCrear;
        this.alModificar = alModificar;
        this.alEliminar = alEliminar;
    }

    // Llamar antes de setUsuario / setModoAlta
    public void setPermisosDisponibles(List<String> permisos) {
        this.permisosCatalogo = permisos != null ? permisos : new ArrayList<String>();
    }

    private void mostrarBotones(boolean modoAlta) {
        ponerVisible(btnModificar, !modoAlta);
        ponerVisible(btnEliminar, !modoAlta);
        ponerVisible(btnAlta, modoAlta);
        ponerVisible(btnCancelar, modoAlta);
    }

    private void ponerVisible(Button boton, boolean visible) {
        boton.setVisible(visible);
        boton.setManaged(visible);
    }

    // Modo edición: el usuario ya viene verificado por el controller
    public void setUsuario(Usuario usuario) {
        this.usuarioGestionado = usuario;

        // En edición no se tocan el username ni la identidad del empleado
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

    // Modo alta: mismo dialog, campos vacíos, usuario y empleado a la vez
    public void setModoAlta() {
        this.usuarioGestionado = null;
        mostrarBotones(true);

        // Id y fecha de creación los pone el sistema
        tfId.clear();
        tfId.setDisable(true);
        tfCreadoEn.clear();
        tfCreadoEn.setDisable(true);

        // El username se genera en el controller
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
        tfEmpleadoFechaAlta.setDisable(true); // la fecha de alta la pone el controller
    }

    // Vuelca el formulario en el usuario de la tabla
    private void aplicarCambios() {
        usuarioGestionado.setPassword_hash(pfPassword.getText());
        usuarioGestionado.setRol(tfRol.getText().trim());
        usuarioGestionado.setActivo(chkActivo.isSelected());
        usuarioGestionado.setPermisos(permisosElegidos());

        // El usuario puede no tener empleado asociado
        Empleado empleado = usuarioGestionado.getEmpleado();
        if (empleado != null) {
            empleado.setPuesto(tfEmpleadoPuesto.getText().trim());
            empleado.setEmail(tfEmpleadoEmail.getText().trim());
            empleado.setTelefono(tfEmpleadoTelefono.getText().trim());
        }
    }

    public void cerrar() {
        ((Stage) tfId.getScene().getWindow()).close();
    }

    // Permisos: MenuButton con un CheckBox por permiso (selección múltiple)
    private void cargarPermisos(Usuario usuario) {
        // Los que ya tiene el usuario quedan marcados
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

    // Permisos marcados, respetando el orden de la lista
    private List<String> permisosElegidos() {
        List<String> elegidos = new ArrayList<String>();
        for (String permiso : permisosDisponibles) {
            if (permisosSeleccionados.contains(permiso)) {
                elegidos.add(permiso);
            }
        }
        return elegidos;
    }

    // Empleado
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

    // Usuario y empleado nuevos con los datos del formulario
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

    // Compara el formulario con el usuario (el username no se edita)
    private boolean hayCambios() {
        Empleado empleado = usuarioGestionado.getEmpleado();

        boolean cambiosUsuario = !Objects.equals(pfPassword.getText(), usuarioGestionado.getPassword_hash())
                || !Objects.equals(tfRol.getText().trim(), usuarioGestionado.getRol())
                || chkActivo.isSelected() != usuarioGestionado.isActivo()
                || !new HashSet<String>(permisosElegidos()).equals(new HashSet<String>(usuarioGestionado.getPermisos()));

        boolean cambiosEmpleado = empleado != null
                && (!Objects.equals(tfEmpleadoPuesto.getText().trim(), empleado.getPuesto())
                || !Objects.equals(tfEmpleadoEmail.getText().trim(), empleado.getEmail())
                || !Objects.equals(tfEmpleadoTelefono.getText().trim(), empleado.getTelefono()));

        return cambiosUsuario || cambiosEmpleado;
    }

    // Botones: cada uno avisa con su Consumer<Usuario> (Cancelar solo cierra)
    @FXML
    private void onAlta(ActionEvent event) {
        if (alCrear != null) {
            alCrear.accept(leerAltaFormulario());
        }
    }

    @FXML
    private void onModificar(ActionEvent event) {
        // Sin cambios no hay nada que modificar
        if (!hayCambios()) {
            cerrar();
            return;
        }

        aplicarCambios();
        if (alModificar != null) {
            alModificar.accept(usuarioGestionado);
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
}
