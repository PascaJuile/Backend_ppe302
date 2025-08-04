package com.example.ppe_302_backend.controller;

import com.example.ppe_302_backend.dto.*;
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
            utilisateur = new Utilisateur();
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
        // Renvoie token + role + user complet
        return ResponseEntity.ok(new LoginResponse(token, utilisateur.getRole(), utilisateur));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Pas de vraie action car le JWT est stateless
        return ResponseEntity.ok("Déconnecté avec succès.");
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

    @PutMapping("/profil")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody UpdateUtilisateurRequest payload) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token manquant ou mal formé.");
        }

        String token = authHeader.substring(7);
        String emailFromToken = JwtUtils.getEmailFromToken(token);

        Optional<Utilisateur> opt = utilisateurRepository.findByEmail(emailFromToken);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé.");
        }

        Utilisateur u = opt.get();
        u.setNom(payload.getNom());
        u.setPrenom(payload.getPrenom());
        u.setEmail(payload.getEmail());

        utilisateurRepository.save(u);
        return ResponseEntity.ok(u);
    }

    @PutMapping("/profil/password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest payload) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token manquant ou mal formé.");
        }

        if (payload.getNewPassword() == null || payload.getConfirmPassword() == null
                || !payload.getNewPassword().equals(payload.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Les mots de passe ne correspondent pas.");
        }

        String token = authHeader.substring(7);
        String emailFromToken = JwtUtils.getEmailFromToken(token);

        Optional<Utilisateur> opt = utilisateurRepository.findByEmail(emailFromToken);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé.");
        }

        Utilisateur u = opt.get();

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(payload.getCurrentPassword(), u.getMotDePasse())) {
            return ResponseEntity.status(401).body("Mot de passe actuel incorrect.");
        }

        u.setMotDePasse(passwordEncoder.encode(payload.getNewPassword()));
        utilisateurRepository.save(u);

        return ResponseEntity.ok("Mot de passe modifié avec succès.");
    }

    @DeleteMapping("/profil")
    public ResponseEntity<?> deleteProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Token manquant ou mal formé.");
        }

        String token = authHeader.substring(7);
        String emailFromToken = JwtUtils.getEmailFromToken(token);

        Optional<Utilisateur> opt = utilisateurRepository.findByEmail(emailFromToken);
        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body("Utilisateur non trouvé.");
        }

        utilisateurRepository.delete(opt.get());
        return ResponseEntity.noContent().build();
    }

}
