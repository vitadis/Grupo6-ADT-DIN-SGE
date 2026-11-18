package dao.implement;

import dao.RegistrarPerdidaDao;
import exceptions.DaoException;
import model.MovimientoStock;
import model.Producto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: RegistrarPerdidaDaoImp
 *
 * @author anazk
 */
class RegistrarPerdidaDaoImpTest {

    private static RegistrarPerdidaDao dao;

    // ---------- PREPARACION -----------
    @BeforeAll
    static void iniciar() {
        dao = RegistrarPerdidaDaoImp.getInstance();
    }

    // ---------- UTILIDADES -----------
    /**
     * Lee el producto guardado con ese id; null si no existe.
     */
    private static Producto buscarPorId(int id) {
        return ProductoDaoImp.getInstance().listarProductos().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Crea un movimiento de pérdida para el producto.
     */
    private static MovimientoStock perdida(Producto p, int cantidad) {
        return new MovimientoStock(p.getId(), p.getNombre(), "PERDIDA", cantidad, "Test JUnit");
    }

    // ---------- SINGLETON -----------
    @Test
    @DisplayName("getInstance devuelve siempre la misma instancia")
    void getInstance_devuelveMismaInstancia() {
        assertSame(RegistrarPerdidaDaoImp.getInstance(), RegistrarPerdidaDaoImp.getInstance());
    }

    // ---------- REGISTRAR PERDIDA: ERRORES -----------
    @Test
    @DisplayName("registrarPerdida con movimiento o producto nulo lanza DaoException")
    void registrarPerdida_nulos() {
        Producto p = buscarPorId(1);

        assertThrows(DaoException.class, () -> dao.registrarPerdida(null, p));
        assertThrows(DaoException.class, () -> dao.registrarPerdida(perdida(p, 1), null));
    }

    @Test
    @DisplayName("registrarPerdida con cantidad 0 lanza DaoException y no cambia el stock")
    void registrarPerdida_cantidadCero() {
        Producto p = buscarPorId(1);
        int stockAntes = p.getStock_actual();

        assertThrows(DaoException.class, () -> dao.registrarPerdida(perdida(p, 0), p));
        assertEquals(stockAntes, buscarPorId(1).getStock_actual());
    }

    @Test
    @DisplayName("registrarPerdida con más cantidad que stock lanza DaoException y no cambia el stock")
    void registrarPerdida_stockInsuficiente() {
        Producto p = buscarPorId(1);
        int stockAntes = p.getStock_actual();

        assertThrows(DaoException.class,
                () -> dao.registrarPerdida(perdida(p, stockAntes + 1), p));
        assertEquals(stockAntes, buscarPorId(1).getStock_actual());
    }

    @Test
    @DisplayName("registrarPerdida de un producto inexistente lanza DaoException")
    void registrarPerdida_productoInexistente() {
        Producto p = buscarPorId(1);
        p.setId(99099);

        assertThrows(DaoException.class, () -> dao.registrarPerdida(perdida(p, 1), p));
        assertNull(buscarPorId(99099));
    }
}
