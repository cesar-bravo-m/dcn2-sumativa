package com.function.generadores.recepcionProductos;

import java.util.HashMap;
import java.util.Map;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.function.shared.DatabaseConfig;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

public class RecepcionDataFetcher implements DataFetcher<Object> {
    
    
    private final ObjectMapper objectMapper;
    
    public RecepcionDataFetcher() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        String fieldName = environment.getField().getName();
        
        switch (fieldName) {
            case "recepcionProductos":
                return getRecepcionProductos();
            case "recepcionProducto":
                String id = environment.getArgument("id");
                return getRecepcionProducto(id);
            case "recibirProductos":
                Map<String, Object> input = environment.getArgument("input");
                return recibirProductos(input);
            default:
                return null;
        }
    }
    
    private Object getRecepcionProductos() {
        return new HashMap<String, Object>() {{
            put("message", "Recepcion productos list - implement as needed");
        }};
    }
    
    private Object getRecepcionProducto(String id) {
        return new HashMap<String, Object>() {{
            put("id", id);
            put("message", "Recepcion producto by ID - implement as needed");
        }};
    }
    
    private Object recibirProductos(Map<String, Object> input) throws Exception {
        Integer productoId = (Integer) input.get("productoId");
        Integer bodegaId = (Integer) input.get("bodegaId");
        Integer cantidad = (Integer) input.get("cantidad");
        
        if (productoId == null || bodegaId == null || cantidad == null || cantidad <= 0) {
            return new HashMap<String, Object>() {{
                put("success", false);
                put("message", "Invalid input parameters");
            }};
        }
        
        InventarioDTO inventario = new InventarioDTO();
        inventario.setProductoId(productoId);
        inventario.setBodegaId(bodegaId);
        inventario.setCantidad(cantidad);
        
        sendEventToGrid(DatabaseConfig.EVENT_TYPE_RECIBIR_PRODUCTOS, inventario);
        
        return new HashMap<String, Object>() {{
            put("success", true);
            put("message", "Productos reception event sent successfully");
            put("inventario", new HashMap<String, Object>() {{
                put("productoId", productoId);
                put("bodegaId", bodegaId);
                put("cantidad", cantidad);
            }});
        }};
    }
    
    private void sendEventToGrid(String eventType, InventarioDTO inventario) throws Exception {
        EventGridPublisherClient<EventGridEvent> client = new EventGridPublisherClientBuilder()
            .endpoint(DatabaseConfig.EVENT_GRID_TOPIC_ENDPOINT)
            .credential(new AzureKeyCredential(DatabaseConfig.EVENT_GRID_TOPIC_KEY))
            .buildEventGridEventPublisherClient();

        String inventarioJson = objectMapper.writeValueAsString(inventario);
        
        EventGridEvent event = new EventGridEvent(
            DatabaseConfig.SUBJECT_RECEPCION,
            eventType,
            BinaryData.fromString(inventarioJson),
            "1.0");

        client.sendEvent(event);
    }
}
