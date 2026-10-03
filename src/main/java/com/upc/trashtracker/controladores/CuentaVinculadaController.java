package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.CuentaVinculadaDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.CuentaVinculada;
import com.upc.trashtracker.servicios.CuentaVinculadaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuenta-vinculada")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class CuentaVinculadaController {

    @Autowired
    private CuentaVinculadaService cuentaVinculadaService;

    @PostMapping
    public ResponseEntity<CuentaVinculada> guardar(@RequestBody CuentaVinculadaDTO cuentaVinculadaDTO) {
        return new ResponseEntity<>(cuentaVinculadaService.guardar(cuentaVinculadaDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CuentaVinculada>> listarTodos() {
        return ResponseEntity.ok(cuentaVinculadaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuentaVinculada> buscarPorId(@PathVariable Long id) {
        return cuentaVinculadaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuentaVinculada> actualizar(@PathVariable Long id, @RequestBody CuentaVinculadaDTO cuentaVinculadaDTO) {
        return ResponseEntity.ok(cuentaVinculadaService.actualizar(id, cuentaVinculadaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cuentaVinculadaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
