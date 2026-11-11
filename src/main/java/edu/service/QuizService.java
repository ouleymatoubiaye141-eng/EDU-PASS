package edu.service;

import edu.dao.QuizDAO;
import edu.model.Question;
import edu.model.Quiz;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuizService {

    private final QuizDAO quizDAO = new QuizDAO();

    public List<Quiz> getAllQuiz()                    { return quizDAO.findAll(); }
    public List<Quiz> getQuizByMatiere(String m)     { return quizDAO.findByMatiere(m); }

    public boolean addQuiz(String titre, String matiere, String nomProf, List<Question> questions) {
        if (titre == null || titre.isBlank())   throw new IllegalArgumentException("Titre obligatoire.");
        if (matiere == null || matiere.isBlank()) throw new IllegalArgumentException("Matière obligatoire.");
        if (questions == null || questions.isEmpty()) throw new IllegalArgumentException("Ajoutez au moins une question.");

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Quiz q = new Quiz(quizDAO.nextQuizId(), titre, matiere, nomProf, date, questions);
        return quizDAO.insert(q);
    }

    public boolean deleteQuiz(int id) { return quizDAO.delete(id); }

    /** Calcule le score : retourne le nombre de bonnes réponses */
    public int calculerScore(Quiz quiz, int[] reponsesEleve) {
        int score = 0;
        List<Question> questions = quiz.getQuestions();
        for (int i = 0; i < questions.size() && i < reponsesEleve.length; i++) {
            if (reponsesEleve[i] == questions.get(i).getBonneReponse()) score++;
        }
        return score;
    }

    public int nextQuestionId() { return quizDAO.nextQuestionId(); }
}
