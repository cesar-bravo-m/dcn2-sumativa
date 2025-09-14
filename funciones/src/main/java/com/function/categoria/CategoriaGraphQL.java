package com.function.categoria;

import java.io.InputStreamReader;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class CategoriaGraphQL {

    private final DatabaseService databaseService = new DatabaseService();
    private GraphQL graphQL;

    public CategoriaGraphQL() {
        init();
    }

     private void init() {
        // 1. Cargar el archivo schema.graphqls desde /resources
        InputStreamReader reader = new InputStreamReader(
                getClass().getResourceAsStream("/schema.graphqls.categoria")
        );
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(reader);

        // 2. Definir resolvers (queries y mutations)
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder
                        .dataFetcher("categorias", env -> databaseService.getAllCategorias())
                        .dataFetcher("categoriaById", env -> {
                            Long id = Long.parseLong(env.getArgument("id"));
                            return databaseService.getCategoriaById(id);
                        })
                )
                .type("Mutation", builder -> builder
                        .dataFetcher("createCategoria", env -> {
                            CategoriaDTO categoria = new CategoriaDTO();
                            categoria.setNombre(env.getArgument("nombre"));
                            return databaseService.createCategoria(categoria);
                        })
                        .dataFetcher("updateCategoria", env -> {
                            CategoriaDTO categoria = new CategoriaDTO();
                            categoria.setId(Long.parseLong(env.getArgument("id")));
                            categoria.setNombre(env.getArgument("nombre"));
                            databaseService.updateCategoria(categoria);
                            return categoria;
                        })
                        .dataFetcher("deleteCategoria", env -> {
                            Long id = Long.parseLong(env.getArgument("id"));
                            return databaseService.deleteCategoria(id);
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
