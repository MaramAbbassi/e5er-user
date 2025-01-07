package com.example.user;


import jakarta.json.bind.annotation.JsonbTransient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Pokemon {


    private Long id;

    private String nom;
    private String description;
    private int miseAPrix;
    private double valeurReelle;





    @JsonbTransient
    private List<String> types;


    @JsonbTransient
    private Map<String, Integer> stats;


    @JsonbTransient
    private Map<Long, Integer> historique_encheres = new HashMap<>(); // On stocke id_utilisateur et montant



    public Pokemon() {}

    public Pokemon(String nom, String description, double valeurReelle) {
        this.nom = nom;
        this.description = description;
        this.valeurReelle = valeurReelle;
        this.miseAPrix = (int) (valeurReelle * (0.6 + Math.random() * 0.8));
    }


    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMiseAPrix() {
        return miseAPrix;
    }

    public void setMiseAPrix(int miseAPrix) {
        this.miseAPrix = miseAPrix;
    }

    public double getValeurReelle() {
        return valeurReelle;
    }

    public void setValeurReelle(double valeurReelle) {
        this.valeurReelle = valeurReelle;
    }

    public Map<Long, Integer> getHistorique_encheres() {return this.historique_encheres;}



    public void ajouterEnchere(Long utilisateurId, int montant) {
        historique_encheres.put(utilisateurId, montant);
    }

    public void supprimerEnchere(Long utilisateurId) {
        historique_encheres.remove(utilisateurId);
    }

    public Integer getMontantEnchere(Long utilisateurId) {
        return historique_encheres.get(utilisateurId);
    }





    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public void setStats(Map<String, Integer> stats) {
        this.stats = stats;
    }

}

