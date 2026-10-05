package com.upc.trashtracker.controladores;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springdoc.core.annotations.ParameterObject;

import com.upc.trashtracker.dto.ReporteRequest;
import com.upc.trashtracker.dto.ReporteResponse;
import com.upc.trashtracker.entidades.Notificacion;
import com.upc.trashtracker.servicios.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @PostMapping(value = "/insertar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReporteResponse> crear(
            @RequestParam("foto") MultipartFile foto,
            @ParameterObject @ModelAttribute ReporteRequest request) {
        ReporteResponse creado = reporteService.crearReporte(foto, request);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ReporteResponse>> misReportes(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(reporteService.listarPorUsuario(usuarioId));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable("id") Long idReporte,
                                         @RequestParam Long usuarioId) {
        reporteService.eliminarReporte(idReporte, usuarioId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notificar-confirmacion/{id}")
    public ResponseEntity<Notificacion> notificarConfirmacion(@PathVariable("id") Long idReporte) {
        Notificacion notificacion = reporteService.enviarConfirmacion(idReporte);
        return new ResponseEntity<>(notificacion, HttpStatus.CREATED);
    }
}
