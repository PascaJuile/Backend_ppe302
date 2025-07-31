package com.example.ppe_302_backend.controller;

import com.example.ppe_302_backend.entity.Evaluation;
import com.example.ppe_302_backend.repository.EvaluationRepository;
import com.example.ppe_302_backend.repository.ClientRepository;
import com.example.ppe_302_backend.repository.EntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "http://localhost:8080")
public class EvaluationController {

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private EntrepriseRepository entrepriseRepository;

    @PostMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<Evaluation> evaluerEntreprise(
            @PathVariable Long entrepriseId,
            @RequestParam Long clientId,
            @RequestParam Integer note,
            @RequestParam(required = false) String commentaire) {

        Evaluation evaluation = new Evaluation();
        evaluation.setClient(clientRepository.findById(clientId).orElse(null));
        evaluation.setEntreprise(entrepriseRepository.findById(entrepriseId).orElse(null));
        evaluation.setNote(note);
        evaluation.setCommentaire(commentaire);

        return ResponseEntity.ok(evaluationRepository.save(evaluation));
    }

    @GetMapping("/entreprise/{entrepriseId}")
    public ResponseEntity<List<Evaluation>> getEvaluations(@PathVariable Long entrepriseId) {
        return ResponseEntity.ok(evaluationRepository.findByEntrepriseId(entrepriseId));
    }

    @GetMapping("/entreprise/{entrepriseId}/moyenne")
    public ResponseEntity<Map<String, Object>> getMoyenneEvaluation(@PathVariable Long entrepriseId) {
        Double moyenne = evaluationRepository.getMoyenneNoteByEntrepriseId(entrepriseId);
        Long total = evaluationRepository.countByEntrepriseId(entrepriseId);

        Map<String, Object> result = new HashMap<>();
        result.put("moyenne", moyenne != null ? moyenne : 0.0);
        result.put("total", total);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/entreprise/{entrepriseId}/statistiques")
    public ResponseEntity<Map<String, Long>> getStatistiquesEvaluations(@PathVariable Long entrepriseId) {
        List<Evaluation> evaluations = evaluationRepository.findByEntrepriseId(entrepriseId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", (long) evaluations.size());
        stats.put("note5", evaluations.stream().filter(e -> e.getNote() == 5).count());
        stats.put("note4", evaluations.stream().filter(e -> e.getNote() == 4).count());
        stats.put("note3", evaluations.stream().filter(e -> e.getNote() == 3).count());
        stats.put("note2", evaluations.stream().filter(e -> e.getNote() == 2).count());
        stats.put("note1", evaluations.stream().filter(e -> e.getNote() == 1).count());

        return ResponseEntity.ok(stats);
    }
}