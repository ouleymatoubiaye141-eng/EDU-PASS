package edu.model;

import java.util.List;

/**
 * Représente une question d'un Quiz avec 4 choix et une bonne réponse.
 */
public class Question {
    private final int id;
    private final String enonce;
    private final List<String> choix;   // 4 choix
    private final int bonneReponse;     // index 0-3

    public Question(int id, String enonce, List<String> choix, int bonneReponse) {
        this.id = id;
        this.enonce = enonce;
        this.choix = choix;
        this.bonneReponse = bonneReponse;
    }

    public int getId()            { return id; }
    public String getEnonce()     { return enonce; }
    public List<String> getChoix() { return choix; }
    public int getBonneReponse()  { return bonneReponse; }
    public String getTexteBonneReponse() { return choix.get(bonneReponse); }
}
