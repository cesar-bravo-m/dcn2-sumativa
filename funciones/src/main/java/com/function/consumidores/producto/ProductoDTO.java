package com.function.consumidores.producto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductoDTO {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("categoria")
    private Long categoria;


    public ProductoDTO() {
    }
    
    public ProductoDTO(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    public ProductoDTO(String nombre) {
        this.nombre = nombre;
    }
    
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
        return "ProductoDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }

    public Long getCategoria() {
        return categoria;
    }

    public void setCategoria(Long categoria) {
        this.categoria = categoria;
    }

}
