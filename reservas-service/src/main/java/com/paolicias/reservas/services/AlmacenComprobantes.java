package com.paolicias.reservas.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

// Guarda las imágenes de los comprobantes en una carpeta del servidor.
// El nombre del archivo lo genera el servidor (nunca se usa el nombre que manda el cliente).
@Service
public class AlmacenComprobantes {

    private static final Map<String, String> EXTENSIONES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/webp", "webp");

    private final Path carpeta;

    public AlmacenComprobantes(@Value("${comprobantes.directorio}") String directorio) {
        this.carpeta = Paths.get(directorio).toAbsolutePath().normalize();
    }

    public String guardar(Long reservaId, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Adjunta la imagen del comprobante");
        }
        String extension = EXTENSIONES.get(archivo.getContentType());
        if (extension == null) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "El comprobante debe ser una imagen JPG, PNG o WEBP");
        }
        String nombre = "reserva_" + reservaId + "_" + System.currentTimeMillis() + "." + extension;
        try (InputStream entrada = archivo.getInputStream()) {
            Files.createDirectories(carpeta);
            Files.copy(entrada, carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el comprobante");
        }
        return nombre;
    }

    public Path ruta(String nombre) {
        Path ruta = carpeta.resolve(nombre).normalize();
        if (!ruta.startsWith(carpeta) || !Files.exists(ruta)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontró el comprobante");
        }
        return ruta;
    }

    // Borra el archivo anterior para no dejar imágenes huérfanas
    public void borrar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        try {
            Path ruta = carpeta.resolve(nombre).normalize();
            if (ruta.startsWith(carpeta)) {
                Files.deleteIfExists(ruta);
            }
        } catch (IOException e) {
            // No es grave: solo queda un archivo sin usar
        }
    }
}
