package dao.implement;

import dao.ComprasDao;
import java.util.List;
import model.Compra;
import model.LineaCompra;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: ComprasDaoImpl
 *
 * @author anazk
 */
class ComprasDaoImplTest {

    private static final double DELTA = 0.01;

    private static ComprasDao dao;

    // ---------- PREPARACION -----------
    @BeforeAll
    static void iniciar() {
        dao = ComprasDaoImpl.getInstance();
    }

    // ---------- SINGLETON -----------
    @Test
    @DisplayName("getInstance devuelve siempre la misma instancia")
    void getInstance_devuelveMismaInstancia() {
        assertSame(ComprasDaoImpl.getInstance(), ComprasDaoImpl.getInstance());
    }

    // ---------- OBTENER: LISTADO -----------
    @Test
    @DisplayName("obtenerCompras devuelve las compras de la colección")
    void obtenerCompras_devuelveCompras() {
        List<Compra> compras = dao.obtenerCompras();

        assertNotNull(compras);
        assertFalse(compras.isEmpty(), "La colección compras debería tener datos");
    }

    @Test
    @DisplayName("obtenerCompras ordena por id ascendente")
    void obtenerCompras_ordenadoPorId() {
        List<Compra> compras = dao.obtenerCompras();

        assertEquals(1, compras.get(0).getId());
        for (int i = 1; i < compras.size(); i++) {
            assertTrue(compras.get(i - 1).getId() < compras.get(i).getId(),
                    "Las compras deben venir ordenadas por _id ascendente");
        }
    }

    // ---------- OBTENER: MAPEO DE DATOS -----------
    @Test
    @DisplayName("obtenerCompras mapea correctamente la primera compra")
    void obtenerCompras_primeraCompra() {
        Compra c = dao.obtenerCompras().get(0);

        assertEquals(1, c.getId());
        assertEquals(1, c.getProveedor_id());
        assertEquals(1, c.getUsuario_id());
        assertEquals("2026-10-01", c.getFecha());
        assertEquals("F-2026-1001", c.getNumero_factura());
        assertEquals(4, c.getDetalle().size());
        assertEquals(148.2, c.getBase(), DELTA);
        assertEquals(28.81, c.getTotal_iva(), DELTA);
        assertEquals(177.01, c.getTotal(), DELTA);
    }

    @Test
    @DisplayName("Todas las compras tienen los campos obligatorios")
    void obtenerCompras_camposObligatorios() {
        for (Compra c : dao.obtenerCompras()) {
            String ref = "Compra " + c.getId();

            assertTrue(c.getProveedor_id() > 0, ref + ": sin proveedor");
            assertNotNull(c.getFecha(), ref + ": fecha nula");
            assertNotNull(c.getNumero_factura(), ref + ": factura nula");
            assertNotNull(c.getDetalle(), ref + ": detalle nulo");
            assertFalse(c.getDetalle().isEmpty(), ref + ": detalle vacío");
        }
    }

    // ---------- OBTENER: COHERENCIA -----------
    @Test
    @DisplayName("El subtotal de cada línea es cantidad x precio por unidad")
    void obtenerCompras_subtotalesCoherentes() {
        for (Compra c : dao.obtenerCompras()) {
            for (LineaCompra l : c.getDetalle()) {
                assertEquals(l.getCantidad() * l.getPrecio_unidad(), l.getSubtotal(), DELTA,
                        "Compra " + c.getId() + ": subtotal incorrecto en " + l.getNombre());
            }
        }
    }

    @Test
    @DisplayName("La base es la suma de los subtotales y el total es base + IVA")
    void obtenerCompras_totalesCoherentes() {
        for (Compra c : dao.obtenerCompras()) {
            double suma = c.getDetalle().stream()
                    .mapToDouble(l -> l.getSubtotal())
                    .sum();

            assertEquals(suma, c.getBase(), DELTA,
                    "Compra " + c.getId() + ": la base no coincide con el detalle");
            assertEquals(c.getBase() + c.getTotal_iva(), c.getTotal(), DELTA,
                    "Compra " + c.getId() + ": el total no es base + IVA");
        }
    }

    // ---------- OBTENER POR PROVEEDOR -----------
    @Test
    @DisplayName("obtenerComprasPorProveedor solo devuelve compras de ese proveedor")
    void obtenerComprasPorProveedor_filtra() {
        long esperadas = dao.obtenerCompras().stream()
                .filter(c -> c.getProveedor_id() == 1)
                .count();

        List<Compra> compras = dao.obtenerComprasPorProveedor(1);

        assertEquals(esperadas, compras.size());
        compras.forEach(c -> assertEquals(1, c.getProveedor_id()));
    }

    @Test
    @DisplayName("obtenerComprasPorProveedor de un proveedor sin compras devuelve lista vacía")
    void obtenerComprasPorProveedor_sinCompras() {
        List<Compra> compras = dao.obtenerComprasPorProveedor(99099);

        assertNotNull(compras);
        assertTrue(compras.isEmpty());
    }

    // ---------- INSERTAR -----------
    @Test
    @DisplayName("insertarCompra con una factura repetida lanza excepción y no inserta")
    void insertarCompra_facturaRepetida() {
        List<Compra> antes = dao.obtenerCompras();
        Compra repetida = antes.get(0); // misma factura F-2026-1001

        assertThrows(IllegalArgumentException.class, () -> dao.insertarCompra(repetida));
        assertEquals(antes.size(), dao.obtenerCompras().size());
    }
}
