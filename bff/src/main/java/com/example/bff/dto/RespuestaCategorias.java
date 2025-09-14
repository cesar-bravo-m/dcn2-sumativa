package com.example.bff.dto;

import java.util.List;

public class RespuestaCategorias {

    private List<CategoriaDto> categoria;

    public List<CategoriaDto> getCategoria() {
        return this.categoria;
    }

    public void setCategoria(List<CategoriaDto> categorias) {
        this.categoria = categorias;
    }

}
