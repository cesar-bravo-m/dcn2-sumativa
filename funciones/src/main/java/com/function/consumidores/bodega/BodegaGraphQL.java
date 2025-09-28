package com.function.consumidores.bodega;

import java.io.InputStreamReader;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class BodegaGraphQL {

    private final DatabaseService databaseService = new DatabaseService();
    private GraphQL graphQL;

    public BodegaGraphQL() {
        init();
    }

     private void init() {
        // 1. Cargar el archivo schema.graphqls desde /resources
        InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/schema.graphqls")
        );
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(reader);

        // 2. Definir resolvers (queries y mutations)
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Mutation", builder -> builder
                        .dataFetcher("createBodega", env -> {
                            BodegaDTO bodega = new BodegaDTO();
                            bodega.setNombre(env.getArgument("nombre"));
                            bodega.setUbicacion(env.getArgument("ubicacion"));
                            return databaseService.createBodega(bodega);
                        })
                        .dataFetcher("updateBodega", env -> {
                            BodegaDTO bodega = new BodegaDTO();
                            bodega.setId(Long.parseLong(env.getArgument("id")));
                            bodega.setNombre(env.getArgument("nombre"));
                            bodega.setUbicacion(env.getArgument("ubicacion"));
                            databaseService.updateBodega(bodega);
                            return bodega;
                        })
                        .dataFetcher("deleteBodega", env -> {
                            Long id = Long.parseLong(env.getArgument("id"));
                            return databaseService.deleteBodega(id);
                        })
                )
                .build();

        // 3. Crear GraphQLSchema uniendo typeRegistry y wiring
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, wiring);

        // 4. Inicializar el motor GraphQL
        this.graphQL = GraphQL.newGraphQL(schema).build();
    }

    public GraphQL getGraphQL() {
        return graphQL;
    }

}
