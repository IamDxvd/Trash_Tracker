package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.PreguntaFrecuenteDTO;
import com.upc.trashtracker.entidades.PreguntaFrecuente;
import com.upc.trashtracker.repositorio.PreguntaFrecuenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PreguntaFrecuenteService {

    @Autowired
    private PreguntaFrecuenteRepository preguntaFrecuenteRepository;

    @Transactional
    public PreguntaFrecuente guardar(PreguntaFrecuenteDTO preguntaFrecuenteDTO) {
        PreguntaFrecuente preguntaFrecuente = new PreguntaFrecuente();
        preguntaFrecuente.setPregunta(preguntaFrecuenteDTO.getPregunta());
        preguntaFrecuente.setRespuesta(preguntaFrecuenteDTO.getRespuesta());
        preguntaFrecuente.setCategoria(preguntaFrecuenteDTO.getCategoria());
        return preguntaFrecuenteRepository.save(preguntaFrecuente);
    }

    public List<PreguntaFrecuente> listarTodos() {
        return preguntaFrecuenteRepository.findAll();
    }

    public Optional<PreguntaFrecuente> buscarPorId(Long id) {
        return preguntaFrecuenteRepository.findById(id);
    }

    @Transactional
    public PreguntaFrecuente actualizar(Long id, PreguntaFrecuenteDTO preguntaFrecuenteDTO) {
        return preguntaFrecuenteRepository.findById(id).map(existente -> {
            existente.setPregunta(preguntaFrecuenteDTO.getPregunta());
            existente.setRespuesta(preguntaFrecuenteDTO.getRespuesta());
            existente.setCategoria(preguntaFrecuenteDTO.getCategoria());
            return preguntaFrecuenteRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Pregunta frecuente no encontrada con ID: " +id));
    }

    @Transactional
    public void eliminar(Long id) {
        preguntaFrecuenteRepository.deleteById(id);
    }
}
