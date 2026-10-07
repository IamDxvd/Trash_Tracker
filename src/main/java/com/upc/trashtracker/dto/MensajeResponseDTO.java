package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MensajeResponseDTO {
    private Long idMensaje;
    private Long grupoId;
    private Long usuarioId;
    private String usuarioNombre;
    private String contenido;
    private LocalDateTime fechaHora;
}
