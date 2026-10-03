package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.RecompensaDTO;
import com.upc.trashtracker.entidades.Recompensa;
import com.upc.trashtracker.repositorio.RecompensaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RecompensaService {

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Transactional
    public Recompensa guardar(RecompensaDTO recompensaDTO) {
        Recompensa recompensa = new Recompensa();
        recompensa.setNombre(recompensaDTO.getNombre());
        recompensa.setCostoPuntos(recompensaDTO.getCostoPuntos());
        recompensa.setStock(recompensaDTO.getStock());
        return recompensaRepository.save(recompensa);
    }

    public List<Recompensa> listarTodos() {
        return recompensaRepository.findAll();
    }

    public Optional<Recompensa> buscarPorId(Long id) {
        return recompensaRepository.findById(id);
    }

    @Transactional
    public Optional<Recompensa> actualizar(Long id, RecompensaDTO recompensaDTO) {
        return recompensaRepository.findById(id).map(existente -> {
            existente.setNombre(recompensaDTO.getNombre());
            existente.setCostoPuntos(recompensaDTO.getCostoPuntos());
            existente.setStock(recompensaDTO.getStock());
            return recompensaRepository.save(existente);
        });
    }

    @Transactional
    public void eliminar(Long id) {
        recompensaRepository.deleteById(id);
    }
}
