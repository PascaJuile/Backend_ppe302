package com.example.ppe_302_backend.repository;

import com.example.ppe_302_backend.entity.DocumentEntreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentEntrepriseRepository extends JpaRepository<DocumentEntreprise, Long> {
    List<DocumentEntreprise> findByEntrepriseId(Long entrepriseId);
}
