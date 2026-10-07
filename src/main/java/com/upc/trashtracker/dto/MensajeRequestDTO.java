package com.upc.trashtracker.dto;

import lombok.Data;

@Data
public class MensajeRequestDTO {
    private Long usuarioId;
    private String contenido;
}
