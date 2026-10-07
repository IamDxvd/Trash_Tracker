package com.upc.trashtracker.dto;
import lombok.Data;

@Data
public class NivelUsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private Integer puntosTotales;
    private Integer nivelActual;
    private String RangoNombre;
    private Integer puntosSiguienteNivel;
}
