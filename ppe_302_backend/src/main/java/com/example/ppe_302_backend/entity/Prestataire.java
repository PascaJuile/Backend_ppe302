package com.example.ppe_302_backend.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Prestataire extends Utilisateur{
    @ElementCollection
    private List<String> competences;
    private String disponibilite;
}
