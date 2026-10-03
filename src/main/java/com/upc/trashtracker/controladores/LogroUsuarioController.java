package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.LogroUsuarioDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.LogroUsuario;
import com.upc.trashtracker.servicios.LogroUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logro-usuario")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class LogroUsuarioController {

    @Autowired
    private LogroUsuarioService logroUsuarioService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<LogroUsuario> guardar(@RequestBody LogroUsuarioDTO logroUsuarioDTO) {
        return new ResponseEntity<>(logroUsuarioService.guardar(logroUsuarioDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<LogroUsuario>> listarTodos() {
        return ResponseEntity.ok(logroUsuarioService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogroUsuario> buscarPorId(@PathVariable Long id) {
        return logroUsuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<LogroUsuario> actualizar(@PathVariable Long id, @RequestBody LogroUsuarioDTO logroUsuarioDTO) {
        return ResponseEntity.ok(logroUsuarioService.actualizar(id, logroUsuarioDTO));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        logroUsuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
