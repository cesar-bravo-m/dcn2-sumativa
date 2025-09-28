package com.function.consumidores.producto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

public class Function {
    
    private final ObjectMapper objectMapper;
    private final DatabaseService databaseService;
    
    public Function() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.databaseService = new DatabaseService();
    }
    
    @FunctionName("Productos")
    public void run(
        @EventGridTrigger(name = "eventGridTrigger") String content,
        final ExecutionContext context
    ) {

        context.getLogger().info("Event Grid message received: " + content);
        
        try {
            if (!databaseService.testConnection()) {
                context.getLogger().severe("Error connecting to database");
                return;
            }

            Gson gson = new Gson();
            JsonObject eventGridEvent = gson.fromJson(content, JsonObject.class);
            context.getLogger().info("Parsed Event Grid event: " + eventGridEvent.toString());

            String eventType = eventGridEvent.get("eventType").getAsString();
            context.getLogger().info("Event Type: " + eventType);
            
            JsonObject dataObject = eventGridEvent.get("data").getAsJsonObject();
            context.getLogger().info("Event data: " + dataObject.toString());
            
            if (eventType.equals("Administracion.CrearProducto")) {
                handleCreateProduct(dataObject, context);
            } else if (eventType.equals("Administracion.ActualizarProducto")) {
                handleUpdateProduct(dataObject, context);
            } else {
                context.getLogger().warning("Unknown event type: " + eventType);
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error processing Event Grid message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleCreateProduct(JsonObject productData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing product creation event");
            
            ProductoDTO producto = objectMapper.readValue(productData.toString(), ProductoDTO.class);
            context.getLogger().info("Product to create: " + producto.toString());
            
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                context.getLogger().severe("Product name is required for creation");
                return;
            }
            
            if (producto.getCategoria() == null) {
                context.getLogger().severe("Product category is required for creation");
                return;
            }
            
            ProductoDTO createdProducto = databaseService.createProducto(producto);
            
            if (createdProducto != null) {
                context.getLogger().info("Product created successfully: " + createdProducto.toString());
            } else {
                context.getLogger().severe("Failed to create product in database");
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error creating product: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleUpdateProduct(JsonObject productData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing product update event");
            
            ProductoDTO producto = objectMapper.readValue(productData.toString(), ProductoDTO.class);
            context.getLogger().info("Product to update: " + producto.toString());
            
            if (producto.getId() == null) {
                context.getLogger().severe("Product ID is required for update");
                return;
            }
            
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                context.getLogger().severe("Product name is required for update");
                return;
            }
            
            if (producto.getCategoria() == null) {
                context.getLogger().severe("Product category is required for update");
                return;
            }
            
            ProductoDTO existingProduct = databaseService.getProductoById(producto.getId());
            if (existingProduct == null) {
                context.getLogger().severe("Product not found with ID: " + producto.getId());
                return;
            }
            
            boolean updated = databaseService.updateProducto(producto);
            
            if (updated) {
                context.getLogger().info("Product updated successfully: " + producto.toString());
            } else {
                context.getLogger().severe("Failed to update product in database");
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error updating product: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
