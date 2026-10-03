package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoConocidoDTO {
    private String tipoDispositivo;
    private LocalDate fechaPrimerAcceso;
    private Long usuarioId;
}
