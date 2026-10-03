package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.ReporteRequest;
import com.upc.trashtracker.dto.ReporteResponse;
import com.upc.trashtracker.entidades.Notificacion;
import com.upc.trashtracker.entidades.Reporte;
import com.upc.trashtracker.entidades.TipoResiduo;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.NotificacionRepository;
import com.upc.trashtracker.repositorio.ReporteRepository;
import com.upc.trashtracker.repositorio.TipoResiduoRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReporteService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoResiduoRepository tipoResiduoRepository;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private static final String ESTADO_INICIAL = "ACTIVO";

    /**
     * HU021 + HU022 + HU023 + HU026: crea un reporte con su evidencia fotográfica,
     * descripción, tipo de residuo, ubicación (lat/lng) y marca de tiempo automática.
     */
    @Transactional
    public ReporteResponse crearReporte(MultipartFile foto, ReporteRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el usuario con id " + request.getUsuarioId()));

        TipoResiduo tipoResiduo = tipoResiduoRepository.findById(request.getTipoResiduoId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el tipo de residuo con id " + request.getTipoResiduoId()));

        // HU021: se guarda la evidencia fotográfica y se obtiene su URL pública
        String fotoUrl = fileStorageService.guardarFoto(foto);

        Reporte reporte = new Reporte();
        reporte.setUsuario(usuario);
        reporte.setTipoResiduo(tipoResiduo);
        reporte.setDescripcion(request.getDescripcion());       // HU022
        reporte.setLatitud(request.getLatitud());               // HU026
        reporte.setLongitud(request.getLongitud());              // HU026
        reporte.setFotoUrl(fotoUrl);
        reporte.setEstado(ESTADO_INICIAL);
        reporte.setFechaHora(LocalDateTime.now());               // HU023: fecha/hora automática del servidor

        Reporte guardado = reporteRepository.save(reporte);
        return toResponse(guardado);
    }

    /**
     * HU024: historial de reportes del usuario autenticado.
     * Nota: mientras no exista Spring Security/JWT, el id de usuario se recibe
     * explícitamente (query param) en vez de tomarse del token de sesión.
     */
    public List<ReporteResponse> listarPorUsuario(Long usuarioId) {
        return reporteRepository.findAll().stream()
                .filter(r -> r.getUsuario() != null && usuarioId.equals(r.getUsuario().getIdUsuario()))
                .sorted((a, b) -> b.getFechaHora().compareTo(a.getFechaHora())) // más reciente primero
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * HU028: eliminar un reporte propio. Verifica que quien elimina sea el dueño.
     */
    @Transactional
    public void eliminarReporte(Long idReporte, Long usuarioId) {
        Reporte reporte = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el reporte con id " + idReporte));

        if (reporte.getUsuario() == null || !reporte.getUsuario().getIdUsuario().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "No puedes eliminar un reporte que no te pertenece.");
        }

        reporteRepository.deleteById(idReporte);
    }

    /**
     * HU030: notificación de confirmación al enviar un reporte.
     * Se invoca aparte de la creación (POST /api/reports/{id}/notify-confirmation)
     * para permitir reintentos desde el frontend si el push/correo falla.
     */
    @Transactional
    public Notificacion enviarConfirmacion(Long idReporte) {
        Reporte reporte = reporteRepository.findById(idReporte)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe el reporte con id " + idReporte));

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(reporte.getUsuario());
        notificacion.setTipo("CONFIRMACION_REPORTE");
        notificacion.setContenido("Tu reporte #" + reporte.getIdReporte() +
                " fue registrado correctamente y ya está visible en el mapa comunitario.");
        notificacion.setLeido(false);
        notificacion.setFecha(LocalDateTime.now());

        return notificacionRepository.save(notificacion);
    }

    // ------------------------------------------------------------------
    private ReporteResponse toResponse(Reporte r) {
        return new ReporteResponse(
                r.getIdReporte(),
                r.getDescripcion(),
                r.getFotoUrl(),
                r.getLatitud(),
                r.getLongitud(),
                r.getEstado(),
                r.getFechaHora(),
                r.getUsuario() != null ? r.getUsuario().getIdUsuario() : null,
                r.getUsuario() != null ? r.getUsuario().getNombre() : null,
                r.getTipoResiduo() != null ? r.getTipoResiduo().getIdTipoResiduo() : null,
                r.getTipoResiduo() != null ? r.getTipoResiduo().getNombre() : null
        );
    }
}
