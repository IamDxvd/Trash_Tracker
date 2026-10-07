package com.upc.trashtracker.security.services;

import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

/** Identidad obtenida del JWT validado por la cadena de seguridad existente. */
@Component("accesoIntegracion")
public class AccesoIntegracion {
    private final UsuarioRepository usuarios;
    public AccesoIntegracion(UsuarioRepository usuarios) { this.usuarios = usuarios; }

    public Long usuarioActualId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Se requiere autenticacion");
        }
        return usuarios.findByCorreo(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no disponible"))
                .getIdUsuario();
    }

    public boolean esPropietario(Long id) { return id != null && id.equals(usuarioActualId()); }

    public void exigirPropietario(Long id) {
        if (!esPropietario(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La operacion debe pertenecer al usuario autenticado");
        }
    }
}
