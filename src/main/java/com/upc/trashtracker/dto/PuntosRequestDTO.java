package com.upc.trashtracker.dto;

import lombok.Data;

@Data
public class PuntosRequestDTO {
    private Long idUsuario;
    private Integer puntos;
    private String motivo;
}
