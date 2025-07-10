package com.example.ppe_302_backend.repository;

import com.example.ppe_302_backend.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
    // Méthodes personnalisées ici si nécessaire
}
