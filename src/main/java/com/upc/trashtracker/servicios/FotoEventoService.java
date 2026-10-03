package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.FotoEventoDTO;
import com.upc.trashtracker.entidades.Evento;
import com.upc.trashtracker.entidades.FotoEvento;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.EventoRepository;
import com.upc.trashtracker.repositorio.FotoEventoRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FotoEventoService {

    @Autowired
    private FotoEventoRepository fotoEventoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public FotoEvento guardar(FotoEventoDTO fotoEventoDTO) {
        FotoEvento fotoEvento = new FotoEvento();
        fotoEvento.setUrlFoto(fotoEventoDTO.getUrlFoto());

        if (fotoEventoDTO.getEventoId() != null) {
            Evento evento = eventoRepository.findById(fotoEventoDTO.getEventoId())
                    .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + fotoEventoDTO.getEventoId()));
            fotoEvento.setEvento(evento);
        }
        if (fotoEventoDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(fotoEventoDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + fotoEventoDTO.getUsuarioId()));
            fotoEvento.setUsuario(usuario);
        }
        return fotoEventoRepository.save(fotoEvento);
    }

    public List<FotoEvento> listarTodos() {
        return fotoEventoRepository.findAll();
    }

    public Optional<FotoEvento> buscarPorId(Long id) {
        return fotoEventoRepository.findById(id);
    }

    @Transactional
    public FotoEvento actualizar(Long id, FotoEventoDTO fotoEventoDTO) {
        return fotoEventoRepository.findById(id).map(existente -> {
            existente.setUrlFoto(fotoEventoDTO.getUrlFoto());
            if (fotoEventoDTO.getEventoId() != null) {
                Evento evento = eventoRepository.findById(fotoEventoDTO.getEventoId())
                        .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + fotoEventoDTO.getEventoId()));
                existente.setEvento(evento);
            }
            if (fotoEventoDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(fotoEventoDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + fotoEventoDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return fotoEventoRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Foto de evento no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        fotoEventoRepository.deleteById(id);
    }
}
