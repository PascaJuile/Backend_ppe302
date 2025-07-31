package com.example.ppe_302_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String description;
    private String adresse;
    private String telephone;
    private String email;
    private String siteWeb;
    private String logoUrl;
    private String localisation;

    @ManyToOne
    @JoinColumn(name = "prestataire_id")
    private Utilisateur prestataire;

    @ManyToOne
    @JoinColumn(name = "secteur_activite_id")
    private SecteurActivite secteurActivite;

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ImageEntreprise> images;

    @OneToMany(mappedBy = "entreprise")
    @JsonManagedReference
    private List<DocumentEntreprise> documents;

    @OneToMany(mappedBy = "entreprise")
    @JsonManagedReference
    private List<RendezVous> rendezVous;

    @OneToMany(
            mappedBy = "entreprise",
            cascade = CascadeType.ALL,     // propage persist/merge/remove
            orphanRemoval = true,          // supprime la dispo si retirée de la collection
            fetch = FetchType.LAZY
    )
    @JsonManagedReference("entreprise-disponibilites")
    private List<DisponibiliteEntreprise> disponibilites = new ArrayList<>();

    // helpers pour garantir la cohérence
    public void addDisponibilite(DisponibiliteEntreprise d) {
        disponibilites.add(d);
        d.setEntreprise(this);
    }

    public void removeDisponibilite(DisponibiliteEntreprise d) {
        disponibilites.remove(d);
        d.setEntreprise(null);
    }

}
