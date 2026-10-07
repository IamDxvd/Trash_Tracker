-- =========================================================================
-- Schema DDL para TrashTracker - PostgreSQL
-- Generado a partir de las mismas entidades JPA del backend (Hibernate crea
-- este mismo esquema automaticamente al levantar la app con ddl-auto=update).
-- Sirve para cargar la base directamente en pgAdmin/psql sin correr la app.
-- =========================================================================

-- Elimina las tablas si ya existen (orden inverso por las FKs)
DROP TABLE IF EXISTS pregunta_frecuente CASCADE;
DROP TABLE IF EXISTS mensaje_chatbot CASCADE;
DROP TABLE IF EXISTS conversacion_chatbot CASCADE;
DROP TABLE IF EXISTS notificacion CASCADE;
DROP TABLE IF EXISTS logro_usuario CASCADE;
DROP TABLE IF EXISTS canje CASCADE;
DROP TABLE IF EXISTS historial_puntos CASCADE;
DROP TABLE IF EXISTS reporte CASCADE;
DROP TABLE IF EXISTS mensaje CASCADE;
DROP TABLE IF EXISTS foto_evento CASCADE;
DROP TABLE IF EXISTS asistencia_evento CASCADE;
DROP TABLE IF EXISTS evento CASCADE;
DROP TABLE IF EXISTS miembro_grupo CASCADE;
DROP TABLE IF EXISTS grupo CASCADE;
DROP TABLE IF EXISTS dispositivo_conocido CASCADE;
DROP TABLE IF EXISTS cuenta_vinculada CASCADE;
DROP TABLE IF EXISTS logro CASCADE;
DROP TABLE IF EXISTS recompensa CASCADE;
DROP TABLE IF EXISTS tipo_residuo CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;
DROP TABLE IF EXISTS rol CASCADE;

CREATE TABLE rol (
                     id_rol BIGSERIAL PRIMARY KEY,
                     nombre_rol VARCHAR(50),
                     descripcion VARCHAR(150)
);

CREATE TABLE usuario (
                         id_usuario BIGSERIAL PRIMARY KEY,
                         nombre VARCHAR(255),
                         correo VARCHAR(150),
                         contrasena_hash VARCHAR(255),
                         idioma_preferido VARCHAR(10),
                         nivel INT,
                         puntos_totales INT,
                         rol_id BIGINT
);

CREATE TABLE tipo_residuo (
                              id_tipo_residuo BIGSERIAL PRIMARY KEY,
                              nombre VARCHAR(50)
);

CREATE TABLE recompensa (
                            id_recompensa BIGSERIAL PRIMARY KEY,
                            nombre VARCHAR(100),
                            costo_puntos INT,
                            stock INT
);

CREATE TABLE logro (
                       id_logro BIGSERIAL PRIMARY KEY,
                       nombre VARCHAR(100),
                       criterio VARCHAR(255)
);

CREATE TABLE cuenta_vinculada (
                                  id_cuenta_vinculada BIGSERIAL PRIMARY KEY,
                                  proveedor VARCHAR(30),
                                  id_externo VARCHAR(100),
                                  usuario_id BIGINT
);

CREATE TABLE dispositivo_conocido (
                                      id_dispositivo_conocido BIGSERIAL PRIMARY KEY,
                                      tipo_dispositivo VARCHAR(50),
                                      fecha_primer_acceso DATE,
                                      usuario_id BIGINT
);

CREATE TABLE grupo (
                       id_grupo BIGSERIAL PRIMARY KEY,
                       nombre VARCHAR(100),
                       zona VARCHAR(100),
                       creador_id BIGINT
);

CREATE TABLE miembro_grupo (
                               id_miembro_grupo BIGSERIAL PRIMARY KEY,
                               rol VARCHAR(20),
                               grupo_id BIGINT,
                               usuario_id BIGINT
);

CREATE TABLE evento (
                        id_evento BIGSERIAL PRIMARY KEY,
                        fecha DATE,
                        ubicacion VARCHAR(150),
                        grupo_id BIGINT,
                        organizador_id BIGINT
);

CREATE TABLE asistencia_evento (
                                   id_asistencia_evento BIGSERIAL PRIMARY KEY,
                                   confirmado BOOLEAN,
                                   evento_id BIGINT,
                                   usuario_id BIGINT
);

CREATE TABLE foto_evento (
                             id_foto_evento BIGSERIAL PRIMARY KEY,
                             url_foto VARCHAR(255),
                             evento_id BIGINT,
                             usuario_id BIGINT
);

CREATE TABLE mensaje (
                         id_mensaje BIGSERIAL PRIMARY KEY,
                         contenido VARCHAR(500),
                         fecha_hora TIMESTAMP,
                         grupo_id BIGINT,
                         usuario_id BIGINT
);

CREATE TABLE reporte (
                         id_reporte BIGSERIAL PRIMARY KEY,
                         descripcion VARCHAR(255),
                         foto_url VARCHAR(255),
                         latitud DECIMAL(9,6),
                         longitud DECIMAL(9,6),
                         estado VARCHAR(20),
                         fecha_hora TIMESTAMP,
                         usuario_id BIGINT,
                         tipo_residuo_id BIGINT
);

CREATE TABLE historial_puntos (
                                  id_historial_puntos BIGSERIAL PRIMARY KEY,
                                  cantidad INT,
                                  motivo VARCHAR(255),
                                  fecha TIMESTAMP,
                                  usuario_id BIGINT
);

CREATE TABLE canje (
                       id_canje BIGSERIAL PRIMARY KEY,
                       fecha TIMESTAMP,
                       estado VARCHAR(20),
                       usuario_id BIGINT,
                       recompensa_id BIGINT
);

CREATE TABLE logro_usuario (
                               id_logro_usuario BIGSERIAL PRIMARY KEY,
                               fecha_obtenido DATE,
                               usuario_id BIGINT,
                               logro_id BIGINT
);

CREATE TABLE notificacion (
                              id_notificacion BIGSERIAL PRIMARY KEY,
                              tipo VARCHAR(30),
                              contenido VARCHAR(255),
                              leido BOOLEAN,
                              fecha TIMESTAMP,
                              usuario_id BIGINT
);

CREATE TABLE conversacion_chatbot (
                                      id_conversacion_chatbot BIGSERIAL PRIMARY KEY,
                                      fecha_inicio TIMESTAMP,
                                      usuario_id BIGINT UNIQUE
);

CREATE TABLE mensaje_chatbot (
                                 id_mensaje_chatbot BIGSERIAL PRIMARY KEY,
                                 emisor VARCHAR(10),
                                 contenido VARCHAR(500),
                                 fecha_hora TIMESTAMP,
                                 conversacion_id BIGINT
);

CREATE TABLE pregunta_frecuente (
                                    id_pregunta_frecuente BIGSERIAL PRIMARY KEY,
                                    pregunta VARCHAR(255),
                                    respuesta VARCHAR(500),
                                    categoria VARCHAR(50)
);

-- Llaves foraneas
ALTER TABLE usuario ADD CONSTRAINT fk_usuario_rol_id FOREIGN KEY (rol_id) REFERENCES rol(id_rol);
ALTER TABLE cuenta_vinculada ADD CONSTRAINT fk_cuenta_vinculada_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE dispositivo_conocido ADD CONSTRAINT fk_dispositivo_conocido_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE grupo ADD CONSTRAINT fk_grupo_creador_id FOREIGN KEY (creador_id) REFERENCES usuario(id_usuario);
ALTER TABLE miembro_grupo ADD CONSTRAINT fk_miembro_grupo_grupo_id FOREIGN KEY (grupo_id) REFERENCES grupo(id_grupo);
ALTER TABLE miembro_grupo ADD CONSTRAINT fk_miembro_grupo_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE evento ADD CONSTRAINT fk_evento_grupo_id FOREIGN KEY (grupo_id) REFERENCES grupo(id_grupo);
ALTER TABLE evento ADD CONSTRAINT fk_evento_organizador_id FOREIGN KEY (organizador_id) REFERENCES usuario(id_usuario);
ALTER TABLE asistencia_evento ADD CONSTRAINT fk_asistencia_evento_evento_id FOREIGN KEY (evento_id) REFERENCES evento(id_evento);
ALTER TABLE asistencia_evento ADD CONSTRAINT fk_asistencia_evento_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE foto_evento ADD CONSTRAINT fk_foto_evento_evento_id FOREIGN KEY (evento_id) REFERENCES evento(id_evento);
ALTER TABLE foto_evento ADD CONSTRAINT fk_foto_evento_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE mensaje ADD CONSTRAINT fk_mensaje_grupo_id FOREIGN KEY (grupo_id) REFERENCES grupo(id_grupo);
ALTER TABLE mensaje ADD CONSTRAINT fk_mensaje_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE reporte ADD CONSTRAINT fk_reporte_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE reporte ADD CONSTRAINT fk_reporte_tipo_residuo_id FOREIGN KEY (tipo_residuo_id) REFERENCES tipo_residuo(id_tipo_residuo);
ALTER TABLE historial_puntos ADD CONSTRAINT fk_historial_puntos_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE canje ADD CONSTRAINT fk_canje_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE canje ADD CONSTRAINT fk_canje_recompensa_id FOREIGN KEY (recompensa_id) REFERENCES recompensa(id_recompensa);
ALTER TABLE logro_usuario ADD CONSTRAINT fk_logro_usuario_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE logro_usuario ADD CONSTRAINT fk_logro_usuario_logro_id FOREIGN KEY (logro_id) REFERENCES logro(id_logro);
ALTER TABLE notificacion ADD CONSTRAINT fk_notificacion_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE conversacion_chatbot ADD CONSTRAINT fk_conversacion_chatbot_usuario_id FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario);
ALTER TABLE mensaje_chatbot ADD CONSTRAINT fk_mensaje_chatbot_conversacion_id FOREIGN KEY (conversacion_id) REFERENCES conversacion_chatbot(id_conversacion_chatbot);
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

-- =========================================================================
-- [AGREGADO] Tablas de integracion (seguridad y ubicacion) que existen como
-- entidades JPA pero no estaban en este script. Misma definicion que
-- db/integracion-aditiva.sql. No modifica ninguna tabla anterior.
-- =========================================================================
DROP TABLE IF EXISTS estado_seguridad_usuario CASCADE;
DROP TABLE IF EXISTS token_revocado CASCADE;
DROP TABLE IF EXISTS token_recuperacion CASCADE;
DROP TABLE IF EXISTS ubicacion_usuario CASCADE;

CREATE TABLE ubicacion_usuario (
                                   id_usuario BIGINT PRIMARY KEY REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                                   latitud DOUBLE PRECISION NOT NULL CHECK (latitud BETWEEN -90 AND 90),
                                   longitud DOUBLE PRECISION NOT NULL CHECK (longitud BETWEEN -180 AND 180)
);

CREATE TABLE token_recuperacion (
                                    hash VARCHAR(64) PRIMARY KEY,
                                    usuario_id BIGINT NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                                    expira TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_token_recuperacion_usuario ON token_recuperacion(usuario_id);

CREATE TABLE token_revocado (
                                hash VARCHAR(64) PRIMARY KEY,
                                expira TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE estado_seguridad_usuario (
                                          usuario_id BIGINT PRIMARY KEY REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                                          tokens_invalidos_hasta TIMESTAMP WITH TIME ZONE NOT NULL
);

-- =========================================================================
-- [AGREGADO] Data complementaria para las tablas que estaban vacias.
-- No toca ningun registro anterior. Estas tablas no usan secuencias
-- (su PK es el id del usuario o un hash), asi que no requieren setval.
-- =========================================================================

-- 22) UBICACION_USUARIO (ultima ubicacion conocida de cada usuario, cerca de sus reportes)
INSERT INTO ubicacion_usuario (id_usuario, latitud, longitud) VALUES
                                                                  (1, -12.046800, -77.042500),
                                                                  (2, -12.050300, -77.038600),
                                                                  (3, -12.049100, -77.040400),
                                                                  (4, -12.048500, -77.039800),
                                                                  (5, -12.046100, -77.043000) ON CONFLICT DO NOTHING;

-- 23) TOKEN_RECUPERACION (hash SHA-256 de tokens de "olvide mi contrasena"; ya expirados, solo historico)
INSERT INTO token_recuperacion (hash, usuario_id, expira) VALUES
                                                              ('468723ff6d45a6b06c501af357035edbdefe6d516747df2b82eeb5568ca8ef48', 3, '2026-09-20 15:15:00-05'),
                                                              ('78f06fc6ff2c26171343005b17b17dc1d6198ccd9bc08dc780cdf020e3f331b8', 4, '2026-09-28 19:45:00-05') ON CONFLICT DO NOTHING;

-- 24) TOKEN_REVOCADO (hash SHA-256 de JWT invalidados al cerrar sesion; ya expirados)
INSERT INTO token_revocado (hash, expira) VALUES
                                              ('ab949d66d36eb2c0557a931b0ce866feaedac9419bd88ec41470f4467930bd6b', '2026-09-18 11:20:00-05'),
                                              ('d4f804e1ce4d03b89a14aa792d04bed722aafa4f51527c8ef4bfdf3c36a3adc5', '2026-09-25 18:40:00-05'),
                                              ('f7a2a58213b8eded858bba8c37ad7b2be9b2ba73e116be916befc6a950fb37f3', '2026-10-01 09:20:00-05') ON CONFLICT DO NOTHING;

-- 25) ESTADO_SEGURIDAD_USUARIO (usuarios que restablecieron contrasena: los JWT emitidos
--     ANTES de esta fecha quedan invalidos; los logins nuevos funcionan normal)
INSERT INTO estado_seguridad_usuario (usuario_id, tokens_invalidos_hasta) VALUES
                                                                              (3, '2026-09-20 15:05:00-05'),
                                                                              (4, '2026-09-28 19:36:00-05') ON CONFLICT DO NOTHING;
