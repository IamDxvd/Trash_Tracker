-- =========================================================================
-- Datos de prueba (seed data) para TrashTracker - PostgreSQL
-- Se carga automaticamente al levantar la app (spring.sql.init.mode=always)
-- Pensado para poder probar de inmediato todos los endpoints en Swagger UI.
-- =========================================================================

-- 1) ROL
INSERT INTO rol (id_rol, nombre_rol, descripcion) VALUES
                                                      (1, 'Ciudadano', 'Usuario final de la plataforma que reporta y participa en la comunidad'),
                                                      (2, 'Administrador', 'Gestiona reportes, valida limpiezas y modera la plataforma') ON CONFLICT DO NOTHING;

-- 2) USUARIO
INSERT INTO usuario (id_usuario, nombre, correo, contrasena_hash, idioma_preferido, nivel, puntos_totales, rol_id) VALUES
                                                                                                                       (1, 'Sofia Ramos', 'sofia.ramos@trashtracker.org', '$2a$10$pZYYn804Rf0HU.7UMT5LXO9r0V4eQa1eBXUo.yyISdeYsgQwNksmi', 'es', 4, 1240, 1),
                                                                                                                       (2, 'Carlos Gomez', 'carlos.gomez@trashtracker.org', '$2a$10$pZYYn804Rf0HU.7UMT5LXO9r0V4eQa1eBXUo.yyISdeYsgQwNksmi', 'es', 2, 480, 1),
                                                                                                                       (3, 'Lucia Fernandez', 'lucia.fernandez@trashtracker.org', '$2a$10$pZYYn804Rf0HU.7UMT5LXO9r0V4eQa1eBXUo.yyISdeYsgQwNksmi', 'es', 3, 860, 1),
                                                                                                                       (4, 'Diego Torres', 'diego.torres@trashtracker.org', '$2a$10$pZYYn804Rf0HU.7UMT5LXO9r0V4eQa1eBXUo.yyISdeYsgQwNksmi', 'es', 1, 120, 1),
                                                                                                                       (5, 'Admin TrashTracker', 'admin@trashtracker.org', '$2a$10$pZYYn804Rf0HU.7UMT5LXO9r0V4eQa1eBXUo.yyISdeYsgQwNksmi', 'es', 5, 0, 2)
    ON CONFLICT (id_usuario) DO UPDATE SET contrasena_hash = EXCLUDED.contrasena_hash;

-- 3) TIPO_RESIDUO
INSERT INTO tipo_residuo (id_tipo_residuo, nombre) VALUES
                                                       (1, 'Plasticos y Envases'),
                                                       (2, 'Organicos y Vertederos'),
                                                       (3, 'Residuos Electronicos'),
                                                       (4, 'Escombros de Construccion') ON CONFLICT DO NOTHING;

-- 4) RECOMPENSA
INSERT INTO recompensa (id_recompensa, nombre, costo_puntos, stock) VALUES
                                                                        (1, 'Kit de Semillas Autoctonas', 200, 50),
                                                                        (2, 'Botella Reutilizable Alum', 350, 30),
                                                                        (3, 'Tote Bag TrashTracker', 150, 100) ON CONFLICT DO NOTHING;

-- 5) LOGRO
INSERT INTO logro (id_logro, nombre, criterio) VALUES
                                                   (1, 'Primer Reporte', 'Publicar tu primera incidencia ambiental en la plataforma'),
                                                   (2, 'Reciclador Novato', 'Reportar con exito un problema de clasificacion de residuos'),
                                                   (3, '10 Reportes', 'Reportar activamente 10 anomalias en tu comunidad'),
                                                   (4, 'Asistente de Evento', 'Participar y registrar tu presencia en una jornada ecologica') ON CONFLICT DO NOTHING;

-- 6) CUENTA_VINCULADA
INSERT INTO cuenta_vinculada (id_cuenta_vinculada, usuario_id, proveedor, id_externo) VALUES
                                                                                          (1, 1, 'GOOGLE', 'google-uid-0001'),
                                                                                          (2, 1, 'FACEBOOK', 'facebook-uid-0001'),
                                                                                          (3, 2, 'GOOGLE', 'google-uid-0002') ON CONFLICT DO NOTHING;

-- 7) DISPOSITIVO_CONOCIDO
INSERT INTO dispositivo_conocido (id_dispositivo_conocido, usuario_id, tipo_dispositivo, fecha_primer_acceso) VALUES
                                                                                                                  (1, 1, 'iPhone 14 Pro Max', '2026-01-15'),
                                                                                                                  (2, 1, 'MacBook Air M2 Chrome', '2026-01-20'),
                                                                                                                  (3, 2, 'Samsung Galaxy S23', '2026-02-01') ON CONFLICT DO NOTHING;

-- 8) GRUPO
INSERT INTO grupo (id_grupo, creador_id, nombre, zona) VALUES
                                                           (1, 1, 'Econautas del Barrio', 'Plaza Central'),
                                                           (2, 2, 'Recicladores Urbanos', 'Zona Industrial Norte'),
                                                           (3, 3, 'Guardianes del Rio', 'Ribera del Rio Lujan') ON CONFLICT DO NOTHING;

-- 9) MIEMBRO_GRUPO
INSERT INTO miembro_grupo (id_miembro_grupo, grupo_id, usuario_id, rol) VALUES
                                                                            (1, 1, 1, 'ORGANIZADOR'),
                                                                            (2, 1, 2, 'MIEMBRO'),
                                                                            (3, 1, 4, 'MIEMBRO'),
                                                                            (4, 2, 2, 'ORGANIZADOR'),
                                                                            (5, 2, 3, 'MODERADOR'),
                                                                            (6, 3, 3, 'ORGANIZADOR') ON CONFLICT DO NOTHING;

-- 10) EVENTO
INSERT INTO evento (id_evento, grupo_id, organizador_id, fecha, ubicacion) VALUES
                                                                               (1, 3, 3, '2026-10-24', 'Ribera del Rio Lujan, Sector Bajo'),
                                                                               (2, 1, 1, '2026-10-28', 'Plaza Central, Salon Comunitario'),
                                                                               (3, 2, 2, '2026-11-05', 'Centro de Gestion Municipal') ON CONFLICT DO NOTHING;

-- 11) ASISTENCIA_EVENTO
INSERT INTO asistencia_evento (id_asistencia_evento, evento_id, usuario_id, confirmado) VALUES
                                                                                            (1, 1, 1, true),
                                                                                            (2, 1, 3, true),
                                                                                            (3, 2, 2, true),
                                                                                            (4, 2, 4, false),
                                                                                            (5, 3, 2, true) ON CONFLICT DO NOTHING;

-- 12) FOTO_EVENTO
INSERT INTO foto_evento (id_foto_evento, evento_id, usuario_id, url_foto) VALUES
                                                                              (1, 1, 3, '/files/evento1-foto1.jpg'),
                                                                              (2, 1, 1, '/files/evento1-foto2.jpg'),
                                                                              (3, 2, 1, '/files/evento2-foto1.jpg') ON CONFLICT DO NOTHING;

-- 13) MENSAJE (chat grupal)
INSERT INTO mensaje (id_mensaje, grupo_id, usuario_id, contenido, fecha_hora) VALUES
                                                                                  (1, 1, 2, 'Quien tiene bolsas de consorcio extra para llevar?', '2026-09-15 10:20:00'),
                                                                                  (2, 1, 1, 'Yo puedo traer unas 10 bolsas mas', '2026-09-15 10:25:00'),
                                                                                  (3, 2, 3, 'Nueva jornada de reciclaje de carton mañana', '2026-09-16 08:00:00'),
                                                                                  (4, 3, 3, 'Mapeando microbasurales en la ribera norte', '2026-09-17 09:10:00') ON CONFLICT DO NOTHING;

-- 14) REPORTE
INSERT INTO reporte (id_reporte, usuario_id, tipo_residuo_id, descripcion, foto_url, latitud, longitud, estado, fecha_hora) VALUES
                                                                                                                                (1, 1, 1, 'Acumulacion significativa de envases plasticos tapando la salida de agua pluvial.', '/files/reporte1.jpg', -12.046374, -77.042793, 'EN_PROCESO', '2026-09-18 10:30:00'),
                                                                                                                                (2, 1, 4, 'Escombro de construccion obstaculizando el paso peatonal.', '/files/reporte2.jpg', -12.047900, -77.041200, 'ACTIVO', '2026-09-17 09:00:00'),
                                                                                                                                (3, 1, 2, 'Contenedor de reciclaje desbordado hace varios dias.', '/files/reporte3.jpg', -12.045500, -77.043900, 'RESUELTO', '2026-09-15 16:45:00'),
                                                                                                                                (4, 2, 1, 'Basural clandestino acumulado cerca del parque.', '/files/reporte4.jpg', -12.050100, -77.038800, 'ACTIVO', '2026-09-16 12:00:00'),
                                                                                                                                (5, 3, 1, 'Bolsas plasticas en sumidero de la avenida principal.', '/files/reporte5.jpg', -12.049300, -77.040100, 'EN_PROCESO', '2026-09-16 14:20:00'),
                                                                                                                                (6, 4, 3, 'Baterias de celular arrojadas en la vereda.', '/files/reporte6.jpg', -12.048700, -77.039500, 'ACTIVO', '2026-09-17 18:10:00') ON CONFLICT DO NOTHING;

-- 15) HISTORIAL_PUNTOS
INSERT INTO historial_puntos (id_historial_puntos, usuario_id, cantidad, motivo, fecha) VALUES
                                                                                            (1, 1, 50, 'REPORTE_VALIDADO', '2026-09-15 17:00:00'),
                                                                                            (2, 1, 30, 'ASISTENCIA_EVENTO', '2026-09-16 20:00:00'),
                                                                                            (3, 2, 40, 'REPORTE_VALIDADO', '2026-09-16 12:30:00'),
                                                                                            (4, 3, 25, 'MENSAJE_COMUNIDAD', '2026-09-16 14:30:00'),
                                                                                            (5, 1, 100, 'REPORTE_VALIDADO', '2026-09-17 09:05:00') ON CONFLICT DO NOTHING;

-- 16) CANJE
INSERT INTO canje (id_canje, usuario_id, recompensa_id, fecha, estado) VALUES
                                                                           (1, 1, 1, '2026-09-10 11:00:00', 'CANJEADO'),
                                                                           (2, 1, 3, '2026-09-01 09:30:00', 'CANJEADO'),
                                                                           (3, 2, 3, '2026-09-12 15:00:00', 'PENDIENTE') ON CONFLICT DO NOTHING;

-- 17) LOGRO_USUARIO
INSERT INTO logro_usuario (id_logro_usuario, usuario_id, logro_id, fecha_obtenido) VALUES
                                                                                       (1, 1, 1, '2026-03-12'),
                                                                                       (2, 1, 2, '2026-03-15'),
                                                                                       (3, 1, 3, '2026-03-20'),
                                                                                       (4, 2, 1, '2026-04-02') ON CONFLICT DO NOTHING;

-- 18) NOTIFICACION
INSERT INTO notificacion (id_notificacion, usuario_id, tipo, contenido, leido, fecha) VALUES
                                                                                          (1, 1, 'CONFIRMACION_REPORTE', 'Tu reporte #1 fue registrado correctamente.', true, '2026-09-18 10:30:05'),
                                                                                          (2, 1, 'EVENTO_NUEVO', 'Se ha publicado un nuevo evento: Limpieza Ribera del Rio Lujan.', false, '2026-09-17 08:00:00'),
                                                                                          (3, 2, 'LOGRO_DESBLOQUEADO', 'Has desbloqueado el logro Primer Reporte.', false, '2026-04-02 10:00:00'),
                                                                                          (4, 3, 'MENSAJE_GRUPO', 'Carlos Gomez envio un nuevo mensaje en el grupo Econautas del Barrio.', true, '2026-09-15 10:26:00') ON CONFLICT DO NOTHING;

-- 19) CONVERSACION_CHATBOT
INSERT INTO conversacion_chatbot (id_conversacion_chatbot, usuario_id, fecha_inicio) VALUES
                                                                                         (1, 1, '2026-09-10 09:00:00'),
                                                                                         (2, 2, '2026-09-14 16:00:00') ON CONFLICT DO NOTHING;

-- 20) MENSAJE_CHATBOT
INSERT INTO mensaje_chatbot (id_mensaje_chatbot, conversacion_id, emisor, contenido, fecha_hora) VALUES
                                                                                                     (1, 1, 'USUARIO', 'Donde puedo reciclar botellas de plastico PET cerca de mi zona?', '2026-09-10 09:00:10'),
                                                                                                     (2, 1, 'BOT', 'En tu zona (Plaza Central) tienes un Punto Verde Municipal a 150 metros.', '2026-09-10 09:00:15'),
                                                                                                     (3, 2, 'USUARIO', 'Como canjeo mis puntos por las semillas autoctonas?', '2026-09-14 16:00:10'),
                                                                                                     (4, 2, 'BOT', 'Dirigete a la pestaña Recompensas y selecciona el Kit de Semillas.', '2026-09-14 16:00:14') ON CONFLICT DO NOTHING;

-- 21) PREGUNTA_FRECUENTE
INSERT INTO pregunta_frecuente (id_pregunta_frecuente, pregunta, respuesta, categoria) VALUES
                                                                                           (1, 'Como creo un reporte ambiental?', 'Ve a la seccion Reportes, toca Crear Reporte y adjunta una foto con la ubicacion.', 'REPORTES'),
                                                                                           (2, 'Como canjeo mis puntos eco?', 'Ingresa a Recompensas, elige un premio y confirma el canje si tienes puntos suficientes.', 'PUNTOS_ECO'),
                                                                                           (3, 'Como me uno a un grupo vecinal?', 'En la seccion Grupos puedes buscar por zona y unirte con un clic.', 'GRUPOS'),
                                                                                           (4, 'Que hago si olvide mi contraseña?', 'En la pantalla de inicio de sesion selecciona Olvide mi contraseña y sigue las instrucciones enviadas a tu correo.', 'CUENTA') ON CONFLICT DO NOTHING;

-- =========================================================================
-- Resincroniza las secuencias de PostgreSQL (identity columns) para que las
-- proximas inserciones hechas desde la API/Swagger no choquen con estos IDs.
-- =========================================================================
SELECT setval(pg_get_serial_sequence('rol', 'id_rol'), (SELECT MAX(id_rol) FROM rol));
SELECT setval(pg_get_serial_sequence('usuario', 'id_usuario'), (SELECT MAX(id_usuario) FROM usuario));
SELECT setval(pg_get_serial_sequence('tipo_residuo', 'id_tipo_residuo'), (SELECT MAX(id_tipo_residuo) FROM tipo_residuo));
SELECT setval(pg_get_serial_sequence('recompensa', 'id_recompensa'), (SELECT MAX(id_recompensa) FROM recompensa));
SELECT setval(pg_get_serial_sequence('logro', 'id_logro'), (SELECT MAX(id_logro) FROM logro));
SELECT setval(pg_get_serial_sequence('cuenta_vinculada', 'id_cuenta_vinculada'), (SELECT MAX(id_cuenta_vinculada) FROM cuenta_vinculada));
SELECT setval(pg_get_serial_sequence('dispositivo_conocido', 'id_dispositivo_conocido'), (SELECT MAX(id_dispositivo_conocido) FROM dispositivo_conocido));
SELECT setval(pg_get_serial_sequence('grupo', 'id_grupo'), (SELECT MAX(id_grupo) FROM grupo));
SELECT setval(pg_get_serial_sequence('miembro_grupo', 'id_miembro_grupo'), (SELECT MAX(id_miembro_grupo) FROM miembro_grupo));
SELECT setval(pg_get_serial_sequence('evento', 'id_evento'), (SELECT MAX(id_evento) FROM evento));
SELECT setval(pg_get_serial_sequence('asistencia_evento', 'id_asistencia_evento'), (SELECT MAX(id_asistencia_evento) FROM asistencia_evento));
SELECT setval(pg_get_serial_sequence('foto_evento', 'id_foto_evento'), (SELECT MAX(id_foto_evento) FROM foto_evento));
SELECT setval(pg_get_serial_sequence('mensaje', 'id_mensaje'), (SELECT MAX(id_mensaje) FROM mensaje));
SELECT setval(pg_get_serial_sequence('reporte', 'id_reporte'), (SELECT MAX(id_reporte) FROM reporte));
SELECT setval(pg_get_serial_sequence('historial_puntos', 'id_historial_puntos'), (SELECT MAX(id_historial_puntos) FROM historial_puntos));
SELECT setval(pg_get_serial_sequence('canje', 'id_canje'), (SELECT MAX(id_canje) FROM canje));
SELECT setval(pg_get_serial_sequence('logro_usuario', 'id_logro_usuario'), (SELECT MAX(id_logro_usuario) FROM logro_usuario));
SELECT setval(pg_get_serial_sequence('notificacion', 'id_notificacion'), (SELECT MAX(id_notificacion) FROM notificacion));
SELECT setval(pg_get_serial_sequence('conversacion_chatbot', 'id_conversacion_chatbot'), (SELECT MAX(id_conversacion_chatbot) FROM conversacion_chatbot));
SELECT setval(pg_get_serial_sequence('mensaje_chatbot', 'id_mensaje_chatbot'), (SELECT MAX(id_mensaje_chatbot) FROM mensaje_chatbot));
SELECT setval(pg_get_serial_sequence('pregunta_frecuente', 'id_pregunta_frecuente'), (SELECT MAX(id_pregunta_frecuente) FROM pregunta_frecuente));
