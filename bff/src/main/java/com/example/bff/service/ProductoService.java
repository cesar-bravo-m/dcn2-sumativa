package com.example.bff.service;

import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import com.example.bff.dto.ProductoDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProductoService {
    
    private final RestTemplate restTemplate;
    private final String azureFunctionUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public ProductoService(RestTemplate restTemplate, 
                          @Value("${azure.functions.productos.url}") String azureFunctionUrl) {
        this.restTemplate = restTemplate;
        this.azureFunctionUrl = azureFunctionUrl;
    }
    
    // GET all
    public List<ProductoDto> getAllProductos() {
        ResponseEntity<List<ProductoDto>> response = restTemplate.exchange(
            azureFunctionUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ProductoDto>>() {}
        );
        return response.getBody();
    }
    
    // GET by ID
    public ProductoDto getProductoById(Long id) {
        String url = azureFunctionUrl + "?id=" + Long.toString(id);
        return restTemplate.getForObject(url, ProductoDto.class);
    }
    
    // POST (raw)
    public ProductoDto createProducto(ProductoDto productoDto) throws Exception {
        URL url = new URL(azureFunctionUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = objectMapper.writeValueAsString(productoDto);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
            try (InputStream is = conn.getInputStream()) {
                return objectMapper.readValue(is, ProductoDto.class);
            }
        } else {
            throw new RuntimeException("POST failed with HTTP code: " + status);
        }
    }
    
    // PUT (raw)
    public ProductoDto updateProducto(Long id, ProductoDto productoDto) throws Exception {
        String urlString = azureFunctionUrl + "?id=" + Long.toString(id);
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = objectMapper.writeValueAsString(productoDto);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_NO_CONTENT) {
            // some APIs return no body on PUT, so we can refetch
            return getProductoById(id);
        } else {
            throw new RuntimeException("PUT failed with HTTP code: " + status);
        }
    }
    
    // DELETE stays with RestTemplate
    public void deleteProducto(Long id) {
        String url = azureFunctionUrl + "?id=" + Long.toString(id);
        restTemplate.delete(url);
    }
}
