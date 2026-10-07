package com.upc.trashtracker.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UbicacionDTO {
    private Long idUsuario;
    private Double latitud;
    private Double longitud;
    private String mensaje;
}
