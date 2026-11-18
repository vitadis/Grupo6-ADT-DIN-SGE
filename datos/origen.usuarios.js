db.usuarios.insertMany([
  {
    "_id": 1,
    "username": "admin",
    "password_hash": "1234",
    "rol": "administrador",
    "permisos": [
      "total"
    ],
    "activo": true,
    "creado_en": ISODate("2025-03-01T00:00:00.000Z")
  },
  {
    "_id": 2,
    "username": "hodei.torres",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "caja",
      "stock"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Hodei",
      "apellido": "Torres",
      "dni": "12121212A",
      "telefono": "612345678",
      "email": "hodei.torres@gmail.com",
      "puesto": "Camarero",
      "fecha_alta": ISODate("2025-03-01T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-03-01T00:00:00.000Z")
  },
  {
    "_id": 3,
    "username": "lucia.martinez",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "stock"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Lucía",
      "apellido": "Martínez",
      "dni": "23456789B",
      "telefono": "623456789",
      "email": "lucia.martinez@gmail.com",
      "puesto": "Cocinera",
      "fecha_alta": ISODate("2025-03-05T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-03-05T00:00:00.000Z")
  },
  {
    "_id": 4,
    "username": "iker.garcia",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "stock",
      "compras"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Iker",
      "apellido": "García",
      "dni": "34567890C",
      "telefono": "634567890",
      "email": "iker.garcia@gmail.com",
      "puesto": "Almacenero",
      "fecha_alta": ISODate("2025-03-10T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-03-10T00:00:00.000Z")
  },
  {
    "_id": 5,
    "username": "nerea.lopez",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "caja",
      "stock"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Nerea",
      "apellido": "López",
      "dni": "45678901D",
      "telefono": "645678901",
      "email": "nerea.lopez@gmail.com",
      "puesto": "Camarera",
      "fecha_alta": ISODate("2025-03-15T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-03-15T00:00:00.000Z")
  },
  {
    "_id": 6,
    "username": "unai.fernandez",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "stock"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Unai",
      "apellido": "Fernández",
      "dni": "56789012E",
      "telefono": "656789012",
      "email": "unai.fernandez@gmail.com",
      "puesto": "Ayudante de cocina",
      "fecha_alta": ISODate("2025-03-20T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-03-20T00:00:00.000Z")
  },
  {
    "_id": 7,
    "username": "ainhoa.perez",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "stock",
      "compras",
      "informes"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Ainhoa",
      "apellido": "Pérez",
      "dni": "67890123F",
      "telefono": "667890123",
      "email": "ainhoa.perez@gmail.com",
      "puesto": "Encargada de almacén",
      "fecha_alta": ISODate("2025-04-01T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-04-01T00:00:00.000Z")
  },
  {
    "_id": 8,
    "username": "mikel.sanchez",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "caja",
      "stock"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Mikel",
      "apellido": "Sánchez",
      "dni": "78901234G",
      "telefono": "678901234",
      "email": "mikel.sanchez@gmail.com",
      "puesto": "Camarero",
      "fecha_alta": ISODate("2025-04-05T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-04-05T00:00:00.000Z")
  },
  {
    "_id": 9,
    "username": "leire.gomez",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "stock"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Leire",
      "apellido": "Gómez",
      "dni": "89012345H",
      "telefono": "689012345",
      "email": "leire.gomez@gmail.com",
      "puesto": "Cocinera",
      "fecha_alta": ISODate("2025-04-10T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-04-10T00:00:00.000Z")
  },
  {
    "_id": 10,
    "username": "aitor.ruiz",
    "password_hash": "1234",
    "rol": "trabajador",
    "permisos": [
      "inicio",
      "stock",
      "compras"
    ],
    "activo": true,
    "empleado": {
      "nombre": "Aitor",
      "apellido": "Ruiz",
      "dni": "90123456J",
      "telefono": "690123456",
      "email": "aitor.ruiz@gmail.com",
      "puesto": "Ayudante de almacén",
      "fecha_alta": ISODate("2025-04-15T00:00:00.000Z")
    },
    "creado_en": ISODate("2025-04-15T00:00:00.000Z")
  }
])
