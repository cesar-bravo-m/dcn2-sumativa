package com.example.bff.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bff.dto.InventarioDto;
import com.example.bff.service.InventarioService;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {
    
    private final InventarioService inventarioService;
    
    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }
    
    @GetMapping
    public ResponseEntity<Object> getAllInventarios() {
        try {
            Object inventarios = inventarioService.getAllInventarios();
            return ResponseEntity.ok(inventarios);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getInventarioById(@PathVariable Integer id) {
        try {
            Object inventario = inventarioService.getInventarioById(id);
            if (inventario != null) {
                return ResponseEntity.ok(inventario);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<InventarioDto> createInventario(@RequestBody InventarioDto inventarioDto) {
        try {
            InventarioDto createdInventario = inventarioService.createInventario(inventarioDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInventario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<String> updateInventario(@PathVariable Integer id, @RequestBody InventarioDto inventarioDto) {
        try {
            System.out.println("### 1");
            boolean updated = inventarioService.updateInventario(id, inventarioDto);
            System.out.println("### 2:"+updated);
            return ResponseEntity.ok(updated ? "Inventario actualizado exitosamente" : "Inventario no encontrado");
        } catch (Exception e) {
            System.out.println("### 3:"+e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventario(@PathVariable Integer id) {
        try {
            System.out.println("### 1");
            inventarioService.deleteInventario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
