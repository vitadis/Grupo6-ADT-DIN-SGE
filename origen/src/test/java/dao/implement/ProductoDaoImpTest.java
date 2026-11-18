package dao.implement;

import java.util.List;
import model.CategoriaProducto;
import model.Producto;
import model.TipoProducto;
import org.bson.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST CLASS: ProductoDaoImp
 *
 * @author Joel
 */
class ProductoDaoImpTest {

    private static final double DELTA = 0.01;
    private static final int ID_INEXISTENTE = 99099;

    private static ProductoDaoImp dao;

    // ---------- PREPARACION -----------
    @BeforeAll
    static void iniciar() {
        dao = ProductoDaoImp.getInstance();
    }

    // ---------- UTILIDADES -----------
    /**
     * Busca un producto por su id; null si no existe.
     */
    private static Producto buscarPorId(int id) {
        return dao.listarProductos().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // ---------- SINGLETON -----------
    @Test
    @DisplayName("getInstance devuelve siempre la misma instancia")
    void getInstance_devuelveMismaInstancia() {
        assertSame(ProductoDaoImp.getInstance(), ProductoDaoImp.getInstance());
    }

    // ---------- LISTAR: LISTADO -----------
    @Test
    @DisplayName("listarProductos devuelve los productos de la colección")
    void listarProductos_devuelveProductos() {
        List<Producto> productos = dao.listarProductos();

        assertNotNull(productos);
        assertFalse(productos.isEmpty(), "La colección productos debería tener datos");
    }

    @Test
    @DisplayName("listarProductos ordena por id ascendente")
    void listarProductos_ordenadoPorId() {
        List<Producto> productos = dao.listarProductos();

        assertEquals(1, productos.get(0).getId());
        for (int i = 1; i < productos.size(); i++) {
            assertTrue(productos.get(i - 1).getId() < productos.get(i).getId(),
                    "Los productos deben venir ordenados por _id ascendente");
        }
    }

    // ---------- LISTAR: MAPEO DE DATOS -----------
    @Test
    @DisplayName("listarProductos mapea correctamente el primer producto")
    void listarProductos_primerProducto() {
        Producto p = dao.listarProductos().get(0);

        assertEquals(1, p.getId());
        assertEquals("Coca-Cola", p.getNombre());
        assertEquals(TipoProducto.VENTA, p.getTipo());
        assertEquals(CategoriaProducto.BEBIDAS, p.getCategoria());
        assertEquals(2.5, p.getPrecio_venta(), DELTA);
        assertEquals(0.21, p.getIva(), DELTA);
        assertEquals(24, p.getStock_minimo());
        assertEquals(1, p.getProveedores_id().get(0));
        assertFalse(p.getLotes().isEmpty(), "Coca-Cola debería tener lotes");
    }

    @Test
    @DisplayName("Todos los productos tienen los campos obligatorios")
    void listarProductos_camposObligatorios() {
        for (Producto p : dao.listarProductos()) {
            String ref = "Producto " + p.getId();

            assertNotNull(p.getNombre(), ref + ": nombre nulo");
            assertNotNull(p.getTipo(), ref + ": tipo nulo");
            assertNotNull(p.getCategoria(), ref + ": categoría nula");
            assertEquals(p.getTipo(), p.getCategoria().getTipo(),
                    ref + ": la categoría no corresponde al tipo");
            assertTrue(p.getStock_actual() >= 0, ref + ": stock negativo");
        }
    }

    @Test
    @DisplayName("Cada lote tiene cantidad positiva y fecha de caducidad")
    void listarProductos_lotesValidos() {
        for (Producto p : dao.listarProductos()) {
            for (Document lote : p.getLotes()) {
                assertTrue(lote.getInteger(Producto.LOTE_CANTIDAD) > 0,
                        "Producto " + p.getId() + ": lote con cantidad no positiva");
                assertNotNull(lote.get(Producto.LOTE_FECHA_CADUCIDAD),
                        "Producto " + p.getId() + ": lote sin caducidad");
            }
        }
    }

    // ---------- MODIFICAR -----------
    @Test
    @DisplayName("modificarProducto actualiza los datos y se puede deshacer")
    void modificarProducto_actualizaDatos() {
        Producto p = buscarPorId(1);
        int minimoOriginal = p.getStock_minimo();

        p.setStock_minimo(minimoOriginal + 1);
        dao.modificarProducto(p);
        assertEquals(minimoOriginal + 1, buscarPorId(1).getStock_minimo());

        p.setStock_minimo(minimoOriginal);
        dao.modificarProducto(p);
        assertEquals(minimoOriginal, buscarPorId(1).getStock_minimo());
    }

    @Test
    @DisplayName("modificarProducto no borra los lotes del producto")
    void modificarProducto_conservaLotes() {
        Producto p = buscarPorId(1);
        List<Document> lotesAntes = p.getLotes();

        p.setLotes(null); // aunque la copia llegue sin lotes, en la BD no se tocan
        dao.modificarProducto(p);

        assertEquals(lotesAntes, buscarPorId(1).getLotes(),
                "Los lotes guardados no deben cambiar al modificar el producto");
    }

    @Test
    @DisplayName("modificarProducto de un producto inexistente lanza excepción")
    void modificarProducto_inexistente() {
        Producto p = buscarPorId(1);
        p.setId(ID_INEXISTENTE);

        assertThrows(IllegalArgumentException.class, () -> dao.modificarProducto(p));
        assertNull(buscarPorId(ID_INEXISTENTE), "No debe crearse el producto");
    }

}
