package edu.ui;

import edu.model.Question;
import edu.service.QuizService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre permettant au professeur de créer un Quiz avec ses questions.
 */
public class QuizCreerView {

    private final String nomProfesseur;
    private final QuizService quizService;
    private final Runnable onSuccess;

    private final List<Question> questions = new ArrayList<>();
    private VBox listeQuestionsBox;
    private Label compteurLabel;

    public QuizCreerView(String nomProfesseur, QuizService quizService, Runnable onSuccess) {
        this.nomProfesseur = nomProfesseur;
        this.quizService   = quizService;
        this.onSuccess     = onSuccess;
    }

    public void show(Stage parent) {
        Stage stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.setTitle("EDU-PASS — Créer un Quiz");
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");

        // ── Header ──────────────────────────────────────────────────
        HBox header = new HBox();
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #0d1b4b; -fx-border-color: rgba(255,255,255,0.07); -fx-border-width: 0 0 1 0;");
        Text titre = new Text("➕  Créer un nouveau Quiz");
        titre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        titre.setFill(Color.WHITE);
        header.getChildren().add(titre);
        root.setTop(header);

        // ── Corps en deux colonnes ───────────────────────────────────
        HBox body = new HBox(0);

        // Colonne gauche : infos quiz + liste questions
        VBox gauche = new VBox(16);
        gauche.setPadding(new Insets(24, 20, 24, 24));
        HBox.setHgrow(gauche, Priority.ALWAYS);

        Text secInfos = new Text("Informations du Quiz");
        secInfos.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        secInfos.setFill(Color.WHITE);

        TextField fTitre = champ("Titre du quiz *");
        ComboBox<String> fMat = new ComboBox<>();
        fMat.getItems().addAll("Mathématiques", "Physique-Chimie");
        fMat.setPromptText("Matière *"); fMat.setMaxWidth(Double.MAX_VALUE);
        styleCombo(fMat);

        // Liste des questions ajoutées
        Text secQuestions = new Text("Questions ajoutées");
        secQuestions.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        secQuestions.setFill(Color.WHITE);

        compteurLabel = new Label("0 question(s)");
        compteurLabel.setFont(Font.font("Segoe UI", 12));
        compteurLabel.setTextFill(Color.web("#64748b"));

        listeQuestionsBox = new VBox(8);
        ScrollPane scroll = new ScrollPane(listeQuestionsBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(220);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Label msgRetour = new Label();
        msgRetour.setFont(Font.font("Segoe UI", 13));
        msgRetour.setWrapText(true);
        msgRetour.setVisible(false);

        // Bouton créer quiz final
        Button btnCreer = Shared.bouton("✓  Enregistrer le Quiz", "#059669", "#10b981");
        btnCreer.setMaxWidth(Double.MAX_VALUE); btnCreer.setPrefHeight(44);
        btnCreer.setOnAction(e -> {
            String titreTxt = fTitre.getText().trim();
            String matiere  = fMat.getValue();
            try {
                quizService.addQuiz(titreTxt, matiere, nomProfesseur, new ArrayList<>(questions));
                msgRetour.setText("✓ Quiz créé avec succès !");
                msgRetour.setTextFill(Color.web("#34d399")); msgRetour.setVisible(true);
                onSuccess.run();
                stage.close();
            } catch (IllegalArgumentException ex) {
                msgRetour.setText("⚠ " + ex.getMessage());
                msgRetour.setTextFill(Color.web("#f87171")); msgRetour.setVisible(true);
            }
        });

        gauche.getChildren().addAll(
            secInfos,
            champAvecLabel("Titre *", fTitre),
            champAvecLabel("Matière *", fMat),
            secQuestions, compteurLabel, scroll, btnCreer, msgRetour
        );

        // Colonne droite : formulaire ajout question
        VBox droite = new VBox(14);
        droite.setPrefWidth(340); droite.setMaxWidth(340);
        droite.setPadding(new Insets(24, 24, 24, 20));
        droite.setStyle("-fx-background-color: #1e293b; -fx-border-color: rgba(255,255,255,0.07); -fx-border-width: 0 0 0 1;");

        Text secAjoutQ = new Text("Ajouter une question");
        secAjoutQ.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        secAjoutQ.setFill(Color.WHITE);

        TextArea fEnonce = new TextArea();
        fEnonce.setPromptText("Énoncé de la question *");
        fEnonce.setPrefRowCount(3); fEnonce.setWrapText(true);
        fEnonce.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-prompt-text-fill: #475569;" +
            "-fx-background-radius: 8; -fx-border-color: rgba(255,255,255,0.12); -fx-border-radius: 8; -fx-border-width: 1;");

        TextField fA = champ("Choix A *");
        TextField fB = champ("Choix B *");
        TextField fC = champ("Choix C *");
        TextField fD = champ("Choix D *");

        ComboBox<String> fBonne = new ComboBox<>();
        fBonne.getItems().addAll("A", "B", "C", "D");
        fBonne.setPromptText("Bonne réponse *");
        fBonne.setMaxWidth(Double.MAX_VALUE);
        styleCombo(fBonne);

        Label msgQ = new Label();
        msgQ.setFont(Font.font("Segoe UI", 12)); msgQ.setWrapText(true); msgQ.setVisible(false);

        Button btnAjouterQ = Shared.bouton("＋  Ajouter cette question", "#1d4ed8", "#2563eb");
        btnAjouterQ.setMaxWidth(Double.MAX_VALUE); btnAjouterQ.setPrefHeight(40);
        btnAjouterQ.setOnAction(e -> {
            String enonce = fEnonce.getText().trim();
            String a = fA.getText().trim(), b = fB.getText().trim();
            String c2 = fC.getText().trim(), d = fD.getText().trim();
            String bonne = fBonne.getValue();

            if (enonce.isEmpty() || a.isEmpty() || b.isEmpty() || c2.isEmpty() || d.isEmpty() || bonne == null) {
                msgQ.setText("⚠ Tous les champs sont obligatoires.");
                msgQ.setTextFill(Color.web("#f87171")); msgQ.setVisible(true); return;
            }

            int bonneIdx = switch (bonne) { case "A" -> 0; case "B" -> 1; case "C" -> 2; default -> 3; };
            Question q = new Question(quizService.nextQuestionId(), enonce, List.of(a, b, c2, d), bonneIdx);
            questions.add(q);
            ajouterCarteQuestion(q, questions.size());

            // Réinitialiser
            fEnonce.clear(); fA.clear(); fB.clear(); fC.clear(); fD.clear();
            fBonne.setValue(null);
            msgQ.setText("✓ Question ajoutée !"); msgQ.setTextFill(Color.web("#34d399")); msgQ.setVisible(true);
        });

        droite.getChildren().addAll(
            secAjoutQ,
            champAvecLabel("Énoncé *", fEnonce),
            champAvecLabel("Choix A *", fA),
            champAvecLabel("Choix B *", fB),
            champAvecLabel("Choix C *", fC),
            champAvecLabel("Choix D *", fD),
            champAvecLabel("Bonne réponse *", fBonne),
            btnAjouterQ, msgQ
        );

        body.getChildren().addAll(gauche, droite);
        root.setCenter(body);

        stage.setScene(new Scene(root, 900, 620));
        stage.show();
    }

    private void ajouterCarteQuestion(Question q, int num) {
        HBox carte = new HBox(12);
        carte.setPadding(new Insets(10, 14, 10, 14));
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 10;" +
            "-fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 10; -fx-border-width: 1;");

        Label numLbl = new Label("Q" + num);
        numLbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        numLbl.setTextFill(Color.web("#3b82f6"));
        numLbl.setMinWidth(28);

        Label eLbl = new Label(q.getEnonce().length() > 55 ? q.getEnonce().substring(0, 55) + "…" : q.getEnonce());
        eLbl.setFont(Font.font("Segoe UI", 12)); eLbl.setTextFill(Color.WHITE);
        HBox.setHgrow(eLbl, Priority.ALWAYS);

        Label bLbl = new Label("✓ " + q.getTexteBonneReponse());
        bLbl.setFont(Font.font("Segoe UI", 11)); bLbl.setTextFill(Color.web("#10b981"));
        bLbl.setMaxWidth(120);

        Button btnSupp = new Button("✕");
        btnSupp.setFont(Font.font(10)); btnSupp.setPrefSize(24, 24);
        btnSupp.setStyle("-fx-background-color: rgba(239,68,68,0.15); -fx-text-fill: #f87171;" +
            "-fx-background-radius: 6; -fx-cursor: hand;");
        btnSupp.setOnAction(e -> {
            questions.remove(q);
            listeQuestionsBox.getChildren().remove(carte);
            compteurLabel.setText(questions.size() + " question(s)");
        });

        carte.getChildren().addAll(numLbl, eLbl, bLbl, btnSupp);
        listeQuestionsBox.getChildren().add(carte);
        compteurLabel.setText(questions.size() + " question(s)");
    }

    private TextField champ(String ph) {
        TextField tf = new TextField(); tf.setPromptText(ph); tf.setPrefHeight(36);
        tf.setFont(Font.font("Segoe UI", 12));
        tf.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-prompt-text-fill: #475569;" +
            "-fx-background-radius: 8; -fx-border-color: rgba(255,255,255,0.12); -fx-border-radius: 8;" +
            "-fx-border-width: 1; -fx-padding: 0 10 0 10;");
        return tf;
    }

    private void styleCombo(ComboBox<String> cb) {
        cb.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-prompt-text-fill: #475569;" +
            "-fx-background-radius: 8; -fx-border-color: rgba(255,255,255,0.12); -fx-border-radius: 8; -fx-border-width: 1;");
        cb.setPrefHeight(36);
    }

    private VBox champAvecLabel(String label, javafx.scene.Node champ) {
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", 11)); lbl.setTextFill(Color.web("#94a3b8"));
        return new VBox(4, lbl, champ);
    }
}
