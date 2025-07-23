package com.example.ppe_302_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageLocation = Paths.get("uploads");

    public FileStorageService() throws IOException {
        Files.createDirectories(storageLocation);
    }

    public String storeFile(MultipartFile file) {
        try {
            String nomFichier = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = storageLocation.resolve(nomFichier);
            Files.copy(file.getInputStream(), destination);
            return nomFichier;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier", e);
        }
    }
}
