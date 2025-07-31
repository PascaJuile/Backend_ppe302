package com.example.ppe_302_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class RendezVous {

    public enum StatutRendezVous {
        EN_ATTENTE, ACCEPTE, REFUSE, NON_TENU, TENU, REJETE
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JsonBackReference
    private Entreprise entreprise;

    @ManyToOne
    private Client client;

    @ManyToOne
    private DisponibiliteEntreprise disponibiliteEntreprise;

    @Enumerated(EnumType.STRING)
    private StatutRendezVous statut;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime heure;

    private String motif;
    private String telephoneClient;
    private String adresseClient;

    @ManyToOne
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;
}
