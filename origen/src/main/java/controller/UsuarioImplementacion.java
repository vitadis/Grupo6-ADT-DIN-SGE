/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;
import model.Usuario;

/**
 * Interfaz de los controladores que necesitan conocer el usuario con la sesión iniciada.
 *
 * @author Joel
 */
public interface UsuarioImplementacion {

    /**
     * Asigna el usuario con la sesión iniciada.
     *
     * @param usuario usuario actual
     */
    void setUsuario(Usuario usuario);
}
