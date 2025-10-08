package com.example.bff.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentasController {

        private static final Logger logger = LoggerFactory.getLogger(VentasController.class);
        private final RestTemplate restTemplate = new RestTemplate();
        
        @Value("${azure.functions.ventas.url}")
        private String azureFunctionUrl;

        @PostMapping("/registrar")
        public ResponseEntity<String> setVenta(@RequestBody String entity) {
                try {
                        logger.info("Registrando nueva venta");
                        ResponseEntity<String> response = restTemplate.postForEntity(azureFunctionUrl, entity, String.class);
                        logger.info("Venta registrada exitosamente. Respuesta de Azure Function: {}", response.getStatusCode());
                        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
                } catch (RestClientException e) {
                        logger.error("Error al registrar venta: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error interno del servidor al registrar la venta\"}");
                } catch (Exception e) {
                        logger.error("Error inesperado al registrar venta: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("{\"error\":\"Error inesperado al procesar la solicitud\"}");
                }
        }
}