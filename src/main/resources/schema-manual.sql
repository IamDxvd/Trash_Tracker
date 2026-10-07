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
