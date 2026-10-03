package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {
    private String contenido;
    private LocalDateTime fechaHora;
    private Long grupoId;
    private Long usuarioId;
}
