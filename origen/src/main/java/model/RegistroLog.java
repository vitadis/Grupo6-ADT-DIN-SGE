package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entrada del registro (log) de la aplicación. Guarda la fecha, el usuario, el nivel, el mensaje
 * y, si la hay, la excepción asociada.
 *
 * @author Joel
 */
public class RegistroLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Fecha y hora del registro con formato {@code yyyy-MM-dd HH:mm:ss}. */
    private final String fecha;
    /** Nombre del usuario que tenía la sesión iniciada. */
    private String user;
    /** Nivel del registro (INFO, WARNING, SEVERE...). */
    private final String nivel;
    /** Mensaje descriptivo. */
    private final String mensaje;
    /** Texto de la excepción asociada, o {@code null} si no hay. */
    private final String excepcion;

    /**
     * Crea un registro con la fecha y hora actuales.
     *
     * @param nivel nivel del registro
     * @param mensaje mensaje descriptivo
     * @param e excepción asociada, o {@code null} si no hay
     */
    public RegistroLog(String nivel, String mensaje, Throwable e) {
        this.fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.nivel = nivel;
        this.mensaje = mensaje;
        this.excepcion = (e == null) ? null : e.toString();
    }

    /**
     * Devuelve la fecha y hora del registro.
     *
     * @return fecha con formato {@code yyyy-MM-dd HH:mm:ss}
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * Devuelve el nivel del registro.
     *
     * @return nivel del registro
     */
    public String getNivel() {
        return nivel;
    }

    /**
     * Devuelve el mensaje del registro.
     *
     * @return mensaje descriptivo
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * Devuelve el texto de la excepción asociada.
     *
     * @return texto de la excepción, o {@code null} si no hay
     */
    public String getExcepcion() {
        return excepcion;
    }

    /**
     * Asigna el usuario que tenía la sesión iniciada.
     *
     * @param user nombre del usuario
     */
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        String texto = "[" + nivel + "]\t" + fecha + "\tUserName: " + user + "\tMessage: " + mensaje;
        if (excepcion != null) {
            texto += "\t\t | Excepción: " + excepcion;
        }
        return texto;
    }
}
