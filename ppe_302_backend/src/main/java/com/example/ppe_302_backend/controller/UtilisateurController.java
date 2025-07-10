package com.example.ppe_302_backend.controller;

import com.example.ppe_302_backend.dto.FormulaireInscription;
import com.example.ppe_302_backend.dto.LoginRequest;
import com.example.ppe_302_backend.dto.LoginResponse;
import com.example.ppe_302_backend.entity.Client;
import com.example.ppe_302_backend.entity.Utilisateur;
import com.example.ppe_302_backend.repository.UtilisateurRepository;
import com.example.ppe_302_backend.security.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://localhost:8082")
public class UtilisateurController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Inscription
    @PostMapping("/inscription")
    public String inscrireUtilisateur(@RequestBody FormulaireInscription formulaireInscription) {
        Optional<Utilisateur> utilisateurExist = utilisateurRepository.findByEmail(formulaireInscription.getEmail());
        if (utilisateurExist.isPresent()) {
            return "Email déjà utilisé.";
        }

        Utilisateur utilisateur;
        if ("CLIENT".equalsIgnoreCase(formulaireInscription.getRole())) {
            utilisateur = new Client();
        } else {
            utilisateur = new Utilisateur();
        }

        utilisateur.setNom(formulaireInscription.getNom());
        utilisateur.setPrenom(formulaireInscription.getPrenom());
        utilisateur.setEmail(formulaireInscription.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(formulaireInscription.getMotDePasse()));
        utilisateur.setRole(formulaireInscription.getRole());
        utilisateur.setDerniereConnection(new Date());

        utilisateurRepository.save(utilisateur);
        return "Utilisateur inscrit avec succès!";
    }

    // ✅ Connexion
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(loginRequest.getEmail());
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé.");
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        if (!passwordEncoder.matches(loginRequest.getMotDePasse(), utilisateur.getMotDePasse())) {
            return ResponseEntity.status(401).body("Mot de passe incorrect.");
        }

        String token = JwtUtils.generateToken(utilisateur.getEmail(), utilisateur.getRole());
        return ResponseEntity.ok(new LoginResponse(token, utilisateur.getRole()));
    }

    // ✅ Obtenir utilisateur par email
    @GetMapping("/{email}")
    public ResponseEntity<?> getUtilisateurByEmail(@PathVariable String email) {
        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isPresent()) {
            return ResponseEntity.ok(utilisateur.get());
        } else {
            return ResponseEntity.status(404).body("Utilisateur non trouvé.");
        }
    }

    // ✅ Obtenir profil via token (Authorization header)
    @GetMapping("/profil")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token manquant ou mal formé.");
        }

        String token = authHeader.substring(7);
        String email = JwtUtils.getEmailFromToken(token);

        Optional<Utilisateur> utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur.isPresent()) {
            return ResponseEntity.ok(utilisateur.get());
        } else {
            return ResponseEntity.status(404).body("Utilisateur non trouvé.");
        }
    }

}
