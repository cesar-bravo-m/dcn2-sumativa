package com.example.msinventario.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msinventario.model.Bodega;
import com.example.msinventario.repository.BodegaRepository;

@RestController
@RequestMapping("/api/bodega")
public class BodegaController {
    
    @Autowired
    private BodegaRepository bodegaRepository;
    
    @GetMapping
    public ResponseEntity<List<Bodega>> getAllBodegas() {
        return ResponseEntity.ok(bodegaRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Bodega> getBodegaById(@PathVariable Integer id) {
        Optional<Bodega> bodega = bodegaRepository.findById(id);
        if (bodega.isPresent()) {
            return ResponseEntity.ok(bodega.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
