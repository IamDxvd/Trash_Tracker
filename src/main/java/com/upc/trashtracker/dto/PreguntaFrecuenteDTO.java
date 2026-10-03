package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PreguntaFrecuenteDTO {
    private String pregunta;
    private String respuesta;
    private String categoria;
}
