package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.MensajeChatbotDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.MensajeChatbot;
import com.upc.trashtracker.servicios.MensajeChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensaje-chatbot")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class MensajeChatbotController {

    @Autowired
    private MensajeChatbotService mensajeChatbotService;

    @PostMapping
    public ResponseEntity<MensajeChatbot> guardar(@RequestBody MensajeChatbotDTO mensajeChatbotDTO) {
        return new ResponseEntity<>(mensajeChatbotService.guardar(mensajeChatbotDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MensajeChatbot>> listarTodos() {
        return ResponseEntity.ok(mensajeChatbotService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MensajeChatbot> buscarPorId(@PathVariable Long id) {
        return mensajeChatbotService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MensajeChatbot> actualizar(@PathVariable Long id, @RequestBody MensajeChatbotDTO mensajeChatbotDTO) {
        return ResponseEntity.ok(mensajeChatbotService.actualizar(id, mensajeChatbotDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        mensajeChatbotService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
