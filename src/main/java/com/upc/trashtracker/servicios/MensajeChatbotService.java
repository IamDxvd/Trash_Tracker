package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.MensajeChatbotDTO;
import com.upc.trashtracker.entidades.ConversacionChatbot;
import com.upc.trashtracker.entidades.MensajeChatbot;
import com.upc.trashtracker.repositorio.ConversacionChatbotRepository;
import com.upc.trashtracker.repositorio.MensajeChatbotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MensajeChatbotService {

    @Autowired
    private MensajeChatbotRepository mensajeChatbotRepository;

    @Autowired
    private ConversacionChatbotRepository conversacionChatbotRepository;

    @Transactional
    public MensajeChatbot guardar(MensajeChatbotDTO mensajeChatbotDTO) {
        MensajeChatbot mensajeChatbot = new MensajeChatbot();
        mensajeChatbot.setEmisor(mensajeChatbotDTO.getEmisor());
        mensajeChatbot.setContenido(mensajeChatbotDTO.getContenido());
        mensajeChatbot.setFechaHora(mensajeChatbotDTO.getFechaHora() != null ? mensajeChatbotDTO.getFechaHora() : LocalDateTime.now());

        if (mensajeChatbotDTO.getConversacionId() != null) {
            ConversacionChatbot conversacion = conversacionChatbotRepository.findById(mensajeChatbotDTO.getConversacionId())
                    .orElseThrow(() -> new RuntimeException("Conversación no encontrada con ID: " + mensajeChatbotDTO.getConversacionId()));
            mensajeChatbot.setConversacion(conversacion);
        }
        return mensajeChatbotRepository.save(mensajeChatbot);
    }

    public List<MensajeChatbot> listarTodos() {
        return mensajeChatbotRepository.findAll();
    }

    public Optional<MensajeChatbot> buscarPorId(Long id) {
        return mensajeChatbotRepository.findById(id);
    }

    @Transactional
    public MensajeChatbot actualizar(Long id, MensajeChatbotDTO mensajeChatbotDTO) {
        return mensajeChatbotRepository.findById(id).map(existente -> {
            existente.setEmisor(mensajeChatbotDTO.getEmisor());
            existente.setContenido(mensajeChatbotDTO.getContenido());
            if (mensajeChatbotDTO.getFechaHora() != null) {
                existente.setFechaHora(mensajeChatbotDTO.getFechaHora());
            }
            if (mensajeChatbotDTO.getConversacionId() != null) {
                ConversacionChatbot conversacion = conversacionChatbotRepository.findById(mensajeChatbotDTO.getConversacionId())
                        .orElseThrow(() -> new RuntimeException("Conversación no encontrada con ID: " + mensajeChatbotDTO.getConversacionId()));
                existente.setConversacion(conversacion);
            }
            return mensajeChatbotRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Mensaje de chatbot no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        mensajeChatbotRepository.deleteById(id);
    }
}
