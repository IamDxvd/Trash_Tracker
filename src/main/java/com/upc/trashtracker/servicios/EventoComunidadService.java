package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.AsistenciaRequestDTO;
import com.upc.trashtracker.dto.AsistenciaResponseDTO;
import com.upc.trashtracker.dto.EventoRequestDTO;
import com.upc.trashtracker.dto.EventoResponseDTO;
import com.upc.trashtracker.dto.FotoEventoResponseDTO;
import com.upc.trashtracker.dto.NotificacionEventoResponseDTO;
import com.upc.trashtracker.entidades.AsistenciaEvento;
import com.upc.trashtracker.entidades.Evento;
import com.upc.trashtracker.entidades.FotoEvento;
import com.upc.trashtracker.entidades.Grupo;
import com.upc.trashtracker.entidades.MiembroGrupo;
import com.upc.trashtracker.entidades.Notificacion;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.AsistenciaEventoRepository;
import com.upc.trashtracker.repositorio.EventoRepository;
import com.upc.trashtracker.repositorio.FotoEventoRepository;
import com.upc.trashtracker.repositorio.GrupoRepository;
import com.upc.trashtracker.repositorio.MiembroGrupoRepository;
import com.upc.trashtracker.repositorio.NotificacionRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class EventoComunidadService {
    @Autowired
    private com.upc.trashtracker.security.services.AccesoIntegracion acceso;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MiembroGrupoRepository miembroGrupoRepository;

    @Autowired
    private AsistenciaEventoRepository asistenciaEventoRepository;

    @Autowired
    private FotoEventoRepository fotoEventoRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private FotoComunidadStorage fileStorageService;

    @Autowired
    private GrupoComunidadService grupoComunidadService;

    private static final String TIPO_NOTIFICACION_EVENTO = "EVENTO_NUEVO";

    @Transactional
    public EventoResponseDTO crearEvento(EventoRequestDTO request) {
        acceso.exigirPropietario(request.getOrganizadorId());
        if (request.getGrupoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Debes indicar el grupo del evento.");
        }
        Grupo grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el grupo con id " + request.getGrupoId()
                                + ". No se puede definir la fecha del evento si no se ha creado el grupo."));

        Usuario organizador = buscarUsuario(request.getOrganizadorId());

        if (!grupoComunidadService.puedeAdministrar(grupo, organizador.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo un organizador del grupo puede crear eventos.");
        }
        if (request.getFecha() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha del evento es obligatoria.");
        }
        if (request.getFecha().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha del evento no puede estar en el pasado.");
        }
        if (request.getUbicacion() == null || request.getUbicacion().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ubicación del evento es obligatoria.");
        }

        Evento evento = new Evento();
        evento.setGrupo(grupo);
        evento.setOrganizador(organizador);
        evento.setFecha(request.getFecha());
        evento.setUbicacion(request.getUbicacion());

        return toResponse(eventoRepository.save(evento));
    }


    @Transactional
    public AsistenciaResponseDTO registrarAsistencia(Long idEvento, AsistenciaRequestDTO request) {
        acceso.exigirPropietario(request.getUsuarioId());
        Evento evento = buscarEvento(idEvento);
        Usuario usuario = buscarUsuario(request.getUsuarioId());

        if (haFinalizado(evento)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El evento ya ha terminado.");
        }

        boolean confirmado = request.getConfirmado() == null || request.getConfirmado();

        AsistenciaEvento asistencia = asistenciaEventoRepository
                .findByEventoIdEventoAndUsuarioIdUsuario(idEvento, usuario.getIdUsuario())
                .orElseGet(() -> {
                    AsistenciaEvento nueva = new AsistenciaEvento();
                    nueva.setEvento(evento);
                    nueva.setUsuario(usuario);
                    return nueva;
                });
        asistencia.setConfirmado(confirmado);
        asistencia = asistenciaEventoRepository.save(asistencia);

        return new AsistenciaResponseDTO(
                asistencia.getIdAsistenciaEvento(),
                evento.getIdEvento(),
                usuario.getIdUsuario(),
                usuario.getNombre(),
                asistencia.getConfirmado(),
                contarConfirmados(idEvento)
        );
    }

    @Transactional
    public NotificacionEventoResponseDTO notificarNuevoEvento(Long idEvento) {
        Evento evento = buscarEvento(idEvento);
        Grupo grupo = evento.getGrupo();
        if (grupo == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El evento no pertenece a ningún grupo.");
        }

        if (!grupoComunidadService.puedeAdministrar(grupo, acceso.usuarioActualId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo un organizador puede notificar al grupo");
        }
        Long idOrganizador = evento.getOrganizador() != null ? evento.getOrganizador().getIdUsuario() : null;
        String contenido = "Nuevo evento de limpieza en el grupo \"" + grupo.getNombre() + "\": "
                + evento.getUbicacion() + ", el " + evento.getFecha() + ".";

        int enviadas = 0;
        for (MiembroGrupo miembro : miembroGrupoRepository.findByGrupoIdGrupo(grupo.getIdGrupo())) {
            if (miembro.getUsuario() == null || miembro.getUsuario().getIdUsuario().equals(idOrganizador)) {
                continue;
            }
            Notificacion notificacion = new Notificacion();
            notificacion.setUsuario(miembro.getUsuario());
            notificacion.setTipo(TIPO_NOTIFICACION_EVENTO);
            notificacion.setContenido(contenido);
            notificacion.setLeido(false);
            notificacion.setFecha(LocalDateTime.now());
            notificacionRepository.save(notificacion);
            enviadas++;
        }

        return new NotificacionEventoResponseDTO(idEvento, grupo.getIdGrupo(), enviadas, contenido);
    }

    @Transactional
    public List<FotoEventoResponseDTO> subirFotos(Long idEvento, Long usuarioId, List<MultipartFile> fotos) {
        acceso.exigirPropietario(usuarioId);
        Evento evento = buscarEvento(idEvento);
        Usuario usuario = buscarUsuario(usuarioId);

        if (haFinalizado(evento)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El evento ya ha terminado.");
        }
        if (!participaEnEvento(evento, usuario.getIdUsuario())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo los miembros del grupo o quienes confirmaron su asistencia pueden subir fotos.");
        }
        if (fotos == null || fotos.isEmpty() || fotos.size() > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes adjuntar entre 1 y 10 fotos.");
        }
        for (MultipartFile foto : fotos) {
            if (foto == null || foto.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Una de las fotos adjuntas está vacía.");
            }
        }

        List<FotoEventoResponseDTO> subidas = new ArrayList<>();
        for (MultipartFile foto : fotos) {
            String urlFoto = fileStorageService.guardarFoto(foto);

            FotoEvento fotoEvento = new FotoEvento();
            fotoEvento.setEvento(evento);
            fotoEvento.setUsuario(usuario);
            fotoEvento.setUrlFoto(urlFoto);

            FotoEvento guardada = fotoEventoRepository.save(fotoEvento);
            subidas.add(new FotoEventoResponseDTO(
                    guardada.getIdFotoEvento(),
                    evento.getIdEvento(),
                    usuario.getIdUsuario(),
                    guardada.getUrlFoto()
            ));
        }
        return subidas;
    }


    private Evento buscarEvento(Long idEvento) {
        return eventoRepository.buscarParaActualizar(idEvento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el evento con id " + idEvento));
    }

    private Usuario buscarUsuario(Long usuarioId) {
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuarioId es obligatorio.");
        }
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el usuario con id " + usuarioId));
    }

    private boolean haFinalizado(Evento evento) {
        return evento.getFecha() != null && evento.getFecha().isBefore(LocalDate.now());
    }

    private boolean participaEnEvento(Evento evento, Long usuarioId) {
        boolean esMiembro = evento.getGrupo() != null
                && miembroGrupoRepository
                .findByGrupoIdGrupoAndUsuarioIdUsuario(evento.getGrupo().getIdGrupo(), usuarioId)
                .isPresent();

        boolean confirmoAsistencia = asistenciaEventoRepository
                .findByEventoIdEventoAndUsuarioIdUsuario(evento.getIdEvento(), usuarioId)
                .map(a -> Boolean.TRUE.equals(a.getConfirmado()))
                .orElse(false);

        return esMiembro || confirmoAsistencia;
    }

    private long contarConfirmados(Long idEvento) {
        return asistenciaEventoRepository.findByEventoIdEvento(idEvento).stream()
                .filter(a -> Boolean.TRUE.equals(a.getConfirmado()))
                .count();
    }

    private EventoResponseDTO toResponse(Evento e) {
        return new EventoResponseDTO(
                e.getIdEvento(),
                e.getGrupo() != null ? e.getGrupo().getIdGrupo() : null,
                e.getGrupo() != null ? e.getGrupo().getNombre() : null,
                e.getOrganizador() != null ? e.getOrganizador().getIdUsuario() : null,
                e.getOrganizador() != null ? e.getOrganizador().getNombre() : null,
                e.getFecha(),
                e.getUbicacion(),
                contarConfirmados(e.getIdEvento())
        );
    }
}
