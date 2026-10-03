package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.AsistenciaEventoDTO;
import com.upc.trashtracker.entidades.AsistenciaEvento;
import com.upc.trashtracker.entidades.Evento;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.AsistenciaEventoRepository;
import com.upc.trashtracker.repositorio.EventoRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AsistenciaEventoService {

    @Autowired
    private AsistenciaEventoRepository asistenciaEventoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public AsistenciaEvento guardar(AsistenciaEventoDTO asistenciaEventoDTO) {
        AsistenciaEvento asistenciaEvento = new AsistenciaEvento();
        asistenciaEvento.setConfirmado(asistenciaEventoDTO.getConfirmado() != null ? asistenciaEventoDTO.getConfirmado() : false);

        if (asistenciaEventoDTO.getEventoId() != null) {
            Evento evento = eventoRepository.findById(asistenciaEventoDTO.getEventoId())
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + asistenciaEventoDTO.getEventoId()));
            asistenciaEvento.setEvento(evento);
        }
        if (asistenciaEventoDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(asistenciaEventoDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + asistenciaEventoDTO.getUsuarioId()));
            asistenciaEvento.setUsuario(usuario);
        }
        return asistenciaEventoRepository.save(asistenciaEvento);
    }

    public List<AsistenciaEvento> listarTodos() {
        return asistenciaEventoRepository.findAll();
    }

    public Optional<AsistenciaEvento> buscarPorId(Long id) {
        return asistenciaEventoRepository.findById(id);
    }

    @Transactional
    public AsistenciaEvento actualizar(Long id, AsistenciaEventoDTO asistenciaEventoDTO) {
        return asistenciaEventoRepository.findById(id).map(existente -> {
            if (asistenciaEventoDTO.getConfirmado() != null) {
                existente.setConfirmado(asistenciaEventoDTO.getConfirmado());
            }
            if (asistenciaEventoDTO.getEventoId() != null) {
                Evento evento = eventoRepository.findById(asistenciaEventoDTO.getEventoId())
                        .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + asistenciaEventoDTO.getEventoId()));
                existente.setEvento(evento);
            }
            if (asistenciaEventoDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(asistenciaEventoDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + asistenciaEventoDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return asistenciaEventoRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Asistencia a evento no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        asistenciaEventoRepository.deleteById(id);
    }
}
