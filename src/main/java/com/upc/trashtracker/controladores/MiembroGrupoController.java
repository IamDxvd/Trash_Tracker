package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.MiembroGrupoDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.MiembroGrupo;
import com.upc.trashtracker.servicios.MiembroGrupoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/miembro-grupo")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class MiembroGrupoController {

    @Autowired
    private MiembroGrupoService miembroGrupoService;

    @PostMapping("/insertar")
    public ResponseEntity<MiembroGrupo> guardar(@RequestBody MiembroGrupoDTO miembroGrupoDTO) {
        return new ResponseEntity<>(miembroGrupoService.guardar(miembroGrupoDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<MiembroGrupo>> listarTodos() {
        return ResponseEntity.ok(miembroGrupoService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<MiembroGrupo> buscarPorId(@PathVariable Long id) {
        return miembroGrupoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<MiembroGrupo> actualizar(@PathVariable Long id, @RequestBody MiembroGrupoDTO miembroGrupoDTO) {
        return ResponseEntity.ok(miembroGrupoService.actualizar(id, miembroGrupoDTO));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        miembroGrupoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
