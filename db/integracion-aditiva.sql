-- PostgreSQL: ejecutar una vez antes de usar el perfil integracion.
-- No elimina ni modifica tablas/datos existentes.
BEGIN;
CREATE TABLE IF NOT EXISTS ubicacion_usuario (
    id_usuario BIGINT PRIMARY KEY REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    latitud DOUBLE PRECISION NOT NULL CHECK (latitud BETWEEN -90 AND 90),
    longitud DOUBLE PRECISION NOT NULL CHECK (longitud BETWEEN -180 AND 180)
);
CREATE TABLE IF NOT EXISTS token_recuperacion (
    hash VARCHAR(64) PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    expira TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_token_recuperacion_usuario ON token_recuperacion(usuario_id);
CREATE TABLE IF NOT EXISTS token_revocado (
    hash VARCHAR(64) PRIMARY KEY,
    expira TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE TABLE IF NOT EXISTS estado_seguridad_usuario (
    usuario_id BIGINT PRIMARY KEY REFERENCES usuario(id_usuario) ON DELETE CASCADE,
    tokens_invalidos_hasta TIMESTAMP WITH TIME ZONE NOT NULL
);
COMMIT;
-- Mantenimiento opcional: borrar registros expirados de token_recuperacion y token_revocado.
-- DELETE FROM token_recuperacion WHERE expira < CURRENT_TIMESTAMP;
-- DELETE FROM token_revocado WHERE expira < CURRENT_TIMESTAMP;
