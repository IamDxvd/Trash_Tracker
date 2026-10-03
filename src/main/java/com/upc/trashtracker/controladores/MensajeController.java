package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.MensajeDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.Mensaje;
import com.upc.trashtracker.servicios.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensaje")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    @PostMapping
    public ResponseEntity<Mensaje> guardar(@RequestBody MensajeDTO mensajeDTO) {
        return new ResponseEntity<>(mensajeService.guardar(mensajeDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Mensaje>> listarTodos() {
        return ResponseEntity.ok(mensajeService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mensaje> buscarPorId(@PathVariable Long id) {
        return mensajeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Mensaje> actualizar(@PathVariable Long id, @RequestBody MensajeDTO mensajeDTO) {
        return ResponseEntity.ok(mensajeService.actualizar(id, mensajeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mensajeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
