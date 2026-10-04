package com.upc.trashtracker.controladores;

import com.upc.trashtracker.dto.ConversacionChatbotDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import com.upc.trashtracker.entidades.ConversacionChatbot;
import com.upc.trashtracker.servicios.ConversacionChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversacion-chatbot")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class ConversacionChatbotController {

    @Autowired
    private ConversacionChatbotService conversacionChatbotService;

    @PostMapping("/insertar")
    public ResponseEntity<ConversacionChatbot> guardar(@RequestBody ConversacionChatbotDTO conversacionChatbotDTO) {
        return new ResponseEntity<>(conversacionChatbotService.guardar(conversacionChatbotDTO), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ConversacionChatbot>> listarTodos() {
        return ResponseEntity.ok(conversacionChatbotService.listarTodos());
    }

    @GetMapping("/buscar-por-Id/{id}")
    public ResponseEntity<ConversacionChatbot> buscarPorId(@PathVariable Long id) {
        return conversacionChatbotService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<ConversacionChatbot> actualizar(@PathVariable Long id, @RequestBody ConversacionChatbotDTO conversacionChatbotDTO) {
        return ResponseEntity.ok(conversacionChatbotService.actualizar(id, conversacionChatbotDTO));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        conversacionChatbotService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
