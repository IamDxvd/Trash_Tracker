package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.EventoDTO;
import com.upc.trashtracker.entidades.Evento;
import com.upc.trashtracker.entidades.Grupo;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.EventoRepository;
import com.upc.trashtracker.repositorio.GrupoRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Evento guardar(EventoDTO eventoDTO) {
        Evento evento = new Evento();
        evento.setFecha(eventoDTO.getFecha());
        evento.setUbicacion(eventoDTO.getUbicacion());

        if (eventoDTO.getGrupoId() != null) {
            Grupo grupo = grupoRepository.findById(eventoDTO.getGrupoId())
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + eventoDTO.getGrupoId()));
            evento.setGrupo(grupo);
        }
        if (eventoDTO.getOrganizadorId() != null) {
            Usuario organizador = usuarioRepository.findById(eventoDTO.getOrganizadorId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + eventoDTO.getOrganizadorId()));
            evento.setOrganizador(organizador);
        }
        return eventoRepository.save(evento);
    }

    public List<Evento> listarTodos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> buscarPorId(Long id) {
        return eventoRepository.findById(id);
    }

    @Transactional
    public Evento actualizar(Long id, EventoDTO eventoDTO) {
        return eventoRepository.findById(id).map(existente -> {
            existente.setFecha(eventoDTO.getFecha());
            existente.setUbicacion(eventoDTO.getUbicacion());
            if (eventoDTO.getGrupoId() != null) {
                Grupo grupo = grupoRepository.findById(eventoDTO.getGrupoId())
                        .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + eventoDTO.getGrupoId()));
                existente.setGrupo(grupo);
            }
            if (eventoDTO.getOrganizadorId() != null) {
                Usuario organizador = usuarioRepository.findById(eventoDTO.getOrganizadorId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + eventoDTO.getOrganizadorId()));
                existente.setOrganizador(organizador);
            }
            return eventoRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        eventoRepository.deleteById(id);
    }
}
