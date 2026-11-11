package edu.service;

import edu.dao.EleveDAO;
import edu.dao.ProfesseurDAO;
import edu.model.Utilisateur;

/**
 * Service d'authentification.
 *
 * Reçoit nom, prénom et code depuis LoginView,
 * interroge EleveDAO et ProfesseurDAO,
 * retourne l'Utilisateur authentifié ou null.
 *
 * En cas d'erreur de base de données, lance une RuntimeException
 * avec un message lisible pour l'afficher dans l'UI.
 */
public class AuthService {

    private final EleveDAO eleveDAO;
    private final ProfesseurDAO professeurDAO;

    public AuthService() {
        // Les DAO sont instanciés ici mais ne touchent PAS à la BD à ce stade.
        // La connexion n'est ouverte qu'au moment de l'appel à authentifier().
        this.eleveDAO = new EleveDAO();
        this.professeurDAO = new ProfesseurDAO();
    }

    /**
     * Tente d'authentifier un utilisateur.
     *
     * @return L'utilisateur (Eleve ou Professeur) si trouvé, null si identifiants incorrects
     * @throws RuntimeException si la base de données est inaccessible
     */
    public Utilisateur authentifier(String nom, String prenom, String code) {
        if (nom == null || prenom == null || code == null ||
            nom.isBlank() || prenom.isBlank() || code.isBlank()) {
            return null;
        }

        try {
            // Vérification côté Professeur en premier
            Utilisateur prof = professeurDAO.findByIdentifiants(nom, prenom, code);
            if (prof != null) {
                System.out.println("[AuthService] Connexion réussie → " + prof);
                return prof;
            }

            // Vérification côté Élève
            Utilisateur eleve = eleveDAO.findByIdentifiants(nom, prenom, code);
            if (eleve != null) {
                System.out.println("[AuthService] Connexion réussie → " + eleve);
                return eleve;
            }

            System.out.println("[AuthService] Identifiants incorrects : " + prenom + " " + nom + " / " + code);
            return null;

        } catch (Exception e) {
            // Remonte l'erreur BD avec un message clair pour l'afficher dans LoginView
            throw new RuntimeException(
                "Impossible de contacter la base de données.\n\n" +
                e.getMessage(), e);
        }
    }
}

