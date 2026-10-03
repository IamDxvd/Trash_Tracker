package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.DispositivoConocidoDTO;
import com.upc.trashtracker.entidades.DispositivoConocido;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.DispositivoConocidoRepository;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DispositivoConocidoService {

    @Autowired
    private DispositivoConocidoRepository dispositivoConocidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public DispositivoConocido guardar(DispositivoConocidoDTO dispositivoConocidoDTO) {
        DispositivoConocido dispositivoConocido = new DispositivoConocido();
        dispositivoConocido.setTipoDispositivo(dispositivoConocidoDTO.getTipoDispositivo());
        dispositivoConocido.setFechaPrimerAcceso(dispositivoConocidoDTO.getFechaPrimerAcceso() != null ? dispositivoConocidoDTO.getFechaPrimerAcceso() : LocalDate.now());

        if (dispositivoConocidoDTO.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dispositivoConocidoDTO.getUsuarioId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dispositivoConocidoDTO.getUsuarioId()));
            dispositivoConocido.setUsuario(usuario);
        }
        return dispositivoConocidoRepository.save(dispositivoConocido);
    }

    public List<DispositivoConocido> listarTodos() {
        return dispositivoConocidoRepository.findAll();
    }

    public Optional<DispositivoConocido> buscarPorId(Long id) {
        return dispositivoConocidoRepository.findById(id);
    }

    @Transactional
    public DispositivoConocido actualizar(Long id, DispositivoConocidoDTO dispositivoConocidoDTO) {
        return dispositivoConocidoRepository.findById(id).map(existente -> {
            existente.setTipoDispositivo(dispositivoConocidoDTO.getTipoDispositivo());
            if (dispositivoConocidoDTO.getFechaPrimerAcceso() != null) {
                existente.setFechaPrimerAcceso(dispositivoConocidoDTO.getFechaPrimerAcceso());
            }
            if (dispositivoConocidoDTO.getUsuarioId() != null) {
                Usuario usuario = usuarioRepository.findById(dispositivoConocidoDTO.getUsuarioId())
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dispositivoConocidoDTO.getUsuarioId()));
                existente.setUsuario(usuario);
            }
            return dispositivoConocidoRepository.save(existente);
        }).orElseThrow(() -> new RuntimeException("Dispositivo conocido no encontrado con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        dispositivoConocidoRepository.deleteById(id);
    }
}
