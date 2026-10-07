package com.upc.trashtracker.dto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LogroUsuarioResponseDTO {
    private Long idLogroUsuario;
    private Long idLogro;
    private String nombreLogro;
    private String criterio;
    private LocalDate fechaObtenido;
    private Boolean desbloqueado;
}
