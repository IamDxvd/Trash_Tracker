package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.LogroDTO;
import com.upc.trashtracker.entidades.Logro;
import com.upc.trashtracker.repositorio.LogroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LogroService {

    @Autowired
    private LogroRepository logroRepository;

    @Transactional
    public Logro guardar(LogroDTO logroDTO) {
        Logro logro = new Logro();
        logro.setNombre(logroDTO.getNombre());
        logro.setCriterio(logroDTO.getCriterio());
        return logroRepository.save(logro);
    }

    public List<Logro> listarTodos() {
        return logroRepository.findAll();
    }

    public Optional<Logro> buscarPorId(Long id) {
        return logroRepository.findById(id);
    }

    @Transactional
    public Logro actualizar(Long id, LogroDTO logroDTO) {
        return logroRepository.findById(id).map(existente -> {
            existente.setNombre(logroDTO.getNombre());
            existente.setCriterio(logroDTO.getCriterio());
            return logroRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Logro no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        logroRepository.deleteById(id);
    }
}
