package com.function.generadores.administracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.function.shared.DatabaseConfig;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class Administracion {
    
    private final ObjectMapper objectMapper;
    
    public Administracion() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @FunctionName("generadorAdministracion")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS, route = "generadorAdministracion/{entityType}") HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        
        context.getLogger().info("Java HTTP trigger processed a request - Method: " + request.getHttpMethod());
        
        String entityType = request.getQueryParameters().get("entityType");
        if (entityType == null) {
            String uri = request.getUri().toString();
            if (uri.contains("/producto")) {
                entityType = "producto";
            } else if (uri.contains("/categoria")) {
                entityType = "categoria";
            } else if (uri.contains("/bodega")) {
                entityType = "bodega";
            }
        }
        
        if (entityType == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Entity type is required. Use path /generadorAdministracion/producto, /generadorAdministracion/categoria, or /generadorAdministracion/bodega")
                .build();
        }
        
        context.getLogger().info("Processing request for entity type: " + entityType);
        
        try {
            if (entityType.equals("producto")) {
                return handleProductoRequests(request, context);
            } else if (entityType.equals("categoria")) {
                return handleCategoriaRequests(request, context);
            } else if (entityType.equals("bodega")) {
                return handleBodegaRequests(request, context);
            } else {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid entity type. Use 'producto', 'categoria', or 'bodega'")
                    .build();
            }
        } catch (Exception e) {
            context.getLogger().severe("Error processing request: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing request: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleProductoRequests(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            return handleProductoGet(request, context);
        } else if (request.getHttpMethod() == HttpMethod.POST) {
            return handleProductoPost(request, context);
        } else if (request.getHttpMethod() == HttpMethod.PUT) {
            return handleProductoPut(request, context);
        } else {
            return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Method not allowed. Use GET, POST, or PUT for producto.")
                .build();
        }
    }
    
    private HttpResponseMessage handleCategoriaRequests(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            return handleCategoriaGet(request, context);
        } else if (request.getHttpMethod() == HttpMethod.POST) {
            return handleCategoriaPost(request, context);
        } else if (request.getHttpMethod() == HttpMethod.PUT) {
            return handleCategoriaPut(request, context);
        } else {
            return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Method not allowed. Use GET, POST, or PUT for categoria.")
                .build();
        }
    }
    
    private HttpResponseMessage handleBodegaRequests(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            return handleBodegaGet(request, context);
        } else if (request.getHttpMethod() == HttpMethod.POST) {
            return handleBodegaPost(request, context);
        } else if (request.getHttpMethod() == HttpMethod.PUT) {
            return handleBodegaPut(request, context);
        } else if (request.getHttpMethod() == HttpMethod.DELETE) {
            return handleBodegaDelete(request, context);
        } else {
            return request.createResponseBuilder(HttpStatus.METHOD_NOT_ALLOWED)
                .body("Method not allowed. Use GET, POST, PUT, or DELETE for bodega.")
                .build();
        }
    }
    
    private HttpResponseMessage handleProductoGet(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing GET request for productos");
        
        String idParam = request.getQueryParameters().get("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Integer productoId = Integer.parseInt(idParam);
                ProductoDTO producto = getProductoById(productoId);
                
                if (producto != null) {
                    String jsonResponse = objectMapper.writeValueAsString(producto);
                    return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Producto not found with ID: " + productoId)
                        .build();
                }
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid producto ID format")
                    .build();
            }
        } else {
            List<ProductoDTO> productos = getAllProductos();
            String jsonResponse = objectMapper.writeValueAsString(productos);
            return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();
        }
    }
    
    private HttpResponseMessage handleCategoriaGet(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing GET request for categorias");
        
        String idParam = request.getQueryParameters().get("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long categoriaId = Long.parseLong(idParam);
                CategoriaDTO categoria = getCategoriaById(categoriaId);
                
                if (categoria != null) {
                    String jsonResponse = objectMapper.writeValueAsString(categoria);
                    return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Categoria not found with ID: " + categoriaId)
                        .build();
                }
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid categoria ID format")
                    .build();
            }
        } else {
            List<CategoriaDTO> categorias = getAllCategorias();
            String jsonResponse = objectMapper.writeValueAsString(categorias);
            return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();
        }
    }
    
    private HttpResponseMessage handleBodegaGet(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing GET request for bodegas");
        
        String idParam = request.getQueryParameters().get("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                Long bodegaId = Long.parseLong(idParam);
                BodegaDTO bodega = getBodegaById(bodegaId);
                
                if (bodega != null) {
                    String jsonResponse = objectMapper.writeValueAsString(bodega);
                    return request.createResponseBuilder(HttpStatus.OK)
                        .header("Content-Type", "application/json")
                        .body(jsonResponse)
                        .build();
                } else {
                    return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body("Bodega not found with ID: " + bodegaId)
                        .build();
                }
            } catch (NumberFormatException e) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Invalid bodega ID format")
                    .build();
            }
        } else {
            List<BodegaDTO> bodegas = getAllBodegas();
            String jsonResponse = objectMapper.writeValueAsString(bodegas);
            return request.createResponseBuilder(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(jsonResponse)
                .build();
        }
    }
    
    private HttpResponseMessage handleProductoPost(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing POST request to create new product");
        
        Optional<String> body = request.getBody();
        if (!body.isPresent() || body.get().trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Request body is required")
                .build();
        }
        
        try {
            ProductoDTO producto = objectMapper.readValue(body.get(), ProductoDTO.class);
            
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Product name is required")
                    .build();
            }
            
            if (producto.getCategoria() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Product category is required")
                    .build();
            }
            
            sendEventToGrid(DatabaseConfig.EVENT_TYPE_CREAR_PRODUCTO, producto, context);
            
            return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .body("Product creation event sent successfully")
                .build();
                
        } catch (Exception e) {
            context.getLogger().severe("Error parsing product JSON: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid JSON format: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleProductoPut(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing PUT request to update product");
        
        String idParam = request.getQueryParameters().get("id");
        if (idParam == null || idParam.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("PUT requires product ID in query parameter: ?id={id}")
                .build();
        }
        
        Integer productId;
        try {
            productId = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid product ID format")
                .build();
        }
        
        Optional<String> body = request.getBody();
        if (!body.isPresent() || body.get().trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Request body is required")
                .build();
        }
        
        try {
            ProductoDTO producto = objectMapper.readValue(body.get(), ProductoDTO.class);
            producto.setId(productId);
            
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Product name is required")
                    .build();
            }
            
            if (producto.getCategoria() == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Product category is required")
                    .build();
            }
            
            sendEventToGrid(DatabaseConfig.EVENT_TYPE_ACTUALIZAR_PRODUCTO, producto, context);
            
            return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .body("Product update event sent successfully")
                .build();
                
        } catch (Exception e) {
            context.getLogger().severe("Error parsing product JSON: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid JSON format: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleCategoriaPost(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing POST request to create new categoria");
        
        Optional<String> body = request.getBody();
        if (!body.isPresent() || body.get().trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Request body is required")
                .build();
        }
        
        try {
            CategoriaDTO categoria = objectMapper.readValue(body.get(), CategoriaDTO.class);
            
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Categoria name is required")
                    .build();
            }
            
            sendEventToGrid(DatabaseConfig.EVENT_TYPE_CREAR_CATEGORIA, categoria, context);
            
            return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .body("Categoria creation event sent successfully")
                .build();
                
        } catch (Exception e) {
            context.getLogger().severe("Error parsing categoria JSON: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid JSON format: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleCategoriaPut(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing PUT request to update categoria");
        
        String idParam = request.getQueryParameters().get("id");
        if (idParam == null || idParam.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("PUT requires categoria ID in query parameter: ?id={id}")
                .build();
        }
        
        Long categoriaId;
        try {
            categoriaId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid categoria ID format")
                .build();
        }
        
        Optional<String> body = request.getBody();
        if (!body.isPresent() || body.get().trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Request body is required")
                .build();
        }
        
        try {
            CategoriaDTO categoria = objectMapper.readValue(body.get(), CategoriaDTO.class);
            categoria.setId(categoriaId);
            
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Categoria name is required")
                    .build();
            }
            
            sendEventToGrid(DatabaseConfig.EVENT_TYPE_ACTUALIZAR_CATEGORIA, categoria, context);
            
            return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .body("Categoria update event sent successfully")
                .build();
                
        } catch (Exception e) {
            context.getLogger().severe("Error parsing categoria JSON: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid JSON format: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleBodegaPost(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing POST request to create new bodega");
        
        Optional<String> body = request.getBody();
        if (!body.isPresent() || body.get().trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Request body is required")
                .build();
        }
        
        try {
            BodegaDTO bodega = objectMapper.readValue(body.get(), BodegaDTO.class);
            
            if (bodega.getNombre() == null || bodega.getNombre().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Bodega name is required")
                    .build();
            }
            
            if (bodega.getUbicacion() == null || bodega.getUbicacion().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Bodega ubicacion is required")
                    .build();
            }
            
            sendEventToGrid(DatabaseConfig.EVENT_TYPE_CREAR_BODEGA, bodega, context);
            
            return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .body("Bodega creation event sent successfully")
                .build();
                
        } catch (Exception e) {
            context.getLogger().severe("Error parsing bodega JSON: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid JSON format: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleBodegaPut(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing PUT request to update bodega");
        
        String idParam = request.getQueryParameters().get("id");
        if (idParam == null || idParam.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("PUT requires bodega ID in query parameter: ?id={id}")
                .build();
        }
        
        Long bodegaId;
        try {
            bodegaId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid bodega ID format")
                .build();
        }
        
        Optional<String> body = request.getBody();
        if (!body.isPresent() || body.get().trim().isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Request body is required")
                .build();
        }
        
        try {
            BodegaDTO bodega = objectMapper.readValue(body.get(), BodegaDTO.class);
            bodega.setId(bodegaId);
            
            if (bodega.getNombre() == null || bodega.getNombre().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Bodega name is required")
                    .build();
            }
            
            if (bodega.getUbicacion() == null || bodega.getUbicacion().trim().isEmpty()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Bodega ubicacion is required")
                    .build();
            }
            
            sendEventToGrid(DatabaseConfig.EVENT_TYPE_ACTUALIZAR_BODEGA, bodega, context);
            
            return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .body("Bodega update event sent successfully")
                .build();
                
        } catch (Exception e) {
            context.getLogger().severe("Error parsing bodega JSON: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid JSON format: " + e.getMessage())
                .build();
        }
    }
    
    private HttpResponseMessage handleBodegaDelete(HttpRequestMessage<Optional<String>> request, ExecutionContext context) throws Exception {
        context.getLogger().info("Processing DELETE request to delete bodega");
        
        String idParam = request.getQueryParameters().get("id");
        if (idParam == null || idParam.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("DELETE requires bodega ID in query parameter: ?id={id}")
                .build();
        }
        
        Long bodegaId;
        try {
            bodegaId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                .body("Invalid bodega ID format")
                .build();
        }
        
        try {
            // Create a BodegaDTO with just the ID for the delete event
            BodegaDTO bodegaToDelete = new BodegaDTO();
            bodegaToDelete.setId(bodegaId);
            
            sendEventToGrid(DatabaseConfig.EVENT_TYPE_ELIMINAR_BODEGA, bodegaToDelete, context);
            
            return request.createResponseBuilder(HttpStatus.ACCEPTED)
                .body("Bodega deletion event sent successfully")
                .build();
                
        } catch (Exception e) {
            context.getLogger().severe("Error sending bodega deletion event: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error processing deletion request: " + e.getMessage())
                .build();
        }
    }
    
    private List<ProductoDTO> getAllProductos() throws SQLException {
        List<ProductoDTO> productos = new ArrayList<>();
        
        String sql = "SELECT id, nombre, categoria FROM producto ORDER BY id";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                productos.add(mapResultSetToProducto(rs));
            }
        }
        
        return productos;
    }
    
    private ProductoDTO getProductoById(Integer productoId) throws SQLException {
        String sql = "SELECT id, nombre, categoria FROM producto WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToProducto(rs);
            }
        }
        
        return null;
    }
    
    private List<CategoriaDTO> getAllCategorias() throws SQLException {
        List<CategoriaDTO> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre FROM categoria";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    categorias.add(mapResultSetToCategoria(rs));
                }
        }
        
        return categorias;
    }
    
    private CategoriaDTO getCategoriaById(Long categoriaId) throws SQLException {
        String sql = "SELECT id, nombre FROM categoria WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, categoriaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCategoria(rs);
            }
        }
        
        return null;
    }
    
    private List<BodegaDTO> getAllBodegas() throws SQLException {
        List<BodegaDTO> bodegas = new ArrayList<>();
        
        String sql = "SELECT id, nombre, ubicacion FROM bodega";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bodegas.add(mapResultSetToBodega(rs));
            }
        }
        
        return bodegas;
    }
    
    private BodegaDTO getBodegaById(Long bodegaId) throws SQLException {
        String sql = "SELECT id, nombre, ubicacion FROM bodega WHERE id = ?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, bodegaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBodega(rs);
            }
        }
        
        return null;
    }
    
    private ProductoDTO mapResultSetToProducto(ResultSet rs) throws SQLException {
        ProductoDTO producto = new ProductoDTO();
        producto.setId(rs.getInt("id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setCategoria(rs.getLong("categoria"));
        
        return producto;
    }
    
    private CategoriaDTO mapResultSetToCategoria(ResultSet rs) throws SQLException {
        CategoriaDTO categoria = new CategoriaDTO();
        categoria.setId(rs.getLong("id"));
        categoria.setNombre(rs.getString("nombre"));
        
        return categoria;
    }
    
    private BodegaDTO mapResultSetToBodega(ResultSet rs) throws SQLException {
        BodegaDTO bodega = new BodegaDTO();
        bodega.setId(rs.getLong("id"));
        bodega.setNombre(rs.getString("nombre"));
        bodega.setUbicacion(rs.getString("ubicacion"));
        
        return bodega;
    }
    
    private void sendEventToGrid(String eventType, Object entity, ExecutionContext context) throws Exception {
        context.getLogger().info("Sending event to Event Grid - Type: " + eventType + ", Entity: " + entity.toString());
        
        EventGridPublisherClient<EventGridEvent> client = new EventGridPublisherClientBuilder()
            .endpoint(DatabaseConfig.EVENT_GRID_TOPIC_ENDPOINT)
            .credential(new AzureKeyCredential(DatabaseConfig.EVENT_GRID_TOPIC_KEY))
            .buildEventGridEventPublisherClient();

        String entityJson = objectMapper.writeValueAsString(entity);
        
        String subject;
        if (eventType.contains("Producto")) {
            subject = DatabaseConfig.SUBJECT_PRODUCTO;
        } else if (eventType.contains("Categoria")) {
            subject = DatabaseConfig.SUBJECT_CATEGORIA;
        } else if (eventType.contains("Bodega")) {
            subject = DatabaseConfig.SUBJECT_BODEGA;
        } else {
            subject = "/administracion/general";
        }
        
        EventGridEvent event = new EventGridEvent(
            subject,
            eventType,
            BinaryData.fromString(entityJson),
            "1.0");

        client.sendEvent(event);
        context.getLogger().info("Event sent successfully to Event Grid");
    }
}
