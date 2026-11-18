db.createCollection("productos", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            title: "Validación de la colección productos",

            required: [
                "_id", "nombre", "tipo", "categoria", "precio_venta", "iva",
                "stock_actual", "stock_minimo", "lotes", "proveedores_id", "activo"
            ],

            additionalProperties: false,

            properties: {
                _id: {
                    bsonType: "int",
                    minimum: 1
                },

                nombre: {
                    bsonType: "string",
                    minLength: 1,
                    pattern: "\\S"
                },

                tipo: {
                    enum: ["VENTA", "INGREDIENTE"]
                },

                categoria: {
                    enum: [
                        "BEBIDAS", "CAFETERIA_VENTA", "BOLLERIA", "COMIDA",
                        "VERDURAS", "HUEVOS", "ACEITES", "EMBUTIDOS",
                        "CAFETERIA_INGREDIENTE", "LACTEOS", "INFUSIONES",
                        "CONGELADOS", "PANADERIA"
                    ]
                },

                precio_venta: {
                    bsonType: ["double", "int"],
                    minimum: 0
                },

                iva: {
                    bsonType: ["double", "int"],
                    minimum: 0,
                    maximum: 1
                },

                stock_actual: {
                    bsonType: "int",
                    minimum: 0
                },

                stock_minimo: {
                    bsonType: "int",
                    minimum: 0
                },

                lotes: {
                    bsonType: "array",

                    items: {
                        bsonType: "object",
                        required: ["cantidad", "fecha_caducidad"],
                        additionalProperties: false,

                        properties: {
                            cantidad: {
                                bsonType: "int",
                                minimum: 1
                            },

                            fecha_caducidad: {
                                oneOf: [
                                    {
                                        bsonType: "string",
                                        pattern: "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$"
                                    },
                                    {
                                        bsonType: "date"
                                    }
                                ]
                            }
                        }
                    }
                },

                receta: {
                    bsonType: "array",

                    items: {
                        bsonType: "object",
                        required: ["producto_id", "cantidad", "unidad"],
                        additionalProperties: false,

                        properties: {
                            producto_id: {
                                bsonType: "int",
                                minimum: 1
                            },

                            cantidad: {
                                bsonType: ["double", "int"],
                                minimum: 0,
                                exclusiveMinimum: true
                            },

                            unidad: {
                                enum: ["kg", "l", "unidad"]
                            }
                        }
                    }
                },

                proveedores_id: {
                    bsonType: "array",
                    uniqueItems: true,

                    items: {
                        bsonType: "int",
                        minimum: 1
                    }
                },

                activo: {
                    bsonType: "bool"
                },

                imagen: {
                    bsonType: "string"
                }
            },

            oneOf: [
                {
                    properties: {
                        tipo: {
                            enum: ["VENTA"]
                        },
                        categoria: {
                            enum: [
                                "BEBIDAS", "CAFETERIA_VENTA",
                                "BOLLERIA", "COMIDA"
                            ]
                        }
                    }
                },
                {
                    properties: {
                        tipo: {
                            enum: ["INGREDIENTE"]
                        },
                        categoria: {
                            enum: [
                                "VERDURAS", "HUEVOS", "ACEITES",
                                "EMBUTIDOS", "CAFETERIA_INGREDIENTE",
                                "LACTEOS", "INFUSIONES", "CONGELADOS",
                                "PANADERIA"
                            ]
                        }
                    }
                }
            ]
        }
    }
});