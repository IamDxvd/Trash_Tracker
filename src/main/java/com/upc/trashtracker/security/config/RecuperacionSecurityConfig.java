package com.upc.trashtracker.security.config;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class RecuperacionSecurityConfig {
    @Bean @Order(1)
    SecurityFilterChain recuperacionFilterChain(HttpSecurity http) throws Exception {
        return http.securityMatcher("/api/usuario/recuperar-contrasena", "/api/usuario/restablecer-contrasena")
                .csrf(AbstractHttpConfigurer::disable).cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a.anyRequest().permitAll()).build();
    }
}
