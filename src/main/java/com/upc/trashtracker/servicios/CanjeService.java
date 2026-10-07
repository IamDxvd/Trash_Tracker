package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.CanjeRequestDTO;
import com.upc.trashtracker.dto.CanjeResponseDTO;
import com.upc.trashtracker.entidades.HistorialPuntos;
import com.upc.trashtracker.repositorio.HistorialPuntosRepository;
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

    // Funciones adicionales integradas del backend unificado.
    @Autowired
    private HistorialPuntosRepository historialPuntosRepository;

    @Transactional
    @org.springframework.security.access.prepost.PreAuthorize("@accesoIntegracion.esPropietario(#request.idUsuario)")
    public CanjeResponseDTO realizarCanje(CanjeRequestDTO request) {
        // 1. Validar Usuario
        Usuario usuario = usuarioRepository.buscarParaActualizar(request.getIdUsuario())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + request.getIdUsuario()));

        // 2. Validar Recompensa
        Recompensa recompensa = recompensaRepository.buscarParaActualizar(request.getIdRecompensa())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Recompensa no encontrada con ID: " + request.getIdRecompensa()));

        if (recompensa.getCostoPuntos() == null || recompensa.getCostoPuntos() <= 0) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Costo de recompensa invalido");
        }
        // 3. Validar Stock
        if (recompensa.getStock() == null || recompensa.getStock() <= 0) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "La recompensa seleccionada no cuenta con stock disponible");
        }

        // 4. Validar Puntos del Usuario
        int puntosActuales = usuario.getPuntosTotales() != null ? usuario.getPuntosTotales() : 0;
        if (puntosActuales < recompensa.getCostoPuntos()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Puntos insuficientes para canjear esta recompensa");
        }

        // 5. Actualizar Puntos y Stock
        usuario.setPuntosTotales(puntosActuales - recompensa.getCostoPuntos());
        recompensa.setStock(recompensa.getStock() - 1);

        usuarioRepository.save(usuario);
        recompensaRepository.save(recompensa);

        // 6. Registrar la transacción de Canje
        Canje canje = new Canje();
        canje.setFecha(LocalDateTime.now());
        canje.setEstado("COMPLETADO");
        canje.setUsuario(usuario);
        canje.setRecompensa(recompensa);
        canje = canjeRepository.save(canje);

        // 7. Registrar el movimiento negativo en HistorialPuntos
        HistorialPuntos historial = new HistorialPuntos();
        historial.setCantidad(-recompensa.getCostoPuntos());
        historial.setMotivo("Canje de Recompensa: " + recompensa.getNombre());
        historial.setFecha(LocalDateTime.now());
        historial.setUsuario(usuario);
        historialPuntosRepository.save(historial);

        // 8. Construir Respuesta DTO
        CanjeResponseDTO response = new CanjeResponseDTO();
        response.setIdCanje(canje.getIdCanje());
        response.setNombreRecompensa(recompensa.getNombre());
        response.setPuntosConsumidos(recompensa.getCostoPuntos());
        response.setPuntosRestantesUsuario(usuario.getPuntosTotales());
        response.setEstado(canje.getEstado());
        response.setFecha(canje.getFecha());

        return response;
    }
}
