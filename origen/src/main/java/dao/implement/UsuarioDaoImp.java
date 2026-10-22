package dao.implement;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.Empleado;
import model.Usuario;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 *
 * @author Joel
 */
public class UsuarioDaoImp extends AccesoMongoDB implements dao.UsuarioDao {

    // singleton
    private static UsuarioDaoImp instance = null;

    private UsuarioDaoImp() {
    }

    public static UsuarioDaoImp getInstance() {
        if (instance == null) {
            instance = new UsuarioDaoImp();
        }
        return instance;
    }

    private static final String COLECCION = "usuarios";

    @Override
    public Usuario obtenerUsuario(String user, String pw) {
        if (user == null || pw == null) {
            return null;
        }
        try (MongoClient client = abrirConexion()) {
            MongoCollection<Document> coleccion = getCollection(client, COLECCION);

            Document doc = coleccion.find(Filters.eq("username", user)).first();
            if (doc == null) {
                return null;
            }

            String guardada = doc.getString("password_hash"); // IMPORTANTE hashear la contraseña
            if (guardada == null || !guardada.equals(pw)) {
                return null;
            }

            Usuario usuario = documentoAUsuario(doc);

            return usuario.puedeAcceder() ? usuario : null;
        } catch (MongoException e) {
            throw new DaoException("Error al consultar el usuario en la base de datos.", e);
        }
    }

    @Override
    public void altaUsuario(Usuario user) {
        //user.validar();

        try (MongoClient client = abrirConexion()) {
            MongoCollection<Document> coleccion = getCollection(client, COLECCION);

            if (coleccion.countDocuments(Filters.eq("username", user.getUsername())) > 0) {
                throw new IllegalArgumentException(
                        "Ya existe un usuario con ese username.");
            }

            ObjectId nuevoId = new ObjectId();
            Document doc = usuarioADocumento(user);
            doc.put("_id", nuevoId);

            coleccion.insertOne(doc);
            user.setId(nuevoId.toHexString());

        } catch (MongoException e) {
            throw new DaoException("Error al dar de alta el usuario.", e);
        }
    }

    @Override
    public void eliminarUsuario(Usuario user) {

    }

    @Override
    public void modificarUsuario(Usuario user) {

    }

    // ---------- Conversión Usuario a Document ----------
    private Document usuarioADocumento(Usuario u) {
        return new Document("username", u.getUsername())
                .append("password_hash", u.getPassword_hash())
                .append("rol", u.getRol())
                .append("permisos", u.getPermisos())
                .append("activo", u.isActivo())
                .append("empleado", empleadoADocumento(u.getEmpleado()))
                .append("creado_en", convertirAFecha(u.getCreado_en()));
    }

    private Usuario documentoAUsuario(Document doc) {
        Usuario u = new Usuario();

        u.setId(doc.get("_id").toString());
        u.setUsername(doc.getString("username"));
        u.setPassword_hash(doc.getString("password_hash"));
        u.setRol(doc.getString("rol"));

        List<String> permisos = doc.getList("permisos", String.class);
        u.setPermisos(permisos != null ? permisos : new ArrayList<>());

        u.setActivo(Boolean.TRUE.equals(doc.getBoolean("activo")));
        u.setEmpleado(documentoAEmpleado(doc.get("empleado", Document.class)));
        u.setCreado_en(convertirAString(doc.get("creado_en")));

        return u;
    }

// ---------- Conversión Empleado a Document ----------
    private Document empleadoADocumento(Empleado e) {
        if (e == null) {
            return null;
        }

        return new Document("nombre", e.getNombre())
                .append("apellido", e.getApellido())
                .append("dni", e.getDni())
                .append("telefono", e.getTelefono())
                .append("email", e.getEmail())
                .append("puesto", e.getPuesto())
                .append("fecha_alta", convertirAFecha(e.getFecha_alta()));
    }

    private Empleado documentoAEmpleado(Document d) {
        if (d == null) {
            return null;
        }

        Empleado e = new Empleado();
        e.setNombre(d.getString("nombre"));
        e.setApellido(d.getString("apellido"));
        e.setDni(d.getString("dni"));
        e.setTelefono(d.getString("telefono"));
        e.setEmail(d.getString("email"));
        e.setPuesto(d.getString("puesto"));
        e.setFecha_alta(convertirAString(d.get("fecha_alta")));

        return e;
    }

// ---------- Conversion fechas de fechas ----------
    private java.util.Date convertirAFecha(String fecha) {
        if (fecha == null || fecha.isBlank()) {
            return null;
        }

        try {
            return java.util.Date.from( java.time.Instant.parse(fecha));
        } catch (java.time.format.DateTimeParseException ex) {
            return null;}
    }

    private String convertirAString(Object fecha) {
        if (fecha == null) {
            return null;
        }
        if (fecha instanceof java.util.Date) {
            return ((java.util.Date) fecha).toInstant().toString();
        }

        return fecha.toString();
    }
}
