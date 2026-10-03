package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialPuntosDTO {
    private Integer cantidad;
    private String motivo;
    private LocalDateTime fecha;
    private Long usuarioId;
}
