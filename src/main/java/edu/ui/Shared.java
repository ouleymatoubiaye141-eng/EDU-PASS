package edu.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Stage;

/**
 * Composants partagés entre EleveView et ProfesseurView.
 */
public class Shared {

    public static HBox construireTopbar(Stage stage, String nomComplet, String sousTitreInfo,
                                         String couleurDot) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(12, 24, 12, 20));
        bar.setStyle("-fx-background-color: #0d1b4b; -fx-border-color: rgba(255,255,255,0.07); -fx-border-width: 0 0 1 0;");

        // Mini-logo
        StackPane logoWrap = new StackPane();
        try {
            Image logoImg = new Image(Shared.class.getResourceAsStream("/logo.jpg"));
            ImageView lv = new ImageView(logoImg);
            lv.setFitWidth(42); lv.setFitHeight(42); lv.setPreserveRatio(true);
            Circle clip = new Circle(21, 21, 21); lv.setClip(clip);
            logoWrap.getChildren().add(lv);
        } catch (Exception ex) {
            Text t = new Text("🎓"); t.setFont(Font.font(20));
            logoWrap.getChildren().add(t);
        }

        Label appName = new Label("EDU-PASS");
        appName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        appName.setTextFill(Color.WHITE);

        Label tagline = new Label("Ton ticket pour le brevet.");
        tagline.setFont(Font.font("Segoe UI", FontPosture.ITALIC, 10));
        tagline.setTextFill(Color.web("#94a3b8"));
        VBox brandBox = new VBox(1, appName, tagline);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);

        // Avatar
        String[] parts = nomComplet.split(" ");
        String initiales = (parts.length >= 2)
            ? "" + parts[0].charAt(0) + parts[1].charAt(0)
            : nomComplet.substring(0, Math.min(2, nomComplet.length()));
        Circle cercle = new Circle(20);
        cercle.setFill(Color.web(couleurDot, 0.20));
        cercle.setStroke(Color.web(couleurDot, 0.40)); cercle.setStrokeWidth(1);
        Text initTxt = new Text(initiales.toUpperCase());
        initTxt.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        initTxt.setFill(Color.web(couleurDot));
        StackPane avatar = new StackPane(cercle, initTxt);

        VBox userInfo = new VBox(2);
        Label nom = new Label(nomComplet);
        nom.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13)); nom.setTextFill(Color.WHITE);
        Label sub = new Label(sousTitreInfo);
        sub.setFont(Font.font("Segoe UI", 10)); sub.setTextFill(Color.web("#64748b"));
        userInfo.getChildren().addAll(nom, sub);

        Button btnLogout = new Button("Déconnexion");
        btnLogout.setFont(Font.font("Segoe UI", 12));
        btnLogout.setStyle(
            "-fx-background-color: rgba(239,68,68,0.15); -fx-text-fill: #f87171;" +
            "-fx-background-radius: 8; -fx-border-color: rgba(239,68,68,0.30);" +
            "-fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand; -fx-padding: 5 12 5 12;");
        btnLogout.setOnAction(e -> new LoginView().show(stage));

        bar.getChildren().addAll(
            logoWrap, new Region() {{ setMinWidth(8); }}, brandBox,
            spacer,
            avatar, new Region() {{ setMinWidth(8); }}, userInfo,
            new Region() {{ setMinWidth(16); }}, btnLogout
        );
        return bar;
    }

    public static <T> TableColumn<T, String> colonne(String titre, String prop) {
        TableColumn<T, String> col = new TableColumn<>(titre);
        col.setCellValueFactory(new PropertyValueFactory<>(prop));
        col.setStyle("-fx-alignment: CENTER-LEFT; -fx-text-fill: #e2e8f0; -fx-font-size: 13;");
        return col;
    }

    public static StackPane carteStats(String valeur, String libelle, String couleur) {
        VBox box = new VBox(4);
        box.setPadding(new Insets(12, 18, 12, 18));
        box.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;" +
            "-fx-border-color: rgba(255,255,255,0.07); -fx-border-radius: 12; -fx-border-width: 1;");
        Text num = new Text(valeur);
        num.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26)); num.setFill(Color.web(couleur));
        Text lbl = new Text(libelle);
        lbl.setFont(Font.font("Segoe UI", 11)); lbl.setFill(Color.web("#64748b"));
        box.getChildren().addAll(num, lbl);
        return new StackPane(box);
    }

    public static Button bouton(String texte, String bg, String hover) {
        Button btn = new Button(texte);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setPrefHeight(38); btn.setPadding(new Insets(0, 18, 0, 18));
        String base = "-fx-background-color:" + bg + "; -fx-text-fill:white; -fx-background-radius:8; -fx-cursor:hand;";
        String hov  = "-fx-background-color:" + hover + "; -fx-text-fill:white; -fx-background-radius:8; -fx-cursor:hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hov));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    public static void alerte(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre); alert.setHeaderText(null); alert.setContentText(msg);
        alert.showAndWait();
    }

    public static Button tabButton(String texte, boolean actif) {
        Button btn = new Button(texte);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setPrefHeight(40); btn.setPadding(new Insets(0, 22, 0, 22));
        appliquerStyleTab(btn, actif);
        return btn;
    }

    public static void appliquerStyleTab(Button btn, boolean actif) {
        if (actif) {
            btn.setStyle("-fx-background-color: linear-gradient(to right,#1d4ed8,#059669);" +
                "-fx-text-fill: white; -fx-background-radius: 10; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #94a3b8;" +
                "-fx-background-radius: 10; -fx-cursor: hand;" +
                "-fx-border-color: rgba(255,255,255,0.10); -fx-border-radius: 10; -fx-border-width: 1;");
        }
    }
}
