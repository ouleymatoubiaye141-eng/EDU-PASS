package edu.dao;

import edu.model.Cours;

import java.util.ArrayList;
import java.util.List;

public class CoursDAO {

    private static final List<Cours> COURS = new ArrayList<>(List.of(
        new Cours(1, "Algèbre — Équations du 1er degré",  "Mathématiques",  "3ème Collège", "Mme Kasse", null, "10/03/2026"),
        new Cours(2, "Géométrie — Théorème de Pythagore", "Mathématiques",  "3ème Collège", "Mme Kasse", null, "09/03/2026"),
        new Cours(3, "Statistiques et probabilités",       "Mathématiques",  "3ème Collège", "Mme Kasse", null, "08/03/2026"),
        new Cours(4, "La matière et ses transformations",  "Physique-Chimie","3ème Collège", "Mr Deme",   null, "07/03/2026"),
        new Cours(5, "Les lois de Newton",                 "Physique-Chimie","3ème Collège", "Mr Deme",   null, "06/03/2026"),
        new Cours(6, "Électricité — circuits en série",    "Physique-Chimie","3ème Collège", "Mr Deme",   null, "05/03/2026")
    ));

    private static final List<Cours> TP = new ArrayList<>(List.of(
        new Cours(101, "TP — Mesure des angles",            "Mathématiques",  "3ème Collège", "Mme Kasse", null, "10/03/2026"),
        new Cours(102, "TP — Construction géométrique",     "Mathématiques",  "3ème Collège", "Mme Kasse", null, "09/03/2026"),
        new Cours(103, "TP — Manipulation pH et réactions", "Physique-Chimie","3ème Collège", "Mr Deme",   null, "08/03/2026"),
        new Cours(104, "TP — Circuits électriques",         "Physique-Chimie","3ème Collège", "Mr Deme",   null, "07/03/2026")
    ));

    private static int nextId = 10;

    public List<Cours> findAllCours() { return new ArrayList<>(COURS); }
    public List<Cours> findAllTP()    { return new ArrayList<>(TP); }

    public boolean insertCours(Cours c) {
        COURS.add(0, new Cours(nextId++, c.getTitre(), c.getMatiere(), c.getNiveau(),
            c.getNomProfesseur(), c.getCheminPdf(),
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        return true;
    }

    public boolean insertTP(Cours c) {
        TP.add(0, new Cours(100 + nextId++, c.getTitre(), c.getMatiere(), c.getNiveau(),
            c.getNomProfesseur(), c.getCheminPdf(),
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        return true;
    }

    public boolean deleteCours(int id) { return COURS.removeIf(c -> c.getId() == id); }
    public boolean deleteTP(int id)    { return TP.removeIf(c -> c.getId() == id); }
}
