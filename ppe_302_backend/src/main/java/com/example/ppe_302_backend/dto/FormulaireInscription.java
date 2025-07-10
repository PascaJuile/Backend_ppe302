package com.example.ppe_302_backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FormulaireInscription {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String confirmationMotDePasse;
    private String role; // ✅ Ajout du champ role

    /**
     * Vérifie si le mot de passe et la confirmation du mot de passe sont identiques.
     *
     * @return true si les mots de passe sont identiques, false sinon.
     */
    public boolean isMotDePasseValide() {
        if (motDePasse == null || confirmationMotDePasse == null) {
            return false;
        }
        return motDePasse.equals(confirmationMotDePasse);
    }

    // ✅ Ce getter est désormais inutile car généré automatiquement par Lombok (@Getter)
    // public String getRole() {
    //     return role;
    // }
}
