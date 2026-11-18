/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;
import org.bson.types.ObjectId;

/**
 * Movimiento de stock de un producto (por ejemplo, una pérdida) que se registra en MongoDB para
 * llevar el historial de cambios en el inventario.
 *
 * @author Hodei.Torres
 */
public class MovimientoStock {

    private ObjectId id;
    private int productoId;
    private String nombreProducto;
    private String tipo;
    private int cantidad;
    private String nota;
    private Date fecha;

    /**
     * Constructor vacío, necesario para el codec POJO de MongoDB.
     */
    public MovimientoStock() {

    }

    /**
     * Crea un movimiento con la fecha actual.
     *
     * @param productoId identificador del producto afectado
     * @param nombreProducto nombre del producto afectado
     * @param tipo tipo de movimiento
     * @param cantidad unidades afectadas
     * @param nota nota explicativa
     */
    public MovimientoStock(int productoId, String nombreProducto, String tipo, int cantidad, String nota) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.nota = nota;
        this.fecha = new Date();
    }
    
    /**
     * Devuelve el identificador del movimiento en MongoDB.
     *
     * @return el identificador del movimiento en MongoDB
     */
    public ObjectId getId() {
        return id;
    }

    /**
     * Asigna el identificador del movimiento en MongoDB.
     *
     * @param id el identificador del movimiento en MongoDB
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    /**
     * Devuelve el identificador del producto afectado.
     *
     * @return el identificador del producto afectado
     */
    public int getProductoId() {
        return productoId;
    }

    /**
     * Asigna el identificador del producto afectado.
     *
     * @param productoId el identificador del producto afectado
     */
    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    /**
     * Devuelve el nombre del producto afectado.
     *
     * @return el nombre del producto afectado
     */
    public String getNombreProducto() {
        return nombreProducto;
    }

    /**
     * Asigna el nombre del producto afectado.
     *
     * @param nombreProducto el nombre del producto afectado
     */
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    /**
     * Devuelve el tipo de movimiento (por ejemplo, pérdida).
     *
     * @return el tipo de movimiento (por ejemplo, pérdida)
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna el tipo de movimiento (por ejemplo, pérdida).
     *
     * @param tipo el tipo de movimiento (por ejemplo, pérdida)
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Devuelve las unidades afectadas por el movimiento.
     *
     * @return las unidades afectadas por el movimiento
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Asigna las unidades afectadas por el movimiento.
     *
     * @param cantidad las unidades afectadas por el movimiento
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Devuelve la nota explicativa del movimiento.
     *
     * @return la nota explicativa del movimiento
     */
    public String getNota() {
        return nota;
    }

    /**
     * Asigna la nota explicativa del movimiento.
     *
     * @param nota la nota explicativa del movimiento
     */
    public void setNota(String nota) {
        this.nota = nota;
    }

    /**
     * Devuelve la fecha del movimiento.
     *
     * @return la fecha del movimiento
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Asigna la fecha del movimiento.
     *
     * @param fecha la fecha del movimiento
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
}
