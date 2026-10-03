package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteRequest {
    private Long usuarioId;        // temporal: hasta implementar Spring Security/JWT
    private Long tipoResiduoId;
    private String descripcion;    // HU022
    private BigDecimal latitud;    // HU026
    private BigDecimal longitud;   // HU026
}
