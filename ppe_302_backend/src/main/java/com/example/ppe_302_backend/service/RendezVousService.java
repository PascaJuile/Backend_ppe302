package com.example.ppe_302_backend.service;

import com.example.ppe_302_backend.entity.DisponibiliteEntreprise;
import com.example.ppe_302_backend.entity.RendezVous;
import com.example.ppe_302_backend.repository.DisponibiliteEntrepriseRepository;
import com.example.ppe_302_backend.repository.RendezVousRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final DisponibiliteEntrepriseRepository disponibiliteEntrepriseRepository;

    public RendezVousService(RendezVousRepository rendezVousRepository,
                             DisponibiliteEntrepriseRepository disponibiliteEntrepriseRepository) {
        this.rendezVousRepository = rendezVousRepository;
        this.disponibiliteEntrepriseRepository = disponibiliteEntrepriseRepository;
    }

    @Transactional
    public RendezVous creerDemande(RendezVous rendezVous) {
        DisponibiliteEntreprise dispo = disponibiliteEntrepriseRepository.findById(
                        rendezVous.getDisponibiliteEntreprise().getId())
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée"));

        // Vérifier qu'il n'y a pas déjà un RDV accepté pour cette dispo
        List<RendezVous> rdvExistants = rendezVousRepository.findByDisponibiliteEntreprise_IdAndStatut(
                dispo.getId(), RendezVous.StatutRendezVous.ACCEPTE);

        if (!rdvExistants.isEmpty()) {
            throw new RuntimeException("Cette disponibilité n'est plus disponible");
        }

        rendezVous.setStatut(RendezVous.StatutRendezVous.EN_ATTENTE);
        return rendezVousRepository.save(rendezVous);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getDemandesParEntreprise(Long entrepriseId) {
        return rendezVousRepository.findByEntreprise_IdOrderByIdDesc(entrepriseId);
    }

    @Transactional
    public RendezVous accepterDemande(Long demandeId) {
        RendezVous demande = rendezVousRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));
        System.out.println("Accepter demande : ID=" + demande.getId() + ", statut actuel=" + demande.getStatut());

        if (demande.getStatut() == RendezVous.StatutRendezVous.ACCEPTE) {
            System.out.println("Déjà accepté");
            return demande;
        }

        // Refuser les autres demandes en attente pour la même dispo
        Long dispoId = demande.getDisponibiliteEntreprise().getId();
        List<RendezVous> autres = rendezVousRepository.findByDisponibiliteEntreprise_IdAndStatut(
                dispoId, RendezVous.StatutRendezVous.EN_ATTENTE);

        for (RendezVous autre : autres) {
            if (!autre.getId().equals(demandeId)) {
                autre.setStatut(RendezVous.StatutRendezVous.REFUSE);
                System.out.println("Refuser demande ID=" + autre.getId());
            }
        }
        rendezVousRepository.saveAll(autres);

        demande.setStatut(RendezVous.StatutRendezVous.ACCEPTE);
        RendezVous sauvegarde = rendezVousRepository.save(demande);
        System.out.println("Demande ID=" + demande.getId() + " acceptée, statut=" + sauvegarde.getStatut());
        return sauvegarde;
    }

    @Transactional
    public RendezVous refuserDemande(Long demandeId) {
        RendezVous demande = rendezVousRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Demande non trouvée"));

        demande.setStatut(RendezVous.StatutRendezVous.REFUSE);
        return rendezVousRepository.save(demande);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getDemandesByPrestataireId(Long prestataireId) {
        return rendezVousRepository.findByPrestataire_Entreprise_Id(prestataireId);
    }

    @Transactional(readOnly = true)
    public List<RendezVous> getDemandesParEntrepriseEtStatut(Long entrepriseId, RendezVous.StatutRendezVous statut) {
        return rendezVousRepository.findByEntreprise_IdAndStatut(entrepriseId, statut);
    }

    public RendezVous marquerTenu(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        rdv.setStatut(RendezVous.StatutRendezVous.TENU);
        return rendezVousRepository.save(rdv);
    }

    public RendezVous marquerNonTenu(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
        rdv.setStatut(RendezVous.StatutRendezVous.NON_TENU);
        return rendezVousRepository.save(rdv);
    }

}
