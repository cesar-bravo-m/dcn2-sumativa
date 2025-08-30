package com.example.msinventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventario")
public class Inventario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", referencedColumnName = "id")
    private Producto producto;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bodega_id", referencedColumnName = "id")
    private Bodega bodega;
    
    @Column(name = "cantidad")
    private Integer cantidad;
    
    public Inventario() {}
    
    public Inventario(Producto producto, Bodega bodega, Integer cantidad) {
        this.producto = producto;
        this.bodega = bodega;
        this.cantidad = cantidad;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public Bodega getBodega() {
        return bodega;
    }
    
    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
