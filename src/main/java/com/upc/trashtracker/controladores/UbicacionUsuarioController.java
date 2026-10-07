package com.upc.trashtracker.controladores;
import com.upc.trashtracker.dto.UbicacionDTO;
import com.upc.trashtracker.servicios.UbicacionUsuarioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/usuario")
@PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
public class UbicacionUsuarioController {
    private final UbicacionUsuarioService ubicaciones;
    public UbicacionUsuarioController(UbicacionUsuarioService ubicaciones) { this.ubicaciones = ubicaciones; }
    @GetMapping("/obtener-ubicacion/{id}")
    public UbicacionDTO obtener(@PathVariable Long id) { return ubicaciones.obtenerUbicacionMapa(id); }
    @PutMapping("/actualizar-ubicacion")
    public UbicacionDTO actualizar(@RequestBody UbicacionDTO dto) { return ubicaciones.actualizarUbicacionMapa(dto); }
}
