package com.example.ppe_302_backend.repository;

import com.example.ppe_302_backend.entity.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByEntrepriseIdOrderByIdDesc(Long entrepriseId);

    List<RendezVous> findByDisponibiliteEntrepriseIdAndStatut(Long disponibiliteEntrepriseId, RendezVous.StatutRendezVous statut);

    List<RendezVous> findByClient_IdOrderByIdDesc(Long clientId);


    // ✅ Nouvelle méthode pour vérifier si un client a déjà pris un rendez-vous sur une disponibilité
    List<RendezVous> findByDisponibiliteEntrepriseIdAndClientId(Long disponibiliteEntrepriseId, Integer clientId);

    List<RendezVous> findByClientId(Long id);



    List<RendezVous> findByPrestataire_Entreprise_Id(Long entrepriseId);

    List<RendezVous> findByEntrepriseIdAndStatut(Long entrepriseId, RendezVous.StatutRendezVous statut);

    // (optionnel) si tu veux trier :
    List<RendezVous> findByEntrepriseIdAndStatutOrderByIdDesc(Long entrepriseId, RendezVous.StatutRendezVous statut);


    List<RendezVous> findByEntreprise_IdAndStatut(Long entrepriseId, RendezVous.StatutRendezVous statut);

    List<RendezVous> findByDisponibiliteEntreprise_IdAndStatut(Long id, RendezVous.StatutRendezVous statutRendezVous);

    List<RendezVous> findByEntreprise_IdOrderByIdDesc(Long entrepriseId);
}
