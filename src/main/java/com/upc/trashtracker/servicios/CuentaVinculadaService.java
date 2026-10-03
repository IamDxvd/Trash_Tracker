package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.CuentaVinculadaDTO;
import com.upc.trashtracker.entidades.CuentaVinculada;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.CuentaVinculadaRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CuentaVinculadaService {

    @Autowired
    private CuentaVinculadaRepository cuentaVinculadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public CuentaVinculada guardar(CuentaVinculadaDTO cuentaVinculadaDTO) {
        CuentaVinculada cuentaVinculada = new CuentaVinculada();
        cuentaVinculada.setProveedor(cuentaVinculadaDTO.getProveedor());
        cuentaVinculada.setIdExterno(cuentaVinculadaDTO.getIdExterno());

        if (cuentaVinculadaDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(cuentaVinculadaDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + cuentaVinculadaDTO.getUsuarioId()));
            cuentaVinculada.setUsuario(usuario);
        }
        return cuentaVinculadaRepository.save(cuentaVinculada);
    }

    public List<CuentaVinculada> listarTodos() {
        return cuentaVinculadaRepository.findAll();
    }

    public Optional<CuentaVinculada> buscarPorId(Long id) {
        return cuentaVinculadaRepository.findById(id);
    }

    @Transactional
    public CuentaVinculada actualizar(Long id, CuentaVinculadaDTO cuentaVinculadaDTO) {
        return cuentaVinculadaRepository.findById(id).map(existente -> {
            existente.setProveedor(cuentaVinculadaDTO.getProveedor());
            existente.setIdExterno(cuentaVinculadaDTO.getIdExterno());
            if (cuentaVinculadaDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(cuentaVinculadaDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + cuentaVinculadaDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return cuentaVinculadaRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Cuenta vinculada no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        cuentaVinculadaRepository.deleteById(id);
    }
}
