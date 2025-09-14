package com.example.bff.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.bff.dto.InventarioDto;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
public class InventarioService {
    
    private final RestTemplate restTemplate;
    private final String microserviceUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public InventarioService(RestTemplate restTemplate, 
                           @Value("${azure.functions.inventario.url}") String microserviceUrl) {
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
    
    public InventarioDto getInventarioById(Integer id) {
        String url = microserviceUrl + "/" + id;
        return restTemplate.getForObject(url, InventarioDto.class);
    }
    
    public InventarioDto createInventario(InventarioDto inventarioDto) throws Exception {
        URL url = new URL(microserviceUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String jsonInputString = objectMapper.writeValueAsString(inventarioDto);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {
            try (InputStream is = conn.getInputStream()) {
                return objectMapper.readValue(is, InventarioDto.class);
            }
        } else {
            throw new RuntimeException("POST failed with HTTP code: " + status);
        }
    }
    
    public boolean updateInventario(Integer id, InventarioDto inventarioDto) throws Exception {
        String urlString = microserviceUrl + "?id=" + Integer.toString(id);
        System.out.println("### 1:"+urlString);
        URL url = new URL(urlString);
        System.out.println("### 2:"+url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        System.out.println("### 3:"+conn);
        conn.setRequestMethod("PUT");
        System.out.println("### 4:"+conn);
        conn.setRequestProperty("Content-Type", "application/json");
        System.out.println("### 5:"+conn);
        conn.setDoOutput(true);
        System.out.println("### 6:"+conn);

        String jsonInputString = objectMapper.writeValueAsString(inventarioDto);
        System.out.println("### 7:"+jsonInputString);

        try (OutputStream os = conn.getOutputStream()) {
            System.out.println("### 8:"+os);
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            System.out.println("### 9:"+input);
            os.write(input, 0, input.length);
            System.out.println("### 10:"+os);
        }

        int status = conn.getResponseCode();
        System.out.println("### 11:"+status);
        if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_NO_CONTENT) {
            System.out.println("### 12:"+status);
            // some APIs return no body on PUT, so we can refetch
            return true;
        } else {
            System.out.println("### 13:"+status);
            throw new RuntimeException("PUT failed with HTTP code: " + status);
        }
    }
    
    public void deleteInventario(Integer id) {
        String url = microserviceUrl + "?id=" + id;
        restTemplate.delete(url);
    }
}
