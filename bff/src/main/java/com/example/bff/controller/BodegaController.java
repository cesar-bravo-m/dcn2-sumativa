package com.example.bff.controller;

import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.bff.dto.BodegaDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/bodegas")
@CrossOrigin(origins = "*")
public class BodegaController {

    private final WebClient webClient;

    public BodegaController(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:7071/api/graphqlBodegas?").build();
    }

    @GetMapping
    public List<BodegaDto> getAllBodegas() {
        String query = "{ bodegas { id nombre ubicacion } }";

        Map<String, Object> response = webClient.post()
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> bodegasData =
                (List<Map<String, Object>>) ((Map) response.get("data")).get("bodegas");

        return bodegasData.stream()
                .map(data -> new BodegaDto(
                        (Integer) data.get("id"),
                        (String) data.get("nombre"),
                        (String) data.get("ubicacion")
                ))
                .toList();
    }

    @GetMapping("/{id}")
    public BodegaDto getBodegaById(Integer id) {
        String query = String.format("{ bodegaById(id: %d) { id nombre ubicacion } }", id);

        Map<String, Object> response = webClient.post()
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        @SuppressWarnings("unchecked")
        Map<String, Object> bodegaData = (Map<String, Object>) ((Map<String, Object>) response.get("data")).get("bodegaById");

        if (bodegaData == null) return null;

        return new BodegaDto(
                (Integer) bodegaData.get("id"),
                (String) bodegaData.get("nombre"),
                (String) bodegaData.get("ubicacion")
        );
    }

    @PostMapping
    public BodegaDto createBodega(BodegaDto bodegaDto) {
        String mutation = String.format(
                "mutation { createBodega(nombre: \"%s\", ubicacion: \"%s\") { id nombre ubicacion } }",
                bodegaDto.getNombre(), bodegaDto.getUbicacion()
        );

        Map<String, Object> response = webClient.post()
                .bodyValue(Map.of("query", mutation))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        @SuppressWarnings("unchecked")
        Map<String, Object> bodegaData = (Map<String, Object>) ((Map<String, Object>) response.get("data")).get("bodegaById");

        return new BodegaDto(
                (Integer) bodegaData.get("id"),
                (String) bodegaData.get("nombre"),
                (String) bodegaData.get("ubicacion")
        );
    }
}
