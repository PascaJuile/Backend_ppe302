package com.example.ppe_302_backend.controller;

import com.example.ppe_302_backend.entity.SecteurActivite;
import com.example.ppe_302_backend.repository.SecteurActiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/secteurs")
@CrossOrigin(origins = "http://localhost:8082")  // Autorise le front
public class SecteurActiviteController {

    @Autowired
    private SecteurActiviteRepository secteurRepo;

    @GetMapping
    public List<SecteurActivite> getAllSecteurs() {
        return secteurRepo.findAll();
    }
}

