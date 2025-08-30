package com.example.msinventario.dto;

public class InventarioRequestDTO {
    
    private Integer productoId;
    private Integer bodegaId;
    private Integer cantidad;
    
    public InventarioRequestDTO() {}
    
    public InventarioRequestDTO(Integer productoId, Integer bodegaId, Integer cantidad) {
        this.productoId = productoId;
        this.bodegaId = bodegaId;
        this.cantidad = cantidad;
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
}
