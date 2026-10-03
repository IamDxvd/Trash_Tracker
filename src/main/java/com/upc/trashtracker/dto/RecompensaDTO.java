package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class RecompensaDTO {
    private Long idRecompensa;
    private String nombre;
    private Integer costoPuntos;
    private Integer stock;

}
