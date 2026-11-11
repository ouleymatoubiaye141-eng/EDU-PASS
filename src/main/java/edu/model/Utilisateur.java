package edu.model;

public abstract class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String code;

    public Utilisateur(int id, String nom, String prenom, String code) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.code = code;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getCode() { return code; }

    public abstract String getRole();

    @Override
    public String toString() {
        return prenom + " " + nom + " [" + getRole() + "]";
    }
}
