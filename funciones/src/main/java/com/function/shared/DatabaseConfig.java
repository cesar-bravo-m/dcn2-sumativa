package com.function.shared;

public class DatabaseConfig {
    
    public static final String DB_URL = "jdbc:postgresql://172.200.178.168:5432/duoc";
    public static final String DB_USER = "postgres";
    public static final String DB_PASSWORD = "84oL4mK6cM8w7SK";
    
    public static final String EVENT_GRID_TOPIC_ENDPOINT = "https://dcn-eventgrid-grupo10.eastus2-1.eventgrid.azure.net/api/events";
    public static final String EVENT_GRID_TOPIC_KEY = "DMCHuZTG7pNesHVpYayE2U48O5LavIzcWafYubKaLUs1QGDFPw3UJQQJ99BIACHYHv6XJ3w3AAABAZEG8wgk";
    
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
