package com.function.bodega;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BodegaDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("ubicacion")
    private String ubicacion;
    
    public BodegaDTO() {
    }
    
    public BodegaDTO(Long id, String nombre, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }
    
    public BodegaDTO(String nombre, String ubicacion) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    @Override
    public String toString() {
        return "BodegaDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }
}