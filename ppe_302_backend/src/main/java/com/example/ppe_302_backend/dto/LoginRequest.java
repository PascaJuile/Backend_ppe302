package com.example.ppe_302_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String motDePasse;

    // Constructeur par défaut (requis par Spring)
    public LoginRequest() {}

    // Constructeur pour debug
    public LoginRequest(String email, String motDePasse) {
        this.email = email;
        this.motDePasse = motDePasse;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                ", motDePasse='" + (motDePasse != null ? "[HIDDEN]" : "null") + '\'' +
                '}';
    }
}
