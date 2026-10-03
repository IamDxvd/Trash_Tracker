package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.MensajeDTO;
import com.upc.trashtracker.entidades.Grupo;
import com.upc.trashtracker.entidades.Mensaje;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.GrupoRepository;
import com.upc.trashtracker.repositorio.MensajeRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Mensaje guardar(MensajeDTO mensajeDTO) {
        Mensaje mensaje = new Mensaje();
        mensaje.setContenido(mensajeDTO.getContenido());
        mensaje.setFechaHora(mensajeDTO.getFechaHora() != null ? mensajeDTO.getFechaHora() : LocalDateTime.now());

        if (mensajeDTO.getGrupoId() != null) {
            Grupo grupo = grupoRepository.findById(mensajeDTO.getGrupoId())
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + mensajeDTO.getGrupoId()));
            mensaje.setGrupo(grupo);
        }
        if (mensajeDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(mensajeDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + mensajeDTO.getUsuarioId()));
            mensaje.setUsuario(usuario);
        }
        return mensajeRepository.save(mensaje);
    }

    public List<Mensaje> listarTodos() {
        return mensajeRepository.findAll();
    }

    public Optional<Mensaje> buscarPorId(Long id) {
        return mensajeRepository.findById(id);
    }

    @Transactional
    public Mensaje actualizar(Long id, MensajeDTO mensajeDTO) {
        return mensajeRepository.findById(id).map(existente -> {
            existente.setContenido(mensajeDTO.getContenido());
            if (mensajeDTO.getFechaHora() != null) {
                existente.setFechaHora(mensajeDTO.getFechaHora());
            }
            if (mensajeDTO.getGrupoId() != null) {
                Grupo grupo = grupoRepository.findById(mensajeDTO.getGrupoId())
                        .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + mensajeDTO.getGrupoId()));
                existente.setGrupo(grupo);
            }
            if (mensajeDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(mensajeDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + mensajeDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return mensajeRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Mensaje no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        mensajeRepository.deleteById(id);
    }
}
