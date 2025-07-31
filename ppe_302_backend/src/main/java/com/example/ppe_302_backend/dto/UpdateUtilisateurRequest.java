package com.example.ppe_302_backend.dto;

import lombok.Data;

@Data
public class UpdateUtilisateurRequest {
    private String nom;
    private String prenom;
    private String email;
}
