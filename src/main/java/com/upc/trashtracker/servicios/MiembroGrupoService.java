package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.MiembroGrupoDTO;
import com.upc.trashtracker.entidades.Grupo;
import com.upc.trashtracker.entidades.MiembroGrupo;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.GrupoRepository;
import com.upc.trashtracker.repositorio.MiembroGrupoRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MiembroGrupoService {

    @Autowired
    private MiembroGrupoRepository miembroGrupoRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public MiembroGrupo guardar(MiembroGrupoDTO miembroGrupoDTO) {
        MiembroGrupo miembroGrupo = new MiembroGrupo();
        miembroGrupo.setRolEnGrupo(miembroGrupoDTO.getRolEnGrupo());

        if (miembroGrupoDTO.getGrupoId() != null) {
            Grupo grupo = grupoRepository.findById(miembroGrupoDTO.getGrupoId())
                    .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + miembroGrupoDTO.getGrupoId()));
            miembroGrupo.setGrupo(grupo);
        }
        if (miembroGrupoDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(miembroGrupoDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + miembroGrupoDTO.getUsuarioId()));
            miembroGrupo.setUsuario(usuario);
        }
        return miembroGrupoRepository.save(miembroGrupo);
    }

    public List<MiembroGrupo> listarTodos() {
        return miembroGrupoRepository.findAll();
    }

    public Optional<MiembroGrupo> buscarPorId(Long id) {
        return miembroGrupoRepository.findById(id);
    }

    @Transactional
    public MiembroGrupo actualizar(Long id, MiembroGrupoDTO miembroGrupoDTO) {
        return miembroGrupoRepository.findById(id).map(existente -> {
            existente.setRolEnGrupo(miembroGrupoDTO.getRolEnGrupo());
            if (miembroGrupoDTO.getGrupoId() != null) {
                Grupo grupo = grupoRepository.findById(miembroGrupoDTO.getGrupoId())
                        .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + miembroGrupoDTO.getGrupoId()));
                existente.setGrupo(grupo);
            }
            if (miembroGrupoDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(miembroGrupoDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + miembroGrupoDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return miembroGrupoRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Miembro de grupo no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        miembroGrupoRepository.deleteById(id);
    }
}
