package com.example.ppe_302_backend.service;

import com.example.ppe_302_backend.entity.SecteurActivite;
import com.example.ppe_302_backend.repository.SecteurActiviteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecteurActiviteService {

    private final SecteurActiviteRepository repository;

    public SecteurActiviteService(SecteurActiviteRepository repository) {
        this.repository = repository;
    }

    public List<SecteurActivite> getAllSecteurs() {
        return repository.findAll();
    }
}
