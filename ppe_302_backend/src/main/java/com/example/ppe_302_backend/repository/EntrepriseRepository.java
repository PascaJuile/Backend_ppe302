package com.example.ppe_302_backend.repository;

import com.example.ppe_302_backend.entity.Entreprise;
import com.example.ppe_302_backend.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    List<Entreprise> findByPrestataire(Utilisateur prestataire);
}