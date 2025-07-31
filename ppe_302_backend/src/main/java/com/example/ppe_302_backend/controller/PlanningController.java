package com.example.ppe_302_backend.controller;

import com.example.ppe_302_backend.entity.DisponibiliteEntreprise;
import com.example.ppe_302_backend.entity.DocumentEntreprise;
import com.example.ppe_302_backend.entity.Entreprise;
import com.example.ppe_302_backend.repository.DocumentEntrepriseRepository;
import com.example.ppe_302_backend.repository.EntrepriseRepository;
import com.example.ppe_302_backend.service.PlanningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/planning")
@CrossOrigin(origins = "http://localhost:8080")
public class PlanningController {

    @Autowired
    private PlanningService planningService;
    @Autowired
    private EntrepriseRepository entrepriseRepository;
    @Autowired
    private DocumentEntrepriseRepository documentEntrepriseRepository;

    @PutMapping("/localisation/{entrepriseId}")
    public ResponseEntity<Entreprise> updateLocalisation(@PathVariable Long entrepriseId,
                                                         @RequestParam String localisation) {
        return ResponseEntity.ok(planningService.updateLocalisation(entrepriseId, localisation));
    }

    @PostMapping("/document/{entrepriseId}")
    public ResponseEntity<DocumentEntreprise> uploadDocument(@PathVariable Long entrepriseId,
                                                             @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(planningService.ajouterDocument(entrepriseId, file));
    }

    @PostMapping("/disponibilite/{entrepriseId}")
    public ResponseEntity<DisponibiliteEntreprise> addDisponibilite(@PathVariable Long entrepriseId,
                                                                    @RequestParam String jour,
                                                                    @RequestParam String heureDebut,
                                                                    @RequestParam String heureFin) {
        return ResponseEntity.ok(planningService.ajouterDisponibilite(entrepriseId, jour, heureDebut, heureFin));
    }
    @DeleteMapping("/disponibilite/{id}")
    public ResponseEntity<Void> supprimerDisponibilite(@PathVariable Long id) {
        planningService.supprimerDisponibilite(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/document/{id}")
    public ResponseEntity<Void> supprimerDocument(@PathVariable Long id) {
        planningService.supprimerDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/disponibilites/{entrepriseId}")
    public ResponseEntity<List<DisponibiliteEntreprise>> getDisponibilites(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(planningService.getDisponibilitesByEntreprise(entrepriseId));
    }

    @GetMapping("/documents/{entrepriseId}")
    public ResponseEntity<List<DocumentEntreprise>> getDocuments(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(planningService.getDocumentsByEntreprise(entrepriseId));
    }

    @PutMapping("/disponibilite/{id}")
    public ResponseEntity<DisponibiliteEntreprise> modifierDisponibilite(@PathVariable Long id,
                                                                         @RequestBody DisponibiliteEntreprise dto) {
        return ResponseEntity.ok(planningService.modifierDisponibilite(id, dto));
    }

    @PutMapping("/document/update/{id}")
    public ResponseEntity<DocumentEntreprise> modifierFichierDocument(
            @PathVariable Long id,
            @RequestParam("fichier") MultipartFile fichier) throws IOException {
        return ResponseEntity.ok(planningService.modifierFichierDocument(id, fichier));
    }



    @GetMapping("/document/telecharger/{filename:.+}")
    public ResponseEntity<Resource> telechargerDocument(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("uploads").resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("Fichier introuvable : " + filename);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/document/upload")
    public ResponseEntity<List<DocumentEntreprise>> ajouterDocuments(
            @RequestParam("fichiers") MultipartFile[] fichiers,
            @RequestParam("entrepriseId") Long entrepriseId) throws IOException {

        List<DocumentEntreprise> documents = new ArrayList<>();

        for (MultipartFile fichier : fichiers) {
            String nomFichier = UUID.randomUUID() + "_" + fichier.getOriginalFilename();
            Path filePath = Paths.get("uploads").resolve(nomFichier);
            Files.copy(fichier.getInputStream(), filePath);

            DocumentEntreprise doc = new DocumentEntreprise();
            doc.setNomFichier(nomFichier);
            doc.setUrlFichier("/api/document/telecharger/" + nomFichier);
            doc.setEntreprise(entrepriseRepository.findById(entrepriseId).orElse(null));

            documents.add(documentEntrepriseRepository.save(doc));
        }

        return ResponseEntity.ok(documents);
    }

}

