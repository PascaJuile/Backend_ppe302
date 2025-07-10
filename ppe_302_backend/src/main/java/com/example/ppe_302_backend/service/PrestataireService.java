package com.example.ppe_302_backend.service;

import com.example.ppe_302_backend.entity.Prestataire;
import com.example.ppe_302_backend.repository.PrestataireRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PrestataireService {

    @Autowired
    private PrestataireRepository prestataireRepository;

    @Transactional
    public Prestataire ajouter(Prestataire prestataire) {
        prestataire.setRole("PRESTATAIRE");
        return prestataireRepository.save(prestataire);
    }

    @Transactional
    public List<Prestataire> lireTous() {
        return prestataireRepository.findAll();
    }

    @Transactional
    public Prestataire lireParId(int id) {
        return prestataireRepository.findById(id).orElse(null);
    }

}
