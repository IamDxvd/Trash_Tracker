# Integración de funciones de Trash Tracker

## Base y alcance

Repositorio: https://github.com/IamDxvd/Trash_Tracker.git

Commit de partida: `1dc60d894f00674f9677b9c1c7c4d8532727b184`.

Fuente comparada: `trashtracker-backend-unificado (1).zip`.

Se incorporaron las funciones ausentes y sus capas de controlador, servicio, DTO, repositorio, persistencia y seguridad. Los archivos originales de autenticación JWT, `SecurityConfig`, registro, perfil, entidades existentes y carga de fotos de reportes se conservaron. Los métodos nuevos en servicios existentes se encuentran debajo del comentario `Funciones adicionales integradas del backend unificado`.

No se reemplazaron archivos enteros por las versiones del ZIP. La comprobación textual verificó que los 45 métodos originales de los archivos Java modificados conservan sus cuerpos; esto no equivale a una prueba exhaustiva de todos los flujos previos. No se hizo push ni se modificó una base de datos del proyecto.

## Funciones y secciones añadidas

Todas las rutas Java siguientes parten de `src/main/java/com/upc/trashtracker/`.

| Función | Servicio | Capas que la acompañan |
|---|---|---|
| Unirse a grupos, chat y asignación de roles | `servicios/GrupoComunidadService.java` | `controladores/GrupoComunidadController.java`; DTO de miembros, mensajes y roles; consultas en `MiembroGrupoRepository`, `MensajeRepository` y bloqueo en `GrupoRepository` |
| Crear eventos, asistencia, avisos y fotos | `servicios/EventoComunidadService.java` | `controladores/EventoComunidadController.java`; DTO de evento, asistencia, notificación y foto; `AsistenciaEventoRepository`, `EventoRepository`; `FotoComunidadStorage` |
| Canje con descuento de saldo, stock e historial | Método `CanjeService.realizarCanje` | `CanjeController`, `CanjeRequestDTO`, `CanjeResponseDTO`; bloqueo de usuario y recompensa; historial de puntos |
| Otorgar puntos y actualizar nivel | Método `HistorialPuntosService.insertarPuntos` | `HistorialPuntosController`, `PuntosRequestDTO`, `PuntosResponseDTO`; saldo e historial en la misma transacción |
| Desbloquear y consultar logros | Métodos nuevos en `LogroUsuarioService` | `LogroUsuarioController`; DTO de solicitud/respuesta y consultas en `LogroUsuarioRepository` |
| Mapa, filtro, búsqueda, detalle y administración de reportes | Métodos nuevos en `ReporteService` | Rutas añadidas a `ReporteController`; consultas en `ReporteRepository`; conserva la base del Git: `/api/reports` |
| Estadísticas, ranking, cálculo de nivel y eliminación de cuenta propia | Métodos nuevos en `UsuarioService` | `UsuarioFuncionesController`; DTO de estadísticas, ranking y nivel; consulta ordenada en `UsuarioRepository` |
| Guardar y recuperar la ubicación personal | `UbicacionUsuarioService` | `UbicacionUsuarioController`, `UbicacionDTO`, entidad y repositorio `UbicacionUsuario` |
| Recuperar contraseña y cerrar sesión | `CuentaSeguridadService` | `CuentaSeguridadController`, `TokenRecuperacion`, `TokenRevocado`, `EstadoSeguridadUsuario` y sus repositorios |

## Seguridad aplicada a las funciones nuevas

- `security/services/AccesoIntegracion.java`: compara los IDs recibidos con la identidad del JWT ya validado. En operaciones personales, ni siquiera un administrador puede hacerse pasar por otro usuario.
- Grupos: el chat requiere membresía; los cambios de rol requieren ser creador u organizador real del grupo. Se conserva la protección del último organizador.
- Eventos: creación y avisos requieren permisos de organización; asistencia y fotos usan el usuario autenticado. Las fotos requieren membresía o asistencia confirmada y no se admiten después del evento.
- Puntos, concesión de logros y escrituras administrativas nuevas de reportes: solo `ADMINISTRADOR`.
- Canjes: transacción única y bloqueos pesimistas de usuario/recompensa; valida saldo, stock y costo positivo. Los nuevos flujos de membresía, asistencia y concesión de logros también serializan sus cambios con bloqueos.
- Fotos de comunidad: 1–10 archivos por solicitud, hasta 10 MB por imagen y 20 millones de píxeles. Se decodifican y reescriben como PNG con nombre aleatorio; se limpian si la transacción se revierte. El almacenamiento original de reportes permanece intacto.
- Recuperación: token aleatorio de 32 bytes, solo su SHA-256 en base de datos, vigencia de 15 minutos y un solo uso. Cambia la contraseña con el `PasswordEncoder` existente e invalida los JWT anteriores. Respuesta genérica cuando el correo no existe.
- `security/filters/RevocacionTokenFilter.java`: rechaza sesiones cerradas y tokens invalidados por un cambio de contraseña. Es un filtro adicional; `JwtRequestFilter` no fue editado.
- `security/config/RecuperacionSecurityConfig.java`: cadena específica que permite solicitar/restablecer contraseña sin sesión. La cadena original sigue atendiendo el resto de rutas.

## Rutas nuevas

Todas requieren JWT y rol `CIUDADANO` o `ADMINISTRADOR`, salvo las dos rutas públicas de recuperación. Los permisos adicionales se indican abajo. Los IDs personales enviados deben coincidir con el JWT.

| Método y ruta | Uso / permiso adicional |
|---|---|
| `POST /api/grupo/{idGrupo}/unirse?usuarioId=...` | Unirse como usuario autenticado |
| `POST /api/grupo/{idGrupo}/mensajes` | JSON: `usuarioId`, `contenido`; miembro |
| `GET /api/grupo/{idGrupo}/mensajes?usuarioId=...` | Chat del grupo; miembro |
| `PATCH /api/grupo/{idGrupo}/miembros/{idUsuario}/rol` | JSON: `solicitanteId`, `rol`; organizador/creador |
| `POST /api/evento/crear` | JSON: `grupoId`, `organizadorId`, `fecha`, `ubicacion`; organizador |
| `POST /api/evento/{idEvento}/asistir` | JSON: `usuarioId`, `confirmado`; reutiliza la asistencia existente |
| `POST /api/evento/{idEvento}/notificar` | Avisos internos a miembros; organizador |
| `POST /api/evento/{idEvento}/fotos` | Multipart: `usuarioId`, lista `fotos` |
| `POST /api/canje/realizar-canje` | JSON: `idUsuario`, `idRecompensa` |
| `POST /api/historial-puntos/agregar-puntos` | JSON: `idUsuario`, `puntos`, `motivo`; administrador |
| `POST /api/logro-usuario/usuario/desbloquear-logro` | JSON: `idUsuario`, `idLogro`; administrador |
| `GET /api/logro-usuario/usuario/{idUsuario}` | Logros propios |
| `GET /api/reports/mapa?tipo=...` | Mapa, filtro opcional por nombre del residuo |
| `GET /api/reports/mapa/todos` | Todos los reportes del mapa |
| `GET /api/reports/mapa/buscar?texto=...` | Búsqueda en descripción, como en el ZIP |
| `GET /api/reports/mapa/{id}` | Detalle de marcador |
| `PATCH /api/reports/admin/{id}/limpiar` | Estado `LIMPIADO`; administrador |
| `PATCH /api/reports/admin/{id}/estado?estado=...` | `ACTIVO`, `EN_PROCESO`, `LIMPIADO`; administrador |
| `GET /api/reports` y `GET /api/reports/{id}` | Lectura general y detalle |
| `POST /api/reports`, `PUT /api/reports/{id}`, `DELETE /api/reports/{id}` | CRUD adicional; administrador |
| `GET /api/usuario/{id}/estadisticas` | Estadísticas propias |
| `POST /api/usuario/actualizar-nivel/{id}` | Recalcular nivel propio según puntos |
| `GET /api/usuario/ver-ranking-usuario` | Ranking sin contraseñas ni correos |
| `DELETE /api/usuario/{id}` | Eliminar cuenta propia |
| `GET /api/usuario/obtener-ubicacion/{id}` | Ubicación propia; 404 si todavía no se guardó |
| `PUT /api/usuario/actualizar-ubicacion` | JSON: `idUsuario`, `latitud`, `longitud` |
| `POST /api/usuario/{id}/cerrar-sesion` | Revoca el JWT de `Authorization: Bearer ...` |
| `POST /api/usuario/recuperar-contrasena` | Pública; JSON: `correo` |
| `POST /api/usuario/restablecer-contrasena` | Pública; JSON: `token`, `contrasena` |

## Funciones equivalentes y simulaciones del ZIP

Registro y login ya estaban implementados mediante `/api/usuario/registro` y `/api/authenticate`; se conservan BCrypt y JWT. No se añadió el login del ZIP que comparaba contraseñas en texto plano. La edición de perfil existente se reutiliza; no se introducen rutas duplicadas para la misma capacidad.

Los métodos CRUD de usuario, roles y tipos de residuo que solo cambian de nombre o firma en el ZIP ya tienen equivalentes en el Git y se conservaron. No se copiaron DTO sin uso.

La ubicación fija y el mensaje de “ubicación actualizada” del ZIP fueron sustituidos por persistencia real en una entidad nueva. Su recuperación de contraseña y cierre de sesión simulados se implementaron mediante SMTP y revocación persistente de tokens. Las notificaciones de eventos son registros internos de la aplicación, no correo ni notificaciones push.

## Base de datos y ejecución

Se añaden cuatro tablas: `ubicacion_usuario`, `token_recuperacion`, `token_revocado`, `estado_seguridad_usuario`. Las tablas ligadas al usuario tienen borrado en cascada mediante claves foráneas. No se alteran las tablas originales.

1. Aplicar `db/integracion-aditiva.sql` a una base PostgreSQL existente con el esquema del Git. El script solo crea las tablas e índice nuevos, dentro de una transacción.
2. Configurar `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET` y `FRONTEND_ORIGIN`.
3. Ejecutar con el perfil `integracion`: `./mvnw spring-boot:run -Dspring-boot.run.profiles=integracion`. En Windows usar `mvnw.cmd`.

Este perfil añadido usa `ddl-auto=validate` y desactiva la carga automática de datos. El archivo original `application.properties` se conserva tal cual: su configuración incluye `create-drop`; no usar ese perfil predeterminado con una base que se quiera conservar.

Para la recuperación por correo, configurar además:

| Variable | Valor |
|---|---|
| `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT` | Servidor SMTP y puerto |
| `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | Credenciales del proveedor |
| `SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH` | `true` si el proveedor requiere autenticación |
| `SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE` | Según el proveedor, normalmente `true` |
| `RECOVERY_FROM` | Remitente autorizado |
| `RECOVERY_URL` | URL HTTPS del frontend que recibirá `?token=...` y enviará la nueva contraseña al backend |

Sin configuración SMTP/URL/remitente, la solicitud devuelve 503 y no afirma haber enviado correo. La nueva contraseña requiere al menos 12 caracteres y como máximo 72 bytes UTF-8. El frontend debe descartar su token después del cierre de sesión. El script SQL incluye consultas opcionales de limpieza de tokens vencidos.

## Validación

Resultado: **16 pruebas, 0 fallos, 0 errores**. Incluye las 15 pruebas nuevas de `IntegracionFuncionesTests` y la prueba original de arranque.

Se verificaron membresías duplicadas, suplantación, acceso al chat, roles, canje y sus efectos sobre saldo/stock/historial, permisos sobre puntos, eventos y asistencia, ubicación persistente, privacidad de estadísticas, logout, reset de contraseña de un solo uso, vencimiento, fotos y una secuencia HTTP real de filtros con JWT usando MockMvc.

Comando reproducible: `./mvnw -Dspring.profiles.active=integracion-test test` (Windows: `mvnw.cmd`). Se ejecutó con JDK 25 compilando para Java 21. `pom.xml` incorpora correo, H2 solo para tests, el procesador explícito de Lombok y el agente de Mockito para ejecutar pruebas sin autoanexarlo al proceso.

Las pruebas usan H2 en modo PostgreSQL; no se ejecutó la migración en un PostgreSQL real ni se probó un envío SMTP real. No se realizó una prueba de carga concurrente ni se validó Swagger; la documentación OpenAPI está desactivada únicamente en el perfil de pruebas. El registro `validacion.txt` acompaña la entrega.

## Aplicar el parche

`integracion-trash-tracker.patch` se generó contra el commit de partida indicado. En una copia de ese repositorio:

```bash
git apply --check integracion-trash-tracker.patch
git apply integracion-trash-tracker.patch
./mvnw -Dspring.profiles.active=integracion-test test
```

El ZIP incluye el repositorio integrado sin `.git`, dependencias descargadas ni archivos de compilación.

## Inventario exacto de archivos

La tabla siguiente indica si cada archivo se añadió o amplió. Los números de línea corresponden a la versión entregada; en los archivos Java existentes se enumeran los métodos añadidos.

| Archivo | Cambio y sección |
|---|---|
| `db/integracion-aditiva.sql` | Añadido |
| `pom.xml` | Ampliado |
| `src/main/java/com/upc/trashtracker/controladores/CanjeController.java` | Ampliado; `realizarCanje` (L55) |
| `src/main/java/com/upc/trashtracker/controladores/CuentaSeguridadController.java` | Añadido; `solicitar` (L15), `restablecer` (L20), `cerrar` (L25) |
| `src/main/java/com/upc/trashtracker/controladores/EventoComunidadController.java` | Añadido; `crear` (L29), `asistir` (L35), `notificar` (L40), `subirFotos` (L45) |
| `src/main/java/com/upc/trashtracker/controladores/GrupoComunidadController.java` | Añadido; `unirse` (L24), `enviarMensaje` (L30), `listarMensajes` (L36), `asignarRol` (L42) |
| `src/main/java/com/upc/trashtracker/controladores/HistorialPuntosController.java` | Ampliado; `insertarPuntos` (L57) |
| `src/main/java/com/upc/trashtracker/controladores/LogroUsuarioController.java` | Ampliado; `desbloquearLogro` (L61), `listarLogrosPorUsuario` (L66) |
| `src/main/java/com/upc/trashtracker/controladores/ReporteController.java` | Ampliado; `guardar` (L55), `listarTodos` (L60), `buscarPorId` (L65), `actualizar` (L72), `mapa` (L83), `todosMapa` (L91), `buscarMapa` (L96), `detalleMapa` (L101), `limpiar` (L108), `estado` (L113) |
| `src/main/java/com/upc/trashtracker/controladores/UbicacionUsuarioController.java` | Añadido; `obtener` (L14), `actualizar` (L16) |
| `src/main/java/com/upc/trashtracker/controladores/UsuarioFuncionesController.java` | Añadido; `estadisticas` (L15), `nivel` (L17), `ranking` (L19), `eliminarCuenta` (L21) |
| `src/main/java/com/upc/trashtracker/dto/AsignarRolRequestDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/AsistenciaRequestDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/AsistenciaResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/CanjeRequestDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/CanjeResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/DesbloquearLogroRequestDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/EventoRequestDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/EventoResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/FotoEventoResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/LogroUsuarioResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/MensajeRequestDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/MensajeResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/MiembroGrupoResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/NivelUsuarioDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/NotificacionEventoResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/PuntosRequestDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/PuntosResponseDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/RankingUsuarioDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/RecuperarContrasenaDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/UbicacionDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/dto/UsuarioEstadisticasDTO.java` | Añadido |
| `src/main/java/com/upc/trashtracker/entidades/EstadoSeguridadUsuario.java` | Añadido |
| `src/main/java/com/upc/trashtracker/entidades/TokenRecuperacion.java` | Añadido |
| `src/main/java/com/upc/trashtracker/entidades/TokenRevocado.java` | Añadido |
| `src/main/java/com/upc/trashtracker/entidades/UbicacionUsuario.java` | Añadido |
| `src/main/java/com/upc/trashtracker/repositorio/AsistenciaEventoRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/EstadoSeguridadUsuarioRepository.java` | Añadido |
| `src/main/java/com/upc/trashtracker/repositorio/EventoRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/GrupoRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/HistorialPuntosRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/LogroUsuarioRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/MensajeRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/MiembroGrupoRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/RecompensaRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/ReporteRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/repositorio/TokenRecuperacionRepository.java` | Añadido |
| `src/main/java/com/upc/trashtracker/repositorio/TokenRevocadoRepository.java` | Añadido |
| `src/main/java/com/upc/trashtracker/repositorio/UbicacionUsuarioRepository.java` | Añadido |
| `src/main/java/com/upc/trashtracker/repositorio/UsuarioRepository.java` | Ampliado |
| `src/main/java/com/upc/trashtracker/security/config/RecuperacionSecurityConfig.java` | Añadido |
| `src/main/java/com/upc/trashtracker/security/filters/RevocacionTokenFilter.java` | Añadido |
| `src/main/java/com/upc/trashtracker/security/services/AccesoIntegracion.java` | Añadido; `usuarioActualId` (L16), `esPropietario` (L26), `exigirPropietario` (L28) |
| `src/main/java/com/upc/trashtracker/servicios/CanjeService.java` | Ampliado; `realizarCanje` (L97) |
| `src/main/java/com/upc/trashtracker/servicios/CuentaSeguridadService.java` | Añadido; `solicitarRecuperacionContrasena` (L44), `restablecerContrasena` (L68), `cerrarSesion` (L86), `estaRevocado` (L93), `hash` (L101) |
| `src/main/java/com/upc/trashtracker/servicios/EventoComunidadService.java` | Añadido; `crearEvento` (L71), `registrarAsistencia` (L110), `notificarNuevoEvento` (L143), `subirFotos` (L176) |
| `src/main/java/com/upc/trashtracker/servicios/FotoComunidadStorage.java` | Añadido; `guardarFoto` (L22), `afterCompletion` (L45) |
| `src/main/java/com/upc/trashtracker/servicios/GrupoComunidadService.java` | Añadido; `unirse` (L50), `enviarMensaje` (L69), `listarMensajes` (L90), `asignarRol` (L102), `puedeAdministrar` (L137) |
| `src/main/java/com/upc/trashtracker/servicios/HistorialPuntosService.java` | Ampliado; `insertarPuntos` (L75) |
| `src/main/java/com/upc/trashtracker/servicios/LogroUsuarioService.java` | Ampliado; `desbloquearLogro` (L87), `listarLogrosPorUsuario` (L116) |
| `src/main/java/com/upc/trashtracker/servicios/ReporteService.java` | Ampliado; `guardar` (L151), `listarTodos` (L156), `buscarPorId` (L161), `actualizar` (L167), `eliminar` (L174), `listarMapa` (L179), `filtrarPorTipo` (L184), `buscar` (L189), `marcarLimpio` (L195), `actualizarEstado` (L206) |
| `src/main/java/com/upc/trashtracker/servicios/UbicacionUsuarioService.java` | Añadido; `obtenerUbicacionMapa` (L21), `actualizarUbicacionMapa` (L28) |
| `src/main/java/com/upc/trashtracker/servicios/UsuarioService.java` | Ampliado; `obtenerEstadisticas` (L70), `actualizarNivel` (L89), `obtenerRanking` (L111), `calcularNivelPorPuntos` (L129), `eliminarCuenta` (L159) |
| `src/main/resources/application-integracion.properties` | Añadido |
| `src/test/java/com/upc/trashtracker/IntegracionFuncionesTests.java` | Añadido |
| `src/test/resources/application-integracion-test.properties` | Añadido |
