package edu;

import edu.ui.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Point d'entrée JavaFX — EduPortail.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        new LoginView().show(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
