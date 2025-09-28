package com.function.shared;

public class DatabaseConfig {
    
    // Recordar cambiar estos valores!!!
    public static final String DB_URL = "";
    public static final String DB_USER = "";
    public static final String DB_PASSWORD = "";
    
    public static final String EVENT_GRID_TOPIC_ENDPOINT = "";
    public static final String EVENT_GRID_TOPIC_KEY = "";
    
    public static final String EVENT_TYPE_CREAR_PRODUCTO = "Administracion.CrearProducto";
    public static final String EVENT_TYPE_ACTUALIZAR_PRODUCTO = "Administracion.ActualizarProducto";
    public static final String EVENT_TYPE_CREAR_CATEGORIA = "Administracion.CrearCategoria";
    public static final String EVENT_TYPE_ACTUALIZAR_CATEGORIA = "Administracion.ActualizarCategoria";
    public static final String EVENT_TYPE_CREAR_BODEGA = "Administracion.CrearBodega";
    public static final String EVENT_TYPE_ACTUALIZAR_BODEGA = "Administracion.ActualizarBodega";
    public static final String EVENT_TYPE_RECIBIR_PRODUCTOS = "RecepcionProductos.RecibirProductos";
    public static final String EVENT_TYPE_PROCESAR_VENTA = "GeneradorVentas.ProcesarVenta";
    
    public static final String SUBJECT_PRODUCTO = "/administracion/producto";
    public static final String SUBJECT_CATEGORIA = "/administracion/categoria";
    public static final String SUBJECT_BODEGA = "/administracion/bodega";
    public static final String SUBJECT_RECEPCION = "/recepcion/productos";
    public static final String SUBJECT_VENTAS = "/ventas/productos";
    
    public static final int DB_CONNECTION_TIMEOUT = 5;
    
    private DatabaseConfig() {
    }
}
