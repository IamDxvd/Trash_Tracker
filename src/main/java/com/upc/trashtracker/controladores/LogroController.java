package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.LogroDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.Logro;
import com.upc.trashtracker.servicios.LogroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logro")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class LogroController {

    @Autowired
    private LogroService logroService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<Logro> guardar(@RequestBody LogroDTO logroDTO) {
        return new ResponseEntity<>(logroService.guardar(logroDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Logro>> listarTodos() {
        return ResponseEntity.ok(logroService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Logro> buscarPorId(@PathVariable Long id) {
        return logroService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Logro> actualizar(@PathVariable Long id, @RequestBody LogroDTO logroDTO) {
        return ResponseEntity.ok(logroService.actualizar(id, logroDTO));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logroService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}