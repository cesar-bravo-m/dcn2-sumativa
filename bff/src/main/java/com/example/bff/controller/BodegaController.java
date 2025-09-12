package com.example.bff.controller;

import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Data;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.bff.dto.BodegaDto;
import com.example.bff.dto.RespuestaBodegaPorId;
import com.example.bff.dto.RespuestaBodegas;

import jakarta.websocket.server.PathParam;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@RequestMapping("/api/bodegas")
@CrossOrigin(origins = "*")
public class BodegaController {

    private final WebClient webClient;

    public BodegaController(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:7071/api/graphqlBodegas?").build();
    }

    @GetMapping
    public Map<String, RespuestaBodegas> getAllBodegas() {
        String query = "{ bodegas { id nombre ubicacion } }";

        Map<String, RespuestaBodegas> response = webClient.post()
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, RespuestaBodegas>>() {})
                .block();

        return response;
    }

    @GetMapping("/{id}")
    public Map<String, RespuestaBodegaPorId> getBodegaById(@PathVariable Integer id) {
        String query = "{ bodegaById(id: "+id+") { id nombre ubicacion } }";
        System.out.println(query);

        Map<String, RespuestaBodegaPorId> response = webClient.post()
                .bodyValue(Map.of("query", query))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, RespuestaBodegaPorId>>() {})
                .block();

        System.out.println("#########################################");
        System.out.println(response);

        return response;
    }

    @PostMapping
    public String createBodega(@RequestBody BodegaDto bodegaDto) {
        String mutation = String.format(
                "mutation { createBodega(nombre: \"%s\", ubicacion: \"%s\") { id nombre ubicacion } }",
                bodegaDto.getNombre(), bodegaDto.getUbicacion()
        );

        webClient.post()
                .bodyValue(Map.of("query", mutation))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();

        return "Bodega insertada, Ok.";
    }

    @PutMapping("/{id}")
    public String updateBodega(@PathVariable Long id, @RequestBody BodegaDto bodegaDto) {

        String mutation = String.format(
                "mutation {\r\n" + //
                                                "    updateBodega(id: %d, nombre: \"%s\", ubicacion: \"%s\") {\r\n" + //
                                                "        id\r\n" + //
                                                "        nombre\r\n" + //
                                                "        ubicacion\r\n" + //
                                                "    }\r\n" + //
                                                "}\r\n" + //
                                                "",
                id, bodegaDto.getNombre(), bodegaDto.getUbicacion()
        );

        System.out.println("########################################################");
        System.out.println(mutation);

        Map<String, BodegaDto> response = webClient.post()
                .bodyValue(Map.of("query", mutation))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, BodegaDto>>() {})
                .block();

        System.out.println("#########################################");
        System.out.println(response);

        return "Bodega actualizada, OK.";
    }


}
