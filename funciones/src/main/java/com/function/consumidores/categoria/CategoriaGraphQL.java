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
                getClass().getResourceAsStream("/categoria.graphqls")
        );
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(reader);

        // 2. Definir resolvers (queries y mutations)
        RuntimeWiring wiring = RuntimeWiring.newRuntimeWiring()
                .type("Query", builder -> builder
                        .dataFetcher("categoria", env -> databaseService.getAllCategorias())
                        .dataFetcher("categoriaById", env -> {
                            Long id = Long.parseLong(env.getArgument("id"));
                            return databaseService.getCategoriaById(id);
                        })
                )
                .type("Mutation", builder -> builder
                        .dataFetcher("createCategoria", env -> {
                            System.out.println("=== 1");
                            CategoriaDTO categoria = new CategoriaDTO();
                            System.out.println("=== 2");
                            categoria.setNombre(env.getArgument("nombre"));
                            System.out.println("=== 3:"+env.getArgument("nombre"));
                            return databaseService.createCategoria(categoria);
                        })
                        .dataFetcher("updateCategoria", env -> {
                            System.out.println("--- 1");
                            CategoriaDTO categoria = new CategoriaDTO();
                            System.out.println("--- 2");
                            categoria.setId(Long.parseLong(env.getArgument("id")));
                            System.out.println("--- 3:"+env.getArgument("id"));
                            categoria.setNombre(env.getArgument("nombre"));
                            System.out.println("--- 4:"+env.getArgument("nombres"));
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
