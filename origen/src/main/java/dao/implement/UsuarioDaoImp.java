package dao.implement;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import exceptions.DaoException;
import java.util.ArrayList;
import java.util.List;
import model.Usuario;
import org.bson.Document;

/**
 * Implementación de las operaciones de acceso a datos de los usuarios mediante
 * MongoDB. Usa la conexión compartida de AccesoMongoDB (no se cierra aquí).
 *
 * @author Joel
 */
public class UsuarioDaoImp extends AccesoMongoDB implements dao.UsuarioDao {

    private static final String COLECCION = "usuarios";
    private static final String COLECCION_PERMISOS = "permisos";

    // singleton
    private static UsuarioDaoImp instance = null;

    private UsuarioDaoImp() {
    }

    /**
     * Obtiene la única instancia de UsuarioDaoImp (patrón Singleton).
     */
    public static synchronized UsuarioDaoImp getInstance() {
        if (instance == null) {
            instance = new UsuarioDaoImp();
        }
        return instance;
    }

    /**
     * Busca un usuario por su nombre de usuario y comprueba su contraseña y sus
     * permisos de acceso.
     *
     * @return usuario si las credenciales son válidas y tiene acceso; null si
     * no
     * @throws DaoException si ocurre un error al consultar la base de datos
     */
    @Override
    public Usuario obtenerUsuario(String user, String pw) {
        if (user == null || pw == null) {
            return null;
        }
        try {
            MongoClient client = getClient();
            MongoCollection<Usuario> coleccion = getCollection(client, COLECCION, Usuario.class);

            Usuario usuario = coleccion.find(Filters.eq("username", user)).first();
            if (usuario == null) {
                return null;
            }

            String guardada = usuario.getPassword_hash(); // BUSCAR LA MANERA DE HASHEAR
            if (guardada == null || !guardada.equals(pw)) {
                return null;
            }

            return usuario.puedeAcceder() ? usuario : null;
        } catch (MongoException e) {
            throw new DaoException("Error al consultar el usuario en la base de datos.", e);
        }
    }

    /**
     * Obtiene todos los usuarios ordenados de forma ascendente por su id.
     *
     * @throws DaoException si ocurre un error al consultar la base de datos
     */
    @Override
    public List<Usuario> listarUsuarios() {
        try {
            MongoClient client = getClient();
            MongoCollection<Usuario> coleccion = getCollection(client, COLECCION, Usuario.class);

            return coleccion.find()
                    .sort(Sorts.ascending("_id"))
                    .into(new ArrayList<>());

        } catch (MongoException e) {
            throw new DaoException("Error al listar los usuarios.", e);
        }
    }

    /**
     * Obtiene la lista de permisos disponibles (campo "todos").
     *
     * @throws DaoException si ocurre un error al consultar la base de datos
     */
    @Override
    public List<String> listarPermisos() {
        try {
            MongoClient client = getClient();
            MongoCollection<Document> coleccion = getCollection(client, COLECCION_PERMISOS, Document.class);

            Document doc = coleccion.find(Filters.exists("todos")).first();
            if (doc == null) {
                return new ArrayList<>();
            }

            List<String> todos = doc.getList("todos", String.class);
            return todos != null ? todos : new ArrayList<>();

        } catch (MongoException e) {
            throw new DaoException("Error al consultar los permisos.", e);
        }
    }

    /**
     * Registra un nuevo usuario asignándole el siguiente id disponible.
     *
     * @throws DaoException si ocurre un error al insertar el usuario
     */
    @Override
    public void altaUsuario(Usuario user) {
        try {
            MongoClient client = getClient();
            MongoCollection<Usuario> coleccion = getCollection(client, COLECCION, Usuario.class);

            // Los _id son enteros y el driver no los genera: siguiente = mayor + 1
            Usuario ultimo = coleccion.find().sort(Sorts.descending("_id")).first();
            user.setId(ultimo == null || ultimo.getId() == null ? 1 : ultimo.getId() + 1);

            coleccion.insertOne(user);

        } catch (MongoException e) {
            throw new DaoException("Error al dar de alta el usuario.", e);
        }
    }

    /**
     * Sustituye los datos del usuario almacenado usando su id como criterio.
     *
     * @throws DaoException si ocurre un error al modificar el usuario
     */
    @Override
    public void modificarUsuario(Usuario user) {
        try {
            MongoClient client = getClient();
            MongoCollection<Usuario> coleccion = getCollection(client, COLECCION, Usuario.class);

            coleccion.replaceOne(Filters.eq("_id", user.getId()), user);

        } catch (MongoException e) {
            throw new DaoException("Error al modificar el usuario.", e);
        }
    }

    /**
     * Elimina el usuario cuyo id coincide con el del objeto recibido.
     *
     * @throws DaoException si ocurre un error al eliminar el usuario
     */
    @Override
    public void eliminarUsuario(Usuario user) {
        try {
            MongoClient client = getClient();
            MongoCollection<Usuario> coleccion = getCollection(client, COLECCION, Usuario.class);

            coleccion.deleteOne(Filters.eq("_id", user.getId()));

        } catch (MongoException e) {
            throw new DaoException("Error al eliminar el usuario.", e);
        }
    }
}
