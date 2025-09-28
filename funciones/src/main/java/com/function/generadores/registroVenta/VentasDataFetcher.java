package com.function.generadores.registroVenta;

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

public class VentasDataFetcher implements DataFetcher<Object> {
    
    
    private final ObjectMapper objectMapper;
    
    public VentasDataFetcher() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        String fieldName = environment.getField().getName();
        
        switch (fieldName) {
            case "ventas":
                return getVentas();
            case "venta":
                String id = environment.getArgument("id");
                return getVenta(id);
            case "procesarVenta":
                Map<String, Object> input = environment.getArgument("input");
                return procesarVenta(input);
            default:
                return null;
        }
    }
    
    private Object getVentas() {
        return new HashMap<String, Object>() {{
            put("message", "Ventas list - implement as needed");
        }};
    }
    
    private Object getVenta(String id) {
        return new HashMap<String, Object>() {{
            put("id", id);
            put("message", "Venta by ID - implement as needed");
        }};
    }
    
    private Object procesarVenta(Map<String, Object> input) throws Exception {
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
        
        sendEventToGrid(DatabaseConfig.EVENT_TYPE_PROCESAR_VENTA, inventario);
        
        return new HashMap<String, Object>() {{
            put("success", true);
            put("message", "Sales event sent successfully");
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
            DatabaseConfig.SUBJECT_VENTAS,
            eventType,
            BinaryData.fromString(inventarioJson),
            "1.0");

        client.sendEvent(event);
    }
}
