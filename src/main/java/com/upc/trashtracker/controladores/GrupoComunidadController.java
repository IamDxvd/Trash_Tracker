package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.AsignarRolRequestDTO;
import com.upc.trashtracker.dto.MensajeRequestDTO;
import com.upc.trashtracker.dto.MensajeResponseDTO;
import com.upc.trashtracker.dto.MiembroGrupoResponseDTO;
import com.upc.trashtracker.servicios.GrupoComunidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupo")
@org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class GrupoComunidadController {

    @Autowired
    private GrupoComunidadService grupoComunidadService;

    @PostMapping("/{idGrupo}/unirse")
    public ResponseEntity<MiembroGrupoResponseDTO> unirse(@PathVariable("idGrupo") Long idGrupo,
                                                       @RequestParam("usuarioId") Long usuarioId) {
        MiembroGrupoResponseDTO miembro = grupoComunidadService.unirse(idGrupo, usuarioId);
        return new ResponseEntity<>(miembro, HttpStatus.CREATED);
    }
    @PostMapping("/{idGrupo}/mensajes")
    public ResponseEntity<MensajeResponseDTO> enviarMensaje(@PathVariable("idGrupo") Long idGrupo,
                                                         @RequestBody MensajeRequestDTO request) {
        MensajeResponseDTO mensaje = grupoComunidadService.enviarMensaje(idGrupo, request);
        return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
    }
    @GetMapping("/{idGrupo}/mensajes")
    public ResponseEntity<List<MensajeResponseDTO>> listarMensajes(@PathVariable("idGrupo") Long idGrupo,
                                                                @RequestParam("usuarioId") Long usuarioId) {
        return ResponseEntity.ok(grupoComunidadService.listarMensajes(idGrupo, usuarioId));
    }

    @PatchMapping("/{idGrupo}/miembros/{idUsuario}/rol")
    public ResponseEntity<MiembroGrupoResponseDTO> asignarRol(@PathVariable("idGrupo") Long idGrupo,
                                                           @PathVariable("idUsuario") Long idUsuario,
                                                           @RequestBody AsignarRolRequestDTO request) {
        return ResponseEntity.ok(grupoComunidadService.asignarRol(idGrupo, idUsuario, request));
    }
}
