package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.ConversacionChatbotDTO;
import com.upc.trashtracker.entidades.ConversacionChatbot;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.ConversacionChatbotRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversacionChatbotService {

    @Autowired
    private ConversacionChatbotRepository conversacionChatbotRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public ConversacionChatbot guardar(ConversacionChatbotDTO conversacionChatbotDTO) {
        ConversacionChatbot conversacionChatbot = new ConversacionChatbot();
        conversacionChatbot.setFechaInicio(conversacionChatbotDTO.getFechaInicio() != null ? conversacionChatbotDTO.getFechaInicio() : LocalDateTime.now());

        if (conversacionChatbotDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(conversacionChatbotDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + conversacionChatbotDTO.getUsuarioId()));
            conversacionChatbot.setUsuario(usuario);
        }
        return conversacionChatbotRepository.save(conversacionChatbot);
    }

    public List<ConversacionChatbot> listarTodos() {
        return conversacionChatbotRepository.findAll();
    }

    public Optional<ConversacionChatbot> buscarPorId(Long id) {
        return conversacionChatbotRepository.findById(id);
    }

    @Transactional
    public ConversacionChatbot actualizar(Long id, ConversacionChatbotDTO conversacionChatbotDTO) {
        return conversacionChatbotRepository.findById(id).map(existente -> {
            if (conversacionChatbotDTO.getFechaInicio() != null) {
                existente.setFechaInicio(conversacionChatbotDTO.getFechaInicio());
            }
            if (conversacionChatbotDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(conversacionChatbotDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + conversacionChatbotDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return conversacionChatbotRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Conversación no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        conversacionChatbotRepository.deleteById(id);
    }
}
