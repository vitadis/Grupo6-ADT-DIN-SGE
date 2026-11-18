package dao.implement;

import dao.ProveedorDao;
import java.util.Arrays;
import java.util.List;
import model.Proveedor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: ProveedorDaoImpl
 *
 * @author anazk
 */
class ProveedorDaoImplTest {

    private static ProveedorDao dao;

    // ---------- PREPARACION -----------
    @BeforeAll
    static void iniciar() {
        dao = ProveedorDaoImpl.getInstance();
    }

    // ---------- UTILIDADES -----------
    /**
     * Busca un proveedor por su id; null si no existe.
     */
    private static Proveedor buscarPorId(int id) {
        return dao.obtenerProveedores().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // ---------- SINGLETON -----------
    @Test
    @DisplayName("getInstance devuelve siempre la misma instancia")
    void getInstance_devuelveMismaInstancia() {
        assertSame(ProveedorDaoImpl.getInstance(), ProveedorDaoImpl.getInstance());
    }

    // ---------- OBTENER: LISTADO -----------
    @Test
    @DisplayName("obtenerProveedores devuelve los proveedores de la colección")
    void obtenerProveedores_devuelveProveedores() {
        List<Proveedor> proveedores = dao.obtenerProveedores();

        assertNotNull(proveedores);
        assertFalse(proveedores.isEmpty(), "La colección proveedores debería tener datos");
    }

    @Test
    @DisplayName("obtenerProveedores ordena por id ascendente")
    void obtenerProveedores_ordenadoPorId() {
        List<Proveedor> proveedores = dao.obtenerProveedores();

        assertEquals(1, proveedores.get(0).getId());
        for (int i = 1; i < proveedores.size(); i++) {
            assertTrue(proveedores.get(i - 1).getId() < proveedores.get(i).getId(),
                    "Los proveedores deben venir ordenados por _id ascendente");
        }
    }

    // ---------- OBTENER: MAPEO DE DATOS -----------
    @Test
    @DisplayName("obtenerProveedores mapea correctamente el primer proveedor")
    void obtenerProveedores_primerProveedor() {
        Proveedor p = dao.obtenerProveedores().get(0);

        assertEquals(1, p.getId());
        assertEquals("Distribuciones Norte S.L.", p.getNombre());
        assertEquals("B48123451", p.getNif_cif());
        assertEquals(2, p.getPlazo_entrega());
        assertEquals(Arrays.asList("lunes", "martes", "miercoles", "jueves", "viernes"),
                p.getDias_laborales());
        assertEquals("Mikel Fernández", p.getContacto().getPersona());
        assertEquals("944123456", p.getContacto().getTelefono());
        assertTrue(p.isActivo());
    }

    @Test
    @DisplayName("Todos los proveedores tienen datos válidos")
    void obtenerProveedores_datosValidos() {
        for (Proveedor p : dao.obtenerProveedores()) {
            assertDoesNotThrow(p::validar, "Proveedor " + p.getId() + " no válido");
        }
    }

    // ---------- ACTUALIZAR -----------
    @Test
    @DisplayName("actualizarProveedores cambia los datos y se puede deshacer")
    void actualizarProveedores_actualizaDatos() {
        Proveedor p = buscarPorId(1);
        int plazoOriginal = p.getPlazo_entrega();

        p.setPlazo_entrega(plazoOriginal + 1);
        dao.actualizarProveedores(p);
        assertEquals(plazoOriginal + 1, buscarPorId(1).getPlazo_entrega());

        p.setPlazo_entrega(plazoOriginal);
        dao.actualizarProveedores(p);
        assertEquals(plazoOriginal, buscarPorId(1).getPlazo_entrega());
    }

    @Test
    @DisplayName("actualizarProveedores de un proveedor inexistente lanza excepción")
    void actualizarProveedores_inexistente() {
        Proveedor p = buscarPorId(1);
        p.setId(99099);

        assertThrows(IllegalArgumentException.class, () -> dao.actualizarProveedores(p));
        assertNull(buscarPorId(99099), "No debe crearse el proveedor");
    }
}
