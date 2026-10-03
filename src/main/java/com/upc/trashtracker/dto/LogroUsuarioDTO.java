package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogroUsuarioDTO {
    private LocalDate fechaObtenido;
    private Long usuarioId;
    private Long logroId;
}
