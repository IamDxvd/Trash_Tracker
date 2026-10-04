package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.RecompensaDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.Recompensa;
import com.upc.trashtracker.servicios.RecompensaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recompensa")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class RecompensaController {

    @Autowired
    private RecompensaService recompensaService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/insertar")
    public ResponseEntity<Recompensa> guardar(@RequestBody RecompensaDTO recompensaDTO) {
        return new ResponseEntity<>(recompensaService.guardar(recompensaDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Recompensa>> listarTodos() {
        return ResponseEntity.ok(recompensaService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<Recompensa> buscarPorId(@PathVariable Long id) {
        return recompensaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Recompensa> actualizar(@PathVariable Long id, @RequestBody RecompensaDTO recompensaDTO) {
        return recompensaService.actualizar(id, recompensaDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recompensaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}