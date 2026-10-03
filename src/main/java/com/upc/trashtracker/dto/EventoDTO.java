package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDTO {
    private LocalDate fecha;
    private String ubicacion;
    private Long grupoId;
    private Long organizadorId;
}
