package com.upc.trashtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaVinculadaDTO {
    private String proveedor;
    private String idExterno;
    private Long usuarioId;
}
