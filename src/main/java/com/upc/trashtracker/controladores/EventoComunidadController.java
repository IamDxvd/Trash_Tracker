package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.AsistenciaRequestDTO;
import com.upc.trashtracker.dto.AsistenciaResponseDTO;
import com.upc.trashtracker.dto.EventoRequestDTO;
import com.upc.trashtracker.dto.EventoResponseDTO;
import com.upc.trashtracker.dto.FotoEventoResponseDTO;
import com.upc.trashtracker.dto.NotificacionEventoResponseDTO;
import com.upc.trashtracker.servicios.EventoComunidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/evento")
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class EventoComunidadController {

    @Autowired
    private EventoComunidadService eventoComunidadService;


    @PostMapping("/crear")
    public ResponseEntity<EventoResponseDTO> crear(@RequestBody EventoRequestDTO request) {
        EventoResponseDTO creado = eventoComunidadService.crearEvento(request);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PostMapping("/{idEvento}/asistir")
    public ResponseEntity<AsistenciaResponseDTO> asistir(@PathVariable("idEvento") Long idEvento,
                                                      @RequestBody AsistenciaRequestDTO request) {
        return ResponseEntity.ok(eventoComunidadService.registrarAsistencia(idEvento, request));
    }
    @PostMapping("/{idEvento}/notificar")
    public ResponseEntity<NotificacionEventoResponseDTO> notificar(@PathVariable("idEvento") Long idEvento) {
        NotificacionEventoResponseDTO respuesta = eventoComunidadService.notificarNuevoEvento(idEvento);
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    @PostMapping(value = "/{idEvento}/fotos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FotoEventoResponseDTO>> subirFotos(@PathVariable("idEvento") Long idEvento,
                                                               @RequestParam("usuarioId") Long usuarioId,
                                                               @RequestParam("fotos") List<MultipartFile> fotos) {
        List<FotoEventoResponseDTO> subidas = eventoComunidadService.subirFotos(idEvento, usuarioId, fotos);
        return new ResponseEntity<>(subidas, HttpStatus.CREATED);
    }
}
