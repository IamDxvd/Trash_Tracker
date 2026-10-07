package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.DesbloquearLogroRequestDTO;
import com.upc.trashtracker.dto.LogroUsuarioResponseDTO;
import java.util.stream.Collectors;
import com.upc.trashtracker.dto.LogroUsuarioDTO;
import com.upc.trashtracker.entidades.Logro;
import com.upc.trashtracker.entidades.LogroUsuario;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.LogroRepository;
import com.upc.trashtracker.repositorio.LogroUsuarioRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LogroUsuarioService {

    @Autowired
    private LogroUsuarioRepository logroUsuarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LogroRepository logroRepository;

    @Transactional
    public LogroUsuario guardar(LogroUsuarioDTO logroUsuarioDTO) {
        LogroUsuario logroUsuario = new LogroUsuario();
        logroUsuario.setFechaObtenido(logroUsuarioDTO.getFechaObtenido() != null ? logroUsuarioDTO.getFechaObtenido() : LocalDate.now());

        if (logroUsuarioDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(logroUsuarioDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + logroUsuarioDTO.getUsuarioId()));
            logroUsuario.setUsuario(usuario);
        }
        if (logroUsuarioDTO.getLogroId() != null) {
            Logro logro = logroRepository.findById(logroUsuarioDTO.getLogroId())
                    .orElseThrow(() -> new RuntimeException("Logro no encontrado con ID: " + logroUsuarioDTO.getLogroId()));
            logroUsuario.setLogro(logro);
        }
        return logroUsuarioRepository.save(logroUsuario);
    }

    public List<LogroUsuario> listarTodos() {
        return logroUsuarioRepository.findAll();
    }

    public Optional<LogroUsuario> buscarPorId(Long id) {
        return logroUsuarioRepository.findById(id);
    }

    @Transactional
    public LogroUsuario actualizar(Long id, LogroUsuarioDTO logroUsuarioDTO) {
        return logroUsuarioRepository.findById(id).map(existente -> {
            if (logroUsuarioDTO.getFechaObtenido() != null) {
                existente.setFechaObtenido(logroUsuarioDTO.getFechaObtenido());
            }
            if (logroUsuarioDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(logroUsuarioDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + logroUsuarioDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            if (logroUsuarioDTO.getLogroId() != null) {
                Logro logro = logroRepository.findById(logroUsuarioDTO.getLogroId())
                        .orElseThrow(() -> new RuntimeException("Logro no encontrado con ID: " + logroUsuarioDTO.getLogroId()));
                existente.setLogro(logro);
            }
            return logroUsuarioRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Logro de usuario no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        logroUsuarioRepository.deleteById(id);
    }

    // Funciones adicionales integradas del backend unificado.
    @Transactional
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMINISTRADOR')")
    public LogroUsuarioResponseDTO desbloquearLogro(DesbloquearLogroRequestDTO request) {
        usuarioRepository.buscarParaActualizar(request.getIdUsuario())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        // Validar si ya lo tiene desbloqueado
        Optional<LogroUsuario> existente = logroUsuarioRepository
                .findByUsuarioIdUsuarioAndLogroIdLogro(request.getIdUsuario(), request.getIdLogro());

        if (existente.isPresent()) {
            LogroUsuario lu = existente.get();
            return mapearADTO(lu, true);
        }

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + request.getIdUsuario()));

        Logro logro = logroRepository.findById(request.getIdLogro())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Logro no encontrado con ID: " + request.getIdLogro()));

        LogroUsuario logroUsuario = new LogroUsuario();
        logroUsuario.setUsuario(usuario);
        logroUsuario.setLogro(logro);
        logroUsuario.setFechaObtenido(LocalDate.now());

        logroUsuario = logroUsuarioRepository.save(logroUsuario);

        return mapearADTO(logroUsuario, true);
    }

    @org.springframework.security.access.prepost.PreAuthorize("@accesoIntegracion.esPropietario(#idUsuario)")
    public List<LogroUsuarioResponseDTO> listarLogrosPorUsuario(Long idUsuario) {
        List<LogroUsuario> obtenidos = logroUsuarioRepository.findByUsuarioIdUsuario(idUsuario);
        return obtenidos.stream()
                .map(lu -> mapearADTO(lu, true))
                .collect(Collectors.toList());
    }

    private LogroUsuarioResponseDTO mapearADTO(LogroUsuario lu, boolean desbloqueado) {
        LogroUsuarioResponseDTO dto = new LogroUsuarioResponseDTO();
        dto.setIdLogroUsuario(lu.getIdLogroUsuario());
        dto.setIdLogro(lu.getLogro().getIdLogro());
        dto.setNombreLogro(lu.getLogro().getNombre());
        dto.setCriterio(lu.getLogro().getCriterio());
        dto.setFechaObtenido(lu.getFechaObtenido());
        dto.setDesbloqueado(desbloqueado);
        return dto;
    }
}
