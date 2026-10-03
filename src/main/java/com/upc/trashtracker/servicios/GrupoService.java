package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.GrupoDTO;
import com.upc.trashtracker.entidades.Grupo;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.GrupoRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class GrupoService {

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Grupo guardar(GrupoDTO grupoDTO) {
        Grupo grupo = new Grupo();
        grupo.setNombre(grupoDTO.getNombre());
        grupo.setZona(grupoDTO.getZona());

        if (grupoDTO.getCreadorId() != null) {
            Usuario creador = usuarioRepository.findById(grupoDTO.getCreadorId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + grupoDTO.getCreadorId()));
            grupo.setCreador(creador);
        }
        return grupoRepository.save(grupo);
    }

    public List<Grupo> listarTodos() {
        return grupoRepository.findAll();
    }

    public Optional<Grupo> buscarPorId(Long id) {
        return grupoRepository.findById(id);
    }

    @Transactional
    public Grupo actualizar(Long id, GrupoDTO grupoDTO) {
        return grupoRepository.findById(id).map(existente -> {
            existente.setNombre(grupoDTO.getNombre());
            existente.setZona(grupoDTO.getZona());
            if (grupoDTO.getCreadorId() != null) {
                Usuario creador = usuarioRepository.findById(grupoDTO.getCreadorId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + grupoDTO.getCreadorId()));
                existente.setCreador(creador);
            }
            return grupoRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        grupoRepository.deleteById(id);
    }
}
