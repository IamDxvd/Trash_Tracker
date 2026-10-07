package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AsistenciaResponseDTO {
    private Long idAsistenciaEvento;
    private Long eventoId;
    private Long usuarioId;
    private String usuarioNombre;
    private Boolean confirmado;
    private Long totalConfirmados;
}
