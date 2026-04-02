package com.example.Entrepaginas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploadConfig {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.upload-dir-libros}")
    private String uploadDirLibros;

    @Value("${file.upload-dir-perfiles}")
    private String uploadDirPerfiles;

    public String getUploadDir() {
        return uploadDir;
    }

    public String getUploadDirLibros() {
        return uploadDirLibros;
    }

    public String getUploadDirPerfiles() {
        return uploadDirPerfiles;
    }
}