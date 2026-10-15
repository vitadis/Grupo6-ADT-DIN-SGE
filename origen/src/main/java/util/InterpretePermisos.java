/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Joel
 */
public class InterpretePermisos {

    /**
     * Devuelve los apartados del navegador a los que el usuario no puede
     * acceder.
     * <p>
     * Un apartado se considera accesible si el usuario tiene su permiso base
     * (por ejemplo {@code "gestion"}) o cualquiera de sus subpermisos (por
     * ejemplo {@code "gestion.ingresosGastos"}). Si el usuario tiene el permiso
     * {@code "total"}, no se devuelve ningún apartado.
     *
     * @param permisos lista de permisos asignados al usuario
     * @return lista de apartados del navegador sin acceso, en el orden del nav;
     * vacía si el usuario tiene {@code "total"} o acceso a todos
     */
    public static List<String> apartadosSinAcceso(List<String> permisos) {
        if (permisos.contains("total")) {
            return new ArrayList<>();
        }

        Map<String, String> nav = new HashMap<>();
        nav.put("Inicio", "inicio");
        nav.put("Caja y pedidos", "caja");
        nav.put("Gestión económica", "gestion");
        nav.put("Gestión stock", "stock");
        nav.put("Compras y proveedores", "compras");
        nav.put("Análisis e informes", "informes");
        nav.put("Administración", "administracion");

        List<String> sinAcceso = new ArrayList<>(nav.keySet());
        sinAcceso.removeIf(apartado -> {
            String base = nav.get(apartado);
            return permisos.stream().anyMatch(p -> p.equals(base) || p.startsWith(base + "."));
        });
        return sinAcceso;
    }
}
