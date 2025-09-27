package com.function.consumidores.inventario;

public class Inventario {
    
    private Integer id;
    private Integer productoId;
    private Integer bodegaId;
    private Integer cantidad;
    
    public Inventario() {}
    
    public Inventario(Integer productoId, Integer bodegaId, Integer cantidad) {
        this.productoId = productoId;
        this.bodegaId = bodegaId;
        this.cantidad = cantidad;
    }
    
    public Inventario(Integer id, Integer productoId, Integer bodegaId, Integer cantidad) {
        this.id = id;
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
        return "Inventario{" +
                "id=" + id +
                ", productoId=" + productoId +
                ", bodegaId=" + bodegaId +
                ", cantidad=" + cantidad +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Inventario that = (Inventario) o;
        
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (productoId != null ? !productoId.equals(that.productoId) : that.productoId != null) return false;
        if (bodegaId != null ? !bodegaId.equals(that.bodegaId) : that.bodegaId != null) return false;
        return cantidad != null ? cantidad.equals(that.cantidad) : that.cantidad == null;
    }
    
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productoId != null ? productoId.hashCode() : 0);
        result = 31 * result + (bodegaId != null ? bodegaId.hashCode() : 0);
        result = 31 * result + (cantidad != null ? cantidad.hashCode() : 0);
        return result;
    }
}