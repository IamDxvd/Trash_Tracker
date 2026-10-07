package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.PuntosRequestDTO;
import com.upc.trashtracker.dto.PuntosResponseDTO;
import com.upc.trashtracker.dto.HistorialPuntosDTO;
import com.upc.trashtracker.entidades.HistorialPuntos;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.HistorialPuntosRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HistorialPuntosService {

    @Autowired
    private HistorialPuntosRepository historialPuntosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public HistorialPuntos guardar(HistorialPuntosDTO historialPuntosDTO) {
        HistorialPuntos historialPuntos = new HistorialPuntos();
        historialPuntos.setCantidad(historialPuntosDTO.getCantidad());
        historialPuntos.setMotivo(historialPuntosDTO.getMotivo());
        historialPuntos.setFecha(historialPuntosDTO.getFecha() != null ? historialPuntosDTO.getFecha() : LocalDateTime.now());

        if (historialPuntosDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(historialPuntosDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + historialPuntosDTO.getUsuarioId()));
            historialPuntos.setUsuario(usuario);
        }
        return historialPuntosRepository.save(historialPuntos);
    }

    public List<HistorialPuntos> listarTodos() {
        return historialPuntosRepository.findAll();
    }

    public Optional<HistorialPuntos> buscarPorId(Long id) {
        return historialPuntosRepository.findById(id);
    }

    @Transactional
    public HistorialPuntos actualizar(Long id, HistorialPuntosDTO historialPuntosDTO) {
        return historialPuntosRepository.findById(id).map(existente -> {
            existente.setCantidad(historialPuntosDTO.getCantidad());
            existente.setMotivo(historialPuntosDTO.getMotivo());
            if (historialPuntosDTO.getFecha() != null) {
                existente.setFecha(historialPuntosDTO.getFecha());
            }
            if (historialPuntosDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(historialPuntosDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + historialPuntosDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return historialPuntosRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Historial de puntos no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        historialPuntosRepository.deleteById(id);
    }

    // Funciones adicionales integradas del backend unificado.
    @Transactional
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    public PuntosResponseDTO insertarPuntos(PuntosRequestDTO request) {
        if (request.getPuntos() == null || request.getPuntos() <= 0 || request.getMotivo() == null || request.getMotivo().isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Puntos positivos y motivo obligatorios");
        }
        Usuario usuario = usuarioRepository.buscarParaActualizar(request.getIdUsuario())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + request.getIdUsuario()));

        // 1. Registrar en el historial
        HistorialPuntos historial = new HistorialPuntos();
        historial.setCantidad(request.getPuntos());
        historial.setMotivo(request.getMotivo());
        historial.setFecha(LocalDateTime.now());
        historial.setUsuario(usuario);
        historial = historialPuntosRepository.save(historial);

        // 2. Acumular puntos en la entidad Usuario
        int saldoActual = usuario.getPuntosTotales() != null ? usuario.getPuntosTotales() : 0;
        int nuevoSaldo = Math.addExact(saldoActual, request.getPuntos());
        usuario.setPuntosTotales(nuevoSaldo);

        // (Lógica opcional HU046: Subida automática de nivel)
        usuario.setNivel(nuevoSaldo < 100 ? 1 : nuevoSaldo < 300 ? 2 : nuevoSaldo < 600 ? 3 : nuevoSaldo < 1000 ? 4 : 5);

        usuarioRepository.save(usuario);

        // 3. Retornar DTO con la respuesta limpia
        PuntosResponseDTO response = new PuntosResponseDTO();
        response.setIdHistorialPuntos(historial.getIdHistorialPuntos());
        response.setPuntosOtorgados(historial.getCantidad());
        response.setMotivo(historial.getMotivo());
        response.setFecha(historial.getFecha());
        response.setPuntosTotalesUsuario(nuevoSaldo);

        return response;
    }
}
