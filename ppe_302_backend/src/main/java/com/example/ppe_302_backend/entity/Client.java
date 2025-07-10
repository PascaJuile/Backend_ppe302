package com.example.ppe_302_backend.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Client extends Utilisateur {
    // Ajoute ici des champs spécifiques au client si nécessaire plus tard
}
