package dao.implement;

import java.util.Arrays;
import java.util.List;
import model.VentaPedido;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: PedidoDaoImp
 *
 * @author Joel
 */
class PedidoDaoImpTest {

    private static final double DELTA = 0.01;
    private static final List<String> METODOS_PAGO = Arrays.asList("EFECTIVO", "TARJETA", "BIZUM");

    private static PedidoDaoImp dao;

    // ---------- PREPARACION -----------
    @BeforeAll
    static void iniciar() {
        dao = PedidoDaoImp.getInstance();
    }

    // ---------- UTILIDADES -----------
    /**
     * Convierte un enum o String a texto en mayúsculas para compararlo.
     */
    private static String valor(Object o) {
        return String.valueOf(o).toUpperCase();
    }

    // ---------- SINGLETON -----------
    @Test
    @DisplayName("getInstance devuelve siempre la misma instancia")
    void getInstance_devuelveMismaInstancia() {
        assertSame(PedidoDaoImp.getInstance(), PedidoDaoImp.getInstance());
    }

    // ---------- CONSULTAR: LISTADO -----------
    @Test
    @DisplayName("consultar devuelve los pedidos de la colección")
    void consultar_devuelvePedidos() {
        List<VentaPedido> pedidos = dao.consultar();

        assertNotNull(pedidos);
        assertFalse(pedidos.isEmpty(), "La colección pedidos debería tener datos");
    }

    @Test
    @DisplayName("consultar ordena por id ascendente")
    void consultar_ordenadoPorId() {
        List<VentaPedido> pedidos = dao.consultar();

        assertEquals(1, pedidos.get(0).getId());
        for (int i = 1; i < pedidos.size(); i++) {
            assertTrue(pedidos.get(i - 1).getId() < pedidos.get(i).getId(),
                    "Los pedidos deben venir ordenados por _id ascendente");
        }
    }

    // ---------- CONSULTAR: MAPEO DE DATOS -----------
    @Test
    @DisplayName("consultar mapea correctamente el primer pedido")
    void consultar_primerPedido() {
        VentaPedido p = dao.consultar().get(0);

        assertEquals(1, p.getId());
        assertEquals(2, p.getUsuario_id());
        assertEquals("2026-10-10", String.valueOf(p.getFecha()));
        assertEquals("COBRADO", valor(p.getEstado()));
        assertEquals("BIZUM", valor(p.getMetodo_pago()));
        assertEquals(5, p.getDetalle().size());
        assertEquals(18.8, p.getTotal(), DELTA);
    }

    @Test
    @DisplayName("Todos los pedidos tienen los campos obligatorios")
    void consultar_camposObligatorios() {
        for (VentaPedido p : dao.consultar()) {
            String ref = "Pedido " + p.getId();

            assertNotNull(p.getId(), ref + ": id nulo");
            assertNotNull(p.getFecha(), ref + ": fecha nula");
            assertNotNull(p.getEstado(), ref + ": estado nulo");
            assertTrue(METODOS_PAGO.contains(valor(p.getMetodo_pago())),
                    ref + ": método de pago no válido");
            assertNotNull(p.getDetalle(), ref + ": detalle nulo");
            assertFalse(p.getDetalle().isEmpty(), ref + ": detalle vacío");
        }
    }

    // ---------- CONSULTAR: COHERENCIA -----------
    @Test
    @DisplayName("El subtotal de cada línea es cantidad x precio unitario")
    void consultar_subtotalesCoherentes() {
        for (VentaPedido p : dao.consultar()) {
            p.getDetalle().forEach(d -> assertEquals(
                    d.getCantidad() * d.getPrecio_unitario(), d.getSubtotal(), DELTA,
                    "Pedido " + p.getId() + ": subtotal incorrecto en " + d.getNombre()));
        }
    }

    @Test
    @DisplayName("El total de cada pedido es la suma de sus subtotales")
    void consultar_totalesCoherentes() {
        for (VentaPedido p : dao.consultar()) {
            double suma = p.getDetalle().stream()
                    .mapToDouble(d -> d.getSubtotal())
                    .sum();

            assertEquals(suma, p.getTotal(), DELTA,
                    "Pedido " + p.getId() + ": el total no coincide con el detalle");
        }
    }
}
