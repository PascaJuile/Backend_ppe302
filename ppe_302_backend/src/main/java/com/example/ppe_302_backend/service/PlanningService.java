package com.example.ppe_302_backend.service;

import com.example.ppe_302_backend.entity.DisponibiliteEntreprise;
import com.example.ppe_302_backend.entity.DocumentEntreprise;
import com.example.ppe_302_backend.entity.Entreprise;
import com.example.ppe_302_backend.repository.DisponibiliteEntrepriseRepository;
import com.example.ppe_302_backend.repository.DocumentEntrepriseRepository;
import com.example.ppe_302_backend.repository.EntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class PlanningService {

    private final DocumentEntrepriseRepository documentRepo;
    private final EntrepriseRepository entrepriseRepository;
    private final DisponibiliteEntrepriseRepository disponibiliteRepo;
    private final FileStorageService fileStorageService;

    @Autowired
    public PlanningService(
            DocumentEntrepriseRepository documentRepo,
            EntrepriseRepository entrepriseRepository,
            DisponibiliteEntrepriseRepository disponibiliteRepo,
            FileStorageService fileStorageService
    ) {
        this.documentRepo = documentRepo;
        this.entrepriseRepository = entrepriseRepository;
        this.disponibiliteRepo = disponibiliteRepo;
        this.fileStorageService = fileStorageService;
    }

    public Entreprise updateLocalisation(Long entrepriseId, String localisation) {
        Entreprise e = entrepriseRepository.findById(entrepriseId).orElseThrow();
        e.setLocalisation(localisation);
        return entrepriseRepository.save(e);
    }

    public DocumentEntreprise ajouterDocument(Long entrepriseId, MultipartFile file) throws IOException {
        String nomFichier = fileStorageService.storeFile(file);
        String url = "/api/planning/document/telecharger/" + nomFichier;

        DocumentEntreprise doc = new DocumentEntreprise();
        doc.setNomFichier(nomFichier);
        doc.setUrlFichier(url);
        doc.setEntreprise(entrepriseRepository.findById(entrepriseId).orElseThrow());
        return documentRepo.save(doc);
    }

    public DisponibiliteEntreprise ajouterDisponibilite(Long entrepriseId, String jour, String debut, String fin) {
        DisponibiliteEntreprise d = new DisponibiliteEntreprise();
        d.setJour(jour);
        d.setHeureDebut(debut);
        d.setHeureFin(fin);
        d.setEntreprise(entrepriseRepository.findById(entrepriseId).orElseThrow());
        return disponibiliteRepo.save(d);
    }

    public List<DisponibiliteEntreprise> getDisponibilitesByEntreprise(Long entrepriseId) {
        return disponibiliteRepo.findByEntrepriseId(entrepriseId);
    }

    public List<DocumentEntreprise> getDocumentsByEntreprise(Long entrepriseId) {
        return documentRepo.findByEntrepriseId(entrepriseId);
    }

    public void supprimerDisponibilite(Long disponibiliteId) {
        disponibiliteRepo.deleteById(disponibiliteId);
    }

    public void supprimerDocument(Long documentId) {
        documentRepo.deleteById(documentId);
    }

    public DisponibiliteEntreprise modifierDisponibilite(Long id, DisponibiliteEntreprise dto) {
        DisponibiliteEntreprise dispo = disponibiliteRepo.findById(id).orElseThrow();
        dispo.setJour(dto.getJour());
        dispo.setHeureDebut(dto.getHeureDebut());
        dispo.setHeureFin(dto.getHeureFin());
        return disponibiliteRepo.save(dispo);
    }

    public DocumentEntreprise modifierFichierDocument(Long id, MultipartFile fichier) throws IOException {
        DocumentEntreprise doc = documentRepo.findById(id).orElseThrow();

        // Supprimer ancien fichier
        Path oldPath = Paths.get("uploads").resolve(doc.getNomFichier());
        Files.deleteIfExists(oldPath);

        // Enregistrer nouveau fichier via service
        String nomFichier = fileStorageService.storeFile(fichier);
        String url = "/api/planning/document/telecharger/" + nomFichier;

        doc.setNomFichier(nomFichier);
        doc.setUrlFichier(url);

        return documentRepo.save(doc);
    }
}
