package com.example.ppe_302_backend.repository;

import com.example.ppe_302_backend.entity.DisponibiliteEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisponibiliteEntrepriseRepository extends JpaRepository<DisponibiliteEntreprise, Long> {
    List<DisponibiliteEntreprise> findByEntrepriseId(Long entrepriseId);
}
