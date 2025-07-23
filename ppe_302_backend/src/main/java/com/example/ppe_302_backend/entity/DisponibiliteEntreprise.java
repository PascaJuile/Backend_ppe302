package com.example.ppe_302_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisponibiliteEntreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jour;
    private String heureDebut;
    private String heureFin;

    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    @JsonBackReference("entreprise-disponibilites")
    private Entreprise entreprise;
}
