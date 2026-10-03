package com.upc.trashtracker.security.dtos;

import lombok.Data;

@Data
public class RegistroDTO {
    private String nombre;
    private String correo;
    private String contrasena;
}
