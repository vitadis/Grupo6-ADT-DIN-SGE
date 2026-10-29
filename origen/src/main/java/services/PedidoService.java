/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import dao.PedidoDao;
import dao.implement.PedidoDaoImp;
import model.VentaPedido;

/**
 *
 * @author Joel
 */
public class PedidoService {

    private final PedidoDao usuarioDao = PedidoDaoImp.getInstance();

    public PedidoService() {
    }

    public void realizarPedido(VentaPedido pedido) {
        if (pedido.estaVacio()) {
            throw new IllegalArgumentException("El pedido no puede estar vacio");
        }
        pedido.validar();
        usuarioDao.agregar(pedido);
    }

}
