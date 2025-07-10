package com.example.ppe_302_backend.service;

import com.example.ppe_302_backend.entity.Client;
import com.example.ppe_302_backend.repository.ClientRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public Client ajouter(Client client) {
        client.setRole("CLIENT");
        return clientRepository.save(client);
    }

    @Transactional
    public List<Client> lireTous() {
        return clientRepository.findAll();
    }

    @Transactional
    public Client lireParId(Long id) {
        return clientRepository.findById(id).orElse(null);
    }
}
