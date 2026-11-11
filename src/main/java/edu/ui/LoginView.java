package edu.ui;

import edu.model.Eleve;
import edu.model.Professeur;
import edu.model.Utilisateur;
import edu.service.AuthService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class LoginView {

    private final AuthService authService = new AuthService();

    public void show(Stage stage) {
        stage.setTitle("EDU-PASS — Connexion");
        stage.setResizable(false);

        StackPane root = new StackPane();
        root.setPrefSize(520, 680);

        // Fond dégradé bleu-vert comme le logo
        Rectangle bg = new Rectangle(520, 680);
        LinearGradient grad = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#0d1b4b")),
            new Stop(0.5, Color.web("#0f3460")),
            new Stop(1, Color.web("#0a7a4b")));
        bg.setFill(grad);

        Circle c1 = new Circle(200); c1.setFill(Color.web("#1d4ed8", 0.08));
        c1.setTranslateX(-160); c1.setTranslateY(-180);
        Circle c2 = new Circle(150); c2.setFill(Color.web("#10b981", 0.10));
        c2.setTranslateX(200); c2.setTranslateY(220);

        // Carte centrale
        VBox card = new VBox(16);
        card.setPadding(new Insets(36, 44, 36, 44));
        card.setMaxWidth(420);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
            "-fx-background-color: rgba(255,255,255,0.06);" +
            "-fx-background-radius: 22;" +
            "-fx-border-color: rgba(255,255,255,0.13);" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 22;"
        );
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.55));
        shadow.setRadius(48); shadow.setOffsetY(12);
        card.setEffect(shadow);

        // Logo EDU-PASS
        StackPane logoWrap = new StackPane();
        try {
            Image logoImg = new Image(getClass().getResourceAsStream("/logo.jpg"));
            ImageView logoView = new ImageView(logoImg);
            logoView.setFitWidth(110);
            logoView.setFitHeight(110);
            logoView.setPreserveRatio(true);
            // Clip circulaire
            Circle clip = new Circle(55, 55, 55);
            logoView.setClip(clip);
            logoWrap.getChildren().add(logoView);
        } catch (Exception ex) {
            // Fallback si image absente
            Text fallback = new Text("🎓");
            fallback.setFont(Font.font(40));
            logoWrap.getChildren().add(fallback);
        }

        // Titre
        Text titre = new Text("EDU-PASS");
        titre.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        titre.setFill(Color.WHITE);

        Text sousTitre = new Text("Ton ticket pour le brevet.");
        sousTitre.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 13));
        sousTitre.setFill(Color.web("#94a3b8"));

        // Champs
        TextField champPrenom = creerChamp("Prénom");
        TextField champNom    = creerChamp("Nom de famille");
        TextField champCode   = creerChamp("Code identifiant  (ex: PROF001)");

        // Erreur
        Label labelErreur = new Label();
        labelErreur.setFont(Font.font("Segoe UI", 12));
        labelErreur.setTextFill(Color.web("#f87171"));
        labelErreur.setVisible(false);
        labelErreur.setWrapText(true);
        labelErreur.setMaxWidth(Double.MAX_VALUE);
        labelErreur.setAlignment(Pos.CENTER);

        // Bouton connexion
        Button btnConnexion = new Button("Se connecter  →");
        btnConnexion.setMaxWidth(Double.MAX_VALUE);
        btnConnexion.setPrefHeight(46);
        btnConnexion.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        String styleBtnBase = "-fx-background-color: linear-gradient(to right, #1d4ed8, #059669);" +
            "-fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
        String styleBtnHov = "-fx-background-color: linear-gradient(to right, #2563eb, #10b981);" +
            "-fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;";
        btnConnexion.setStyle(styleBtnBase);
        btnConnexion.setOnMouseEntered(e -> btnConnexion.setStyle(styleBtnHov));
        btnConnexion.setOnMouseExited(e -> btnConnexion.setStyle(styleBtnBase));

        // Comptes de démo
        VBox aide = new VBox(6);
        aide.setPadding(new Insets(12, 14, 12, 14));
        aide.setStyle(
            "-fx-background-color: rgba(59,130,246,0.08);" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: rgba(59,130,246,0.20);" +
            "-fx-border-width: 1; -fx-border-radius: 10;"
        );
        Text aideTitle = new Text("Comptes de démonstration");
        aideTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        aideTitle.setFill(Color.web("#60a5fa"));
        aide.getChildren().addAll(
            aideTitle,
            compteInfo("PROF",  "Mme Kasse",      "PROF001"),
            compteInfo("PROF",  "Mr Deme",         "PROF002"),
            compteInfo("ÉLÈVE", "Arcel Sagna",     "ELV001"),
            compteInfo("ÉLÈVE", "Ibrahima Ndour",  "ELV002")
        );

        // Action
        Runnable tenterConnexion = () -> {
            String prenom = champPrenom.getText().trim();
            String nom    = champNom.getText().trim();
            String code   = champCode.getText().trim();
            if (prenom.isEmpty() || nom.isEmpty() || code.isEmpty()) {
                labelErreur.setText("Veuillez remplir tous les champs.");
                labelErreur.setVisible(true); return;
            }
            Utilisateur utilisateur;
            try {
                utilisateur = authService.authentifier(nom, prenom, code);
            } catch (RuntimeException ex) {
                labelErreur.setText(ex.getMessage());
                labelErreur.setVisible(true); return;
            }
            if (utilisateur == null) {
                labelErreur.setText("Identifiants incorrects. Vérifiez nom, prénom et code.");
                labelErreur.setVisible(true);
                champCode.clear();
                agiter(card); return;
            }
            labelErreur.setVisible(false);
            if (utilisateur instanceof Eleve)
                new EleveView((Eleve) utilisateur).show(stage);
            else if (utilisateur instanceof Professeur)
                new ProfesseurView((Professeur) utilisateur).show(stage);
        };

        btnConnexion.setOnAction(e -> tenterConnexion.run());
        champCode.setOnAction(e -> tenterConnexion.run());
        champNom.setOnAction(e -> tenterConnexion.run());

        card.getChildren().addAll(
            logoWrap, titre, sousTitre,
            creerLigneChamp("Prénom", champPrenom),
            creerLigneChamp("Nom", champNom),
            creerLigneChamp("Code identifiant", champCode),
            btnConnexion, labelErreur, aide
        );

        root.getChildren().addAll(bg, c1, c2, card);

        card.setOpacity(0); card.setTranslateY(30);
        FadeTransition ft = new FadeTransition(Duration.millis(500), card); ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), card); tt.setToY(0);
        ft.play(); tt.play();

        stage.setScene(new Scene(root));
        stage.show();
    }

    private TextField creerChamp(String ph) {
        TextField tf = new TextField();
        tf.setPromptText(ph); tf.setPrefHeight(42);
        tf.setFont(Font.font("Segoe UI", 14));
        String base = "-fx-background-color: rgba(255,255,255,0.07); -fx-text-fill: white;" +
            "-fx-prompt-text-fill: #64748b; -fx-background-radius: 10;" +
            "-fx-border-color: rgba(255,255,255,0.15); -fx-border-radius: 10;" +
            "-fx-border-width: 1; -fx-padding: 0 12 0 12;";
        String focus = "-fx-background-color: rgba(255,255,255,0.10); -fx-text-fill: white;" +
            "-fx-prompt-text-fill: #64748b; -fx-background-radius: 10;" +
            "-fx-border-color: #3b82f6; -fx-border-radius: 10;" +
            "-fx-border-width: 1.5; -fx-padding: 0 12 0 12;";
        tf.setStyle(base);
        tf.focusedProperty().addListener((o, old, f) -> tf.setStyle(f ? focus : base));
        return tf;
    }

    private VBox creerLigneChamp(String label, TextField champ) {
        Label lbl = new Label(label);
        lbl.setFont(Font.font("Segoe UI", 12));
        lbl.setTextFill(Color.web("#94a3b8"));
        return new VBox(5, lbl, champ);
    }

    private HBox compteInfo(String role, String nom, String code) {
        boolean isProf = role.equals("PROF");
        Label badge = new Label(role);
        badge.setFont(Font.font("Segoe UI", FontWeight.BOLD, 9));
        badge.setTextFill(isProf ? Color.web("#60a5fa") : Color.web("#34d399"));
        badge.setStyle("-fx-background-color: " +
            (isProf ? "rgba(59,130,246,0.15)" : "rgba(52,211,153,0.15)") +
            "; -fx-background-radius: 20; -fx-padding: 2 7 2 7;");
        Label info = new Label(nom + "  ·  " + code);
        info.setFont(Font.font("Segoe UI", 11));
        info.setTextFill(Color.web("#94a3b8"));
        HBox row = new HBox(8, badge, info);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void agiter(VBox node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(60), node);
        tt.setFromX(0); tt.setByX(10); tt.setCycleCount(4); tt.setAutoReverse(true);
        tt.play();
    }
}
