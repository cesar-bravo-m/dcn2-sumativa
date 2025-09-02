package com.example.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InventarioDto {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("productoId")
    private Integer productoId;
    
    @JsonProperty("bodegaId")
    private Integer bodegaId;
    
    @JsonProperty("cantidad")
    private Integer cantidad;
    
    // Default constructor
    public InventarioDto() {}
    
    // Constructor with parameters
    public InventarioDto(Integer id, Integer productoId, Integer bodegaId, Integer cantidad) {
        this.id = id;
        this.productoId = productoId;
        this.bodegaId = bodegaId;
        this.cantidad = cantidad;
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getProductoId() {
        return productoId;
    }
    
    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }
    
    public Integer getBodegaId() {
        return bodegaId;
    }
    
    public void setBodegaId(Integer bodegaId) {
        this.bodegaId = bodegaId;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    @Override
    public String toString() {
        return "InventarioDto{" +
                "id=" + id +
                ", productoId=" + productoId +
                ", bodegaId=" + bodegaId +
                ", cantidad=" + cantidad +
                '}';
    }
}
