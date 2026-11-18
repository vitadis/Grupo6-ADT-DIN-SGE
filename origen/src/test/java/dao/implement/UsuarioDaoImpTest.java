package dao.implement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.Empleado;
import model.Usuario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: UsuarioDaoImp
 *
 * @author Joel
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioDaoImpTest {

    private static final String USER_TEST = "test.junit";
    private static final String PASS_TEST = "test1234";

    private static UsuarioDaoImp dao;

    // ---------- PREPARACION Y LIMPIEZA -----------
    @BeforeAll
    static void iniciar() {
        dao = UsuarioDaoImp.getInstance();
        eliminarUsuarioDePrueba(); // por si una ejecución anterior falló
    }

    @AfterAll
    static void limpiar() {
        eliminarUsuarioDePrueba(); // garantiza que no quedan datos extra
    }

    // ---------- UTILIDADES -----------
    /**
     * Busca un usuario por su username; null si no existe.
     */
    private static Usuario buscarPorUsername(String username) {
        return dao.listarUsuarios().stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Elimina el usuario de prueba si existe.
     */
    private static void eliminarUsuarioDePrueba() {
        Usuario existente = buscarPorUsername(USER_TEST);
        if (existente != null) {
            dao.eliminarUsuario(existente);
        }
    }

    /**
     * Crea un trabajador de prueba con permisos inicio y caja.
     */
    private static Usuario crearUsuarioDePrueba() {
        Empleado empleado = new Empleado();
        empleado.setNombre("Test");
        empleado.setApellido("Junit");
        empleado.setDni("00000000T");
        empleado.setTelefono("600000000");
        empleado.setEmail("test.junit@gmail.com");
        empleado.setPuesto("Camarero");

        Usuario u = new Usuario();
        u.setUsername(USER_TEST);
        u.setPassword_hash(PASS_TEST);
        u.setRol("trabajador");
        u.setPermisos(new ArrayList<>(Arrays.asList("inicio", "caja")));
        u.setActivo(true);
        u.setEmpleado(empleado);
        return u;
    }

    // ---------- PERMISOS -----------
    @Test
    @DisplayName("listarPermisos devuelve la lista 'todos'")
    void listarPermisos_devuelveTodos() {
        List<String> permisos = dao.listarPermisos();

        assertNotNull(permisos);
        assertTrue(permisos.containsAll(Arrays.asList(
                "total", "inicio", "caja", "gestion",
                "stock", "compras", "informes", "administracion")));
    }

    // ---------- LOGIN -----------
    @Test
    @DisplayName("obtenerUsuario con credenciales válidas devuelve el usuario")
    void obtenerUsuario_credencialesValidas() {
        Usuario u = dao.obtenerUsuario("admin", "1234");

        assertNotNull(u);
        assertEquals("admin", u.getUsername());
        assertEquals("administrador", u.getRol());
        assertTrue(u.getPermisos().contains("total"));
    }

    @Test
    @DisplayName("obtenerUsuario con contraseña incorrecta devuelve null")
    void obtenerUsuario_passwordIncorrecta() {
        assertNull(dao.obtenerUsuario("admin", "incorrecta"));
    }

    @Test
    @DisplayName("obtenerUsuario con usuario inexistente devuelve null")
    void obtenerUsuario_usuarioInexistente() {
        assertNull(dao.obtenerUsuario("no.existe", "1234"));
    }

    @Test
    @DisplayName("obtenerUsuario con parámetros null devuelve null")
    void obtenerUsuario_parametrosNull() {
        assertNull(dao.obtenerUsuario(null, "1234"));
        assertNull(dao.obtenerUsuario("admin", null));
    }

    // ---------- LISTADO -----------
    @Test
    @DisplayName("listarUsuarios ordena por id ascendente")
    void listarUsuarios_ordenadoPorId() {
        List<Usuario> usuarios = dao.listarUsuarios();

        assertFalse(usuarios.isEmpty());
        for (int i = 1; i < usuarios.size(); i++) {
            assertTrue(usuarios.get(i - 1).getId() < usuarios.get(i).getId());
        }
    }

    // ---------- CRUD: ALTA -----------
    @Test
    @Order(1)
    @DisplayName("altaUsuario inserta el usuario con el siguiente id")
    void altaUsuario_insertaCorrectamente() {
        List<Usuario> antes = dao.listarUsuarios();
        int mayorIdAntes = antes.get(antes.size() - 1).getId();

        dao.altaUsuario(crearUsuarioDePrueba());

        Usuario guardado = buscarPorUsername(USER_TEST);
        assertNotNull(guardado);
        assertEquals(mayorIdAntes + 1, guardado.getId());
        assertEquals("trabajador", guardado.getRol());
        assertEquals(Arrays.asList("inicio", "caja"), guardado.getPermisos());
        assertTrue(guardado.isActivo());
        assertEquals("Test", guardado.getEmpleado().getNombre());
        assertEquals("00000000T", guardado.getEmpleado().getDni());
        assertEquals(antes.size() + 1, dao.listarUsuarios().size());
    }

    @Test
    @Order(2)
    @DisplayName("El usuario creado puede iniciar sesión")
    void obtenerUsuario_usuarioRecienCreado() {
        Usuario u = dao.obtenerUsuario(USER_TEST, PASS_TEST);

        assertNotNull(u);
        assertEquals(USER_TEST, u.getUsername());
    }

    // ---------- CRUD: MODIFICAR -----------
    @Test
    @Order(3)
    @DisplayName("modificarUsuario actualiza los datos")
    void modificarUsuario_actualizaDatos() {
        Usuario u = buscarPorUsername(USER_TEST);
        assertNotNull(u);

        u.setPassword_hash("nueva5678");
        u.setPermisos(new ArrayList<>(Arrays.asList("inicio", "stock", "compras")));
        u.getEmpleado().setPuesto("Almacenero");
        u.getEmpleado().setTelefono("611111111");

        dao.modificarUsuario(u);

        Usuario modificado = buscarPorUsername(USER_TEST);
        assertNotNull(modificado);
        assertEquals(u.getId(), modificado.getId());
        assertEquals("nueva5678", modificado.getPassword_hash());
        assertEquals(Arrays.asList("inicio", "stock", "compras"), modificado.getPermisos());
        assertEquals("Almacenero", modificado.getEmpleado().getPuesto());
        assertEquals("611111111", modificado.getEmpleado().getTelefono());
    }

    @Test
    @Order(4)
    @DisplayName("Tras modificar, la contraseña antigua no funciona")
    void obtenerUsuario_trasModificarPassword() {
        assertNull(dao.obtenerUsuario(USER_TEST, PASS_TEST));
        assertNotNull(dao.obtenerUsuario(USER_TEST, "nueva5678"));
    }

    @Test
    @Order(5)
    @DisplayName("Un usuario desactivado no puede acceder")
    void obtenerUsuario_usuarioInactivo() {
        Usuario u = buscarPorUsername(USER_TEST);
        assertNotNull(u);

        u.setActivo(false);
        dao.modificarUsuario(u);

        assertNull(dao.obtenerUsuario(USER_TEST, "nueva5678"));
    }

    // ---------- CRUD: ELIMINAR -----------
    @Test
    @Order(6)
    @DisplayName("eliminarUsuario borra el usuario de prueba")
    void eliminarUsuario_borraUsuario() {
        Usuario u = buscarPorUsername(USER_TEST);
        assertNotNull(u);
        int total = dao.listarUsuarios().size();

        dao.eliminarUsuario(u);

        assertNull(buscarPorUsername(USER_TEST));
        assertEquals(total - 1, dao.listarUsuarios().size());
    }
}
