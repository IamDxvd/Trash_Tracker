package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.PuntosRequestDTO;
import com.upc.trashtracker.dto.PuntosResponseDTO;
import com.upc.trashtracker.dto.HistorialPuntosDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.HistorialPuntos;
import com.upc.trashtracker.servicios.HistorialPuntosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historial-puntos")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class HistorialPuntosController {

    @Autowired
    private HistorialPuntosService historialPuntosService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/registrar")
    public ResponseEntity<HistorialPuntos> guardar(@RequestBody HistorialPuntosDTO historialPuntosDTO) {
        return new ResponseEntity<>(historialPuntosService.guardar(historialPuntosDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listado")
    public ResponseEntity<List<HistorialPuntos>> listarTodos() {
        return ResponseEntity.ok(historialPuntosService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<HistorialPuntos> buscarPorId(@PathVariable Long id) {
        return historialPuntosService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/modificar/{id}")
    public ResponseEntity<HistorialPuntos> actualizar(@PathVariable Long id, @RequestBody HistorialPuntosDTO historialPuntosDTO) {
        return ResponseEntity.ok(historialPuntosService.actualizar(id, historialPuntosDTO));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/borrar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        historialPuntosService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/agregar-puntos")
    public ResponseEntity<PuntosResponseDTO> insertarPuntos(@RequestBody PuntosRequestDTO request) {
        return new ResponseEntity<>(historialPuntosService.insertarPuntos(request), HttpStatus.CREATED);
    }
}
