package com.example.ppe_302_backend.controller;

import com.example.ppe_302_backend.dto.DemandeRdvRequest;
import com.example.ppe_302_backend.dto.RendezVousDto;
import com.example.ppe_302_backend.entity.DisponibiliteEntreprise;
import com.example.ppe_302_backend.entity.RendezVous;
import com.example.ppe_302_backend.repository.ClientRepository;
import com.example.ppe_302_backend.repository.DisponibiliteEntrepriseRepository;
import com.example.ppe_302_backend.repository.EntrepriseRepository;
import com.example.ppe_302_backend.repository.RendezVousRepository;
import com.example.ppe_302_backend.service.RendezVousService;
import com.example.ppe_302_backend.util.StatutRendezVous;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/rdv")
public class RendezVousController {

    private final RendezVousRepository rendezVousRepository;
    private final ClientRepository clientRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final DisponibiliteEntrepriseRepository disponibiliteEntrepriseRepository;
    private final RendezVousService rendezVousService;

    public RendezVousController(RendezVousRepository rendezVousRepository,
                                ClientRepository clientRepository,
                                EntrepriseRepository entrepriseRepository,
                                DisponibiliteEntrepriseRepository disponibiliteEntrepriseRepository,
                                RendezVousService rendezVousService) {
        this.rendezVousRepository = rendezVousRepository;
        this.clientRepository = clientRepository;
        this.entrepriseRepository = entrepriseRepository;
        this.disponibiliteEntrepriseRepository = disponibiliteEntrepriseRepository;
        this.rendezVousService = rendezVousService;
    }

    /* -------------------------
       Création d’une demande
       ------------------------- */
    @PostMapping("/demande")
    public ResponseEntity<?> creerDemande(@RequestBody DemandeRdvRequest req) {
        try {
            var entreprise = entrepriseRepository.findById(req.getEntrepriseId())
                    .orElseThrow(() -> new RuntimeException("Entreprise introuvable"));
            var dispo = disponibiliteEntrepriseRepository.findById(req.getDisponibiliteEntrepriseId())
                    .orElseThrow(() -> new RuntimeException("Disponibilité introuvable"));
            var client = clientRepository.findById(req.getClientId())
                    .orElseThrow(() -> new RuntimeException("Client introuvable"));

            RendezVous rdv = new RendezVous();
            rdv.setEntreprise(entreprise);
            rdv.setDisponibiliteEntreprise(dispo);
            rdv.setClient(client);
            rdv.setMotif(req.getMotif());
            rdv.setAdresseClient(req.getAdresseClient());
            rdv.setTelephoneClient(req.getTelephoneClient());
            rdv.setStatut(RendezVous.StatutRendezVous.EN_ATTENTE);

            RendezVous saved = rendezVousRepository.save(rdv);
            return ResponseEntity.ok(toDto(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /* -------------------------
       Actions : accepter / refuser
       ------------------------- */
    @PutMapping("/accepter/{demandeId}")
    public ResponseEntity<?> accepterDemande(@PathVariable Long demandeId) {
        try {
            RendezVous demande = rendezVousService.accepterDemande(demandeId);
            return ResponseEntity.ok(toDto(demande));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/refuser/{demandeId}")
    public ResponseEntity<?> refuserDemande(@PathVariable Long demandeId) {
        try {
            RendezVous demande = rendezVousService.refuserDemande(demandeId);
            return ResponseEntity.ok(toDto(demande));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /* -------------------------
       Récupération par statut
       ------------------------- */

    /** Tous les RDV (quel que soit le statut) d'une entreprise */
    @GetMapping("/demandes-entreprise/{entrepriseId}/all")
    public ResponseEntity<List<RendezVousDto>> getDemandesAll(@PathVariable Long entrepriseId) {
        List<RendezVous> list = rendezVousService.getDemandesParEntreprise(entrepriseId);
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    /** RDV EN_ATTENTE d'une entreprise */
    @GetMapping("/demandes-entreprise/{entrepriseId}/en-attente")
    public ResponseEntity<List<RendezVousDto>> getDemandesEnAttente(@PathVariable Long entrepriseId) {
        List<RendezVous> list = rendezVousService.getDemandesParEntrepriseEtStatut(
                entrepriseId, RendezVous.StatutRendezVous.EN_ATTENTE
        );
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    /** RDV ACCEPTES d'une entreprise */
    @GetMapping("/demandes-entreprise/{entrepriseId}/acceptes")
    public ResponseEntity<List<RendezVousDto>> getDemandesAcceptees(@PathVariable Long entrepriseId) {
        List<RendezVous> list = rendezVousService.getDemandesParEntrepriseEtStatut(
                entrepriseId, RendezVous.StatutRendezVous.ACCEPTE
        );
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    /** RDV REFUSES d'une entreprise */
    @GetMapping("/demandes-entreprise/{entrepriseId}/refuses")
    public ResponseEntity<List<RendezVousDto>> getDemandesRefusees(@PathVariable Long entrepriseId) {
        List<RendezVous> list = rendezVousService.getDemandesParEntrepriseEtStatut(
                entrepriseId, RendezVous.StatutRendezVous.REFUSE
        );
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    /* -------------------------
       Autres endpoints existants (facultatifs)
       ------------------------- */

    @GetMapping("/disponibles/{entrepriseId}")
    public ResponseEntity<List<DisponibiliteEntreprise>> getDisponibilitesDisponibles(@PathVariable Long entrepriseId) {
        List<DisponibiliteEntreprise> toutes = disponibiliteEntrepriseRepository.findByEntrepriseId(entrepriseId);
        List<DisponibiliteEntreprise> libres = toutes.stream()
                .filter(dispo -> rendezVousRepository
                        .findByDisponibiliteEntrepriseIdAndStatut(dispo.getId(), RendezVous.StatutRendezVous.ACCEPTE)
                        .isEmpty())
                .collect(Collectors.toList());
        return ResponseEntity.ok(libres);
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkRendezVousExist(@RequestBody Map<String, Object> payload) {
        Long disponibiliteEntrepriseId = Long.valueOf(payload.get("disponibiliteEntrepriseId").toString());
        Integer clientId = Integer.valueOf(payload.get("clientId").toString());

        List<RendezVous> existing = rendezVousRepository.findByDisponibiliteEntrepriseIdAndClientId(disponibiliteEntrepriseId, clientId);
        boolean exists = !existing.isEmpty();

        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @GetMapping("/client/{clientId}/mes-rendez-vous")
    public ResponseEntity<List<RendezVousDto>> getMesRendezVous(@PathVariable Long clientId) {
        try {
            List<RendezVous> rendezVousList = rendezVousRepository.findByClient_IdOrderByIdDesc((long) clientId.intValue());

            // Filtrer seulement les RDV EN_ATTENTE côté client (selon ton besoin)
            List<RendezVousDto> mesRdv = rendezVousList.stream()
                    .filter(rdv -> rdv.getStatut() == RendezVous.StatutRendezVous.EN_ATTENTE)
                    .map(this::toDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(mesRdv);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }

    @GetMapping("/entreprise/{entrepriseId}/statistiques")
    public ResponseEntity<Map<String, Long>> getStatistiques(@PathVariable Long entrepriseId) {
        try {
            List<RendezVous> tousRdv = rendezVousService.getDemandesParEntreprise(entrepriseId);

            Map<String, Long> stats = new HashMap<>();
            stats.put("recus", (long) tousRdv.size());
            stats.put("acceptes", tousRdv.stream()
                    .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.ACCEPTE)
                    .count());
            stats.put("refuses", tousRdv.stream()
                    .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.REFUSE)
                    .count());
            stats.put("tenus", tousRdv.stream()
                    .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.TENU)
                    .count());
            stats.put("nonTenus", tousRdv.stream()
                    .filter(r -> r.getStatut() == RendezVous.StatutRendezVous.NON_TENU)
                    .count());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new HashMap<>());
        }
    }

    /* -------------------------
       Mapper Entité -> DTO
       ------------------------- */
    private RendezVousDto toDto(RendezVous rdv) {
        RendezVousDto dto = new RendezVousDto();
        dto.setId(rdv.getId());
        dto.setMotif(rdv.getMotif());
        dto.setAdresseClient(rdv.getAdresseClient());
        dto.setTelephoneClient(rdv.getTelephoneClient());
        dto.setStatut(rdv.getStatut() != null ? rdv.getStatut().toString() : null);

        if (rdv.getDisponibiliteEntreprise() != null) {
            dto.setJour(rdv.getDisponibiliteEntreprise().getJour());
            dto.setHeureDebut(rdv.getDisponibiliteEntreprise().getHeureDebut());
            dto.setHeureFin(rdv.getDisponibiliteEntreprise().getHeureFin());
        }
        if (rdv.getClient() != null) {
            dto.setNomClient(rdv.getClient().getNom());
            dto.setPrenomClient(rdv.getClient().getPrenom());
            dto.setEmailClient(rdv.getClient().getEmail());
            dto.setClientId(rdv.getClient().getId()); // utile pour front notif
        }
        if (rdv.getEntreprise() != null) {
            dto.setNomEntreprise(rdv.getEntreprise().getNom());
        }
        return dto;
    }

    // RDV TENU
    @GetMapping("/demandes-entreprise/{entrepriseId}/tenus")
    public ResponseEntity<List<RendezVousDto>> getDemandesTenus(@PathVariable Long entrepriseId) {
        List<RendezVous> list = rendezVousService.getDemandesParEntrepriseEtStatut(
                entrepriseId, RendezVous.StatutRendezVous.TENU
        );
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    // RDV NON_TENU
    @GetMapping("/demandes-entreprise/{entrepriseId}/non-tenus")
    public ResponseEntity<List<RendezVousDto>> getDemandesNonTenus(@PathVariable Long entrepriseId) {
        List<RendezVous> list = rendezVousService.getDemandesParEntrepriseEtStatut(
                entrepriseId, RendezVous.StatutRendezVous.NON_TENU
        );
        return ResponseEntity.ok(list.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @PutMapping("/marquer-tenu/{id}")
    public ResponseEntity<RendezVous> marquerTenu(@PathVariable Long id) {
        RendezVous rdv = rendezVousService.marquerTenu(id);
        return ResponseEntity.ok(rdv);
    }

    @PutMapping("/marquer-non-tenu/{id}")
    public ResponseEntity<RendezVous> marquerNonTenu(@PathVariable Long id) {
        RendezVous rdv = rendezVousService.marquerNonTenu(id);
        return ResponseEntity.ok(rdv);
    }

}
