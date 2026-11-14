package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Joel
 */
public class RegistroLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String fecha;
    private String user;
    private final String nivel;
    private final String mensaje;
    private final String excepcion;

    public RegistroLog(String nivel, String mensaje, Throwable e) {
        this.fecha = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.nivel = nivel;
        this.mensaje = mensaje;
        this.excepcion = (e == null) ? null : e.toString();
    }

    public String getFecha() {
        return fecha;
    }

    public String getNivel() {
        return nivel;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getExcepcion() {
        return excepcion;
    }

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
