package com.upc.trashtracker.security.services;

import com.upc.trashtracker.entidades.Usuario;
import com.upc.trashtracker.security.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = userRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (usuario.getRol() != null && usuario.getRol().getNombreRol() != null) {
            authorities.add(new SimpleGrantedAuthority(toAuthority(usuario.getRol().getNombreRol())));
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getCorreo())
                .password(Objects.requireNonNullElse(usuario.getContrasenaHash(), ""))
                .authorities(authorities)
                .build();
    }

    private String toAuthority(String nombreRol) {
        String limpio = Normalizer.normalize(nombreRol.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("\\s+", "_")
                .toUpperCase();
        return "ROLE_" + limpio;
    }
}
