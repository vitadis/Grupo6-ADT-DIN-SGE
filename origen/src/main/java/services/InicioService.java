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
 *
 * @author Hodei.Torres
 */
public class InicioService {

    private final GestionStockService stockService = new GestionStockService();
    private final ComprasService comprasService = new ComprasService();

    /** Productos con stock 0. */
    public List<Producto> productosAgotados() throws DaoException {
        List<Producto> lista = new ArrayList<>();
        for (Producto p : stockService.listarProductos()) {
            if (p.getStock_actual() == 0) {
                lista.add(p);
            }
        }
        return lista;
    }

    /** Productos con stock mayor que 0 y hasta el mínimo. */
    public List<Producto> productosStockBajo() throws DaoException {
        List<Producto> lista = new ArrayList<>();
        for (Producto p : stockService.listarProductos()) {
            if (p.tieneStockBajo()) {
                lista.add(p);
            }
        }
        return lista;
    }

    /** Las últimas compras, de la más nueva a la más antigua. */
    public List<Compra> ultimasCompras(int max) throws DaoException {
        List<Compra> todas = comprasService.listarCompras();
        List<Compra> ultimas = new ArrayList<>();
        for (int i = todas.size() - 1; i >= 0 && ultimas.size() < max; i--) {
            ultimas.add(todas.get(i));
        }
        return ultimas;
    }

    public int totalCompras() throws DaoException {
        return comprasService.listarCompras().size();
    }

    /** Suma de las compras del mes actual. */
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

    /** Nombre de cada proveedor por id (incluye inactivos). */
    public Map<Integer, String> nombresProveedores() throws DaoException {
        Map<Integer, String> nombres = new HashMap<Integer, String>();
        for (Proveedor p : comprasService.listarProveedores()) {
            nombres.put(p.getId(), p.getNombre());
        }
        return nombres;
    }
}
