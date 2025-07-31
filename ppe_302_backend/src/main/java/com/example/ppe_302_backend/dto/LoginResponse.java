package com.example.ppe_302_backend.dto;

import com.example.ppe_302_backend.entity.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
    private Utilisateur user;
}
