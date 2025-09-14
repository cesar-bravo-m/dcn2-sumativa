package com.example.bff.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.bff.dto.CategoriaDto;
import com.example.bff.dto.RespuestaCategoriaPorId;
import com.example.bff.dto.RespuestaCategorias;



@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final WebClient webClient;
    private final String azureFunctionUrl;

    public CategoriaController(WebClient.Builder builder, @Value("${azure.functions.categorias.url}") String azureFunctionUrl) {
        this.webClient = builder.baseUrl(azureFunctionUrl).build();
        this.azureFunctionUrl = azureFunctionUrl;
    }

    @GetMapping
    public Map<String, RespuestaCategorias> getAllCategorias() {
        String query = "{ categoria { id nombre } }";

        Map<String, RespuestaCategorias> response = webClient.post()
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, RespuestaCategorias>>() {})
                .block();

        return response;
    }

    @GetMapping("/{id}")
    public Map<String, RespuestaCategoriaPorId> getCategoriaById(@PathVariable Integer id) {
        String query = "{ categoriaById(id: "+id+") { id nombre } }";
        System.out.println(query);

        Map<String, RespuestaCategoriaPorId> response = webClient.post()
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, RespuestaCategoriaPorId>>() {})
                .block();

        System.out.println("#########################################");
        System.out.println(response);

        return response;
    }

    @PostMapping
    public String createCategoria(@RequestBody CategoriaDto categoriaDto) {
        String mutation = String.format(
                "mutation { createCategoria(nombre: \"%s\") { id nombre } }",
                categoriaDto.getNombre()
        );

        webClient.post()
                .bodyValue(Map.of("query", mutation))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return "Categoria insertada, Ok.";
    }

    @DeleteMapping("/{id}")
    public String deleteCategoria(@PathVariable Long id) {
        String mutation = String.format(
                "mutation { deleteCategoria(id: \"%d\") }",
                id
        );

        System.out.println("#########################################");
        System.out.println(mutation);

        webClient.post()
                .bodyValue(Map.of("query", mutation))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return "Categoria eliminada, Ok.";
    }

    @PutMapping("/{id}")
    public String updateCategoria(@PathVariable Long id, @RequestBody CategoriaDto categoriaDto) {
        String mutation = String.format(
                "mutation {\r\n" + //
                                                "    updateCategoria(id: %d, nombre: \"%s\") {\r\n" + //
                                                "        id\r\n" + //
                                                "        nombre\r\n" + //
                                                "    }\r\n" + //
                                                "}\r\n" + //
                                                "",
                id, categoriaDto.getNombre()
        );

        Map<String, CategoriaDto> response = webClient.post()
                .bodyValue(Map.of("query", mutation))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, CategoriaDto>>() {})
                .block();

        return "Categoria actualizada, OK.";
    }


}
