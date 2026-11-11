package edu.service;

import edu.dao.CoursDAO;
import edu.model.Cours;

import java.util.List;

public class CoursService {

    private final CoursDAO coursDAO = new CoursDAO();

    public List<Cours> getAllCours() { return coursDAO.findAllCours(); }
    public List<Cours> getAllTP()    { return coursDAO.findAllTP(); }

    public boolean addCours(Cours c) { valider(c); return coursDAO.insertCours(c); }
    public boolean addTP(Cours c)    { valider(c); return coursDAO.insertTP(c); }

    public boolean deleteCours(int id) { return coursDAO.deleteCours(id); }
    public boolean deleteTP(int id)    { return coursDAO.deleteTP(id); }

    private void valider(Cours c) {
        if (c == null) throw new IllegalArgumentException("Le cours ne peut pas être null.");
        if (c.getTitre() == null || c.getTitre().isBlank()) throw new IllegalArgumentException("Titre obligatoire.");
        if (c.getMatiere() == null || c.getMatiere().isBlank()) throw new IllegalArgumentException("Matière obligatoire.");
    }
}
