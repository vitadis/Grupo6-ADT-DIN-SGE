package dao.implement;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import exceptions.DaoException;
import model.Usuario;

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
            MongoCollection<Usuario> coleccion = getCollection(client, COLECCION, Usuario.class);

            Usuario usuario = coleccion.find(Filters.eq("username", user)).first();
            if (usuario == null) {
                return null;
            }

            String guardada = usuario.getPassword_hash(); // IMPORTANTE buscar la manera para hashear el pw
            if (guardada == null || !guardada.equals(pw)) {
                return null;
            }

            return usuario.puedeAcceder() ? usuario : null;
        } catch (MongoException e) {
            throw new DaoException("Error al consultar el usuario en la base de datos.", e);
        }
    }

    @Override
    public void altaUsuario(Usuario user) {

        try (MongoClient client = abrirConexion()) {
            MongoCollection<Usuario> coleccion = getCollection(client, COLECCION, Usuario.class);

            if (coleccion.countDocuments(Filters.eq("username", user.getUsername())) > 0) {
                throw new IllegalArgumentException(
                        "Ya existe un usuario con ese username.");
            }

            // Los _id son enteros y el driver no los genera: siguiente = mayor + 1
            Usuario ultimo = coleccion.find().sort(Sorts.descending("_id")).first();
            user.setId(ultimo == null || ultimo.getId() == null ? 1 : ultimo.getId() + 1);

            coleccion.insertOne(user);

        } catch (MongoException e) {
            throw new DaoException("Error al dar de alta el usuario.", e);
        }
    }

    @Override
    public void eliminarUsuario(Usuario user) {

    }

    @Override
    public void modificarUsuario(Usuario user) {

        if (user == null) {
            throw new IllegalArgumentException(
                    "El usuario no puede ser nulo.");
        }

        if (user.getId() == null) {
            throw new IllegalArgumentException(
                    "El id del usuario no puede ser nulo.");
        }

        try (MongoClient client = abrirConexion()) {

            MongoCollection<Usuario> coleccion = getCollection(client, COLECCION, Usuario.class);

            Usuario usuarioExistente = coleccion.find(Filters.eq("_id", user.getId())).first();

            if (usuarioExistente == null) {
                throw new IllegalArgumentException("No existe ningún usuario con el id " + user.getId() + ".");
            }

            coleccion.replaceOne(Filters.eq("_id", user.getId()), user);

        } catch (MongoException e) {
            throw new DaoException(
                    "Error al modificar el usuario.", e);
        }
    }
}
