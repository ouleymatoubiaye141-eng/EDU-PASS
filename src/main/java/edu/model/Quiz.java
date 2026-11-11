package edu.model;

import java.util.List;

/**
 * Un Quiz appartient à une matière, a un titre et contient une liste de questions.
 */
public class Quiz {
    private final int id;
    private String titre;
    private String matiere;
    private final String nomProfesseur;
    private final String dateAjout;
    private final List<Question> questions;

    public Quiz(int id, String titre, String matiere, String nomProfesseur,
                String dateAjout, List<Question> questions) {
        this.id = id;
        this.titre = titre;
        this.matiere = matiere;
        this.nomProfesseur = nomProfesseur;
        this.dateAjout = dateAjout;
        this.questions = questions;
    }

    public int getId()                   { return id; }
    public String getTitre()             { return titre; }
    public String getMatiere()           { return matiere; }
    public String getNomProfesseur()     { return nomProfesseur; }
    public String getDateAjout()         { return dateAjout; }
    public List<Question> getQuestions() { return questions; }
    public int getNbQuestions()          { return questions.size(); }
}
