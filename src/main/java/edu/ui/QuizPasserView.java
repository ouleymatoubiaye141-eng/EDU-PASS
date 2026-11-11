package edu.ui;

import edu.model.Question;
import edu.model.Quiz;
import edu.service.QuizService;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

/**
 * Interface pour qu'un élève passe un quiz question par question,
 * puis affiche le résultat avec score en pourcentage.
 */
public class QuizPasserView {

    private final Quiz quiz;
    private final QuizService quizService = new QuizService();
    private final Stage parentStage;
    private final Runnable onRetour;

    private int questionCourante = 0;
    private final int[] reponsesEleve;
    private ToggleGroup toggleGroup;

    public QuizPasserView(Quiz quiz, Stage parentStage, Runnable onRetour) {
        this.quiz = quiz;
        this.parentStage = parentStage;
        this.onRetour = onRetour;
        this.reponsesEleve = new int[quiz.getNbQuestions()];
        for (int i = 0; i < reponsesEleve.length; i++) reponsesEleve[i] = -1;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("EDU-PASS — Quiz : " + quiz.getTitre());
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");
        root.setPrefSize(680, 520);

        // Header
        HBox header = new HBox(16);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle("-fx-background-color: #0d1b4b; -fx-border-color: rgba(255,255,255,0.07); -fx-border-width: 0 0 1 0;");

        Text titreQuiz = new Text("📝  " + quiz.getTitre());
        titreQuiz.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        titreQuiz.setFill(Color.WHITE);

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Label badgeMatiere = new Label(quiz.getMatiere());
        boolean isMath = quiz.getMatiere().contains("Math");
        badgeMatiere.setStyle("-fx-background-color: " + (isMath ? "rgba(59,130,246,0.20)" : "rgba(245,158,11,0.20)") + ";" +
            "-fx-text-fill: " + (isMath ? "#60a5fa" : "#fbbf24") + ";" +
            "-fx-background-radius: 20; -fx-padding: 4 12 4 12; -fx-font-size: 12;");

        header.getChildren().addAll(titreQuiz, sp, badgeMatiere);
        root.setTop(header);

        // Corps — question
        VBox corps = construireQuestion(stage);
        root.setCenter(corps);

        corps.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(300), corps);
        ft.setToValue(1); ft.play();

        stage.setScene(new Scene(root));
        stage.show();
    }

    private VBox construireQuestion(Stage stage) {
        List<Question> questions = quiz.getQuestions();
        Question q = questions.get(questionCourante);
        int total = questions.size();

        VBox box = new VBox(24);
        box.setPadding(new Insets(30, 40, 30, 40));
        box.setAlignment(Pos.TOP_LEFT);

        // Barre de progression
        double pct = (double) questionCourante / total;
        StackPane progressBar = new StackPane();
        progressBar.setPrefHeight(6);
        progressBar.setMaxHeight(6);

        HBox barBg = new HBox();
        barBg.setMaxWidth(Double.MAX_VALUE);
        barBg.setPrefHeight(6);
        barBg.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 3;");

        HBox barFill = new HBox();
        barFill.setPrefHeight(6);
        barFill.setMinWidth(0);
        barFill.setPrefWidth(560 * pct);
        barFill.setStyle("-fx-background-color: linear-gradient(to right, #1d4ed8, #059669); -fx-background-radius: 3;");

        progressBar.getChildren().addAll(barBg, barFill);
        StackPane.setAlignment(barFill, Pos.CENTER_LEFT);

        // Compteur
        Label compteur = new Label("Question " + (questionCourante + 1) + " / " + total);
        compteur.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        compteur.setTextFill(Color.web("#64748b"));

        // Énoncé
        Text enonce = new Text(q.getEnonce());
        enonce.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        enonce.setFill(Color.WHITE);
        enonce.setWrappingWidth(580);

        // Choix radio
        toggleGroup = new ToggleGroup();
        VBox choixBox = new VBox(12);

        List<String> choix = q.getChoix();
        String[] lettres = {"A", "B", "C", "D"};
        for (int i = 0; i < choix.size(); i++) {
            final int idx = i;
            HBox ligne = new HBox(14);
            ligne.setAlignment(Pos.CENTER_LEFT);
            ligne.setPadding(new Insets(12, 18, 12, 18));
            ligne.setCursor(javafx.scene.Cursor.HAND);

            // Badge lettre
            StackPane badge = new StackPane();
            Circle cercle = new Circle(14);
            cercle.setFill(Color.web("#1e293b"));
            cercle.setStroke(Color.web("#334155")); cercle.setStrokeWidth(1.5);
            Text lettreTxt = new Text(lettres[i]);
            lettreTxt.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
            lettreTxt.setFill(Color.web("#94a3b8"));
            badge.getChildren().addAll(cercle, lettreTxt);

            Label texteChoix = new Label(choix.get(i));
            texteChoix.setFont(Font.font("Segoe UI", 14));
            texteChoix.setTextFill(Color.WHITE);

            RadioButton rb = new RadioButton();
            rb.setToggleGroup(toggleGroup);
            rb.setUserData(idx);
            rb.setVisible(false); rb.setManaged(false);

            // Pré-sélection si déjà répondu
            if (reponsesEleve[questionCourante] == i) {
                rb.setSelected(true);
                styleChoixSelectionne(ligne, cercle, lettreTxt, texteChoix);
            } else {
                styleChoixNormal(ligne, cercle, lettreTxt, texteChoix);
            }

            ligne.getChildren().addAll(badge, texteChoix, rb);

            // Clic sur la ligne entière
            ligne.setOnMouseClicked(e -> {
                rb.setSelected(true);
                reponsesEleve[questionCourante] = idx;
                // Réinitialiser toutes les lignes
                choixBox.getChildren().forEach(node -> {
                    if (node instanceof HBox l) {
                        Circle c2 = (Circle) ((StackPane) l.getChildren().get(0)).getChildren().get(0);
                        Text lt2 = (Text) ((StackPane) l.getChildren().get(0)).getChildren().get(1);
                        Label txt2 = (Label) l.getChildren().get(1);
                        styleChoixNormal(l, c2, lt2, txt2);
                    }
                });
                styleChoixSelectionne(ligne, cercle, lettreTxt, texteChoix);
            });

            final int idxFinal = i;
            ligne.setOnMouseEntered(e -> {
                if (reponsesEleve[questionCourante] != idxFinal)
                    ligne.setStyle("-fx-background-color: rgba(59,130,246,0.08); -fx-background-radius: 12;" +
                        "-fx-border-color: rgba(59,130,246,0.20); -fx-border-radius: 12; -fx-border-width: 1;");
            });
            ligne.setOnMouseExited(e -> {
                if (reponsesEleve[questionCourante] != idxFinal)
                    styleChoixNormal(ligne, cercle, lettreTxt, texteChoix);
            });

            choixBox.getChildren().add(ligne);
        }

        // Boutons navigation
        HBox navRow = new HBox(12);
        navRow.setAlignment(Pos.CENTER_RIGHT);
        navRow.setPadding(new Insets(8, 0, 0, 0));

        if (questionCourante > 0) {
            Button btnPrev = Shared.bouton("← Précédent", "#334155", "#475569");
            btnPrev.setOnAction(e -> {
                questionCourante--;
                rafraichir(stage);
            });
            navRow.getChildren().add(btnPrev);
        }

        Region spacerNav = new Region(); HBox.setHgrow(spacerNav, Priority.ALWAYS);
        navRow.getChildren().add(spacerNav);

        if (questionCourante < total - 1) {
            Button btnNext = Shared.bouton("Suivant →", "#1d4ed8", "#2563eb");
            btnNext.setOnAction(e -> {
                if (reponsesEleve[questionCourante] == -1) {
                    Shared.alerte("Répondez d'abord", "Sélectionnez une réponse avant de continuer.");
                    return;
                }
                questionCourante++;
                rafraichir(stage);
            });
            navRow.getChildren().add(btnNext);
        } else {
            Button btnTerminer = Shared.bouton("✓  Terminer le Quiz", "#059669", "#10b981");
            btnTerminer.setOnAction(e -> {
                if (reponsesEleve[questionCourante] == -1) {
                    Shared.alerte("Répondez d'abord", "Sélectionnez une réponse pour terminer.");
                    return;
                }
                afficherResultat(stage);
            });
            navRow.getChildren().add(btnTerminer);
        }

        box.getChildren().addAll(progressBar, compteur, enonce, choixBox, navRow);
        return box;
    }

    private void rafraichir(Stage stage) {
        BorderPane root = (BorderPane) stage.getScene().getRoot();
        VBox nouveau = construireQuestion(stage);
        nouveau.setOpacity(0);
        root.setCenter(nouveau);
        FadeTransition ft = new FadeTransition(Duration.millis(250), nouveau);
        ft.setToValue(1); ft.play();
    }

    // ── Page de résultat ────────────────────────────────────────────
    private void afficherResultat(Stage stage) {
        int score = quizService.calculerScore(quiz, reponsesEleve);
        int total = quiz.getNbQuestions();
        int pourcentage = (int) Math.round((double) score / total * 100);

        // Couleur selon résultat
        String couleur;
        String emoji;
        String mention;
        if (pourcentage >= 80)      { couleur = "#10b981"; emoji = "🏆"; mention = "Excellent !"; }
        else if (pourcentage >= 60) { couleur = "#3b82f6"; emoji = "👍"; mention = "Bien !"; }
        else if (pourcentage >= 40) { couleur = "#f59e0b"; emoji = "📚"; mention = "À améliorer"; }
        else                         { couleur = "#ef4444"; emoji = "💪"; mention = "Continue tes efforts !"; }

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");
        root.setPrefSize(680, 560);

        VBox content = new VBox(28);
        content.setPadding(new Insets(40, 60, 40, 60));
        content.setAlignment(Pos.CENTER);

        // Cercle de score animé
        StackPane cercleScore = new StackPane();
        Circle fond = new Circle(80); fond.setFill(Color.web("#1e293b"));
        fond.setStroke(Color.web("#334155")); fond.setStrokeWidth(3);

        Arc arc = new Arc(0, 0, 74, 74, 90, -(pourcentage / 100.0 * 360));
        arc.setType(ArcType.OPEN);
        arc.setFill(Color.TRANSPARENT);
        arc.setStroke(Color.web(couleur)); arc.setStrokeWidth(8);

        VBox centreTexte = new VBox(2);
        centreTexte.setAlignment(Pos.CENTER);
        Text pctText = new Text(pourcentage + "%");
        pctText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        pctText.setFill(Color.web(couleur));
        Text scoreTexte = new Text(score + "/" + total);
        scoreTexte.setFont(Font.font("Segoe UI", 13));
        scoreTexte.setFill(Color.web("#94a3b8"));
        centreTexte.getChildren().addAll(pctText, scoreTexte);

        cercleScore.getChildren().addAll(fond, arc, centreTexte);

        // Animation de l'arc
        Arc arcAnim = new Arc(0, 0, 74, 74, 90, 0);
        arcAnim.setType(ArcType.OPEN);
        arcAnim.setFill(Color.TRANSPARENT);
        arcAnim.setStroke(Color.web(couleur)); arcAnim.setStrokeWidth(8);
        cercleScore.getChildren().add(arcAnim);
        arc.setVisible(false);

        double targetAngle = -(pourcentage / 100.0 * 360);
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(1200),
            new javafx.animation.KeyValue(arcAnim.lengthProperty(), targetAngle,
                javafx.animation.Interpolator.EASE_OUT)));
        tl.play();

        // Textes résultat
        Text emojiText = new Text(emoji);
        emojiText.setFont(Font.font(36));

        Text mentionText = new Text(mention);
        mentionText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        mentionText.setFill(Color.WHITE);

        Text sousTitre = new Text("Quiz : " + quiz.getTitre() + "  ·  " + quiz.getMatiere());
        sousTitre.setFont(Font.font("Segoe UI", 13));
        sousTitre.setFill(Color.web("#64748b"));

        // Détail question par question
        VBox detail = new VBox(8);
        detail.setPadding(new Insets(16, 20, 16, 20));
        detail.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 14;" +
            "-fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 14; -fx-border-width: 1;");

        Text detailTitre = new Text("Détail des réponses");
        detailTitre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        detailTitre.setFill(Color.web("#94a3b8"));
        detail.getChildren().add(detailTitre);

        List<Question> questions = quiz.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            boolean correct = reponsesEleve[i] == q.getBonneReponse();
            HBox ligne = new HBox(10);
            ligne.setAlignment(Pos.CENTER_LEFT);
            Label ico = new Label(correct ? "✓" : "✗");
            ico.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            ico.setTextFill(correct ? Color.web("#10b981") : Color.web("#ef4444"));
            ico.setMinWidth(20);
            Label qLabel = new Label("Q" + (i + 1) + ". " + q.getEnonce());
            qLabel.setFont(Font.font("Segoe UI", 12));
            qLabel.setTextFill(Color.web("#e2e8f0"));
            qLabel.setMaxWidth(380);
            qLabel.setWrapText(true);

            VBox infos = new VBox(2, qLabel);
            if (!correct) {
                Label bonneRep = new Label("✓ Bonne réponse : " + q.getTexteBonneReponse());
                bonneRep.setFont(Font.font("Segoe UI", 11));
                bonneRep.setTextFill(Color.web("#10b981"));
                infos.getChildren().add(bonneRep);
            }
            ligne.getChildren().addAll(ico, infos);
            detail.getChildren().add(ligne);
        }

        ScrollPane scroll = new ScrollPane(detail);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(200);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        // Bouton retour
        Button btnRetour = Shared.bouton("← Retour aux Quiz", "#334155", "#475569");
        btnRetour.setOnAction(e -> { stage.close(); onRetour.run(); });

        content.getChildren().addAll(cercleScore, emojiText, mentionText, sousTitre, scroll, btnRetour);

        root.setCenter(content);
        root.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(500), root);
        ft.setToValue(1); ft.play();

        stage.getScene().setRoot(root);
    }

    private void styleChoixNormal(HBox ligne, Circle c, Text lettre, Label txt) {
        ligne.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;" +
            "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 12; -fx-border-width: 1;");
        c.setFill(Color.web("#1e293b")); c.setStroke(Color.web("#334155"));
        lettre.setFill(Color.web("#94a3b8")); txt.setTextFill(Color.WHITE);
    }

    private void styleChoixSelectionne(HBox ligne, Circle c, Text lettre, Label txt) {
        ligne.setStyle("-fx-background-color: rgba(59,130,246,0.15); -fx-background-radius: 12;" +
            "-fx-border-color: #3b82f6; -fx-border-radius: 12; -fx-border-width: 1.5;");
        c.setFill(Color.web("#1d4ed8", 0.4)); c.setStroke(Color.web("#3b82f6"));
        lettre.setFill(Color.web("#60a5fa")); txt.setTextFill(Color.web("#60a5fa"));
    }
}
