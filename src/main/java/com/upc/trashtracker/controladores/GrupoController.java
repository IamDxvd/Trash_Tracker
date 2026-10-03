package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.GrupoDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.Grupo;
import com.upc.trashtracker.servicios.GrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grupo")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @PostMapping
    public ResponseEntity<Grupo> guardar(@RequestBody GrupoDTO grupoDTO) {
        return new ResponseEntity<>(grupoService.guardar(grupoDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Grupo>> listarTodos() {
        return ResponseEntity.ok(grupoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grupo> buscarPorId(@PathVariable Long id) {
        return grupoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grupo> actualizar(@PathVariable Long id, @RequestBody GrupoDTO grupoDTO) {
        return ResponseEntity.ok(grupoService.actualizar(id, grupoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        grupoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
