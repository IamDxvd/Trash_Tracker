package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.RolDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.Rol;
import com.upc.trashtracker.servicios.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rol")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class RolController {

    @Autowired
    private RolService rolService;

    @PostMapping("/insertar")
    public ResponseEntity<Rol> guardar(@RequestBody RolDTO rolDTO) {
        if (rolDTO.getNombreRol() == null || rolDTO.getNombreRol().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return new ResponseEntity<>(rolService.crear(rolDTO.getNombreRol(), rolDTO.getDescripcion()), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Rol>> listarTodos() {
        return ResponseEntity.ok(rolService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<Rol> buscarPorId(@PathVariable Long id) {
        return rolService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Rol> actualizar(@PathVariable Long id, @RequestBody RolDTO rolDTO) {
        if (id == 1 || id == 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (rolService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rolService.actualizarDatos(id, rolDTO.getNombreRol(), rolDTO.getDescripcion()));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (id == 1 || id == 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        rolService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
