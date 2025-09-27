package com.function.consumidores.inventario;

public class InventarioDTO {
    
    private Integer id;
    private Integer productoId;
    private Integer bodegaId;
    private Integer cantidad;
    
    public InventarioDTO() {}
    
    public InventarioDTO(Integer productoId, Integer bodegaId, Integer cantidad) {
        this.productoId = productoId;
        this.bodegaId = bodegaId;
        this.cantidad = cantidad;
    }
    
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
        return "InventarioDTO{" +
                "id=" + id +
                ", productoId=" + productoId +
                ", bodegaId=" + bodegaId +
                ", cantidad=" + cantidad +
                '}';
    }
}
