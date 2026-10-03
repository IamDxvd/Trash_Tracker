package com.upc.trashtracker.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads/reportes}")
    private String uploadDir;

    public String guardarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException("Debe adjuntar una foto como evidencia del reporte.");
        }
        try {
            Path directorio = Paths.get(uploadDir);
            if (!Files.exists(directorio)) {
                Files.createDirectories(directorio);
            }

            String extension = "";
            String nombreOriginal = foto.getOriginalFilename();
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }
            String nombreArchivo = UUID.randomUUID() + extension;

            Path destino = directorio.resolve(nombreArchivo);
            Files.copy(foto.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

            // URL publica servida por WebConfig (/files/**)
            return "/files/" + nombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("No se pudo guardar la foto del reporte: " + e.getMessage(), e);
        }
    }
}
