package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.AsistenciaEventoDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.AsistenciaEvento;
import com.upc.trashtracker.servicios.AsistenciaEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asistencia-evento")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class AsistenciaEventoController {

    @Autowired
    private AsistenciaEventoService asistenciaEventoService;

    @PostMapping("/insertar")
    public ResponseEntity<AsistenciaEvento> guardar(@RequestBody AsistenciaEventoDTO asistenciaEventoDTO) {
        return new ResponseEntity<>(asistenciaEventoService.guardar(asistenciaEventoDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<AsistenciaEvento>> listarTodos() {
        return ResponseEntity.ok(asistenciaEventoService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<AsistenciaEvento> buscarPorId(@PathVariable Long id) {
        return asistenciaEventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<AsistenciaEvento> actualizar(@PathVariable Long id, @RequestBody AsistenciaEventoDTO asistenciaEventoDTO) {
        return ResponseEntity.ok(asistenciaEventoService.actualizar(id, asistenciaEventoDTO));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        asistenciaEventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
