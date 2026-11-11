package edu.model;

public class Professeur extends Utilisateur {
    private String matiere;

    public Professeur(int id, String nom, String prenom, String code, String matiere) {
        super(id, nom, prenom, code);
        this.matiere = matiere;
    }

    public String getMatiere() { return matiere; }

    @Override
    public String getRole() { return "PROFESSEUR"; }
}
