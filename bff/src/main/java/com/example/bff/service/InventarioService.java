package com.example.bff.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.bff.dto.InventarioDto;

@Service
public class InventarioService {
    
    private final RestTemplate restTemplate;
    private final String microserviceUrl;
    
    public InventarioService(RestTemplate restTemplate, 
                           @Value("${microservice.inventario.url}") String microserviceUrl) {
        this.restTemplate = restTemplate;
        this.microserviceUrl = microserviceUrl;
    }
    
    public Object getAllInventarios() {
        ResponseEntity<Object> response = restTemplate.exchange(
            microserviceUrl,
            HttpMethod.GET,
            null,
            Object.class
        );
        return response.getBody();
    }
    
    public Object getInventarioById(Integer id) {
        String url = microserviceUrl + "/" + id;
        return restTemplate.getForObject(url, Object.class);
    }
    
    public InventarioDto createInventario(InventarioDto inventarioDto) {
        return restTemplate.postForObject(microserviceUrl, inventarioDto, InventarioDto.class);
    }
    
    public InventarioDto updateInventario(Integer id, InventarioDto inventarioDto) {
        String url = microserviceUrl + "/" + id;
        restTemplate.put(url, inventarioDto);
        // Return the updated object from the microservice
        return restTemplate.getForObject(url, InventarioDto.class);
    }
    
    public void deleteInventario(Integer id) {
        String url = microserviceUrl + "/" + id;
        restTemplate.delete(url);
    }
}
