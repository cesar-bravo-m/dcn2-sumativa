package com.example.bff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdministracionController {

        private final RestTemplate restTemplate = new RestTemplate();
        @Value("${azure.functions.admin.url}")
        private String azureFunctionUrl;

        @GetMapping("/bodega")
        public String getBodegas() {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/bodega", String.class);
                return response.getBody();

        }

        @GetMapping("/bodegas/{id}")
        public String getBodegaById(@RequestBody Long id) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/bodega", String.class);
                return response.getBody();

        }

        @PostMapping("/bodegas")
        public String setBodega(@RequestBody String entity) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/bodega", String.class);
                return response.getBody();

        }

        @PutMapping("/bodega/{id}")
        public String updateBodega(@PathVariable Long id, @RequestBody String entity) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/bodega", String.class);
                return response.getBody();

        }

        @GetMapping("/categoria")
        public String getCtegorias() {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/categoria", String.class);
                return response.getBody();

        }

        @GetMapping("/categoria/{id}")
        public String getCategoriaById(@RequestBody Long id) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/categoria", String.class);
                return response.getBody();

        }

        @PostMapping("/categoria")
        public String setCategoria(@RequestBody String entity) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/categoria", String.class);
                return response.getBody();

        }

        @PutMapping("/categoria/{id}")
        public String updateCategoria(@PathVariable Long id, @RequestBody String entity) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/categoria", String.class);
                return response.getBody();

        }

        @GetMapping("/producto")
        public String getProductos() {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/producto", String.class);
                return response.getBody();

        }

        @GetMapping("/producto/{id}")
        public String getProdutoById(@RequestBody Long id) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/producto", String.class);
                return response.getBody();

        }

        @PostMapping("/producto")
        public String setProducto(@RequestBody String entity) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/producto", String.class);
                return response.getBody();

        }
        
        @PutMapping("/producto/{id}")
        public String updateProducto(@PathVariable Long id, @RequestBody String entity) {
                ResponseEntity<String> response = restTemplate.getForEntity(azureFunctionUrl+"/producto", String.class);
                return response.getBody();

        }

}