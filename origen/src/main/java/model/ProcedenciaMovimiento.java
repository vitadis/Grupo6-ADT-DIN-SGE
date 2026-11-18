package model;

/**
 * Documento del que procede un registro económico ({@link Movimiento}): la colección y el
 * {@code _id} del documento. Por ejemplo, el gasto de una compra lleva
 * {@code origen: {coleccion: "compras", procedencia_id: 1}} y el ingreso de un pedido cobrado
 * {@code origen: {coleccion: "pedidos", procedencia_id: 7}}. Los registros que se meten a mano
 * (alquiler, nóminas...) no tienen procedencia.
 *
 * @author anazk
 */
public class ProcedenciaMovimiento {

    /**
     * Colección de procedencia para las compras.
     */
    public static final String COMPRAS = "compras";
    /**
     * Colección de procedencia para los pedidos.
     */
    public static final String PEDIDOS = "pedidos";

    /**
     * Nombre de la colección de procedencia: {@link #COMPRAS} o {@link #PEDIDOS}.
     */
    private String coleccion;
    /**
     * {@code _id} de la procedencia de ese movimiento en esa colección.
     */
    private int procedencia_id;

    /**
     * Constructor vacío. Lo usa el driver de MongoDB al leer.
     */
    public ProcedenciaMovimiento() {
    }

    /**
     * Crea una procedencia y la valida con {@link #validar()}.
     *
     * @param coleccion colección de procedencia
     * @param procedencia_id {@code _id} del documento
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public ProcedenciaMovimiento(String coleccion, int procedencia_id) {
        this.coleccion = coleccion;
        this.procedencia_id = procedencia_id;

        validar();
    }

    /**
     * Comprueba que la colección es {@link #COMPRAS} o {@link #PEDIDOS} y que el
     * {@code procedencia_id} es mayor que 0.
     *
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public void validar() {
        if (!COMPRAS.equals(coleccion) && !PEDIDOS.equals(coleccion)) {
            throw new IllegalArgumentException(
                    "La colección de origen debe ser compras o pedidos.");
        }

        if (procedencia_id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador debe de origen debe ser mayor que 0.");
        }
    }

    /**
     * Devuelve la colección de procedencia.
     *
     * @return la colección
     */
    public String getColeccion() {
        return coleccion;
    }

    /**
     * Cambia la colección de procedencia.
     *
     * @param coleccion la colección
     */
    public void setColeccion(String coleccion) {
        this.coleccion = coleccion;
    }

    /**
     * Devuelve el {@code _id} del documento de procedencia.
     *
     * @return el identificador
     */
    public int getProcedencia_id() {
        return procedencia_id;
    }

    /**
     * Cambia el {@code _id} del documento de procedencia.
     *
     * @param procedencia_id el identificador
     */
    public void setProcedencia_id(int procedencia_id) {
        this.procedencia_id = procedencia_id;
    }

    @Override
    public String toString() {
        return coleccion + "/" + procedencia_id;
    }
}
