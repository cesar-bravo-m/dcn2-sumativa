package com.function;

import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;

public class ConsumidorVentas {
    @FunctionName("ConsumidorVentas")
    public void run(
        @EventGridTrigger(name = "eventGridTrigger") String content,
        final ExecutionContext context
    ) {
        Logger logger = context.getLogger();
        logger.info("Función con Event Grid trigger ejecutada");
        Gson gson = new Gson();
        JsonObject eventGridEvent = gson.fromJson(content, JsonObject.class);
        logger.info("Event recibido: " + eventGridEvent.toString());
        String eventType = eventGridEvent.get("eventType").getAsString();
        String data = eventGridEvent.get("data").toString();
        logger.info("Tipo de evento: " + eventType);
        logger.info("Datos: " + data);
    }
}
