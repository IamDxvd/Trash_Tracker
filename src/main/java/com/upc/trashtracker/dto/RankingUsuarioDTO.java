package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RankingUsuarioDTO {
    private Integer posicion;
    private Long idUsuario;
    private String nombre;
    private Integer puntosTotales;
    private Integer nivel;
}
