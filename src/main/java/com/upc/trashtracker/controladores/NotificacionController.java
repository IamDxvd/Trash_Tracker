package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.NotificacionDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.Notificacion;
import com.upc.trashtracker.servicios.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notificacion")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @PostMapping("/insertar")
    public ResponseEntity<Notificacion> guardar(@RequestBody NotificacionDTO notificacionDTO) {
        return new ResponseEntity<>(notificacionService.guardar(notificacionDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Notificacion>> listarTodos() {
        return ResponseEntity.ok(notificacionService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<Notificacion> buscarPorId(@PathVariable Long id) {
        return notificacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Notificacion> actualizar(@PathVariable Long id, @RequestBody NotificacionDTO notificacionDTO) {
        return ResponseEntity.ok(notificacionService.actualizar(id, notificacionDTO));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notificacionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
