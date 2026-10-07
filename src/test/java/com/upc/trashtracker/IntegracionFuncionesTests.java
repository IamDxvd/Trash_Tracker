package com.upc.trashtracker;

import com.upc.trashtracker.dto.*;
import com.upc.trashtracker.entidades.*;
import com.upc.trashtracker.repositorio.*;
import com.upc.trashtracker.servicios.*;
import com.upc.trashtracker.security.util.JwtUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:integracion;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect", "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.mode=never", "springdoc.api-docs.enabled=false", "springdoc.swagger-ui.enabled=false",
    "jwt.secret=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA==",
    "ip.frontend=http://localhost:4200"
})
@Transactional
class IntegracionFuncionesTests {
    @Autowired UsuarioRepository usuarios;
    @Autowired GrupoRepository grupos;
    @Autowired MiembroGrupoRepository miembros;
    @Autowired RecompensaRepository recompensas;
    @Autowired HistorialPuntosRepository historiales;
    @Autowired CanjeRepository canjes;
    @Autowired EventoRepository eventos;
    @Autowired NotificacionRepository notificaciones;
    @Autowired TokenRecuperacionRepository recuperaciones;
    @Autowired GrupoComunidadService comunidad;
    @Autowired EventoComunidadService comunidadEventos;
    @Autowired CanjeService canje;
    @Autowired HistorialPuntosService puntos;
    @Autowired UsuarioService usuarioService;
    @Autowired UbicacionUsuarioService ubicacion;
    @Autowired CuentaSeguridadService cuentas;
    @Autowired FotoComunidadStorage fotos;
    @Autowired PasswordEncoder encoder;
    @Autowired JwtUtil jwt;
    @Autowired RolRepository roles;
    @Autowired org.springframework.web.context.WebApplicationContext web;
    @Autowired com.upc.trashtracker.security.filters.RevocacionTokenFilter revocacionFilter;
    Usuario yo, otro;
    Grupo grupo;

    @BeforeEach void preparar() {
        yo = nuevoUsuario("yo@test.local", 200); otro = nuevoUsuario("otro@test.local", 100);
        grupo = new Grupo(); grupo.setNombre("Comunidad"); grupo.setCreador(yo); grupo = grupos.save(grupo);
        autenticar(yo, "CIUDADANO");
    }
    @AfterEach void limpiar() { SecurityContextHolder.clearContext(); }
    Usuario nuevoUsuario(String correo, int saldo) {
        Usuario u = new Usuario(); u.setCorreo(correo); u.setNombre(correo); u.setPuntosTotales(saldo);
        u.setNivel(1); u.setContrasenaHash(encoder.encode("Password123456")); return usuarios.save(u);
    }
    void autenticar(Usuario u, String rol) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(u.getCorreo(), "",
                List.of(new SimpleGrantedAuthority("ROLE_" + rol))));
    }
    Recompensa recompensa(int stock, int costo) {
        Recompensa r = new Recompensa(); r.setNombre("Bolsa"); r.setStock(stock); r.setCostoPuntos(costo); return recompensas.save(r);
    }
    CanjeRequestDTO solicitud(Recompensa r) {
        CanjeRequestDTO d = new CanjeRequestDTO(); d.setIdUsuario(yo.getIdUsuario()); d.setIdRecompensa(r.getIdRecompensa()); return d;
    }
    @Test void unirseRechazaSuplantacionYDuplicados() {
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> comunidad.unirse(grupo.getIdGrupo(), otro.getIdUsuario())).getStatusCode().value());
        comunidad.unirse(grupo.getIdGrupo(), yo.getIdUsuario());
        assertEquals(409, assertThrows(ResponseStatusException.class, () -> comunidad.unirse(grupo.getIdGrupo(), yo.getIdUsuario())).getStatusCode().value());
    }
    @Test void chatSoloMiembrosYAutorAutenticado() {
        MensajeRequestDTO d = new MensajeRequestDTO(); d.setUsuarioId(yo.getIdUsuario()); d.setContenido("Hola");
        assertThrows(ResponseStatusException.class, () -> comunidad.enviarMensaje(grupo.getIdGrupo(), d));
        comunidad.unirse(grupo.getIdGrupo(), yo.getIdUsuario());
        comunidad.enviarMensaje(grupo.getIdGrupo(), d);
        assertEquals(1, comunidad.listarMensajes(grupo.getIdGrupo(), yo.getIdUsuario()).size());
        d.setUsuarioId(otro.getIdUsuario()); assertThrows(ResponseStatusException.class, () -> comunidad.enviarMensaje(grupo.getIdGrupo(), d));
    }
    @Test void rolRequiereOrganizadorReal() {
        comunidad.unirse(grupo.getIdGrupo(), yo.getIdUsuario());
        autenticar(otro, "CIUDADANO"); comunidad.unirse(grupo.getIdGrupo(), otro.getIdUsuario());
        AsignarRolRequestDTO d = new AsignarRolRequestDTO(); d.setSolicitanteId(yo.getIdUsuario()); d.setRol("ORGANIZADOR");
        assertThrows(ResponseStatusException.class, () -> comunidad.asignarRol(grupo.getIdGrupo(), otro.getIdUsuario(), d));
        d.setSolicitanteId(otro.getIdUsuario());
        assertThrows(ResponseStatusException.class, () -> comunidad.asignarRol(grupo.getIdGrupo(), otro.getIdUsuario(), d));
    }
    @Test void canjeDescuentaStockSaldoEHistorial() {
        Recompensa r = recompensa(1, 80); CanjeRequestDTO d = solicitud(r);
        assertEquals(120, canje.realizarCanje(d).getPuntosRestantesUsuario());
        assertEquals(0, recompensas.findById(r.getIdRecompensa()).orElseThrow().getStock());
        assertEquals(-80, historiales.findAll().getFirst().getCantidad()); assertEquals(1, canjes.count());
        assertThrows(RuntimeException.class, () -> canje.realizarCanje(d));
        assertEquals(1, canjes.count());
    }
    @Test void canjeAjenoEsDenegado() {
        CanjeRequestDTO d = solicitud(recompensa(1,80)); d.setIdUsuario(otro.getIdUsuario());
        assertThrows(AccessDeniedException.class, () -> canje.realizarCanje(d)); assertEquals(0, canjes.count());
    }
    @Test void puntosSoloAdminYValidos() {
        PuntosRequestDTO d = new PuntosRequestDTO(); d.setIdUsuario(yo.getIdUsuario()); d.setPuntos(150); d.setMotivo("Limpieza");
        assertThrows(AccessDeniedException.class, () -> puntos.insertarPuntos(d));
        autenticar(yo,"ADMINISTRADOR"); assertEquals(350,puntos.insertarPuntos(d).getPuntosTotalesUsuario());
        assertEquals(3,usuarios.findById(yo.getIdUsuario()).orElseThrow().getNivel());
        d.setPuntos(-20); assertThrows(ResponseStatusException.class, () -> puntos.insertarPuntos(d));
    }
    @Test void notificarSoloOrganizadorYAsistenciaIdempotente() {
        EventoRequestDTO d = new EventoRequestDTO(); d.setGrupoId(grupo.getIdGrupo()); d.setOrganizadorId(yo.getIdUsuario());
        d.setFecha(LocalDate.now().plusDays(1)); d.setUbicacion("Parque");
        Long id = comunidadEventos.crearEvento(d).getIdEvento();
        autenticar(otro,"CIUDADANO"); comunidad.unirse(grupo.getIdGrupo(),otro.getIdUsuario());
        assertThrows(ResponseStatusException.class, () -> comunidadEventos.notificarNuevoEvento(id));
        AsistenciaRequestDTO asistencia = new AsistenciaRequestDTO(); asistencia.setUsuarioId(otro.getIdUsuario()); asistencia.setConfirmado(true);
        var primera = comunidadEventos.registrarAsistencia(id,asistencia);
        assertEquals(primera.getIdAsistenciaEvento(),comunidadEventos.registrarAsistencia(id,asistencia).getIdAsistenciaEvento());
        autenticar(yo,"CIUDADANO"); comunidadEventos.notificarNuevoEvento(id); assertEquals(1,notificaciones.count());
    }
    @Test void ubicacionPersisteYNoAdmiteUsuarioAjeno() {
        UbicacionDTO d = new UbicacionDTO(yo.getIdUsuario(), -12.0,-77.0,null);
        ubicacion.actualizarUbicacionMapa(d); assertEquals(-12.0,ubicacion.obtenerUbicacionMapa(yo.getIdUsuario()).getLatitud());
        d.setLatitud(91.0); assertThrows(ResponseStatusException.class, () -> ubicacion.actualizarUbicacionMapa(d));
        assertThrows(AccessDeniedException.class, () -> ubicacion.obtenerUbicacionMapa(otro.getIdUsuario()));
    }
    @Test void estadisticasSoloPropietarioYRankingDisponible() {
        assertEquals(yo.getIdUsuario(),usuarioService.obtenerEstadisticas(yo.getIdUsuario()).getIdUsuario());
        assertThrows(AccessDeniedException.class, () -> usuarioService.obtenerEstadisticas(otro.getIdUsuario()));
        assertEquals(2,usuarioService.obtenerRanking().size());
    }
    String tokenActual() {
        return jwt.generateToken(org.springframework.security.core.userdetails.User.withUsername(yo.getCorreo()).password("x").roles("CIUDADANO").build());
    }
    @Test void logoutRevocaTokenPersistido() {
        String token = tokenActual(); assertFalse(cuentas.estaRevocado(token));
        cuentas.cerrarSesion(yo.getIdUsuario(),"Bearer " + token); assertTrue(cuentas.estaRevocado(token));
    }
    @Test void resetCambiaHashRevocaSesionesYEsDeUnSoloUso() {
        String jwtAnterior = tokenActual(); String token = "a".repeat(43);
        TokenRecuperacion r = new TokenRecuperacion(); r.setHash(CuentaSeguridadService.hash(token));
        r.setUsuarioId(yo.getIdUsuario()); r.setExpira(Instant.now().plusSeconds(900)); recuperaciones.save(r);
        cuentas.restablecerContrasena(token,"ClaveNueva12345");
        assertTrue(encoder.matches("ClaveNueva12345",usuarios.findById(yo.getIdUsuario()).orElseThrow().getContrasenaHash()));
        assertTrue(cuentas.estaRevocado(jwtAnterior));
        assertThrows(ResponseStatusException.class, () -> cuentas.restablecerContrasena(token,"OtraClave12345"));
    }
    @Test void recuperacionSinSmtpNoSimulaEnvio() {
        assertEquals(503,assertThrows(ResponseStatusException.class, () -> cuentas.solicitarRecuperacionContrasena(yo.getCorreo())).getStatusCode().value());
    }
    @Test void fotosRechazanContenidoActivoYReescribenImagen(@org.junit.jupiter.api.io.TempDir java.nio.file.Path dir) throws Exception {
        org.springframework.test.util.ReflectionTestUtils.setField(fotos,"uploadDir",dir.toString());
        var activa = new org.springframework.mock.web.MockMultipartFile("fotos","ataque.html","text/html","<script>alert(1)</script>".getBytes());
        assertThrows(ResponseStatusException.class, () -> fotos.guardarFoto(activa));
        var buffer = new java.io.ByteArrayOutputStream();
        javax.imageio.ImageIO.write(new java.awt.image.BufferedImage(2,2,java.awt.image.BufferedImage.TYPE_INT_RGB),"png",buffer);
        var imagen = new org.springframework.mock.web.MockMultipartFile("fotos","foto.html","image/png",buffer.toByteArray());
        String url = fotos.guardarFoto(imagen); assertTrue(url.endsWith(".png"));
        assertNotNull(javax.imageio.ImageIO.read(dir.resolve(url.substring("/files/".length())).toFile()));
    }
    @Test void tokenExpiradoNoCambiaContrasena() {
        String token = "b".repeat(43); TokenRecuperacion r = new TokenRecuperacion();
        r.setHash(CuentaSeguridadService.hash(token)); r.setUsuarioId(yo.getIdUsuario()); r.setExpira(Instant.now().minusSeconds(1)); recuperaciones.save(r);
        assertThrows(ResponseStatusException.class, () -> cuentas.restablecerContrasena(token,"NuevaClave123456"));
        assertTrue(encoder.matches("Password123456",usuarios.findById(yo.getIdUsuario()).orElseThrow().getContrasenaHash()));
    }
    @Test void httpJwtAutenticaYBloqueaSuplantacionYLogout() throws Exception {
        Rol rol = new Rol(); rol.setNombreRol("CIUDADANO"); rol = roles.save(rol); yo.setRol(rol); usuarios.save(yo);
        var mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(web)
                .addFilters(revocacionFilter)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity()).build();
        String token = tokenActual(); SecurityContextHolder.clearContext();
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/grupo/"+grupo.getIdGrupo()+"/unirse")
                .param("usuarioId",yo.getIdUsuario().toString())).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/grupo/"+grupo.getIdGrupo()+"/unirse")
                .header("Authorization","Bearer "+token).param("usuarioId",otro.getIdUsuario().toString()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isForbidden());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/grupo/"+grupo.getIdGrupo()+"/unirse")
                .header("Authorization","Bearer "+token).param("usuarioId",yo.getIdUsuario().toString()))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isCreated());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/usuario/"+yo.getIdUsuario()+"/cerrar-sesion")
                .header("Authorization","Bearer "+token)).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isNoContent());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/usuario/ver-ranking-usuario")
                .header("Authorization","Bearer "+token)).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isUnauthorized());
    }
}
