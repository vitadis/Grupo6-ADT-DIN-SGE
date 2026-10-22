package util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Interpreta los permisos del usuario para decidir qué apartados del menú deben
 * ocultarse.
 *
 * @author Joel
 */
public class InterpretePermisos {

    /**
     * Mapa de navegación: texto del botón del menú -> clave base de permiso. Se
     * usa {@link LinkedHashMap} para conservar el orden de aparición en el
     * menú.
     */
    private static final Map<String, String> NAV = new LinkedHashMap<>();

    static {
        NAV.put("Inicio", "inicio");
        NAV.put("Caja y pedidos", "caja");
        NAV.put("Gestión económica", "gestion");
        NAV.put("Gestión stock", "stock");
        NAV.put("Compras y proveedores", "compras");
        NAV.put("Análisis e informes", "informes");
        NAV.put("Administración", "administracion");
    }

    /**
     * Devuelve los apartados del menú a los que el usuario NO tiene acceso.
     *
     * <p>
     * Flujo:</p>
     * <ol>
     * <li>Si la lista de permisos contiene {@code "total"}, el usuario tiene
     * acceso a todo y se devuelve una lista vacía.</li>
     * <li>En caso contrario, se parte de todos los textos de botón definidos en
     * {@link #NAV}, en el orden del menú.</li>
     * <li>Para cada apartado se obtiene su clave base (por ejemplo
     * {@code "gestion"}). Si algún permiso del usuario es igual a esa clave o
     * es un subpermiso que empieza por ella seguida de un punto
     * ({@code "gestion.ingresosGastos"}), el apartado se considera accesible y
     * se descarta de la lista.</li>
     * <li>Lo que queda es lo que debe ocultarse en el menú.</li>
     * </ol>
     *
     * <p>
     * Ejemplo: con {@code ["caja", "gestion.ingresosGastos", "inicio"]} el
     * resultado es {@code [Gestión stock, Compras y proveedores,
     * Análisis e informes, Administración]}.</p>
     *
     * @param permisos lista de permisos del usuario (no debe ser {@code null})
     * @return lista con el texto de los botones sin acceso, en el orden del
     * menú; vacía si el usuario tiene acceso a todo
     */
    public static List<String> apartadosSinAcceso(List<String> permisos) {
        List<String> sinAcceso = new ArrayList<>();
        if (permisos.contains("total")) {
            return sinAcceso;
        }

        NAV.forEach((apartado, base) -> {
            boolean tieneAcceso = permisos.stream()
                    .anyMatch(p -> p.equals(base) || p.startsWith(base + ":"));
            if (!tieneAcceso) {
                sinAcceso.add(apartado);
            }
        });
        return sinAcceso;
    }
}
