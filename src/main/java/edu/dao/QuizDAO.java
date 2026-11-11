package edu.dao;

import edu.model.Question;
import edu.model.Quiz;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    private static final List<Quiz> QUIZ_LIST = new ArrayList<>(List.of(

        new Quiz(1, "Quiz Algèbre — Équations", "Mathématiques", "Mme Kasse", "10/03/2026", List.of(
            new Question(1, "Quelle est la solution de 2x + 4 = 10 ?",
                List.of("x = 2", "x = 3", "x = 4", "x = 7"), 1),
            new Question(2, "Si 3x = 15, alors x vaut ?",
                List.of("3", "4", "5", "6"), 2),
            new Question(3, "Développe (x + 2)(x - 2) ?",
                List.of("x² - 4", "x² + 4", "x² - 2x + 4", "2x² - 4"), 0),
            new Question(4, "Quelle est la solution de x - 7 = 0 ?",
                List.of("x = -7", "x = 0", "x = 7", "x = 14"), 2),
            new Question(5, "Simplifie 4x + 3x - 2x ?",
                List.of("5x", "9x", "3x", "7x"), 0)
        )),

        new Quiz(2, "Quiz Géométrie — Pythagore", "Mathématiques", "Mme Kasse", "09/03/2026", List.of(
            new Question(6, "Dans un triangle rectangle, si a=3 et b=4, c vaut ?",
                List.of("5", "6", "7", "8"), 0),
            new Question(7, "Le théorème de Pythagore s'applique à quel triangle ?",
                List.of("Équilatéral", "Isocèle", "Rectangle", "Obtusangle"), 2),
            new Question(8, "Si l'hypoténuse = 13 et un côté = 5, l'autre côté vaut ?",
                List.of("8", "10", "11", "12"), 3),
            new Question(9, "L'hypoténuse est le côté :",
                List.of("Le plus court", "Adjacent à l'angle droit", "Opposé à l'angle droit", "Égal aux autres"), 2),
            new Question(10, "Dans c² = a² + b², que représente c ?",
                List.of("Un côté quelconque", "La médiane", "L'hypoténuse", "La hauteur"), 2)
        )),

        new Quiz(3, "Quiz Physique — Lois de Newton", "Physique-Chimie", "Mr Deme", "08/03/2026", List.of(
            new Question(11, "Quelle est l'unité de la force ?",
                List.of("Joule", "Newton", "Pascal", "Watt"), 1),
            new Question(12, "La 1ère loi de Newton s'appelle ?",
                List.of("Loi d'action-réaction", "Principe d'inertie", "Loi de gravitation", "Principe fondamental"), 1),
            new Question(13, "F = m × a, que représente 'a' ?",
                List.of("L'aire", "L'amplitude", "L'accélération", "L'altitude"), 2),
            new Question(14, "Si une force de 10N agit sur 2kg, l'accélération est ?",
                List.of("2 m/s²", "5 m/s²", "10 m/s²", "20 m/s²"), 1),
            new Question(15, "La 3ème loi de Newton parle de :",
                List.of("L'inertie", "L'énergie", "Action et réaction", "La gravité"), 2)
        )),

        new Quiz(4, "Quiz Chimie — La matière", "Physique-Chimie", "Mr Deme", "07/03/2026", List.of(
            new Question(16, "Combien de protons a l'hydrogène ?",
                List.of("0", "1", "2", "3"), 1),
            new Question(17, "L'eau a pour formule chimique ?",
                List.of("CO2", "H2O2", "H2O", "NaCl"), 2),
            new Question(18, "Un atome est électriquement ?",
                List.of("Positif", "Négatif", "Neutre", "Variable"), 2),
            new Question(19, "Les électrons se trouvent ?",
                List.of("Dans le noyau", "Autour du noyau", "Dans les protons", "Dans les neutrons"), 1),
            new Question(20, "Le symbole du carbone est ?",
                List.of("Ca", "Co", "C", "Cr"), 2)
        ))
    ));

    private static int nextId = 5;
    private static int nextQId = 100;

    public List<Quiz> findAll() { return new ArrayList<>(QUIZ_LIST); }

    public List<Quiz> findByMatiere(String matiere) {
        return QUIZ_LIST.stream()
            .filter(q -> q.getMatiere().contains(matiere))
            .toList();
    }

    public boolean insert(Quiz quiz) {
        QUIZ_LIST.add(0, quiz);
        return true;
    }

    public boolean delete(int id) {
        return QUIZ_LIST.removeIf(q -> q.getId() == id);
    }

    public int nextQuizId()     { return nextId++; }
    public int nextQuestionId() { return nextQId++; }
}
