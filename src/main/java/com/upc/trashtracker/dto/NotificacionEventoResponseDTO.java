package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class NotificacionEventoResponseDTO {
    private Long eventoId;
    private Long grupoId;
    private Integer notificacionesEnviadas;
    private String contenido;
}
