package com.example.msinventario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.msinventario.dto.InventarioRequestDTO;
import com.example.msinventario.model.Bodega;
import com.example.msinventario.model.Inventario;
import com.example.msinventario.model.Producto;
import com.example.msinventario.repository.BodegaRepository;
import com.example.msinventario.repository.InventarioRepository;
import com.example.msinventario.repository.ProductoRepository;

@Service
public class InventarioService {
    
    @Autowired
    private InventarioRepository inventarioRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private BodegaRepository bodegaRepository;
    
    public Inventario save(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }
    
    public Inventario createFromDTO(InventarioRequestDTO dto) {
        System.out.println("Service: Looking for producto with ID: " + dto.getProductoId());
        Optional<Producto> producto = productoRepository.findById(dto.getProductoId());
        System.out.println("Service: Producto found: " + producto.isPresent());
        
        System.out.println("Service: Looking for bodega with ID: " + dto.getBodegaId());
        Optional<Bodega> bodega = bodegaRepository.findById(dto.getBodegaId());
        System.out.println("Service: Bodega found: " + bodega.isPresent());
        
        if (producto.isPresent() && bodega.isPresent()) {
            System.out.println("Service: Both entities found, creating inventario");
            Inventario inventario = new Inventario();
            inventario.setProducto(producto.get());
            inventario.setBodega(bodega.get());
            inventario.setCantidad(dto.getCantidad());
            return inventarioRepository.save(inventario);
        } else {
            System.out.println("Service: One or both entities not found, returning null");
        }
        return null;
    }
    
    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }
    
    public Optional<Inventario> findById(Integer id) {
        return inventarioRepository.findById(id);
    }
    
    public Inventario update(Integer id, Inventario inventarioDetails) {
        Optional<Inventario> optionalInventario = inventarioRepository.findById(id);
        if (optionalInventario.isPresent()) {
            Inventario inventario = optionalInventario.get();
            inventario.setProducto(inventarioDetails.getProducto());
            inventario.setBodega(inventarioDetails.getBodega());
            inventario.setCantidad(inventarioDetails.getCantidad());
            return inventarioRepository.save(inventario);
        }
        return null;
    }
    
    public Inventario updateFromDTO(Integer id, InventarioRequestDTO dto) {
        Optional<Inventario> optionalInventario = inventarioRepository.findById(id);
        if (optionalInventario.isPresent()) {
            Inventario inventario = optionalInventario.get();
            
            if (dto.getProductoId() != null) {
                Optional<Producto> producto = productoRepository.findById(dto.getProductoId());
                if (producto.isPresent()) {
                    inventario.setProducto(producto.get());
                } else {
                    return null;
                }
            }
            
            if (dto.getBodegaId() != null) {
                Optional<Bodega> bodega = bodegaRepository.findById(dto.getBodegaId());
                if (bodega.isPresent()) {
                    inventario.setBodega(bodega.get());
                } else {
                    return null;
                }
            }
            
            if (dto.getCantidad() != null) {
                inventario.setCantidad(dto.getCantidad());
            }
            
            return inventarioRepository.save(inventario);
        }
        return null;
    }
    
    public boolean deleteById(Integer id) {
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
