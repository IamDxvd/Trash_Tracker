package com.upc.trashtracker.security.controllers;

import com.upc.trashtracker.entidades.Rol;
import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.repositorio.RolRepository;
import com.upc.trashtracker.security.dtos.AuthRequestDTO;
import com.upc.trashtracker.security.dtos.AuthResponseDTO;
import com.upc.trashtracker.security.dtos.RegistroDTO;
import com.upc.trashtracker.security.repositories.UserRepository;
import com.upc.trashtracker.security.services.CustomUserDetailsService;
import com.upc.trashtracker.security.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          CustomUserDetailsService userDetailsService, UserRepository userRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequestDTO authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getCorreo(), authRequest.getContrasena()));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Correo o contrasena incorrectos"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getCorreo());
        String token = jwtUtil.generateToken(userDetails);

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, token)
                .body(new AuthResponseDTO(token, roles));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(Authentication authentication) {
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(Map.of("correo", authentication.getName(), "roles", roles));
    }

    @PostMapping("/usuario/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()
                || dto.getCorreo() == null || dto.getCorreo().isBlank()
                || dto.getContrasena() == null || dto.getContrasena().length() < 8) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Nombre, correo y contraseña (mínimo 8 caracteres) son obligatorios"));
        }
        if (userRepository.findByCorreo(dto.getCorreo()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "El correo ya está registrado"));
        }
        Rol ciudadano = rolRepository.findByNombreRol("Ciudadano").orElseThrow();
        Usuario u = new Usuario();
        u.setNombre(dto.getNombre());
        u.setCorreo(dto.getCorreo());
        u.setContrasenaHash(passwordEncoder.encode(dto.getContrasena()));
        u.setRol(ciudadano);
        u.setNivel(1);
        u.setPuntosTotales(0);
        Usuario guardado = userRepository.save(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "idUsuario", guardado.getIdUsuario(),
                "nombre", guardado.getNombre(),
                "correo", guardado.getCorreo()));
    }
}
