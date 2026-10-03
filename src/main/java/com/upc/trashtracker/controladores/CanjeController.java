package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.CanjeDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.Canje;
import com.upc.trashtracker.servicios.CanjeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/canje")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class CanjeController {

    @Autowired
    private CanjeService canjeService;

    @PostMapping
    public ResponseEntity<Canje> guardar(@RequestBody CanjeDTO canjeDTO) {
        return new ResponseEntity<>(canjeService.guardar(canjeDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Canje>> listarTodos() {
        return ResponseEntity.ok(canjeService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Canje> buscarPorId(@PathVariable Long id) {
        return canjeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Canje> actualizar(@PathVariable Long id, @RequestBody CanjeDTO canjeDTO) {
        return ResponseEntity.ok(canjeService.actualizar(id, canjeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        canjeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
