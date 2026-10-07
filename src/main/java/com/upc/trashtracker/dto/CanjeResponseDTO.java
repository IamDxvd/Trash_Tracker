package com.upc.trashtracker.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CanjeResponseDTO {
    private Long idCanje;
    private String nombreRecompensa;
    private Integer puntosConsumidos;
    private Integer puntosRestantesUsuario;
    private String estado;
    private LocalDateTime fecha;
}
