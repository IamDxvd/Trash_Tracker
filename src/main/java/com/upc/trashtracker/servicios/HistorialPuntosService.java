package com.upc.trashtracker.servicios;

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
}
