package edu.dao;

import edu.model.Eleve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EleveDAO {

    private static final List<Eleve> ELEVES = new ArrayList<>(Arrays.asList(
        new Eleve(1, "Sagna",  "Arcel",    "ELV001", "3ème Collège"),
        new Eleve(2, "Ndour",  "Ibrahima", "ELV002", "3ème Collège")
    ));

    private static int nextId = 3;

    public Eleve findByIdentifiants(String nom, String prenom, String code) {
        return ELEVES.stream()
            .filter(e ->
                e.getNom().equalsIgnoreCase(nom.trim()) &&
                e.getPrenom().equalsIgnoreCase(prenom.trim()) &&
                e.getCode().equalsIgnoreCase(code.trim())
            )
            .findFirst()
            .orElse(null);
    }

    public List<Eleve> findAll() {
        return new ArrayList<>(ELEVES);
    }

    public boolean addEleve(String nom, String prenom, String code) {
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank() || code == null || code.isBlank())
            return false;
        ELEVES.add(new Eleve(nextId++, nom.trim(), prenom.trim(), code.trim(), "3ème Collège"));
        return true;
    }

    public boolean deleteEleve(int id) {
        return ELEVES.removeIf(e -> e.getId() == id);
    }
}
