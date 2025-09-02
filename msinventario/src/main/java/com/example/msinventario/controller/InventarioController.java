package com.example.msinventario.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.msinventario.dto.InventarioRequestDTO;
import com.example.msinventario.model.Inventario;
import com.example.msinventario.service.InventarioService;

@RestController
@RequestMapping("/api/inventario")
@CrossOrigin(origins = "*")
public class InventarioController {
    
    @Autowired
    private InventarioService inventarioService;
    
    @PostMapping
    public ResponseEntity<Inventario> createInventario(@RequestBody InventarioRequestDTO inventarioDTO) {
        System.out.println("##########");
        System.out.println(inventarioDTO);
        System.out.println(inventarioDTO.getProductoId());
        System.out.println(inventarioDTO.getBodegaId());
        System.out.println(inventarioDTO.getCantidad());
        System.out.println("##########");
        try {
            System.out.println("### 1");
            Inventario savedInventario = inventarioService.createFromDTO(inventarioDTO);
            System.out.println("### 2");
            if (savedInventario != null) {
                System.out.println("### 3");
                return new ResponseEntity<>(savedInventario, HttpStatus.CREATED);
            } else {
                System.out.println("### 4");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            System.out.println("######");
            System.out.println(e);
            System.out.println("######");
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Inventario>> getAllInventario() {
        try {
            List<Inventario> inventarios = inventarioService.findAll();
            if (inventarios.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(inventarios, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> getInventarioById(@PathVariable Integer id) {
        try {
            Optional<Inventario> inventario = inventarioService.findById(id);
            if (inventario.isPresent()) {
                return new ResponseEntity<>(inventario.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> updateInventario(@PathVariable Integer id, @RequestBody InventarioRequestDTO inventarioDTO) {
        try {
            Inventario updatedInventario = inventarioService.updateFromDTO(id, inventarioDTO);
            if (updatedInventario != null) {
                return new ResponseEntity<>(updatedInventario, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteInventario(@PathVariable Integer id) {
        try {
            boolean deleted = inventarioService.deleteById(id);
            if (deleted) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
