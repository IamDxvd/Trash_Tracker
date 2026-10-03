package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.DispositivoConocidoDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.DispositivoConocido;
import com.upc.trashtracker.servicios.DispositivoConocidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispositivo-conocido")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class DispositivoConocidoController {

    @Autowired
    private DispositivoConocidoService dispositivoConocidoService;

    @PostMapping
    public ResponseEntity<DispositivoConocido> guardar(@RequestBody DispositivoConocidoDTO dispositivoConocidoDTO) {
        return new ResponseEntity<>(dispositivoConocidoService.guardar(dispositivoConocidoDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<DispositivoConocido>> listarTodos() {
        return ResponseEntity.ok(dispositivoConocidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DispositivoConocido> buscarPorId(@PathVariable Long id) {
        return dispositivoConocidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DispositivoConocido> actualizar(@PathVariable Long id, @RequestBody DispositivoConocidoDTO dispositivoConocidoDTO) {
        return ResponseEntity.ok(dispositivoConocidoService.actualizar(id, dispositivoConocidoDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        dispositivoConocidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
