package dao.implement;

import dao.AñadirProductoDao;
import exceptions.DaoException;
import java.util.List;
import model.Producto;
import model.Proveedor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: AnadirProductoDaoImp
 *
 * @author anazk
 */
class AnadirProductoDaoImpTest {

    private static final int ID_INEXISTENTE = 99099;

    private static AñadirProductoDao dao;

    // ---------- PREPARACION -----------
    @BeforeAll
    static void iniciar() {
        dao = AnadirProductoDaoImp.getInstance();
    }

    // ---------- UTILIDADES -----------
    /**
     * Número de productos guardados.
     */
    private static int totalProductos() {
        return ProductoDaoImp.getInstance().listarProductos().size();
    }

    // ---------- SINGLETON -----------
    @Test
    @DisplayName("getInstance devuelve siempre la misma instancia")
    void getInstance_devuelveMismaInstancia() {
        assertSame(AnadirProductoDaoImp.getInstance(), AnadirProductoDaoImp.getInstance());
    }

    // ---------- PROVEEDORES ACTIVOS -----------
    @Test
    @DisplayName("listarProveedoresActivos devuelve solo proveedores activos")
    void listarProveedoresActivos_soloActivos() {
        List<Proveedor> proveedores = dao.listarProveedoresActivos();

        assertNotNull(proveedores);
        assertFalse(proveedores.isEmpty(), "Debería haber proveedores activos");
        proveedores.forEach(p -> assertTrue(p.isActivo(),
                "Proveedor " + p.getId() + ": no está activo"));
    }

    // ---------- INSERTAR PRODUCTO -----------
    @Test
    @DisplayName("insertarProducto con producto nulo lanza DaoException")
    void insertarProducto_nulo() {
        assertThrows(DaoException.class, () -> dao.insertarProducto(null));
    }

    @Test
    @DisplayName("insertarProducto con un producto sin datos lanza DaoException y no inserta")
    void insertarProducto_noValido() {
        int total = totalProductos();

        assertThrows(DaoException.class, () -> dao.insertarProducto(new Producto()));
        assertEquals(total, totalProductos());
    }

    // ---------- AÑADIR STOCK -----------
    @Test
    @DisplayName("anadirStock con cantidad 0 lanza DaoException")
    void anadirStock_cantidadCero() {
        assertThrows(DaoException.class, () -> dao.anadirStock(1, 0, "2027-05-01"));
    }

    @Test
    @DisplayName("anadirStock con cantidad negativa lanza DaoException")
    void anadirStock_cantidadNegativa() {
        assertThrows(DaoException.class, () -> dao.anadirStock(1, -3, "2027-05-01"));
    }

    @Test
    @DisplayName("anadirStock a un producto inexistente lanza DaoException")
    void anadirStock_inexistente() {
        int total = totalProductos();

        assertThrows(DaoException.class, () -> dao.anadirStock(ID_INEXISTENTE, 4, "2027-05-01"));
        assertEquals(total, totalProductos(), "No debe crearse ningún producto");
    }
}
