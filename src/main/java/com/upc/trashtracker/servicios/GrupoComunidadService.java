package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.AsignarRolRequestDTO;
import com.upc.trashtracker.dto.MensajeRequestDTO;
import com.upc.trashtracker.dto.MensajeResponseDTO;
import com.upc.trashtracker.dto.MiembroGrupoResponseDTO;
import com.upc.trashtracker.entidades.Grupo;
import com.upc.trashtracker.entidades.Mensaje;
import com.upc.trashtracker.entidades.MiembroGrupo;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.GrupoRepository;
import com.upc.trashtracker.repositorio.MensajeRepository;
import com.upc.trashtracker.repositorio.MiembroGrupoRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class GrupoComunidadService {
    @Autowired
    private com.upc.trashtracker.security.services.AccesoIntegracion acceso;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MiembroGrupoRepository miembroGrupoRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    public static final String ROL_MIEMBRO = "MIEMBRO";
    public static final String ROL_MODERADOR = "MODERADOR";
    public static final String ROL_ORGANIZADOR = "ORGANIZADOR";

    private static final List<String> ROLES_VALIDOS = List.of(ROL_MIEMBRO, ROL_MODERADOR, ROL_ORGANIZADOR);

    @Transactional
    public MiembroGrupoResponseDTO unirse(Long idGrupo, Long usuarioId) {
        acceso.exigirPropietario(usuarioId);
        Grupo grupo = buscarGrupo(idGrupo);
        Usuario usuario = buscarUsuario(usuarioId);

        if (miembroGrupoRepository.findByGrupoIdGrupoAndUsuarioIdUsuario(idGrupo, usuarioId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya formas parte del grupo \"" + grupo.getNombre() + "\".");
        }

        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setGrupo(grupo);
        miembro.setUsuario(usuario);
        miembro.setRolEnGrupo(ROL_MIEMBRO);

        return toResponse(miembroGrupoRepository.save(miembro));
    }

    @Transactional
    public MensajeResponseDTO enviarMensaje(Long idGrupo, MensajeRequestDTO request) {
        acceso.exigirPropietario(request.getUsuarioId());
        Grupo grupo = buscarGrupo(idGrupo);
        Usuario usuario = buscarUsuario(request.getUsuarioId());
        exigirMiembro(idGrupo, usuario.getIdUsuario());

        if (request.getContenido() == null || request.getContenido().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El mensaje no puede estar vacío.");
        }

        Mensaje mensaje = new Mensaje();
        mensaje.setGrupo(grupo);
        mensaje.setUsuario(usuario);
        mensaje.setContenido(request.getContenido());
        mensaje.setFechaHora(LocalDateTime.now());

        return toResponse(mensajeRepository.save(mensaje));
    }

    @Transactional
    public List<MensajeResponseDTO> listarMensajes(Long idGrupo, Long usuarioId) {
        acceso.exigirPropietario(usuarioId);
        buscarGrupo(idGrupo);
        Usuario usuario = buscarUsuario(usuarioId);
        exigirMiembro(idGrupo, usuario.getIdUsuario());

        return mensajeRepository.findByGrupoIdGrupoOrderByFechaHoraAsc(idGrupo).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MiembroGrupoResponseDTO asignarRol(Long idGrupo, Long idUsuario, AsignarRolRequestDTO request) {
        acceso.exigirPropietario(request.getSolicitanteId());
        Grupo grupo = buscarGrupo(idGrupo);

        if (!puedeAdministrar(grupo, request.getSolicitanteId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No tienes permisos para asignar roles en este grupo.");
        }

        String nuevoRol = request.getRol() == null ? "" : request.getRol().trim().toUpperCase();
        if (!ROLES_VALIDOS.contains(nuevoRol)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Rol inválido. Los roles permitidos son: MIEMBRO, MODERADOR u ORGANIZADOR.");
        }

        MiembroGrupo objetivo = miembroGrupoRepository
                .findByGrupoIdGrupoAndUsuarioIdUsuario(idGrupo, idUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El usuario " + idUsuario + " no es miembro de este grupo."));

        // El grupo no puede quedarse sin organizadores
        if (ROL_ORGANIZADOR.equals(objetivo.getRolEnGrupo()) && !ROL_ORGANIZADOR.equals(nuevoRol)) {
            long organizadores = miembroGrupoRepository.findByGrupoIdGrupo(idGrupo).stream()
                    .filter(m -> ROL_ORGANIZADOR.equals(m.getRolEnGrupo()))
                    .count();
            if (organizadores <= 1) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "El grupo debe conservar al menos un organizador.");
            }
        }

        objetivo.setRolEnGrupo(nuevoRol);
        return toResponse(miembroGrupoRepository.save(objetivo));
    }

    public boolean puedeAdministrar(Grupo grupo, Long usuarioId) {
        if (usuarioId == null) {
            return false;
        }
        if (grupo.getCreador() != null && usuarioId.equals(grupo.getCreador().getIdUsuario())) {
            return true;
        }
        return miembroGrupoRepository.findByGrupoIdGrupoAndUsuarioIdUsuario(grupo.getIdGrupo(), usuarioId)
                .map(m -> ROL_ORGANIZADOR.equals(m.getRolEnGrupo()))
                .orElse(false);
    }
    private Grupo buscarGrupo(Long idGrupo) {
        return grupoRepository.buscarParaActualizar(idGrupo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el grupo con id " + idGrupo));
    }

    private Usuario buscarUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuarioId es obligatorio.");
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el usuario con id " + usuarioId));
    }

    private void exigirMiembro(Long idGrupo, Long usuarioId) {
        if (miembroGrupoRepository.findByGrupoIdGrupoAndUsuarioIdUsuario(idGrupo, usuarioId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Debes ser miembro del grupo para usar su chat.");
        }
    }

    private MiembroGrupoResponseDTO toResponse(MiembroGrupo m) {
        return new MiembroGrupoResponseDTO(
                m.getIdMiembroGrupo(),
                m.getGrupo() != null ? m.getGrupo().getIdGrupo() : null,
                m.getGrupo() != null ? m.getGrupo().getNombre() : null,
                m.getUsuario() != null ? m.getUsuario().getIdUsuario() : null,
                m.getUsuario() != null ? m.getUsuario().getNombre() : null,
                m.getRolEnGrupo()
        );
    }

    private MensajeResponseDTO toResponse(Mensaje m) {
        return new MensajeResponseDTO(
                m.getIdMensaje(),
                m.getGrupo() != null ? m.getGrupo().getIdGrupo() : null,
                m.getUsuario() != null ? m.getUsuario().getIdUsuario() : null,
                m.getUsuario() != null ? m.getUsuario().getNombre() : null,
                m.getContenido(),
                m.getFechaHora()
        );
    }
}
