package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MiembroGrupoDTO {
    private String rolEnGrupo;
    private Long grupoId;
    private Long usuarioId;
}
