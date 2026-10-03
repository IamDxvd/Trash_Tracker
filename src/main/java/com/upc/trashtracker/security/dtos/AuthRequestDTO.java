package com.upc.trashtracker.security.dtos;

import lombok.Data;

@Data
public class AuthRequestDTO {
    private String correo;
    private String contrasena;
}
