package dao.implement;

import dao.MovimientoDao;
import java.util.Arrays;
import java.util.List;
import model.Movimiento;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: MovimientoDaoImpl (colección registros_economicos)
 *
 * @author anazk
 */
class MovimientoDaoImplTest {

    private static final double DELTA = 0.01;
    private static final List<String> TIPOS = Arrays.asList("ingreso", "gasto");

    private static MovimientoDao dao;

    // ---------- PREPARACION -----------
    @BeforeAll
    static void iniciar() {
        dao = MovimientoDaoImpl.getInstance();
    }

    // ---------- SINGLETON -----------
    @Test
    @DisplayName("getInstance devuelve siempre la misma instancia")
    void getInstance_devuelveMismaInstancia() {
        assertSame(MovimientoDaoImpl.getInstance(), MovimientoDaoImpl.getInstance());
    }

    // ---------- OBTENER: LISTADO -----------
    @Test
    @DisplayName("obtenerMovimientos devuelve los movimientos de la colección")
    void obtenerMovimientos_devuelveMovimientos() {
        List<Movimiento> movimientos = dao.obtenerMovimientos();

        assertNotNull(movimientos);
        assertFalse(movimientos.isEmpty(), "La colección registros_economicos debería tener datos");
    }

    @Test
    @DisplayName("obtenerMovimientos ordena por id ascendente")
    void obtenerMovimientos_ordenadoPorId() {
        List<Movimiento> movimientos = dao.obtenerMovimientos();

        assertEquals(1, movimientos.get(0).getId());
        for (int i = 1; i < movimientos.size(); i++) {
            assertTrue(movimientos.get(i - 1).getId() < movimientos.get(i).getId(),
                    "Los movimientos deben venir ordenados por _id ascendente");
        }
    }

    // ---------- OBTENER: MAPEO DE DATOS -----------
    @Test
    @DisplayName("obtenerMovimientos mapea correctamente el primer movimiento")
    void obtenerMovimientos_primerMovimiento() {
        Movimiento m = dao.obtenerMovimientos().get(0);

        assertEquals(1, m.getId());
        assertEquals("2026-10-01", m.getFecha());
        assertEquals("Aprovisionamiento", m.getCategoria());
        assertEquals("gasto", m.getTipo());
        assertEquals(177.01, m.getImporte(), DELTA);
        assertEquals(1, m.getUsuario_id());
        assertEquals("compras", m.getOrigen().getColeccion());
        assertEquals(1, m.getOrigen().getProcedencia_id());
    }

    @Test
    @DisplayName("Todos los movimientos son ingreso o gasto con importe positivo")
    void obtenerMovimientos_camposObligatorios() {
        for (Movimiento m : dao.obtenerMovimientos()) {
            String ref = "Movimiento " + m.getId();

            assertNotNull(m.getFecha(), ref + ": fecha nula");
            assertNotNull(m.getCategoria(), ref + ": categoría nula");
            assertTrue(TIPOS.contains(m.getTipo()), ref + ": tipo no válido");
            assertTrue(m.getImporte() > 0, ref + ": importe no positivo");
        }
    }

    // ---------- OBTENER: COHERENCIA -----------
    @Test
    @DisplayName("Los gastos de compras son de Aprovisionamiento y los ingresos de pedidos son Ventas")
    void obtenerMovimientos_categoriaSegunOrigen() {
        for (Movimiento m : dao.obtenerMovimientos()) {
            if (m.getOrigen() == null) {
                continue;
            }
            String ref = "Movimiento " + m.getId();
            if ("compras".equals(m.getOrigen().getColeccion())) {
                assertEquals("gasto", m.getTipo(), ref);
                assertEquals("Aprovisionamiento", m.getCategoria(), ref);
            } else if ("pedidos".equals(m.getOrigen().getColeccion())) {
                assertEquals("ingreso", m.getTipo(), ref);
                assertEquals("Ventas", m.getCategoria(), ref);
            }
        }
    }
}
