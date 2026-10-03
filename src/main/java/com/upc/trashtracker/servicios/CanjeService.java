package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.CanjeDTO;
import com.upc.trashtracker.entidades.Canje;
import com.upc.trashtracker.entidades.Recompensa;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.CanjeRepository;
import com.upc.trashtracker.repositorio.RecompensaRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CanjeService {

    @Autowired
    private CanjeRepository canjeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Transactional
    public Canje guardar(CanjeDTO canjeDTO) {
        Canje canje = new Canje();
        canje.setFecha(canjeDTO.getFecha() != null ? canjeDTO.getFecha() : LocalDateTime.now());
        canje.setEstado(canjeDTO.getEstado() != null ? canjeDTO.getEstado() : "PENDIENTE");

        if (canjeDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(canjeDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + canjeDTO.getUsuarioId()));
            canje.setUsuario(usuario);
        }
        if (canjeDTO.getRecompensaId() != null) {
            Recompensa recompensa = recompensaRepository.findById(canjeDTO.getRecompensaId())
                    .orElseThrow(() -> new RuntimeException("Recompensa no encontrada con ID: " + canjeDTO.getRecompensaId()));
            canje.setRecompensa(recompensa);
        }
        return canjeRepository.save(canje);
    }

    public List<Canje> listarTodos() {
        return canjeRepository.findAll();
    }

    public Optional<Canje> buscarPorId(Long id) {
        return canjeRepository.findById(id);
    }

    @Transactional
    public Canje actualizar(Long id, CanjeDTO canjeDTO) {
        return canjeRepository.findById(id).map(existente -> {
            if (canjeDTO.getFecha() != null) {
                existente.setFecha(canjeDTO.getFecha());
            }
            if (canjeDTO.getEstado() != null) {
                existente.setEstado(canjeDTO.getEstado());
            }
            if (canjeDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(canjeDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + canjeDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            if (canjeDTO.getRecompensaId() != null) {
                Recompensa recompensa = recompensaRepository.findById(canjeDTO.getRecompensaId())
                        .orElseThrow(() -> new RuntimeException("Recompensa no encontrada con ID: " + canjeDTO.getRecompensaId()));
                existente.setRecompensa(recompensa);
            }
            return canjeRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Canje no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        canjeRepository.deleteById(id);
    }
}
