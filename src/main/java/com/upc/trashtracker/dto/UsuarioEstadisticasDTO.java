package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEstadisticasDTO {
    private Long idUsuario;
    private String nombre;
    private Integer puntosTotales;
    private Integer nivel;
    private Integer totalReportesRealizados;
    private Integer totalEventosAsistidos;
}
