package com.example.ppe_302_backend.dto;

public class DemandeRdvRequest {
    private Long entrepriseId;
    private Long disponibiliteEntrepriseId;
    private Long clientId;
    private String motif;
    private String adresseClient;
    private String telephoneClient;

    public Long getEntrepriseId() { return entrepriseId; }
    public void setEntrepriseId(Long entrepriseId) { this.entrepriseId = entrepriseId; }

    public Long getDisponibiliteEntrepriseId() { return disponibiliteEntrepriseId; }
    public void setDisponibiliteEntrepriseId(Long disponibiliteEntrepriseId) { this.disponibiliteEntrepriseId = disponibiliteEntrepriseId; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }

    public String getAdresseClient() { return adresseClient; }
    public void setAdresseClient(String adresseClient) { this.adresseClient = adresseClient; }

    public String getTelephoneClient() { return telephoneClient; }
    public void setTelephoneClient(String telephoneClient) { this.telephoneClient = telephoneClient; }
}
