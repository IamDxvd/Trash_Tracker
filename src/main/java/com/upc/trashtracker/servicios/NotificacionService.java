package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.NotificacionDTO;
import com.upc.trashtracker.entidades.Notificacion;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.NotificacionRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Notificacion guardar(NotificacionDTO notificacionDTO) {
        Notificacion notificacion = new Notificacion();
        notificacion.setTipo(notificacionDTO.getTipo());
        notificacion.setContenido(notificacionDTO.getContenido());
        notificacion.setLeido(notificacionDTO.getLeido() != null ? notificacionDTO.getLeido() : false);
        notificacion.setFecha(notificacionDTO.getFecha() != null ? notificacionDTO.getFecha() : LocalDateTime.now());

        if (notificacionDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(notificacionDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + notificacionDTO.getUsuarioId()));
            notificacion.setUsuario(usuario);
        }
        return notificacionRepository.save(notificacion);

    }

    public List<Notificacion> listarTodos() {
        return notificacionRepository.findAll();
    }

    public Optional<Notificacion> buscarPorId(Long id) {
        return notificacionRepository.findById(id);
    }

    @Transactional
    public Notificacion actualizar(Long id, NotificacionDTO notificacionDTO) {
        return notificacionRepository.findById(id).map(existente -> {
            existente.setTipo(notificacionDTO.getTipo());
            existente.setContenido(notificacionDTO.getContenido());
            if (notificacionDTO.getLeido() != null) {
                existente.setLeido(notificacionDTO.getLeido());
            }
            if (notificacionDTO.getFecha() != null) {
                existente.setFecha(notificacionDTO.getFecha());
            }
            if (notificacionDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(notificacionDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + notificacionDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return notificacionRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Notificación no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        notificacionRepository.deleteById(id);
    }
}
