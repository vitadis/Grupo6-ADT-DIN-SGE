/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import exceptions.DaoException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Compra;
import model.Producto;
import model.Proveedor;

/**
 * Datos que se muestran en la pantalla de inicio: alertas de stock, últimas compras y gastos.
 *
 * @author Hodei.Torres
 */
public class InicioService {

    private final GestionStockService stockService = new GestionStockService();
    private final ComprasService comprasService = new ComprasService();

    /**
     * Crea el servicio.
     */
    public InicioService() {
    }

    /**
     * Devuelve los productos sin stock.
     *
     * @return lista de productos agotados
     * @throws DaoException si falla el acceso a la base de datos
     */
    public List<Producto> productosAgotados() throws DaoException {
        List<Producto> lista = new ArrayList<>();
        for (Producto p : stockService.listarProductos()) {
            if (p.getStock_actual() == 0) {
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Devuelve los productos con stock bajo (sin estar agotados).
     *
     * @return lista de productos con stock bajo
     * @throws DaoException si falla el acceso a la base de datos
     */
    public List<Producto> productosStockBajo() throws DaoException {
        List<Producto> lista = new ArrayList<>();
        for (Producto p : stockService.listarProductos()) {
            if (p.tieneStockBajo()) {
                lista.add(p);
            }
        }
        return lista;
    }

    /**
     * Devuelve las últimas compras, de la más reciente a la más antigua.
     *
     * @param max número máximo de compras
     * @return lista con como mucho {@code max} compras
     * @throws DaoException si falla el acceso a la base de datos
     */
    public List<Compra> ultimasCompras(int max) throws DaoException {
        List<Compra> todas = comprasService.listarCompras();
        List<Compra> ultimas = new ArrayList<>();
        for (int i = todas.size() - 1; i >= 0 && ultimas.size() < max; i--) {
            ultimas.add(todas.get(i));
        }
        return ultimas;
    }

    /**
     * Devuelve el número total de compras registradas.
     *
     * @return número de compras
     * @throws DaoException si falla el acceso a la base de datos
     */
    public int totalCompras() throws DaoException {
        return comprasService.listarCompras().size();
    }

    /**
     * Suma el total de las compras del mes actual.
     *
     * @return gastos del mes en euros
     * @throws DaoException si falla el acceso a la base de datos
     */
    public double gastosDelMes() throws DaoException {
        LocalDate hoy = LocalDate.now();
        double gastos = 0;
        for (Compra c : comprasService.listarCompras()) {
            LocalDate f = LocalDate.parse(c.getFecha());
            if (f.getYear() == hoy.getYear() && f.getMonth() == hoy.getMonth()) {
                gastos += c.getTotal();
            }
        }
        return gastos;
    }

    /**
     * Devuelve un mapa con el nombre de cada proveedor según su identificador.
     *
     * @return mapa id de proveedor -&gt; nombre
     * @throws DaoException si falla el acceso a la base de datos
     */
    public Map<Integer, String> nombresProveedores() throws DaoException {
        Map<Integer, String> nombres = new HashMap<Integer, String>();
        for (Proveedor p : comprasService.listarProveedores()) {
            nombres.put(p.getId(), p.getNombre());
        }
        return nombres;
    }
}
