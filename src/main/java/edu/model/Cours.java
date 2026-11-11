package edu.model;

public class Cours {
    private int id;
    private String titre;
    private String matiere;
    private String niveau;
    private String nomProfesseur;
    private String cheminPdf;
    private String dateAjout;

    public Cours(int id, String titre, String matiere, String niveau,
                 String nomProfesseur, String cheminPdf, String dateAjout) {
        this.id = id;
        this.titre = titre;
        this.matiere = matiere;
        this.niveau = niveau;
        this.nomProfesseur = nomProfesseur;
        this.cheminPdf = cheminPdf;
        this.dateAjout = dateAjout;
    }

    // Constructeur sans id (pour l'insertion — l'id est auto-généré par la BD)
    public Cours(String titre, String matiere, String niveau,
                 String nomProfesseur, String cheminPdf) {
        this(-1, titre, matiere, niveau, nomProfesseur, cheminPdf, null);
    }

    public int getId() { return id; }
    public String getTitre() { return titre; }
    public String getMatiere() { return matiere; }
    public String getNiveau() { return niveau; }
    public String getNomProfesseur() { return nomProfesseur; }
    public String getCheminPdf() { return cheminPdf; }
    public String getDateAjout() { return dateAjout; }

    @Override
    public String toString() {
        return "[" + id + "] " + titre + " — " + matiere + " (" + niveau + ") par " + nomProfesseur;
    }
}
