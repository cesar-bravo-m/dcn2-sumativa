package com.example.bff.dto;

import java.util.List;

public class RespuestaBodegas {

    private List<BodegaDto> bodegas;

    public List<BodegaDto> getBodegas() {
        return this.bodegas;
    }

    public void setBodegas(List<BodegaDto> bodegas) {
        this.bodegas = bodegas;
    }

}
