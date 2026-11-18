/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.PedidoDao;
import dao.implement.PedidoDaoImp;
import java.util.List;
import model.VentaPedido;

/**
 * Lógica de negocio de los pedidos de venta.
 *
 * @author Joel
 */
public class PedidoService {

    private final PedidoDao pedidoDao = PedidoDaoImp.getInstance();

    /**
     * Crea el servicio usando el DAO de pedidos.
     */
    public PedidoService() {
    }

    /**
     * Valida el pedido y lo guarda.
     *
     * @param pedido pedido que se va a guardar
     * @throws IllegalArgumentException si el pedido está vacío o no es válido
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public void realizarPedido(VentaPedido pedido) {
        if (pedido.estaVacio()) {
            throw new IllegalArgumentException("El pedido no puede estar vacio");
        }
        pedido.validar();
        pedidoDao.agregar(pedido);
    }

    /**
     * Devuelve todos los pedidos guardados.
     *
     * @return lista de pedidos
     * @throws exceptions.DaoException si falla el acceso a la base de datos
     */
    public List<VentaPedido> listaPedidos() {
        List<VentaPedido> pedidos = pedidoDao.consultar();
        return pedidos;
    }

}
