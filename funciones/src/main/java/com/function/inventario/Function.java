package com.function.inventario;

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
    
    @FunctionName("Inventario")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Procesando solicitud HTTP de Java para Inventarios.");

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
            
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleGet(HttpRequestMessage<Optional<String>> request, 
                                        ExecutionContext context) throws Exception {
        String idParam = request.getQueryParameters().get("id");
        String productoIdParam = request.getQueryParameters().get("productoId");
        String bodegaIdParam = request.getQueryParameters().get("bodegaId");
        
        if (idParam != null && !idParam.isEmpty()) {
            // Get inventario by ID
            try {
                Integer inventarioId = Integer.parseInt(idParam);
                InventarioDTO inventario = databaseService.getInventarioById(inventarioId);
                
                if (inventario != null) {
                    String jsonResponse = objectMapper.writeValueAsString(inventario);
                    return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Inventario no encontrado con ID: " + inventarioId)
                        .build();
                }
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Formato de ID de inventario inválido")
                    .build();
            }
        } else if (productoIdParam != null && !productoIdParam.isEmpty()) {
            // Get inventarios by producto ID
            try {
                Integer productoId = Integer.parseInt(productoIdParam);
                List<InventarioDTO> inventarios = databaseService.getInventariosByProductoId(productoId);
                context.getLogger().info("Recuperados " + inventarios.size() + " inventarios para producto ID: " + productoId);
                
                String jsonResponse = objectMapper.writeValueAsString(inventarios);
                return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(jsonResponse)
                    .build();
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Formato de ID de producto inválido")
                    .build();
            }
        } else if (bodegaIdParam != null && !bodegaIdParam.isEmpty()) {
            // Get inventarios by bodega ID
            try {
                Integer bodegaId = Integer.parseInt(bodegaIdParam);
                List<InventarioDTO> inventarios = databaseService.getInventariosByBodegaId(bodegaId);
                context.getLogger().info("Recuperados " + inventarios.size() + " inventarios para bodega ID: " + bodegaId);
                
                String jsonResponse = objectMapper.writeValueAsString(inventarios);
                return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(jsonResponse)
                    .build();
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Formato de ID de bodega inválido")
                    .build();
            }
        } else {
            // Get all inventarios
            List<InventarioDTO> inventarios = databaseService.getAllInventarios();
            context.getLogger().info("Recuperados " + inventarios.size() + " inventarios de la base de datos");
            
            String jsonResponse = objectMapper.writeValueAsString(inventarios);
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
            InventarioDTO inventario = objectMapper.readValue(body.get(), InventarioDTO.class);
            
            // Validate required fields
            if (inventario.getProductoId() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("productoId es requerido")
                    .build();
            }
            if (inventario.getBodegaId() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("bodegaId es requerido")
                    .build();
            }
            if (inventario.getCantidad() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("cantidad es requerida")
                    .build();
            }
            if (inventario.getCantidad() < 0) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("cantidad no puede ser negativa")
                    .build();
            }
            
            InventarioDTO createdInventario = databaseService.createInventario(inventario);
            
            if (createdInventario != null) {
                String jsonResponse = objectMapper.writeValueAsString(createdInventario);
                return request.createResponseBuilder(HttpStatus.CREATED)
                    .header("Content-Type", "application/json")
                    .body(jsonResponse)
                    .build();
            } else {
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear inventario")
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
            Integer inventarioId = Integer.parseInt(idParam);
            Optional<String> body = request.getBody();
            
            if (!body.isPresent()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Se requiere cuerpo de solicitud")
                    .build();
            }
            
            InventarioDTO inventario = objectMapper.readValue(body.get(), InventarioDTO.class);
            inventario.setId(inventarioId);
            
            // Validate required fields
            if (inventario.getProductoId() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("productoId es requerido")
                    .build();
            }
            if (inventario.getBodegaId() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("bodegaId es requerido")
                    .build();
            }
            if (inventario.getCantidad() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("cantidad es requerida")
                    .build();
            }
            if (inventario.getCantidad() < 0) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("cantidad no puede ser negativa")
                    .build();
            }
            
            boolean updated = databaseService.updateInventario(inventario);
            
            if (updated) {
                InventarioDTO updatedInventario = databaseService.getInventarioById(inventarioId);
                String jsonResponse = objectMapper.writeValueAsString(updatedInventario);
                return request.createResponseBuilder(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(jsonResponse)
                    .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Inventario no encontrado con ID: " + inventarioId)
                    .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Formato de ID de inventario inválido")
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
            Integer inventarioId = Integer.parseInt(idParam);
            boolean deleted = databaseService.deleteInventario(inventarioId);
            
            if (deleted) {
                return request.createResponseBuilder(HttpStatus.NO_CONTENT)
                    .body("Inventario eliminado exitosamente")
                    .build();
            } else {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                    .body("Inventario no encontrado con ID: " + inventarioId)
                    .build();
            }
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Formato de ID de inventario inválido")
                .build();
        }
    }
}
