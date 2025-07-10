package com.example.ppe_302_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntrepriseRequest {

    @NotBlank(message = "Le nom de l'entreprise est obligatoire")
    private String nom;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "L'adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    @NotBlank(message = "L'email de l'entreprise est obligatoire")
    @Email(message = "L'email de l'entreprise est invalide")
    private String email;

    private String siteWeb; // optionnel

    @NotBlank(message = "L'email du prestataire est obligatoire")
    @Email(message = "L'email du prestataire est invalide")
    private String prestataireEmail;

    @NotNull(message = "Le secteur d'activité est obligatoire")
    private Long secteurActiviteId;
}
