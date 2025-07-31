package com.example.ppe_302_backend.controller;

import com.example.ppe_302_backend.dto.EntrepriseRequest;
import com.example.ppe_302_backend.entity.DisponibiliteEntreprise;
import com.example.ppe_302_backend.entity.Entreprise;
import com.example.ppe_302_backend.entity.Utilisateur;
import com.example.ppe_302_backend.repository.ImageEntrepriseRepository;
import com.example.ppe_302_backend.repository.SecteurActiviteRepository;
import com.example.ppe_302_backend.repository.UtilisateurRepository;
import com.example.ppe_302_backend.service.EntrepriseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/entreprises")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class EntrepriseController {

    private final EntrepriseService entrepriseService;
    private final SecteurActiviteRepository secteurActiviteRepository;
    private final UtilisateurRepository utilisateurRepository; // Ajouté ici
    private final ImageEntrepriseRepository imageEntrepriseRepository;

    @PostMapping("/create-with-images")
    public ResponseEntity<?> creerEntrepriseAvecImages(
            @Valid @RequestPart("data") EntrepriseRequest request,
            @RequestPart("images") MultipartFile[] images,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile
    ) {
        Entreprise entreprise = entrepriseService.creerEntreprise(request, images, logoFile);
        return ResponseEntity.ok(entreprise);
    }


    @GetMapping("/by-prestataire/{email}")
    public ResponseEntity<?> getEntrepriseByPrestataire(@PathVariable String email) {
        List<Entreprise> entreprises = entrepriseService.getByPrestataireEmail(email);
        return ResponseEntity.ok(entreprises.isEmpty() ? null : entreprises.get(0));
    }

    @GetMapping("/secteurs")
    public ResponseEntity<?> getSecteurs() {
        return ResponseEntity.ok(secteurActiviteRepository.findAll());
    }

    // Obtenir utilisateur par email (route explicite)
    @GetMapping("/by-email/{email}")
    public ResponseEntity<?> getByEmailViaPath(@PathVariable String email) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isPresent()) {
            return ResponseEntity.ok(utilisateur.get());
        } else {
            return ResponseEntity.status(404).body("Utilisateur non trouvé.");
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntreprise(
            @PathVariable Long id,
            @Valid @RequestPart("data") EntrepriseRequest request,
            @RequestPart(value = "images", required = false) MultipartFile[] images,
            @RequestPart(value = "logo", required = false) MultipartFile logoFile
    ) {
        Entreprise updated = entrepriseService.updateEntreprise(id, request, images, logoFile);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/images/{id}")
    public ResponseEntity<?> supprimerImage(@PathVariable Long id) {
        imageEntrepriseRepository.deleteById(id);
        return ResponseEntity.ok("Image supprimée.");
    }
    @GetMapping
    public ResponseEntity<?> getAllEntreprises() {
        List<Entreprise> entreprises = entrepriseService.getAllEntreprises();
        return ResponseEntity.ok(entreprises);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getEntrepriseById(@PathVariable Long id) {
        Optional<Entreprise> entreprise = entrepriseService.getEntrepriseById(id);
        if (entreprise.isPresent()) {
            return ResponseEntity.ok(entreprise.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/disponibilites")
    public ResponseEntity<?> getDisponibilitesByEntreprise(@PathVariable Long id) {
        List<DisponibiliteEntreprise> disponibilites = entrepriseService.getDisponibilitesByEntreprise(id);
        return ResponseEntity.ok(disponibilites);
    }


}