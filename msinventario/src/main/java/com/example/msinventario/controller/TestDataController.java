package com.example.msinventario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msinventario.model.Bodega;
import com.example.msinventario.model.Producto;
import com.example.msinventario.repository.BodegaRepository;
import com.example.msinventario.repository.ProductoRepository;

@RestController
@RequestMapping("/api/test")
public class TestDataController {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private BodegaRepository bodegaRepository;
    
    @PostMapping("/add-test-data")
    public ResponseEntity<String> addTestData() {
        try {
            // Add test products
            Producto producto1 = new Producto("Laptop");
            Producto producto2 = new Producto("Mouse");
            productoRepository.save(producto1);
            productoRepository.save(producto2);
            
            // Add test warehouses
            Bodega bodega1 = new Bodega("Bodega Central", "Santiago");
            Bodega bodega2 = new Bodega("Bodega Norte", "Antofagasta");
            bodegaRepository.save(bodega1);
            bodegaRepository.save(bodega2);
            
            return ResponseEntity.ok("Test data added successfully. Products: " + 
                productoRepository.count() + ", Warehouses: " + bodegaRepository.count());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error adding test data: " + e.getMessage());
        }
    }
}
