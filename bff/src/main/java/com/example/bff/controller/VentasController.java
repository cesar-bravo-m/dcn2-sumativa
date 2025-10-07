package com.example.bff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentasController {

        private final RestTemplate restTemplate = new RestTemplate();
        @Value("${azure.functions.ventas.url}")
        private String azureFunctionUrl;

        @PostMapping("/registrar")
        public String setVenta(@RequestBody String entity) {
                ResponseEntity<String> response = restTemplate.postForEntity(azureFunctionUrl, entity, String.class);
                System.out.println("Response from Azure Function: " + response);
                return response.getBody();
        }

}