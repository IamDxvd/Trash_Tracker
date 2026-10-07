package com.upc.trashtracker.controladores;
import com.upc.trashtracker.dto.RecuperarContrasenaDTO;
import com.upc.trashtracker.servicios.CuentaSeguridadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.Map;

@RestController @RequestMapping("/api/usuario")
public class CuentaSeguridadController {
    private final CuentaSeguridadService cuentas;
    public CuentaSeguridadController(CuentaSeguridadService cuentas) { this.cuentas = cuentas; }
    public record RestablecerRequest(String token, String contrasena) {}
    @PostMapping("/recuperar-contrasena")
    public Map<String, String> solicitar(@RequestBody RecuperarContrasenaDTO dto) {
        cuentas.solicitarRecuperacionContrasena(dto.getCorreo());
        return Map.of("mensaje", "Si el correo esta registrado, recibiras un enlace de recuperacion");
    }
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Void> restablecer(@RequestBody RestablecerRequest dto) {
        cuentas.restablecerContrasena(dto.token(), dto.contrasena()); return ResponseEntity.noContent().build();
    }
    @PostMapping("/{id}/cerrar-sesion")
    @PreAuthorize("hasAnyRole('CIUDADANO', 'ADMINISTRADOR')")
    public ResponseEntity<Void> cerrar(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        cuentas.cerrarSesion(id, token); return ResponseEntity.noContent().build();
    }
}
