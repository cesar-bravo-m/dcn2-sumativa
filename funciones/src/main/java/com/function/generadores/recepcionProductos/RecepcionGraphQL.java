package com.function.generadores.recepcionProductos;

import java.io.InputStream;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class RecepcionGraphQL {
    
    private GraphQL graphQL;
    
    public RecepcionGraphQL() {
        try {
            InputStream schemaStream = getClass().getClassLoader().getResourceAsStream("recepcion.graphqls");
            if (schemaStream == null) {
                throw new RuntimeException("Schema file not found");
            }
            
            String schema = new String(schemaStream.readAllBytes());
            schemaStream.close();
            
            TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schema);
            RuntimeWiring runtimeWiring = buildWiring();
            SchemaGenerator schemaGenerator = new SchemaGenerator();
            GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
            
            this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing GraphQL", e);
        }
    }
    
    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("recepcionProductos", new RecepcionDataFetcher())
                        .dataFetcher("recepcionProducto", new RecepcionDataFetcher()))
                .type("Mutation", typeWiring -> typeWiring
                        .dataFetcher("recibirProductos", new RecepcionDataFetcher()))
                .build();
    }
    
    public GraphQL getGraphQL() {
        return graphQL;
    }
}
