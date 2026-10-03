package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FotoEventoDTO {
    private String urlFoto;
    private Long eventoId;
    private Long usuarioId;
}
