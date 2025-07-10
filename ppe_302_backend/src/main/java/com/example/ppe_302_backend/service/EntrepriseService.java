package com.example.ppe_302_backend.service;

import com.example.ppe_302_backend.dto.EntrepriseRequest;
import com.example.ppe_302_backend.entity.*;
import com.example.ppe_302_backend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ImageEntrepriseRepository imageEntrepriseRepository;
    private final ImageStorageService imageStorageService;
    private final SecteurActiviteRepository secteurActiviteRepository;

    public Entreprise creerEntreprise(EntrepriseRequest request, MultipartFile[] images, MultipartFile logoFile) {
        Utilisateur prestataire = utilisateurRepository.findByEmail(request.getPrestataireEmail())
                .orElseThrow(() -> new RuntimeException("Prestataire introuvable."));

        SecteurActivite secteur = secteurActiviteRepository.findById(request.getSecteurActiviteId())
                .orElseThrow(() -> new RuntimeException("Secteur d'activité introuvable"));

        Entreprise entreprise = new Entreprise();
        entreprise.setNom(request.getNom());
        entreprise.setDescription(request.getDescription());
        entreprise.setAdresse(request.getAdresse());
        entreprise.setTelephone(request.getTelephone());
        entreprise.setEmail(request.getEmail());
        entreprise.setSiteWeb(request.getSiteWeb());
        entreprise.setPrestataire(prestataire);
        entreprise.setSecteurActivite(secteur);

        // Upload du logo
        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String logoUrl = imageStorageService.store(logoFile);
                entreprise.setLogoUrl(logoUrl);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du téléversement du logo.");
            }
        }

        entreprise = entrepriseRepository.save(entreprise);

        // Upload des images multiples
        for (MultipartFile file : images) {
            if (file != null && !file.isEmpty()) {
                try {
                    String imageUrl = imageStorageService.store(file);
                    ImageEntreprise img = new ImageEntreprise();
                    img.setImageUrl(imageUrl);
                    img.setEntreprise(entreprise);
                    imageEntrepriseRepository.save(img);
                } catch (IOException e) {
                    throw new RuntimeException("Erreur d'upload image: " + file.getOriginalFilename());
                }
            }
        }

        return entreprise;
    }

    public List<Entreprise> getByPrestataireEmail(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return entrepriseRepository.findByPrestataire(user);
    }

    public Entreprise updateEntreprise(Long id, EntrepriseRequest request, MultipartFile[] images, MultipartFile logoFile) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));

        entreprise.setNom(request.getNom());
        entreprise.setDescription(request.getDescription());
        entreprise.setAdresse(request.getAdresse());
        entreprise.setTelephone(request.getTelephone());
        entreprise.setEmail(request.getEmail());
        entreprise.setSiteWeb(request.getSiteWeb());

        if (logoFile != null && !logoFile.isEmpty()) {
            try {
                String logoUrl = imageStorageService.store(logoFile);
                entreprise.setLogoUrl(logoUrl);
            } catch (IOException e) {
                throw new RuntimeException("Erreur logo", e);
            }
        }

        entreprise = entrepriseRepository.save(entreprise);

        if (images != null) {
            for (MultipartFile file : images) {
                if (!file.isEmpty()) {
                    try {
                        String imageUrl = imageStorageService.store(file);
                        ImageEntreprise image = new ImageEntreprise();
                        image.setImageUrl(imageUrl);
                        image.setEntreprise(entreprise);
                        imageEntrepriseRepository.save(image);
                    } catch (IOException e) {
                        throw new RuntimeException("Erreur image: " + file.getOriginalFilename());
                    }
                }
            }
        }

        return entreprise;
    }

}
