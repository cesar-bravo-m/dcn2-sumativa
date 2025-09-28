package com.function.consumidores.inventario;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.function.shared.DatabaseConfig;
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
    
    @FunctionName("Inventario")
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
            
            if (eventType.equals(DatabaseConfig.EVENT_TYPE_RECIBIR_PRODUCTOS)) {
                handleRecibirProductos(dataObject, context);
            } else if (eventType.equals(DatabaseConfig.EVENT_TYPE_PROCESAR_VENTA)) {
                handleProcesarVenta(dataObject, context);
            } else {
                context.getLogger().warning("Unknown event type: " + eventType);
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error processing Event Grid message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleRecibirProductos(JsonObject inventarioData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing product reception event");
            
            InventarioDTO inventario = objectMapper.readValue(inventarioData.toString(), InventarioDTO.class);
            context.getLogger().info("Inventory data to process: " + inventario.toString());
            
            if (inventario.getProductoId() == null) {
                context.getLogger().severe("Product ID is required for inventory update");
                return;
            }
            
            if (inventario.getBodegaId() == null) {
                context.getLogger().severe("Bodega ID is required for inventory update");
                return;
            }
            
            if (inventario.getCantidad() == null || inventario.getCantidad() <= 0) {
                context.getLogger().severe("Valid quantity is required for inventory update");
                return;
            }
            
            InventarioDTO existingInventario = databaseService.getInventarioByProductoAndBodega(
                inventario.getProductoId(), inventario.getBodegaId());
            
            if (existingInventario != null) {
                existingInventario.setCantidad(existingInventario.getCantidad() + inventario.getCantidad());
                boolean updated = databaseService.updateInventario(existingInventario);
                
                if (updated) {
                    context.getLogger().info("Inventory updated successfully: " + existingInventario.toString());
                } else {
                    context.getLogger().severe("Failed to update inventory in database");
                }
            } else {
                InventarioDTO createdInventario = databaseService.createInventario(inventario);
                
                if (createdInventario != null) {
                    context.getLogger().info("New inventory created successfully: " + createdInventario.toString());
                } else {
                    context.getLogger().severe("Failed to create new inventory in database");
                }
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error processing inventory update: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleProcesarVenta(JsonObject inventarioData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing sales event");
            
            InventarioDTO inventario = objectMapper.readValue(inventarioData.toString(), InventarioDTO.class);
            context.getLogger().info("Sales data to process: " + inventario.toString());
            
            if (inventario.getProductoId() == null) {
                context.getLogger().severe("Product ID is required for sales processing");
                return;
            }
            
            if (inventario.getBodegaId() == null) {
                context.getLogger().severe("Bodega ID is required for sales processing");
                return;
            }
            
            if (inventario.getCantidad() == null || inventario.getCantidad() <= 0) {
                context.getLogger().severe("Valid quantity is required for sales processing");
                return;
            }
            
            InventarioDTO existingInventario = databaseService.getInventarioByProductoAndBodega(
                inventario.getProductoId(), inventario.getBodegaId());
            
            if (existingInventario != null) {
                int newQuantity = existingInventario.getCantidad() - inventario.getCantidad();
                
                if (newQuantity < 0) {
                    context.getLogger().severe("Insufficient inventory. Available: " + existingInventario.getCantidad() + 
                        ", Requested: " + inventario.getCantidad());
                    return;
                }
                
                existingInventario.setCantidad(newQuantity);
                boolean updated = databaseService.updateInventario(existingInventario);
                
                if (updated) {
                    context.getLogger().info("Inventory reduced successfully: " + existingInventario.toString());
                } else {
                    context.getLogger().severe("Failed to update inventory in database");
                }
            } else {
                context.getLogger().severe("No inventory found for product " + inventario.getProductoId() + 
                    " in bodega " + inventario.getBodegaId());
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error processing sales: " + e.getMessage());
            e.printStackTrace();
        }
    }
}