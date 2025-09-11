package com.function.bodega;

import java.util.Map;

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

import graphql.ExecutionInput;
import graphql.GraphQL;

public class Function {
    
    private final DatabaseService databaseService;
    private final ObjectMapper objectMapper;
    
    public Function() {
        this.databaseService = new DatabaseService();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); 
    }
    
    @FunctionName("graphqlBodegas")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Map<String, Object>> request,
            final ExecutionContext context) {

        BodegaGraphQL bodegaGraphQL = new BodegaGraphQL();
        GraphQL graphQL = bodegaGraphQL.getGraphQL();

        String query = request.getBody().get("query").toString();

        ExecutionInput executionInput = ExecutionInput.newExecutionInput()
                .query(query)
                .build();

        Map<String, Object> result = graphQL.execute(executionInput).toSpecification();

        return request.createResponseBuilder(HttpStatus.OK)
                .body(result)
                .build();
    }

}
