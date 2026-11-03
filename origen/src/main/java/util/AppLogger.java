/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import model.RegistroLog;

/**
 * @author Joel
 */
public class AppLogger extends Logger {

    // atributos
    private String user = "NO IDENTIFICADO";
    // archivo y carpeta
    private static final File ARCHIVO = new File("logs/app.log");

    // singleton
    private static final AppLogger INSTANCIA = new AppLogger();

    private AppLogger() {
        super("origen", null);
        // level por default
        setLevel(Level.INFO);
        // no enviar al logger raíz (evita duplicar en consola)
        setUseParentHandlers(false);
    }

    public static AppLogger getInstance() {
        return INSTANCIA;
    }

    // ESCRITURA
    @Override
    public void log(LogRecord record) {
        if (!isLoggable(record.getLevel())) {
            return;
        }

        RegistroLog registro = new RegistroLog(
                record.getLevel().getName(),
                record.getMessage(),
                record.getThrown());
        registro.setUser(user);

        File carpeta = ARCHIVO.getAbsoluteFile().getParentFile();
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }

        // si el archivo ya tiene contenido, se añade sin cabecera nueva
        boolean existe = ARCHIVO.exists() && ARCHIVO.length() > 0;

        try (FileOutputStream fos = new FileOutputStream(ARCHIVO, true); ObjectOutputStream oos = existe
                ? new AppendObjectOutputStream(fos)
                : new ObjectOutputStream(fos)) {

            oos.writeObject(registro);

        } catch (IOException ex) {
            System.err.println("No se pudo escribir el log: " + ex.getMessage());
        }
    }

    // LECTURA
    public List<RegistroLog> leer() {
        List<RegistroLog> registros = new ArrayList<>();

        if (!ARCHIVO.exists() || ARCHIVO.length() == 0) {
            return registros;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            while (true) {
                registros.add((RegistroLog) ois.readObject());
            }
        } catch (EOFException fin) {
            // termina lectura
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("No se pudo leer el log: " + ex.getMessage());
        }

        return registros;
    }

    // ObjectOutputStream sin cabecera, para añadir
    private static class AppendObjectOutputStream extends ObjectOutputStream {

        public AppendObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset();
        }
    }

    public void setUser(String user) {
        this.user = user;
    }
}
