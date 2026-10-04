package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.EventoDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.Evento;
import com.upc.trashtracker.servicios.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evento")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping("/insertar")
    public ResponseEntity<Evento> guardar(@RequestBody EventoDTO eventoDTO) {
        return new ResponseEntity<>(eventoService.guardar(eventoDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Evento>> listarTodos() {
        return ResponseEntity.ok(eventoService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<Evento> buscarPorId(@PathVariable Long id) {
        return eventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Evento> actualizar(@PathVariable Long id, @RequestBody EventoDTO eventoDTO) {
        return ResponseEntity.ok(eventoService.actualizar(id, eventoDTO));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
