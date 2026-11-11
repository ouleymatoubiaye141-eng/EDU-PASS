package edu.model;

public class Eleve extends Utilisateur {
    private final String classe = "3ème Collège";

    public Eleve(int id, String nom, String prenom, String code, String classeIgnored) {
        super(id, nom, prenom, code);
    }

    public String getClasse() { return classe; }

    @Override
    public String getRole() { return "ELEVE"; }
}
