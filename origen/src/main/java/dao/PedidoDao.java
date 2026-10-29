/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import model.VentaPedido;

/**
 *
 * @author Joel
 */
public interface PedidoDao {

    public List<VentaPedido> consultar();

    public void agregar(VentaPedido pedido);
}
