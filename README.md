# ORIGEN BAR - Proyecto de aplicación

## 1. Configuración de la base de datos

### Requisitos

- **MongoDB Compass:** versión 1.53.2 o superior. [Descargar](https://www.mongodb.com/try/download/compass).

### Conexión

Conectarse desde MongoDB Compass a:

```text
mongodb://localhost:27017
```

Si no existe, crear la base de datos `origen`.

### Validación e importación de pedidos

1. Abrir el [validador](./datos/origen.validator.js) desde MongoDB Shell, conectado a `origen`, y ejecutar su contenido.
2. Importar los [pedidos](./datos/origen.collection_pedidos.json) en la colección `pedidos` mediante **Add Data → Import JSON or CSV**.

### Importación de colecciones

Importar los siguientes ficheros desde MongoDB Compass mediante **Add Data → Import JSON or CSV**:

| Colección | Fichero |
|---|---|
| compras | [origen.compras.json](./datos/origen.compras.json) |
| movimientos_stock | [origen.movimientos_stock.json](./datos/origen.movimientos_stock.json) |
| permisos | [origen.permisos.json](./datos/origen.permisos.json) |
| productos | [origen.productos.json](./datos/origen.productos.json) |
| proveedores | [origen.proveedores.json](./datos/origen.proveedores.json) |
| registros_economicos | [origen.registros_economicos.json](./datos/origen.registros_economicos.json) |
| usuarios | [origen.usuarios.json](./datos/origen.usuarios.json) |

> **Nota:** Respetar los nombres de las colecciones indicados.

## 2. Instalación y ejecución

## 3. Tecnologías utilizadas

## 4. Arquitectura del proyecto

## 5. Funcionalidades

## 6. Autores
