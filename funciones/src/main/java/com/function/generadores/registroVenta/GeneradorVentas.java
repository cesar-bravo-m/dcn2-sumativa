package com.function.generadores.registroVenta;

import java.util.Optional;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.BinaryData;
import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions .HttpResponseMessage;
import com.microsoft.azure.functions .HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

public class GeneradorVentas {
    @FunctionName("generadorVentas")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        String eventGridTopicEndpoint = "";
        String eventGridTopicKey = "";

        try {
            EventGridPublisherClient<EventGridEvent> client = new EventGridPublisherClientBuilder()
                .endpoint(eventGridTopicEndpoint)
                .credential(new AzureKeyCredential(eventGridTopicKey))
                .buildEventGridEventPublisherClient();

            EventGridEvent event = new EventGridEvent("/EventGridEvents/example/source","Example. EventType", BinaryData.fromObject("Hello World"), "0.1");

            client.sendEvent(event);

            return request.createResponseBuilder(HttpStatus.OK)
                .body("Evento creado correctamente")
                .build();
        } catch (Exception e) {
            context.getLogger().severe("Error al publicar evento: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al publicar evento: " + e.getMessage() )
                .build();
        }
    }
}
