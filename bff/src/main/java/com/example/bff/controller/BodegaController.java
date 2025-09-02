package com.example.bff.controller;

import java.util.List;

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

import com.example.bff.dto.BodegaDto;
import com.example.bff.service.BodegaService;

@RestController
@RequestMapping("/api/bodegas")
@CrossOrigin(origins = "*")
public class BodegaController {
    
    private final BodegaService bodegaService;
    
    public BodegaController(BodegaService bodegaService) {
        this.bodegaService = bodegaService;
    }
    
    @GetMapping
    public ResponseEntity<List<BodegaDto>> getAllBodegas() {
        try {
            List<BodegaDto> bodegas = bodegaService.getAllBodegas();
            return ResponseEntity.ok(bodegas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<BodegaDto> getBodegaById(@PathVariable Integer id) {
        try {
            BodegaDto bodega = bodegaService.getBodegaById(id);
            if (bodega != null) {
                return ResponseEntity.ok(bodega);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    public ResponseEntity<BodegaDto> createBodega(@RequestBody BodegaDto bodegaDto) {
        try {
            BodegaDto createdBodega = bodegaService.createBodega(bodegaDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBodega);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<BodegaDto> updateBodega(@PathVariable Integer id, @RequestBody BodegaDto bodegaDto) {
        try {
            BodegaDto updatedBodega = bodegaService.updateBodega(id, bodegaDto);
            return ResponseEntity.ok(updatedBodega);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBodega(@PathVariable Integer id) {
        try {
            bodegaService.deleteBodega(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
