package com.example.bff.service;

import com.example.bff.dto.ProductoDto;
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
    
    public ProductoService(RestTemplate restTemplate, 
                          @Value("${azure.functions.productos.url}") String azureFunctionUrl) {
        this.restTemplate = restTemplate;
        this.azureFunctionUrl = azureFunctionUrl;
    }
    
    public List<ProductoDto> getAllProductos() {
        ResponseEntity<List<ProductoDto>> response = restTemplate.exchange(
            azureFunctionUrl,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<ProductoDto>>() {}
        );
        return response.getBody();
    }
    
    public ProductoDto getProductoById(Long id) {
        String url = azureFunctionUrl + "?id=" + Long.toString(id);
        return restTemplate.getForObject(url, ProductoDto.class);
    }
    
    public ProductoDto createProducto(ProductoDto productoDto) {
        return restTemplate.postForObject(azureFunctionUrl, productoDto, ProductoDto.class);
    }
    
    public ProductoDto updateProducto(Long id, ProductoDto productoDto) {
        String url = azureFunctionUrl + "?id=" + Long.toString(id);
        restTemplate.put(url, productoDto);
        return getProductoById(id);
    }
    
    public void deleteProducto(Long id) {
        String url = azureFunctionUrl + "?id=" + Long.toString(id);
        restTemplate.delete(url);
    }
}
