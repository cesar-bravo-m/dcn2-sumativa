package com.example.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductoDto {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("nombre")
    private String nombre;
    
    // Default constructor
    public ProductoDto() {}
    
    // Constructor with parameters
    public ProductoDto(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
    
    @Override
    public String toString() {
        return "ProductoDto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
