package com.upc.trashtracker.dto;
import lombok.Data;

@Data
public class DesbloquearLogroRequestDTO {
    private Long idUsuario;
    private Long idLogro;
}
