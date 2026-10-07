package com.upc.trashtracker.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EventoRequestDTO {
    private Long grupoId;
    private Long organizadorId;
    private LocalDate fecha;
    private String ubicacion;
}
