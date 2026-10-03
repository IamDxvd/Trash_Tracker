package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.TipoResiduoDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.TipoResiduo;
import com.upc.trashtracker.servicios.TipoResiduoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipo-residuo")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class TipoResiduoController {

    @Autowired
    private TipoResiduoService tipoResiduoService;

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<TipoResiduo> guardar(@RequestBody TipoResiduoDTO tipoResiduoDTO) {
        if (tipoResiduoDTO.getNombre() == null || tipoResiduoDTO.getNombre().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(tipoResiduoService.crear(tipoResiduoDTO.getNombre()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TipoResiduo>> listarTodos() {
        return ResponseEntity.ok(tipoResiduoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoResiduo> buscarPorId(@PathVariable Long id) {
        return tipoResiduoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<TipoResiduo>  actualizar(@PathVariable Long id, @RequestBody TipoResiduoDTO tipoResiduoDTO) {
        if (tipoResiduoDTO.getNombre() == null || tipoResiduoDTO.getNombre().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (tipoResiduoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tipoResiduoService.actualizarNombre(id, tipoResiduoDTO.getNombre()));
    }

    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        tipoResiduoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
