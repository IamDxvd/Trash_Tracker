package com.upc.trashtracker.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Iterator;
import java.util.UUID;

/** Reencodes photos rather than publishing client-supplied extensions or active content. */
@Service
public class FotoComunidadStorage {
    @Value("${app.upload.dir:uploads/reportes}") private String uploadDir;
    public String guardarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty() || foto.getSize() > 10 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto obligatoria; maximo 10 MB");
        }
        Path destino = null;
        try (InputStream input = foto.getInputStream(); var stream = ImageIO.createImageInputStream(input)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
            if (!readers.hasNext()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagen no valida");
            ImageReader reader = readers.next();
            BufferedImage imagen;
            try {
                reader.setInput(stream);
                if ((long) reader.getWidth(0) * reader.getHeight(0) > 20_000_000) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Imagen demasiado grande");
                }
                imagen = reader.read(0);
            } finally { reader.dispose(); }
            Path directorio = Paths.get(uploadDir); Files.createDirectories(directorio);
            destino = directorio.resolve(UUID.randomUUID() + ".png");
            if (!ImageIO.write(imagen, "png", destino.toFile())) throw new IOException("No hay codificador PNG");
            Path archivo = destino;
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override public void afterCompletion(int status) {
                        if (status != STATUS_COMMITTED) {
                            try { Files.deleteIfExists(archivo); }
                            catch (IOException e) { org.slf4j.LoggerFactory.getLogger(FotoComunidadStorage.class).warn("No se pudo limpiar foto de transaccion fallida", e); }
                        }
                    }
                });
            }
            return "/files/" + destino.getFileName();
        } catch (IOException e) {
            if (destino != null) try { Files.deleteIfExists(destino); } catch (IOException suppressed) { e.addSuppressed(suppressed); }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo procesar la foto", e);
        }
    }
}
