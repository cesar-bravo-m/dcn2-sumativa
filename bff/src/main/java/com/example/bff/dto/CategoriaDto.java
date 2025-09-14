package com.example.bff.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoriaDto {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("nombre")
    private String nombre;
    
    public CategoriaDto() {
    }
    
    public CategoriaDto(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public CategoriaDto(String nombre) {
        this.nombre = nombre;
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
    
    @Override
    public String toString() {
        return "CategoriaDto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
    
}