package com.example.ppe_302_backend.service;

import com.example.ppe_302_backend.entity.Utilisateur;
import com.example.ppe_302_backend.repository.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UtilisateurService {
    @Autowired
    protected UtilisateurRepository utilisateurRepository; // Change private en protected

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Utilisateur inscription(Utilisateur utilisateur) {
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public Utilisateur connection(String email, String motDePasse) {
        return utilisateurRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(motDePasse, u.getMotDePasse()))
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));
    }


    @Transactional
    public Utilisateur creer(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public List<Utilisateur> lire() {
        return utilisateurRepository.findAll();
    }

}
