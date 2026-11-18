package dao.implement;

import dao.GestionStockDao;
import java.util.List;
import model.Producto;
import model.Proveedor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: GestionStockDaoImp
 *
 * @author anazk
 */
class GestionStockDaoImpTest {

    private static GestionStockDao dao;

    // ---------- PREPARACION -----------
    @BeforeAll
    static void iniciar() {
        dao = GestionStockDaoImp.getInstance();
    }

    // ---------- SINGLETON -----------
    @Test
    @DisplayName("getInstance devuelve siempre la misma instancia")
    void getInstance_devuelveMismaInstancia() {
        assertSame(GestionStockDaoImp.getInstance(), GestionStockDaoImp.getInstance());
    }

    // ---------- PROVEEDORES ACTIVOS -----------
    @Test
    @DisplayName("listarActivos devuelve los proveedores activos")
    void listarActivos_devuelveProveedores() {
        List<Proveedor> proveedores = dao.listarActivos();

        assertNotNull(proveedores);
        assertFalse(proveedores.isEmpty(), "Debería haber proveedores activos");
        proveedores.forEach(p -> assertTrue(p.isActivo(),
                "Proveedor " + p.getId() + ": no está activo"));
    }

    @Test
    @DisplayName("listarActivos ordena por nombre")
    void listarActivos_ordenadoPorNombre() {
        List<Proveedor> proveedores = dao.listarActivos();

        for (int i = 1; i < proveedores.size(); i++) {
            assertTrue(proveedores.get(i - 1).getNombre().compareTo(proveedores.get(i).getNombre()) <= 0,
                    "Los proveedores deben venir ordenados por nombre");
        }
    }

    // ---------- PRODUCTOS ACTIVOS -----------
    @Test
    @DisplayName("listarProductos devuelve los productos activos")
    void listarProductos_devuelveProductos() {
        List<Producto> productos = dao.listarProductos();

        assertNotNull(productos);
        assertFalse(productos.isEmpty(), "Debería haber productos activos");
        productos.forEach(p -> assertTrue(p.isActivo(),
                "Producto " + p.getId() + ": no está activo"));
    }

    @Test
    @DisplayName("listarProductos ordena por nombre")
    void listarProductos_ordenadoPorNombre() {
        List<Producto> productos = dao.listarProductos();

        for (int i = 1; i < productos.size(); i++) {
            assertTrue(productos.get(i - 1).getNombre().compareTo(productos.get(i).getNombre()) <= 0,
                    "Los productos deben venir ordenados por nombre");
        }
    }

    @Test
    @DisplayName("Ningún producto tiene stock negativo")
    void listarProductos_stockNoNegativo() {
        for (Producto p : dao.listarProductos()) {
            String ref = "Producto " + p.getId();

            assertTrue(p.getStock_actual() >= 0, ref + ": stock actual negativo");
            assertTrue(p.getStock_minimo() >= 0, ref + ": stock mínimo negativo");
        }
    }
}
