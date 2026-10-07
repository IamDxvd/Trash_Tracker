package com.upc.trashtracker.controladores;

import com.upc.trashtracker.entidades.Reporte;
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

    @PostMapping
    public ResponseEntity<Reporte> guardar(@RequestBody Reporte reporte) {
        return new ResponseEntity<>(reporteService.guardar(reporte), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Reporte>> listarTodos() {
        return ResponseEntity.ok(reporteService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> buscarPorId(@PathVariable Long id) {
        return reporteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reporte> actualizar(@PathVariable Long id, @RequestBody Reporte reporte) {
        return ResponseEntity.ok(reporteService.actualizar(id, reporte));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reporteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mapa")
    public ResponseEntity<List<Reporte>> mapa(@RequestParam(required = false) String tipo) {
        if (tipo != null) {
            return ResponseEntity.ok(reporteService.filtrarPorTipo(tipo));
        }
        return ResponseEntity.ok(reporteService.listarMapa());
    }

    @GetMapping("/mapa/todos")
    public ResponseEntity<List<Reporte>> todosMapa() {
        return ResponseEntity.ok(reporteService.listarMapa());
    }

    @GetMapping("/mapa/buscar")
    public ResponseEntity<List<Reporte>> buscarMapa(@RequestParam String texto) {
        return ResponseEntity.ok(reporteService.buscar(texto));
    }

    @GetMapping("/mapa/{id}")
    public ResponseEntity<Reporte> detalleMapa(@PathVariable Long id) {
        return reporteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/admin/{id}/limpiar")
    public ResponseEntity<Reporte> limpiar(@PathVariable Long id) {
        return ResponseEntity.ok(reporteService.marcarLimpio(id));
    }

    @PatchMapping("/admin/{id}/estado")
    public ResponseEntity<Reporte> estado(@PathVariable Long id, @RequestParam String estado) {
        return ResponseEntity.ok(reporteService.actualizarEstado(id, estado));
    }
}
