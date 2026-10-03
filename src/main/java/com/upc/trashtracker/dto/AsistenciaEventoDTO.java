package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsistenciaEventoDTO {
    private Boolean confirmado;
    private Long eventoId;
    private Long usuarioId;
}
