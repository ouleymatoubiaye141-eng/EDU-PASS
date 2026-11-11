package edu.dao;

import edu.model.Professeur;

import java.util.Arrays;
import java.util.List;

public class ProfesseurDAO {

    private static final List<Professeur> PROFESSEURS = Arrays.asList(
        new Professeur(1, "Kasse", "Mme", "PROF001", "Mathématiques"),
        new Professeur(2, "Deme",  "Mr",  "PROF002", "Physique-Chimie")
    );

    public Professeur findByIdentifiants(String nom, String prenom, String code) {
        return PROFESSEURS.stream()
            .filter(p ->
                p.getNom().equalsIgnoreCase(nom.trim()) &&
                p.getPrenom().equalsIgnoreCase(prenom.trim()) &&
                p.getCode().equalsIgnoreCase(code.trim())
            )
            .findFirst()
            .orElse(null);
    }

    public List<Professeur> findAll() {
        return PROFESSEURS;
    }
}
