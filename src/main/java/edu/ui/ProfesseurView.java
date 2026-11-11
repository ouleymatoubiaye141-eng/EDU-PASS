package edu.ui;

import edu.model.Cours;
import edu.model.Eleve;
import edu.model.Professeur;
import edu.model.Quiz;
import edu.service.CoursService;
import edu.service.EleveService;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class ProfesseurView {

    private final Professeur professeur;
    private final CoursService coursService = new CoursService();
    private final EleveService eleveService  = new EleveService();
    private final QuizService quizService    = new QuizService();

    private String ongletActif = "cours";

    public ProfesseurView(Professeur professeur) { this.professeur = professeur; }

    public void show(Stage stage) {
        stage.setTitle("EDU-PASS — Espace Professeur");
        stage.setResizable(true);
        stage.setScene(new Scene(buildRoot(stage), 1220, 720));
        stage.show();
    }

    private BorderPane buildRoot(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");
        root.setTop(Shared.construireTopbar(stage,
            professeur.getPrenom() + " " + professeur.getNom(),
            professeur.getMatiere() + "  ·  Professeur", "#3b82f6"));
        HBox body = buildBody(stage, root);
        body.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(350), body); ft.setToValue(1); ft.play();
        root.setCenter(body);
        return root;
    }

    @SuppressWarnings("unchecked")
    private HBox buildBody(Stage stage, BorderPane root) {
        HBox body = new HBox(0);

        // ── Panneau gauche ──────────────────────────────────────────
        VBox gauche = new VBox(16);
        gauche.setPadding(new Insets(24, 20, 24, 28));
        HBox.setHgrow(gauche, Priority.ALWAYS);

        Text titreSection = new Text("Tableau de bord");
        titreSection.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titreSection.setFill(Color.WHITE);

        Button btnCours  = Shared.tabButton("📚  Cours",  ongletActif.equals("cours"));
        Button btnTP     = Shared.tabButton("🔬  TP",     ongletActif.equals("tp"));
        Button btnQuiz   = Shared.tabButton("📝  Quiz",   ongletActif.equals("quiz"));
        Button btnEleves = Shared.tabButton("👥  Élèves", ongletActif.equals("eleves"));

        btnCours.setOnAction(e  -> { ongletActif = "cours";  root.setCenter(buildBody(stage, root)); });
        btnTP.setOnAction(e     -> { ongletActif = "tp";     root.setCenter(buildBody(stage, root)); });
        btnQuiz.setOnAction(e   -> { ongletActif = "quiz";   root.setCenter(buildBody(stage, root)); });
        btnEleves.setOnAction(e -> { ongletActif = "eleves"; root.setCenter(buildBody(stage, root)); });

        HBox onglets = new HBox(10, btnCours, btnTP, btnQuiz, btnEleves);

        // Stats globales
        HBox stats = new HBox(12);
        stats.getChildren().addAll(
            Shared.carteStats(String.valueOf(coursService.getAllCours().size()), "Cours",   "#3b82f6"),
            Shared.carteStats(String.valueOf(coursService.getAllTP().size()),    "TP",      "#10b981"),
            Shared.carteStats(String.valueOf(quizService.getAllQuiz().size()),   "Quiz",    "#f59e0b"),
            Shared.carteStats(String.valueOf(eleveService.getAllEleves().size()),"Élèves",  "#a855f7")
        );

        gauche.getChildren().addAll(titreSection, onglets, stats);

        if (ongletActif.equals("eleves")) {
            gauche.getChildren().add(buildPanneauEleves());
        } else if (ongletActif.equals("quiz")) {
            gauche.getChildren().add(buildTableauQuiz(stage, root));
        } else {
            gauche.getChildren().add(buildTableauCours(stage, root));
        }

        body.getChildren().addAll(gauche, buildPanneauDroit(stage, root));
        return body;
    }

    // ── Tableau Cours / TP ──────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildTableauCours(Stage stage, BorderPane root) {
        VBox box = new VBox(12); VBox.setVgrow(box, Priority.ALWAYS);

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
        List<Cours> liste = ongletActif.equals("tp") ? coursService.getAllTP() : coursService.getAllCours();
        ObservableList<Cours> data = FXCollections.observableArrayList(liste);
        table.setItems(data); table.setPlaceholder(new Label("Aucun élément."));
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = new HBox(10); actions.setAlignment(Pos.CENTER_LEFT);
        Button btnSupp = btnDanger("🗑  Supprimer");
        btnSupp.setOnAction(e -> {
            Cours sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { Shared.alerte("Aucune sélection", "Sélectionnez un élément."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION);
            c.setTitle("Confirmer"); c.setHeaderText("Supprimer « " + sel.getTitre() + " » ?");
            Optional<ButtonType> r = c.showAndWait();
            if (r.isPresent() && r.get() == ButtonType.OK) {
                boolean ok = ongletActif.equals("tp") ? coursService.deleteTP(sel.getId()) : coursService.deleteCours(sel.getId());
                if (ok) data.setAll(ongletActif.equals("tp") ? coursService.getAllTP() : coursService.getAllCours());
            }
        });
        Button btnAct = Shared.bouton("↻  Actualiser", "#334155", "#475569");
        btnAct.setOnAction(e -> data.setAll(ongletActif.equals("tp") ? coursService.getAllTP() : coursService.getAllCours()));
        actions.getChildren().addAll(btnSupp, btnAct);
        box.getChildren().addAll(table, actions);
        return box;
    }

    // ── Tableau Quiz ────────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildTableauQuiz(Stage stage, BorderPane root) {
        VBox box = new VBox(12); VBox.setVgrow(box, Priority.ALWAYS);

        TableView<Quiz> table = new TableView<>();
        table.setStyle("-fx-background-color: #1e293b; -fx-border-color: rgba(255,255,255,0.08);" +
            "-fx-border-radius: 12; -fx-background-radius: 12;");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Quiz, String> colT = new TableColumn<>("Titre");
        colT.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTitre()));
        TableColumn<Quiz, String> colM = new TableColumn<>("Matière");
        colM.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getMatiere()));
        TableColumn<Quiz, String> colQ = new TableColumn<>("Questions");
        colQ.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().getNbQuestions())));
        TableColumn<Quiz, String> colP = new TableColumn<>("Professeur");
        colP.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNomProfesseur()));
        TableColumn<Quiz, String> colD = new TableColumn<>("Date");
        colD.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getDateAjout()));
        table.getColumns().addAll(colT, colM, colQ, colP, colD);

        ObservableList<Quiz> data = FXCollections.observableArrayList(quizService.getAllQuiz());
        table.setItems(data); table.setPlaceholder(new Label("Aucun quiz."));
        VBox.setVgrow(table, Priority.ALWAYS);

        HBox actions = new HBox(10); actions.setAlignment(Pos.CENTER_LEFT);
        Button btnSupp = btnDanger("🗑  Supprimer");
        btnSupp.setOnAction(e -> {
            Quiz sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { Shared.alerte("Aucune sélection", "Sélectionnez un quiz."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION);
            c.setTitle("Confirmer"); c.setHeaderText("Supprimer « " + sel.getTitre() + " » ?");
            Optional<ButtonType> r = c.showAndWait();
            if (r.isPresent() && r.get() == ButtonType.OK) {
                if (quizService.deleteQuiz(sel.getId())) data.setAll(quizService.getAllQuiz());
            }
        });
        Button btnAct = Shared.bouton("↻  Actualiser", "#334155", "#475569");
        btnAct.setOnAction(e -> data.setAll(quizService.getAllQuiz()));
        actions.getChildren().addAll(btnSupp, btnAct);
        box.getChildren().addAll(table, actions);
        return box;
    }

    // ── Panneau Élèves ──────────────────────────────────────────────
    @SuppressWarnings("unchecked")
    private VBox buildPanneauEleves() {
        VBox box = new VBox(12); VBox.setVgrow(box, Priority.ALWAYS);

        TableView<Eleve> tbl = new TableView<>();
        tbl.setStyle("-fx-background-color: #1e293b; -fx-border-color: rgba(255,255,255,0.08);" +
            "-fx-border-radius: 12; -fx-background-radius: 12;");
        tbl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tbl.getColumns().addAll(
            Shared.colonne("Prénom", "prenom"),
            Shared.colonne("Nom",    "nom"),
            Shared.colonne("Code",   "code"),
            Shared.colonne("Classe", "classe")
        );
        ObservableList<Eleve> data = FXCollections.observableArrayList(eleveService.getAllEleves());
        tbl.setItems(data); tbl.setPlaceholder(new Label("Aucun élève."));
        VBox.setVgrow(tbl, Priority.ALWAYS);

        HBox actions = new HBox(10);
        Button btnSupp = btnDanger("🗑  Retirer élève");
        btnSupp.setOnAction(e -> {
            Eleve sel = tbl.getSelectionModel().getSelectedItem();
            if (sel == null) { Shared.alerte("Aucune sélection", "Sélectionnez un élève."); return; }
            Alert c = new Alert(Alert.AlertType.CONFIRMATION);
            c.setTitle("Confirmer"); c.setHeaderText("Retirer " + sel.getPrenom() + " " + sel.getNom() + " ?");
            Optional<ButtonType> r = c.showAndWait();
            if (r.isPresent() && r.get() == ButtonType.OK)
                if (eleveService.deleteEleve(sel.getId())) data.setAll(eleveService.getAllEleves());
        });
        Button btnAct = Shared.bouton("↻  Actualiser", "#334155", "#475569");
        btnAct.setOnAction(e -> data.setAll(eleveService.getAllEleves()));
        actions.getChildren().addAll(btnSupp, btnAct);
        box.getChildren().addAll(tbl, actions);
        return box;
    }

    // ── Panneau droit (formulaire contextuel) ───────────────────────
    private VBox buildPanneauDroit(Stage stage, BorderPane root) {
        VBox form = new VBox(16);
        form.setPrefWidth(310); form.setMaxWidth(310);
        form.setPadding(new Insets(24, 26, 24, 20));
        form.setStyle("-fx-background-color: #1e293b; -fx-border-color: rgba(255,255,255,0.07); -fx-border-width: 0 0 0 1;");

        String nomProf = professeur.getPrenom() + " " + professeur.getNom();

        switch (ongletActif) {
            case "cours", "tp" -> {
                String label = ongletActif.equals("tp") ? "Ajouter un TP" : "Ajouter un cours";
                Text titre = new Text(label); titre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16)); titre.setFill(Color.WHITE);

                TextField fTitre = champForm("Titre *");
                ComboBox<String> fMat = new ComboBox<>();
                fMat.getItems().addAll("Mathématiques", "Physique-Chimie");
                fMat.setPromptText("Matière *"); fMat.setMaxWidth(Double.MAX_VALUE); styleCombo(fMat);

                HBox ligPdf = new HBox(8);
                TextField fPdf = champForm("Chemin PDF (optionnel)");
                HBox.setHgrow(fPdf, Priority.ALWAYS);
                Button btnParc = new Button("📁");
                btnParc.setStyle("-fx-background-color: #334155; -fx-text-fill: white;" +
                    "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 8 10 8 10;");
                btnParc.setOnAction(e -> {
                    FileChooser fc = new FileChooser();
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
                    File f = fc.showOpenDialog(stage);
                    if (f != null) fPdf.setText(f.getAbsolutePath());
                });
                ligPdf.getChildren().addAll(fPdf, btnParc);

                Label msg = new Label(); msg.setFont(Font.font("Segoe UI", 12)); msg.setWrapText(true); msg.setVisible(false);

                Button btnAjouter = Shared.bouton("＋  Ajouter", "#1d4ed8", "#2563eb");
                btnAjouter.setMaxWidth(Double.MAX_VALUE); btnAjouter.setPrefHeight(44);
                btnAjouter.setOnAction(e -> {
                    String t = fTitre.getText().trim(); String m = fMat.getValue();
                    if (t.isEmpty() || m == null) {
                        msg.setText("⚠ Titre et matière obligatoires."); msg.setTextFill(Color.web("#f87171")); msg.setVisible(true); return;
                    }
                    Cours nouveau = new Cours(t, m, "3ème Collège", nomProf, fPdf.getText().trim().isEmpty() ? null : fPdf.getText().trim());
                    try {
                        boolean ok = ongletActif.equals("tp") ? coursService.addTP(nouveau) : coursService.addCours(nouveau);
                        if (ok) {
                            msg.setText("✓ Ajouté !"); msg.setTextFill(Color.web("#34d399"));
                            fTitre.clear(); fMat.setValue(null); fPdf.clear();
                            root.setCenter(buildBody(stage, root));
                        }
                    } catch (IllegalArgumentException ex) {
                        msg.setText("⚠ " + ex.getMessage()); msg.setTextFill(Color.web("#f87171"));
                    }
                    msg.setVisible(true);
                });

                form.getChildren().addAll(titre, champAvecLabel("Titre *", fTitre),
                    champAvecLabel("Matière *", fMat), champAvecLabel("PDF", ligPdf), btnAjouter, msg);
            }

            case "quiz" -> {
                Text titre = new Text("Créer un Quiz"); titre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16)); titre.setFill(Color.WHITE);
                Label info = new Label("Créez un quiz interactif avec questions à choix multiple. Les élèves obtiennent leur score automatiquement.");
                info.setFont(Font.font("Segoe UI", 12)); info.setTextFill(Color.web("#64748b")); info.setWrapText(true);

                Button btnCreer = Shared.bouton("➕  Nouveau Quiz", "#1d4ed8", "#2563eb");
                btnCreer.setMaxWidth(Double.MAX_VALUE); btnCreer.setPrefHeight(44);
                btnCreer.setOnAction(e ->
                    new QuizCreerView(nomProf, quizService,
                        () -> root.setCenter(buildBody(stage, root))).show(stage));

                form.getChildren().addAll(titre, info, btnCreer);
            }

            case "eleves" -> {
                Text titre = new Text("Ajouter un élève"); titre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16)); titre.setFill(Color.WHITE);
                TextField fPrenom = champForm("Prénom *");
                TextField fNom    = champForm("Nom *");
                TextField fCode   = champForm("Code  (ex: ELV010)");
                Label msg = new Label(); msg.setFont(Font.font("Segoe UI", 12)); msg.setWrapText(true); msg.setVisible(false);

                Button btnAjouter = Shared.bouton("＋  Ajouter l'élève", "#1d4ed8", "#2563eb");
                btnAjouter.setMaxWidth(Double.MAX_VALUE); btnAjouter.setPrefHeight(44);
                btnAjouter.setOnAction(e -> {
                    try {
                        if (eleveService.addEleve(fNom.getText(), fPrenom.getText(), fCode.getText())) {
                            msg.setText("✓ Élève ajouté !"); msg.setTextFill(Color.web("#34d399"));
                            fPrenom.clear(); fNom.clear(); fCode.clear();
                            root.setCenter(buildBody(stage, root));
                        }
                    } catch (IllegalArgumentException ex) {
                        msg.setText("⚠ " + ex.getMessage()); msg.setTextFill(Color.web("#f87171"));
                    }
                    msg.setVisible(true);
                });
                form.getChildren().addAll(titre, champAvecLabel("Prénom *", fPrenom),
                    champAvecLabel("Nom *", fNom), champAvecLabel("Code *", fCode), btnAjouter, msg);
            }
        }
        return form;
    }

    private TextField champForm(String ph) {
        TextField tf = new TextField(); tf.setPromptText(ph); tf.setPrefHeight(38);
        tf.setFont(Font.font("Segoe UI", 13));
        tf.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-prompt-text-fill: #475569;" +
            "-fx-background-radius: 8; -fx-border-color: rgba(255,255,255,0.12); -fx-border-radius: 8;" +
            "-fx-border-width: 1; -fx-padding: 0 10 0 10;");
        return tf;
    }

    private void styleCombo(ComboBox<String> cb) {
        cb.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white; -fx-prompt-text-fill: #475569;" +
            "-fx-background-radius: 8; -fx-border-color: rgba(255,255,255,0.12); -fx-border-radius: 8; -fx-border-width: 1;");
        cb.setPrefHeight(38);
    }

    private VBox champAvecLabel(String label, javafx.scene.Node champ) {
        Label lbl = new Label(label); lbl.setFont(Font.font("Segoe UI", 11)); lbl.setTextFill(Color.web("#94a3b8"));
        return new VBox(5, lbl, champ);
    }

    private Button btnDanger(String texte) {
        Button btn = new Button(texte); btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setPrefHeight(38); btn.setPadding(new Insets(0, 16, 0, 16));
        String base = "-fx-background-color: rgba(239,68,68,0.15); -fx-text-fill: #f87171;" +
            "-fx-background-radius: 8; -fx-border-color: rgba(239,68,68,0.35); -fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand;";
        String hov  = "-fx-background-color: rgba(239,68,68,0.28); -fx-text-fill: #fca5a5;" +
            "-fx-background-radius: 8; -fx-border-color: rgba(239,68,68,0.50); -fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hov));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }
}
