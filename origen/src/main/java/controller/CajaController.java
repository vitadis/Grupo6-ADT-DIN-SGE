/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import model.CategoriaProducto;
import model.Usuario;

import java.util.Optional;

/**
 * FXML Controller class
 *
 * @author Joel
 */
public class CajaController implements UsuarioImplementacion {

    @FXML
    private ToggleGroup categoriasGroup;
    @FXML
    private FlowPane contenedorCategorias;
    @FXML
    private RadioButton rbTodo;

    @FXML
    private void initialize() {
        // "Todo" no pertenece al enum: userData null = sin filtro
        rbTodo.setUserData(null);
        cargarCategorias();
    }

    /**
     * Crea un RadioButton por cada valor de model.CategoriaProducto.
     */
    private void cargarCategorias() {
        for (CategoriaProducto categoria : CategoriaProducto.values()) {
            RadioButton rb = new RadioButton(categoria.toString());
            rb.setToggleGroup(categoriasGroup);
            rb.setUserData(categoria);
            rb.getStyleClass().add("chip-categoria");
            contenedorCategorias.getChildren().add(rb);
        }
    }

    /**
     * Categoría seleccionada vacio si es todo -> se usa para el filtrado segun
     * la categoria
     */
    public Optional<CategoriaProducto> getCategoriaSeleccionada() {
        Toggle seleccionado = categoriasGroup.getSelectedToggle();
        if (seleccionado == null || seleccionado.getUserData() == null) {
            return Optional.empty();
        }
        return Optional.of((CategoriaProducto) seleccionado.getUserData());
    }

    // handlers, javafx
    @FXML
    private void onHistorialVentas() {
    }

    @FXML
    private void onCancelar() {
    }

    @FXML
    private void onCobrar() {
        System.out.println(usuario);
    }

    private Usuario usuario;

    @Override
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
}
