package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class EventoResponseDTO {
    private Long idEvento;
    private Long grupoId;
    private String grupoNombre;
    private Long organizadorId;
    private String organizadorNombre;
    private LocalDate fecha;
    private String ubicacion;
    private Long totalConfirmados;
}
