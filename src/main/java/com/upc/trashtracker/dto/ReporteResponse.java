package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResponse {
    private Long idReporte;
    private String descripcion;
    private String fotoUrl;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String estado;
    private LocalDateTime fechaHora;
    private Long usuarioId;
    private String usuarioNombre;
    private Long tipoResiduoId;
    private String tipoResiduoNombre;
}
