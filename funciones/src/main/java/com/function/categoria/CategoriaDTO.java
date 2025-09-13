package com.function.categoria;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CategoriaDTO {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("nombre")
    private String nombre;
    
    public CategoriaDTO() {
    }
    
    public CategoriaDTO(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public CategoriaDTO(String nombre) {
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
        return "CategoriaDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}