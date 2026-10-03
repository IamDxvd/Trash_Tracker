package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanjeDTO {
    private LocalDateTime fecha;
    private String estado;
    private Long usuarioId;
    private Long recompensaId;
}
