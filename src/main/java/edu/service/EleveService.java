package edu.service;

import edu.dao.EleveDAO;
import edu.model.Eleve;

import java.util.List;

public class EleveService {

    private final EleveDAO eleveDAO = new EleveDAO();

    public List<Eleve> getAllEleves() { return eleveDAO.findAll(); }

    public boolean addEleve(String nom, String prenom, String code) {
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank() || code == null || code.isBlank())
            throw new IllegalArgumentException("Tous les champs sont obligatoires.");
        return eleveDAO.addEleve(nom, prenom, code);
    }

    public boolean deleteEleve(int id) { return eleveDAO.deleteEleve(id); }
}
