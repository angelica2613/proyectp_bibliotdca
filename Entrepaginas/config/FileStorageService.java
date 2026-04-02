package com.example.Entrepaginas.config;

import com.example.Entrepaginas.config.FileUploadConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final String uploadDirPerfilesWebPath = "/uploads/perfiles/";

    @Autowired
    public FileStorageService(FileUploadConfig fileUploadConfig) {
        this.fileStorageLocation = Paths.get(fileUploadConfig.getUploadDirPerfiles())
                                        .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de almacenamiento de archivos.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            return uploadDirPerfilesWebPath + fileName; // Retorna la ruta web relativa
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo " + fileName + ". Por favor, inténtelo de nuevo!", ex);
        }
    }

    public void deleteFile(String filePath) {
        if (filePath != null && filePath.startsWith(uploadDirPerfilesWebPath)) {
            String fileName = filePath.substring(uploadDirPerfilesWebPath.length());
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            try {
                Files.deleteIfExists(targetLocation);
            } catch (IOException ex) {
                // Log the error but don't throw, as it might be an old or missing file
                System.err.println("Error al eliminar el archivo: " + targetLocation.toString() + " - " + ex.getMessage());
            }
        }
    }
}