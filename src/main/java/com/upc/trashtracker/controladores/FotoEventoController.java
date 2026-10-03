package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.FotoEventoDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.FotoEvento;
import com.upc.trashtracker.servicios.FotoEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foto-evento")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class FotoEventoController {

    @Autowired
    private FotoEventoService fotoEventoService;

    @PostMapping
    public ResponseEntity<FotoEvento> guardar(@RequestBody FotoEventoDTO fotoEventoDTO) {
        return new ResponseEntity<>(fotoEventoService.guardar(fotoEventoDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<FotoEvento>> listarTodos() {
        return ResponseEntity.ok(fotoEventoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FotoEvento> buscarPorId(@PathVariable Long id) {
        return fotoEventoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FotoEvento> actualizar(@PathVariable Long id, @RequestBody FotoEventoDTO fotoEventoDTO) {
        return ResponseEntity.ok(fotoEventoService.actualizar(id, fotoEventoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        fotoEventoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
