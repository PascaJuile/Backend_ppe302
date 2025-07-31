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
    private String telephone;
    private String adresse;
}
