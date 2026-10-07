package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.UbicacionDTO;
import com.upc.trashtracker.entidades.UbicacionUsuario;
import com.upc.trashtracker.repositorio.UbicacionUsuarioRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class UbicacionUsuarioService {
    private final UbicacionUsuarioRepository ubicaciones;
    private final UsuarioRepository usuarios;
    public UbicacionUsuarioService(UbicacionUsuarioRepository ubicaciones, UsuarioRepository usuarios) {
        this.ubicaciones = ubicaciones; this.usuarios = usuarios;
    }
    @PreAuthorize("@accesoIntegracion.esPropietario(#idUsuario)")
    public UbicacionDTO obtenerUbicacionMapa(Long idUsuario) {
        UbicacionUsuario u = ubicaciones.findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ubicacion aun no registrada"));
        return new UbicacionDTO(idUsuario, u.getLatitud(), u.getLongitud(), "Ubicacion guardada");
    }
    @Transactional
    @PreAuthorize("@accesoIntegracion.esPropietario(#dto.idUsuario)")
    public UbicacionDTO actualizarUbicacionMapa(UbicacionDTO dto) {
        if (dto.getLatitud() == null || dto.getLongitud() == null || !Double.isFinite(dto.getLatitud())
                || !Double.isFinite(dto.getLongitud()) || Math.abs(dto.getLatitud()) > 90 || Math.abs(dto.getLongitud()) > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coordenadas invalidas");
        }
        usuarios.buscarParaActualizar(dto.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        UbicacionUsuario u = new UbicacionUsuario();
        u.setIdUsuario(dto.getIdUsuario()); u.setLatitud(dto.getLatitud()); u.setLongitud(dto.getLongitud());
        ubicaciones.save(u);
        return new UbicacionDTO(u.getIdUsuario(), u.getLatitud(), u.getLongitud(), "Ubicacion actualizada");
    }
}
