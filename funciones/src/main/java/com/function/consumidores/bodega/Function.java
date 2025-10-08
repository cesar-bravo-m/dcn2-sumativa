package com.function.consumidores.bodega;

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
    
    @FunctionName("Bodegas")
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
            
            if (eventType.equals("Administracion.CrearBodega")) {
                handleCreateBodega(dataObject, context);
            } else if (eventType.equals("Administracion.ActualizarBodega")) {
                handleUpdateBodega(dataObject, context);
            } else if (eventType.equals("Administracion.EliminarBodega")) {
                handleDeleteBodega(dataObject, context);
            } else {
                context.getLogger().warning("Unknown event type: " + eventType);
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error processing Event Grid message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleCreateBodega(JsonObject bodegaData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing bodega creation event");
            
            BodegaDTO bodega = objectMapper.readValue(bodegaData.toString(), BodegaDTO.class);
            context.getLogger().info("Bodega to create: " + bodega.toString());
            
            if (bodega.getNombre() == null || bodega.getNombre().trim().isEmpty()) {
                context.getLogger().severe("Bodega name is required for creation");
                return;
            }
            
            if (bodega.getUbicacion() == null || bodega.getUbicacion().trim().isEmpty()) {
                context.getLogger().severe("Bodega ubicacion is required for creation");
                return;
            }
            
            BodegaDTO createdBodega = databaseService.createBodega(bodega);
            
            if (createdBodega != null) {
                context.getLogger().info("Bodega created successfully: " + createdBodega.toString());
            } else {
                context.getLogger().severe("Failed to create bodega in database");
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error creating bodega: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleUpdateBodega(JsonObject bodegaData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing bodega update event");
            
            BodegaDTO bodega = objectMapper.readValue(bodegaData.toString(), BodegaDTO.class);
            context.getLogger().info("Bodega to update: " + bodega.toString());
            
            if (bodega.getId() == null) {
                context.getLogger().severe("Bodega ID is required for update");
                return;
            }
            
            if (bodega.getNombre() == null || bodega.getNombre().trim().isEmpty()) {
                context.getLogger().severe("Bodega name is required for update");
                return;
            }
            
            if (bodega.getUbicacion() == null || bodega.getUbicacion().trim().isEmpty()) {
                context.getLogger().severe("Bodega ubicacion is required for update");
                return;
            }
            
            BodegaDTO existingBodega = databaseService.getBodegaById(bodega.getId());
            if (existingBodega == null) {
                context.getLogger().severe("Bodega not found with ID: " + bodega.getId());
                return;
            }
            
            boolean updated = databaseService.updateBodega(bodega);
            
            if (updated) {
                context.getLogger().info("Bodega updated successfully: " + bodega.toString());
            } else {
                context.getLogger().severe("Failed to update bodega in database");
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error updating bodega: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleDeleteBodega(JsonObject bodegaData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing bodega deletion event");
            
            BodegaDTO bodega = objectMapper.readValue(bodegaData.toString(), BodegaDTO.class);
            context.getLogger().info("Bodega to delete: " + bodega.toString());
            
            if (bodega.getId() == null) {
                context.getLogger().severe("Bodega ID is required for deletion");
                return;
            }
            
            // Check if bodega exists before attempting deletion
            BodegaDTO existingBodega = databaseService.getBodegaById(bodega.getId());
            if (existingBodega == null) {
                context.getLogger().severe("Bodega not found with ID: " + bodega.getId());
                return;
            }
            
            boolean deleted = databaseService.deleteBodega(bodega.getId());
            
            if (deleted) {
                context.getLogger().info("Bodega deleted successfully with ID: " + bodega.getId() + ". Associated inventario records were also deleted.");
            } else {
                context.getLogger().severe("Failed to delete bodega from database");
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error deleting bodega: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
