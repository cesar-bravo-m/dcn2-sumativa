package com.example.bff.service;

import com.example.bff.dto.BodegaDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BodegaService {
    
    private final RestTemplate restTemplate;
    private final String azureFunctionUrl;
    
    public BodegaService(RestTemplate restTemplate, 
                        @Value("${azure.functions.bodegas.url}") String azureFunctionUrl) {
        this.restTemplate = restTemplate;
        this.azureFunctionUrl = azureFunctionUrl;
    }
    
    public List<BodegaDto> getAllBodegas() {
        ResponseEntity<List<BodegaDto>> response = restTemplate.exchange(
            azureFunctionUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<BodegaDto>>() {}
        );
        return response.getBody();
    }
    
    public BodegaDto getBodegaById(Integer id) {
        String url = azureFunctionUrl + "?id=" + id;
        return restTemplate.getForObject(url, BodegaDto.class);
    }
    
    public BodegaDto createBodega(BodegaDto bodegaDto) {
        return restTemplate.postForObject(azureFunctionUrl, bodegaDto, BodegaDto.class);
    }
    
    public BodegaDto updateBodega(Integer id, BodegaDto bodegaDto) {
        String url = azureFunctionUrl + "?id=" + id;
        restTemplate.put(url, bodegaDto);
        return getBodegaById(id);
    }
    
    public void deleteBodega(Integer id) {
        String url = azureFunctionUrl + "?id=" + id;
        restTemplate.delete(url);
    }
}
