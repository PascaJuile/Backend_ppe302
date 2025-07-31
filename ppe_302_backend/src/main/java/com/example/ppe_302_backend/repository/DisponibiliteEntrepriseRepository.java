package com.example.ppe_302_backend.repository;

import com.example.ppe_302_backend.entity.DisponibiliteEntreprise;
import com.example.ppe_302_backend.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibiliteEntrepriseRepository extends JpaRepository<DisponibiliteEntreprise, Long> {

    // Permet de récupérer toutes les disponibilités d'une entreprise à partir de son ID
    List<DisponibiliteEntreprise> findByEntrepriseId(Long entrepriseId);

    <Disponibilite> List<Disponibilite> findByEntreprise(Entreprise entreprise);
}
