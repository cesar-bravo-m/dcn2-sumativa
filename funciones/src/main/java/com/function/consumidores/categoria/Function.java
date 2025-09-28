package com.function.consumidores.categoria;

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
    
    @FunctionName("Categorias")
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
            
            if (eventType.equals("Administracion.CrearCategoria")) {
                handleCreateCategoria(dataObject, context);
            } else if (eventType.equals("Administracion.ActualizarCategoria")) {
                handleUpdateCategoria(dataObject, context);
            } else {
                context.getLogger().warning("Unknown event type: " + eventType);
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error processing Event Grid message: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleCreateCategoria(JsonObject categoriaData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing categoria creation event");
            
            CategoriaDTO categoria = objectMapper.readValue(categoriaData.toString(), CategoriaDTO.class);
            context.getLogger().info("Categoria to create: " + categoria.toString());
            
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                context.getLogger().severe("Categoria name is required for creation");
                return;
            }
            
            CategoriaDTO createdCategoria = databaseService.createCategoria(categoria);
            
            if (createdCategoria != null) {
                context.getLogger().info("Categoria created successfully: " + createdCategoria.toString());
            } else {
                context.getLogger().severe("Failed to create categoria in database");
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error creating categoria: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleUpdateCategoria(JsonObject categoriaData, ExecutionContext context) {
        try {
            context.getLogger().info("Processing categoria update event");
            
            CategoriaDTO categoria = objectMapper.readValue(categoriaData.toString(), CategoriaDTO.class);
            context.getLogger().info("Categoria to update: " + categoria.toString());
            
            if (categoria.getId() == null) {
                context.getLogger().severe("Categoria ID is required for update");
                return;
            }
            
            if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
                context.getLogger().severe("Categoria name is required for update");
                return;
            }
            
            CategoriaDTO existingCategoria = databaseService.getCategoriaById(categoria.getId());
            if (existingCategoria == null) {
                context.getLogger().severe("Categoria not found with ID: " + categoria.getId());
                return;
            }
            
            boolean updated = databaseService.updateCategoria(categoria);
            
            if (updated) {
                context.getLogger().info("Categoria updated successfully: " + categoria.toString());
            } else {
                context.getLogger().severe("Failed to update categoria in database");
            }
            
        } catch (Exception e) {
            context.getLogger().severe("Error updating categoria: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
