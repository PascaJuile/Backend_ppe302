package com.example.ppe_302_backend.repository;

import com.example.ppe_302_backend.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByEntrepriseId(Long entrepriseId);

    @Query("SELECT AVG(e.note) FROM Evaluation e WHERE e.entreprise.id = :entrepriseId")
    Double getMoyenneNoteByEntrepriseId(Long entrepriseId);

    Long countByEntrepriseId(Long entrepriseId);
}