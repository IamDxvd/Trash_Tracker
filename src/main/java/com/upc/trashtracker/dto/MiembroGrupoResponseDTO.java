package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class MiembroGrupoResponseDTO {
    private Long idMiembroGrupo;
    private Long grupoId;
    private String grupoNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private String rol;
}
