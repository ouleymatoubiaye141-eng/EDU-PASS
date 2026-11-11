package edu.ui;

import edu.model.Cours;
import edu.model.Eleve;
import edu.model.Quiz;
import edu.service.CoursService;
import edu.service.QuizService;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class EleveView {

    private final Eleve eleve;
    private final CoursService coursService = new CoursService();
    private final QuizService quizService   = new QuizService();

    private String ongletActif  = "cours";
    private String matiereActive = "tous";

    public EleveView(Eleve eleve) { this.eleve = eleve; }

    public void show(Stage stage) {
        stage.setTitle("EDU-PASS — Espace Élève");
        stage.setResizable(true);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");
        root.setTop(Shared.construireTopbar(stage,
            eleve.getPrenom() + " " + eleve.getNom(),
            "3ème Collège  ·  Élève", "#10b981"));
        root.setCenter(construireContenu(stage, root));

        stage.setScene(new Scene(root, 980, 660));
        stage.show();
    }

    @SuppressWarnings("unchecked")
    private VBox construireContenu(Stage stage, BorderPane root) {
        VBox content = new VBox(18);
        content.setPadding(new Insets(24, 30, 24, 30));

        // ── Onglets ──
        Button btnCours = Shared.tabButton("📚  Cours",              ongletActif.equals("cours"));
        Button btnTP    = Shared.tabButton("🔬  Travaux Pratiques",  ongletActif.equals("tp"));
        Button btnQuiz  = Shared.tabButton("📝  Quiz",               ongletActif.equals("quiz"));

        btnCours.setOnAction(e -> { ongletActif = "cours"; matiereActive = "tous"; refresh(stage, root); });
        btnTP.setOnAction(e ->    { ongletActif = "tp";    matiereActive = "tous"; refresh(stage, root); });
        btnQuiz.setOnAction(e ->  { ongletActif = "quiz";  matiereActive = "tous"; refresh(stage, root); });

        HBox headerRow = new HBox(16);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        Text titreSection = new Text("Mes ressources");
        titreSection.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titreSection.setFill(Color.WHITE);
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        headerRow.getChildren().addAll(titreSection, sp,
            new HBox(10, btnCours, btnTP, btnQuiz));

        // ── Filtres matière ──
        Button btnTous = filtreBtn("Toutes matières", matiereActive.equals("tous"));
        Button btnMath = filtreBtn("📐 Mathématiques",  matiereActive.equals("math"));
        Button btnPhys = filtreBtn("⚗️ Physique-Chimie", matiereActive.equals("phys"));
        btnTous.setOnAction(e -> { matiereActive = "tous"; refresh(stage, root); });
        btnMath.setOnAction(e -> { matiereActive = "math"; refresh(stage, root); });
        btnPhys.setOnAction(e -> { matiereActive = "phys"; refresh(stage, root); });
        HBox filtres = new HBox(10, btnTous, btnMath, btnPhys);
        filtres.setAlignment(Pos.CENTER_LEFT);

        if (ongletActif.equals("quiz")) {
            // ── Vue Quiz ──
            List<Quiz> quizList = getQuizFiltres();
            long nbMath = quizList.stream().filter(q -> q.getMatiere().contains("Math")).count();
            long nbPhys = quizList.stream().filter(q -> q.getMatiere().contains("Physique")).count();
            HBox stats = new HBox(12);
            stats.getChildren().addAll(
                Shared.carteStats(String.valueOf(quizList.size()), "Quiz disponibles", "#3b82f6"),
                Shared.carteStats(String.valueOf(nbMath), "Mathématiques", "#10b981"),
                Shared.carteStats(String.valueOf(nbPhys), "Physique-Chimie", "#f59e0b")
            );

            // Grille de cartes Quiz
            VBox grille = new VBox(12);
            VBox.setVgrow(grille, Priority.ALWAYS);

            if (quizList.isEmpty()) {
                Label empty = new Label("Aucun quiz disponible.");
                empty.setTextFill(Color.web("#64748b")); empty.setFont(Font.font("Segoe UI", 14));
                grille.getChildren().add(empty);
            } else {
                for (Quiz quiz : quizList) {
                    grille.getChildren().add(carteQuiz(quiz, stage));
                }
            }

            ScrollPane scrollQuiz = new ScrollPane(grille);
            scrollQuiz.setFitToWidth(true);
            scrollQuiz.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            VBox.setVgrow(scrollQuiz, Priority.ALWAYS);

            content.getChildren().addAll(headerRow, filtres, stats, scrollQuiz);

        } else {
            // ── Vue Cours / TP ──
            List<Cours> liste = filtrerCours(getListeCours());
            long nbMath = liste.stream().filter(c -> c.getMatiere().contains("Math")).count();
            long nbPhys = liste.stream().filter(c -> c.getMatiere().contains("Physique")).count();
            HBox stats = new HBox(12);
            stats.getChildren().addAll(
                Shared.carteStats(String.valueOf(liste.size()), labelOnglet() + " disponibles", "#3b82f6"),
                Shared.carteStats(String.valueOf(nbMath), "Mathématiques", "#10b981"),
                Shared.carteStats(String.valueOf(nbPhys), "Physique-Chimie", "#f59e0b")
            );

            TableView<Cours> table = new TableView<>();
            table.setStyle("-fx-background-color: #1e293b; -fx-border-color: rgba(255,255,255,0.08);" +
                "-fx-border-radius: 12; -fx-background-radius: 12;");
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            table.getColumns().addAll(
                Shared.colonne("Titre",      "titre"),
                Shared.colonne("Matière",    "matiere"),
                Shared.colonne("Professeur", "nomProfesseur"),
                Shared.colonne("Date",       "dateAjout")
            );
            ObservableList<Cours> data = FXCollections.observableArrayList(liste);
            table.setItems(data);
            table.setPlaceholder(new Label("Aucun élément."));
            VBox.setVgrow(table, Priority.ALWAYS);

            HBox footer = new HBox(12); footer.setAlignment(Pos.CENTER_LEFT);
            Button btnPdf = Shared.bouton("📄  Ouvrir PDF", "#1d4ed8", "#2563eb");
            btnPdf.setOnAction(e -> ouvrirPdf(table));
            Button btnAct = Shared.bouton("↻  Actualiser", "#334155", "#475569");
            btnAct.setOnAction(e -> data.setAll(filtrerCours(getListeCours())));
            Label hint = new Label("Sélectionnez un élément pour ouvrir son PDF");
            hint.setFont(Font.font("Segoe UI", 12)); hint.setTextFill(Color.web("#64748b"));
            footer.getChildren().addAll(btnPdf, btnAct, hint);

            content.getChildren().addAll(headerRow, filtres, stats, table, footer);
        }

        content.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(300), content);
        ft.setToValue(1); ft.play();
        return content;
    }

    // Carte cliquable pour chaque quiz
    private HBox carteQuiz(Quiz quiz, Stage stage) {
        HBox carte = new HBox(16);
        carte.setPadding(new Insets(16, 20, 16, 20));
        carte.setAlignment(Pos.CENTER_LEFT);
        carte.setCursor(javafx.scene.Cursor.HAND);
        boolean isMath = quiz.getMatiere().contains("Math");
        String couleur = isMath ? "#3b82f6" : "#f59e0b";

        carte.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 14;" +
            "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14; -fx-border-width: 1;");

        // Icône matière
        StackPane ico = new StackPane();
        javafx.scene.shape.Rectangle r = new javafx.scene.shape.Rectangle(48, 48);
        r.setArcWidth(12); r.setArcHeight(12);
        r.setFill(Color.web(couleur, 0.15));
        r.setStroke(Color.web(couleur, 0.30)); r.setStrokeWidth(1);
        Text icoTxt = new Text(isMath ? "📐" : "⚗️");
        icoTxt.setFont(Font.font(20));
        ico.getChildren().addAll(r, icoTxt);

        // Infos
        VBox infos = new VBox(4);
        HBox.setHgrow(infos, Priority.ALWAYS);
        Label titre = new Label(quiz.getTitre());
        titre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titre.setTextFill(Color.WHITE);
        HBox meta = new HBox(12);
        Label matLbl = new Label(quiz.getMatiere());
        matLbl.setFont(Font.font("Segoe UI", 11));
        matLbl.setTextFill(Color.web(couleur));
        Label qLbl = new Label(quiz.getNbQuestions() + " questions");
        qLbl.setFont(Font.font("Segoe UI", 11)); qLbl.setTextFill(Color.web("#64748b"));
        Label profLbl = new Label("par " + quiz.getNomProfesseur());
        profLbl.setFont(Font.font("Segoe UI", 11)); profLbl.setTextFill(Color.web("#64748b"));
        meta.getChildren().addAll(matLbl, qLbl, profLbl);
        infos.getChildren().addAll(titre, meta);

        // Bouton commencer
        Button btnStart = new Button("Commencer →");
        btnStart.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        btnStart.setPadding(new Insets(8, 16, 8, 16));
        btnStart.setStyle("-fx-background-color: linear-gradient(to right,#1d4ed8,#059669);" +
            "-fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;");
        btnStart.setOnAction(e -> {
            new QuizPasserView(quiz, stage, () -> {}).show();
        });

        carte.setOnMouseEntered(e -> carte.setStyle("-fx-background-color: rgba(59,130,246,0.10); -fx-background-radius: 14;" +
            "-fx-border-color: rgba(59,130,246,0.30); -fx-border-radius: 14; -fx-border-width: 1;"));
        carte.setOnMouseExited(e -> carte.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 14;" +
            "-fx-border-color: rgba(255,255,255,0.08); -fx-border-radius: 14; -fx-border-width: 1;"));

        carte.getChildren().addAll(ico, infos, btnStart);
        return carte;
    }

    private void refresh(Stage stage, BorderPane root) {
        root.setCenter(construireContenu(stage, root));
    }

    private List<Cours> getListeCours() {
        return ongletActif.equals("tp") ? coursService.getAllTP() : coursService.getAllCours();
    }

    private List<Cours> filtrerCours(List<Cours> liste) {
        return switch (matiereActive) {
            case "math" -> liste.stream().filter(c -> c.getMatiere().contains("Math")).toList();
            case "phys" -> liste.stream().filter(c -> c.getMatiere().contains("Physique")).toList();
            default     -> liste;
        };
    }

    private List<Quiz> getQuizFiltres() {
        List<Quiz> tous = quizService.getAllQuiz();
        return switch (matiereActive) {
            case "math" -> tous.stream().filter(q -> q.getMatiere().contains("Math")).toList();
            case "phys" -> tous.stream().filter(q -> q.getMatiere().contains("Physique")).toList();
            default     -> tous;
        };
    }

    private String labelOnglet() {
        return ongletActif.equals("tp") ? "TP" : "Cours";
    }

    private Button filtreBtn(String texte, boolean actif) {
        Button btn = new Button(texte);
        btn.setFont(Font.font("Segoe UI", 12));
        btn.setPrefHeight(32); btn.setPadding(new Insets(0, 14, 0, 14));
        btn.setStyle(actif
            ? "-fx-background-color: rgba(59,130,246,0.25); -fx-text-fill: #60a5fa; -fx-background-radius: 20;" +
              "-fx-border-color: #3b82f6; -fx-border-radius: 20; -fx-border-width: 1; -fx-cursor: hand;"
            : "-fx-background-color: #1e293b; -fx-text-fill: #64748b; -fx-background-radius: 20;" +
              "-fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 20; -fx-border-width: 1; -fx-cursor: hand;");
        return btn;
    }

    private void ouvrirPdf(TableView<Cours> table) {
        Cours sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { Shared.alerte("Aucune sélection", "Sélectionnez un élément."); return; }
        String chemin = sel.getCheminPdf();
        if (chemin == null || chemin.isBlank()) { Shared.alerte("PDF indisponible", "Aucun PDF associé."); return; }
        try {
            File f = new File(chemin);
            if (!f.exists()) { Shared.alerte("Fichier introuvable", chemin); return; }
            Desktop.getDesktop().open(f);
        } catch (Exception ex) { Shared.alerte("Erreur", ex.getMessage()); }
    }
}
