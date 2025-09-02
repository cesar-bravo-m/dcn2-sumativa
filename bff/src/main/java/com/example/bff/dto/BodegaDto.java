package com.example.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BodegaDto {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("nombre")
    private String nombre;
    
    @JsonProperty("ubicacion")
    private String ubicacion;
    
    // Default constructor
    public BodegaDto() {}
    
    // Constructor with parameters
    public BodegaDto(Integer id, String nombre, String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.ubicacion = ubicacion;
    }
    
    // Getters and setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
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
        return "BodegaDto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }
}
