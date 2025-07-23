package com.example.ppe_302_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DisponibiliteRequest {
    // Getters et setters
    private String jour;
    private String heureDebut;
    private String heureFin;

}

