package com.function.consumidores.producto;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class Function {
    
    private final ObjectMapper objectMapper;
    private final DatabaseService databaseService;
    
    public Function() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.databaseService = new DatabaseService();
    }
    
    @FunctionName("Productos")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Procesando solicitud HTTP de Java para Productos.");

        try {
            if (!databaseService.testConnection()) {
                context.getLogger().severe("Error al conectar con la base de datos");
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error de conexión con la base de datos")
                    .build();
            }
            
            String method = request.getHttpMethod().name();
            context.getLogger().info("Método HTTP: " + method);
            
            switch (method) {
                case "GET":
                    return handleGet(request, context);
                case "POST":
                    return handlePost(request, context);
                case "PUT":
                    return handlePut(request, context);
                case "DELETE":
                    return handleDelete(request, context);
                default:
                    return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED)
                        .body("Método no permitido")
                        .build();
            }
                
        } catch (Exception e) {
            context.getLogger().severe("Error procesando solicitud: " + e.getMessage());
            e.printStackTrace();
            
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleGet(HttpRequestMessage<Optional<String>> request, 
                                        ExecutionContext context) throws Exception {
        String idParam = request.getQueryParameters().get("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Integer productoId = Integer.parseInt(idParam);
                ProductoDTO producto = databaseService.getProductoById(productoId);

                System.out.println(producto);
                
                if (producto != null) {
                    String jsonResponse = objectMapper.writeValueAsString(producto);
                    return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado con ID: " + productoId)
                        .build();
                }
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Formato de ID de producto inválido")
                    .build();
            }
        } else {
            List<ProductoDTO> productos = databaseService.getAllProductos();
            context.getLogger().info("Recuperados " + productos.size() + " productos de la base de datos");
            
            String jsonResponse = objectMapper.writeValueAsString(productos);
            return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();
        }
    }
    
    private HttpResponseMessage handlePost(HttpRequestMessage<Optional<String>> request, 
                                         ExecutionContext context) throws Exception {
        Optional<String> body = request.getBody();
        if (!body.isPresent()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Se requiere cuerpo de solicitud")
                .build();
        }
        
        try {
            ProductoDTO producto = objectMapper.readValue(body.get(), ProductoDTO.class);
            
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("nombre es requerido")
                    .build();
            }
            
            ProductoDTO createdProducto = databaseService.createProducto(producto);
            
            if (createdProducto != null) {
                String jsonResponse = objectMapper.writeValueAsString(createdProducto);
                return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(jsonResponse)
                    .build();
            } else {
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear producto")
                    .build();
            }
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Formato JSON inválido: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handlePut(HttpRequestMessage<Optional<String>> request, 
                                        ExecutionContext context) throws Exception {
        String idParam = request.getQueryParameters().get("id");
        
        if (idParam == null || idParam.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("PUT requiere ID en parámetro de consulta: ?id={id}")
                .build();
        }
        
        try {
            Integer productoId = Integer.parseInt(idParam);
            Optional<String> body = request.getBody();
            
            if (!body.isPresent()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Se requiere cuerpo de solicitud")
                    .build();
            }
            
            ProductoDTO producto = objectMapper.readValue(body.get(), ProductoDTO.class);
            producto.setId(productoId);
            
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("nombre es requerido")
                    .build();
            }
            
            boolean updated = databaseService.updateProducto(producto);
            
            if (updated) {
                ProductoDTO updatedProducto = databaseService.getProductoById(productoId);
                String jsonResponse = objectMapper.writeValueAsString(updatedProducto);
                return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(jsonResponse)
                    .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado con ID: " + productoId)
                    .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Formato de ID de producto inválido")
                .build();
        } catch (Exception e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Formato JSON inválido: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleDelete(HttpRequestMessage<Optional<String>> request, 
                                           ExecutionContext context) throws Exception {
        String idParam = request.getQueryParameters().get("id");
        
        if (idParam == null || idParam.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("DELETE requiere ID en parámetro de consulta: ?id={id}")
                .build();
        }
        
        try {
            Integer productoId = Integer.parseInt(idParam);
            boolean deleted = databaseService.deleteProducto(productoId);
            
            if (deleted) {
                return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                    .body("Producto eliminado exitosamente")
                    .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Producto no encontrado con ID: " + productoId)
                    .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Formato de ID de producto inválido")
                .build();
        }
    }
}
