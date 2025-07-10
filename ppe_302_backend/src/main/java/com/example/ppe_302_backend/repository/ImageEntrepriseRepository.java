package com.example.ppe_302_backend.repository;

import com.example.ppe_302_backend.entity.ImageEntreprise;
import com.example.ppe_302_backend.entity.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImageEntrepriseRepository extends JpaRepository<ImageEntreprise, Long> {
    List<ImageEntreprise> findByEntreprise(Entreprise entreprise);
}