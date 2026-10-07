package com.upc.trashtracker.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PuntosResponseDTO {
    private Long idHistorialPuntos;
    private Integer puntosOtorgados;
    private String motivo;
    private LocalDateTime fecha;
    private Integer puntosTotalesUsuario;
}
