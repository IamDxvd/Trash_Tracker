package com.upc.trashtracker.servicios;

import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario guardar(Usuario usuario) {
        usuario.setContrasenaHash(encriptar(usuario.getContrasenaHash()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public Usuario actualizarPerfil(Long id, String nombre, String idiomaPreferido) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("No existe el usuario " + id));
        if (nombre != null && !nombre.isBlank()) u.setNombre(nombre);
        if (idiomaPreferido != null) u.setIdiomaPreferido(idiomaPreferido);
        return usuarioRepository.save(u);
    }

    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    // Si ya viene en BCrypt se deja igual; si es texto plano se encripta
    private String encriptar(String contrasena) {
        if (contrasena == null || contrasena.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$")) {
            return contrasena;
        }
        return passwordEncoder.encode(contrasena);
    }
}
