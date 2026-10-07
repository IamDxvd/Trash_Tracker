package com.upc.trashtracker.servicios;

import com.upc.trashtracker.dto.NivelUsuarioDTO;
import com.upc.trashtracker.dto.RankingUsuarioDTO;
import com.upc.trashtracker.dto.RecuperarContrasenaDTO;
import com.upc.trashtracker.dto.UbicacionDTO;
import com.upc.trashtracker.dto.UsuarioEstadisticasDTO;
import java.util.ArrayList;
import java.util.stream.Collectors;
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

    // Funciones adicionales integradas del backend unificado.
    @org.springframework.security.access.prepost.PreAuthorize("@accesoIntegracion.esPropietario(#idUsuario)")
    @Transactional(readOnly = true)
    public UsuarioEstadisticasDTO obtenerEstadisticas(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        int totalReportes = usuario.getReportes() != null ? usuario.getReportes().size() : 0;
        int totalEventos = usuario.getAsistencias() != null ? usuario.getAsistencias().size() : 0;

        return new UsuarioEstadisticasDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getPuntosTotales(),
                usuario.getNivel(),
                totalReportes,
                totalEventos
        );
    }

    @Transactional
    @org.springframework.security.access.prepost.PreAuthorize("@accesoIntegracion.esPropietario(#idUsuario)")
    public NivelUsuarioDTO actualizarNivel(Long idUsuario) {
        Usuario usuario = usuarioRepository.buscarParaActualizar(idUsuario)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + idUsuario));

        int puntos = usuario.getPuntosTotales() != null ? usuario.getPuntosTotales() : 0;
        int nuevoNivel = calcularNivelPorPuntos(puntos);

        usuario.setNivel(nuevoNivel);
        usuarioRepository.save(usuario);

        NivelUsuarioDTO response = new NivelUsuarioDTO();
        response.setIdUsuario(usuario.getIdUsuario());
        response.setNombre(usuario.getNombre());
        response.setPuntosTotales(puntos);
        response.setNivelActual(nuevoNivel);
        response.setRangoNombre(obtenerNombreRango(nuevoNivel));
        response.setPuntosSiguienteNivel(obtenerPuntosSiguienteNivel(nuevoNivel));

        return response;
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
    public List<RankingUsuarioDTO> obtenerRanking() {
        List<Usuario> usuariosOrdenados = usuarioRepository.findAllByOrderByPuntosTotalesDesc();
        List<RankingUsuarioDTO> rankingList = new ArrayList<>();

        int posicion = 1;
        for (Usuario u : usuariosOrdenados) {
            RankingUsuarioDTO dto = new RankingUsuarioDTO();
            dto.setPosicion(posicion++);
            dto.setIdUsuario(u.getIdUsuario());
            dto.setNombre(u.getNombre());
            dto.setPuntosTotales(u.getPuntosTotales() != null ? u.getPuntosTotales() : 0);
            dto.setNivel(u.getNivel() != null ? u.getNivel() : 1);
            rankingList.add(dto);
        }

        return rankingList;
    }

    public int calcularNivelPorPuntos(int puntos) {
        if (puntos < 100) return 1;       // Nivel 1: Principiante
        if (puntos < 300) return 2;       // Nivel 2: Eco-Activista
        if (puntos < 600) return 3;       // Nivel 3: Protector Verde
        if (puntos < 1000) return 4;      // Nivel 4: Guardián Ambiental
        return 5;                          // Nivel 5: Héroe Reciclador
    }

    private String obtenerNombreRango(int nivel) {
        return switch (nivel) {
            case 1 -> "Principiante Eco";
            case 2 -> "Eco-Activista";
            case 3 -> "Protector Verde";
            case 4 -> "Guardián Ambiental";
            default -> "Héroe Reciclador";
        };
    }

    private Integer obtenerPuntosSiguienteNivel(int nivel) {
        return switch (nivel) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 600;
            case 4 -> 1000;
            default -> 0; // Nivel máximo alcanzado
        };
    }

    @Transactional
    @org.springframework.security.access.prepost.PreAuthorize("@accesoIntegracion.esPropietario(#idUsuario)")
    public void eliminarCuenta(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        usuarioRepository.deleteById(idUsuario);
    }
}
