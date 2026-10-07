package com.upc.trashtracker.controladores;
import com.upc.trashtracker.dto.*;
import com.upc.trashtracker.servicios.UsuarioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
@RestController
@RequestMapping("/api/usuario")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class UsuarioFuncionesController {
    private final UsuarioService usuarios;
    public UsuarioFuncionesController(UsuarioService usuarios) { this.usuarios = usuarios; }
    @GetMapping("/{id}/estadisticas")
    public UsuarioEstadisticasDTO estadisticas(@PathVariable Long id) { return usuarios.obtenerEstadisticas(id); }
    @PostMapping("/actualizar-nivel/{id}")
    public NivelUsuarioDTO nivel(@PathVariable Long id) { return usuarios.actualizarNivel(id); }
    @GetMapping("/ver-ranking-usuario")
    public List<RankingUsuarioDTO> ranking() { return usuarios.obtenerRanking(); }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCuenta(@PathVariable Long id) {
        usuarios.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }
}
