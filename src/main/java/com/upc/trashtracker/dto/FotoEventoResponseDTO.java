package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FotoEventoResponseDTO {
    private Long idFotoEvento;
    private Long eventoId;
    private Long usuarioId;
    private String urlFoto;
}
