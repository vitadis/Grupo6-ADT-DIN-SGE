/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.VentaPedido;

/**
 * Operaciones de acceso a datos de los pedidos de venta.
 *
 * @author Joel
 */
public interface PedidoDao {

    /**
     * Devuelve todos los pedidos ordenados por identificador.
     *
     * @return lista de pedidos (vacía si no hay ninguno)
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public List<VentaPedido> consultar();

    /**
     * Guarda un pedido nuevo.
     *
     * @param pedido pedido que se va a guardar (ya validado)
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public void agregar(VentaPedido pedido);
}
