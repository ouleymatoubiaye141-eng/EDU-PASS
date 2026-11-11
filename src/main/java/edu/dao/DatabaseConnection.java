package edu.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fournit une connexion unique (Singleton) à la base de données MySQL.
 *
 * IMPORTANT : cette classe ne lance AUCUNE exception au chargement.
 * Les erreurs sont remontées uniquement quand getConnection() est appelé
 * (c.-à-d. au moment du clic sur "Se connecter", pas au démarrage).
 *
 * Modifiez URL, USER et PASSWORD selon votre configuration.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/eduportail?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "votre_mot_de_passe";  // ← à modifier

    private static Connection instance = null;

    private DatabaseConnection() {}

    /**
     * Retourne la connexion active, ou en crée une nouvelle si nécessaire.
     * Lance une SQLException si la connexion échoue (BD absente, mauvais mdp, etc.)
     */
    public static Connection getConnection() throws SQLException {
        if (instance == null || instance.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(
                    "Driver MySQL introuvable.\n" +
                    "→ Ajoutez mysql-connector-j-xx.jar au classpath.\n" +
                    "  Commande : javac -cp .;mysql-connector-j.jar ...\n" +
                    "             java  -cp .;mysql-connector-j.jar edu.Main", e);
            }

            try {
                instance = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connexion établie avec succès.");
            } catch (SQLException e) {
                throw new SQLException(
                    "Impossible de se connecter à la base de données.\n" +
                    "Vérifiez :\n" +
                    "  1. MySQL est démarré (service mysql start)\n" +
                    "  2. La base 'eduportail' existe (exécutez schema.sql)\n" +
                    "  3. Le mot de passe dans DatabaseConnection.java\n\n" +
                    "Détail : " + e.getMessage(), e);
            }
        }
        return instance;
    }

    public static void closeConnection() {
        if (instance != null) {
            try {
                instance.close();
                System.out.println("[DB] Connexion fermée.");
            } catch (SQLException e) {
                System.err.println("[DB] Erreur fermeture : " + e.getMessage());
            }
        }
    }
}

