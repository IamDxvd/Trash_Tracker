package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.PreguntaFrecuenteDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.PreguntaFrecuente;
import com.upc.trashtracker.servicios.PreguntaFrecuenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pregunta-frecuente")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class PreguntaFrecuenteController {

    @Autowired
    private PreguntaFrecuenteService preguntaFrecuenteService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/insertar")
    public ResponseEntity<PreguntaFrecuente> guardar(@RequestBody PreguntaFrecuenteDTO preguntaFrecuenteDTO) {
        return new ResponseEntity<>(preguntaFrecuenteService.guardar(preguntaFrecuenteDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PreguntaFrecuente>> listarTodos() {
        return ResponseEntity.ok(preguntaFrecuenteService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<PreguntaFrecuente> buscarPorId(@PathVariable Long id) {
        return preguntaFrecuenteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<PreguntaFrecuente> actualizar(@PathVariable Long id, @RequestBody PreguntaFrecuenteDTO preguntaFrecuenteDTO) {
        return ResponseEntity.ok(preguntaFrecuenteService.actualizar(id, preguntaFrecuenteDTO));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        preguntaFrecuenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
