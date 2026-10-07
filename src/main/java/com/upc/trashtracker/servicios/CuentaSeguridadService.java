package com.upc.trashtracker.servicios;

import com.upc.trashtracker.entidades.*;
import com.upc.trashtracker.repositorio.*;
import com.upc.trashtracker.security.util.JwtUtil;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
public class CuentaSeguridadService {
    private final UsuarioRepository usuarios;
    private final TokenRecuperacionRepository recuperaciones;
    private final TokenRevocadoRepository revocados;
    private final EstadoSeguridadUsuarioRepository estados;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;
    private final ObjectProvider<JavaMailSender> correo;
    @Value("${app.recovery.url:}") private String recoveryUrl;
    @Value("${app.recovery.from:}") private String from;

    public CuentaSeguridadService(UsuarioRepository usuarios, TokenRecuperacionRepository recuperaciones,
            TokenRevocadoRepository revocados, EstadoSeguridadUsuarioRepository estados,
            PasswordEncoder encoder, JwtUtil jwt, ObjectProvider<JavaMailSender> correo) {
        this.usuarios = usuarios; this.recuperaciones = recuperaciones; this.revocados = revocados;
        this.estados = estados; this.encoder = encoder; this.jwt = jwt; this.correo = correo;
    }

    @Transactional
    public void solicitarRecuperacionContrasena(String direccion) {
        if (direccion == null || !direccion.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correo invalido");
        }
        JavaMailSender sender = correo.getIfAvailable();
        if (sender == null || !recoveryUrl.startsWith("https://") || from.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Recuperacion por correo no configurada");
        }
        usuarios.findByCorreo(direccion.trim()).ifPresent(usuario -> {
            usuarios.buscarParaActualizar(usuario.getIdUsuario()).orElseThrow();
            recuperaciones.deleteByUsuarioId(usuario.getIdUsuario());
            byte[] bytes = new byte[32]; new SecureRandom().nextBytes(bytes);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            TokenRecuperacion registro = new TokenRecuperacion();
            registro.setHash(hash(token)); registro.setUsuarioId(usuario.getIdUsuario());
            registro.setExpira(Instant.now().plusSeconds(900)); recuperaciones.save(registro);
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom(from); mensaje.setTo(usuario.getCorreo()); mensaje.setSubject("Restablecer contrasena de Trash Tracker");
            mensaje.setText("Enlace valido durante 15 minutos: " + recoveryUrl + (recoveryUrl.contains("?") ? "&" : "?") + "token=" + token);
            sender.send(mensaje);
        });
    }

    @Transactional
    public void restablecerContrasena(String token, String contrasena) {
        if (token == null || token.length() != 43 || contrasena == null || contrasena.length() < 12
                || contrasena.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token o contrasena invalida (12 caracteres minimo, 72 bytes maximo)");
        }
        // Acquire the user lock before token lock, in the same order as token issuance.
        TokenRecuperacion candidato = recuperaciones.findById(hash(token)).orElseThrow(CuentaSeguridadService::tokenInvalido);
        Usuario usuario = usuarios.buscarParaActualizar(candidato.getUsuarioId()).orElseThrow(CuentaSeguridadService::tokenInvalido);
        TokenRecuperacion registro = recuperaciones.bloquear(hash(token)).orElseThrow(CuentaSeguridadService::tokenInvalido);
        if (!registro.getExpira().isAfter(Instant.now())) throw tokenInvalido();
        usuario.setContrasenaHash(encoder.encode(contrasena)); usuarios.save(usuario);
        EstadoSeguridadUsuario estado = new EstadoSeguridadUsuario();
        estado.setUsuarioId(usuario.getIdUsuario()); estado.setTokensInvalidosHasta(Instant.now()); estados.save(estado);
        recuperaciones.deleteByUsuarioId(usuario.getIdUsuario());
    }

    @Transactional
    @PreAuthorize("@accesoIntegracion.esPropietario(#idUsuario)")
    public void cerrarSesion(Long idUsuario, String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) throw tokenInvalido();
        String token = authorization.substring(7);
        TokenRevocado registro = new TokenRevocado(); registro.setHash(hash(token));
        registro.setExpira(jwt.extractExpiration(token).toInstant()); revocados.save(registro);
    }

    public boolean estaRevocado(String token) {
        if (revocados.existsById(hash(token))) return true;
        return usuarios.findByCorreo(jwt.extractUsername(token)).flatMap(u -> estados.findById(u.getIdUsuario()))
                .map(e -> {
                    java.util.Date fecha = jwt.extractClaim(token, io.jsonwebtoken.Claims::getIssuedAt);
                    return fecha == null || !fecha.toInstant().isAfter(e.getTokensInvalidosHasta());
                }).orElse(false);
    }
    public static String hash(String texto) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(texto.getBytes(StandardCharsets.UTF_8))); }
        catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }
    private static ResponseStatusException tokenInvalido() {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalido o expirado");
    }
}
