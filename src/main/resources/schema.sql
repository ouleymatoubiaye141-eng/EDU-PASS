-- ============================================================
--  EduPortail — Script SQL de création de la base de données
--  SGBD : MySQL 8+
--  Commande : mysql -u root -p < schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS eduportail
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE eduportail;

-- ---------------------------------------------------------------
-- Table des élèves
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS eleves (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nom     VARCHAR(100) NOT NULL,
    prenom  VARCHAR(100) NOT NULL,
    code    VARCHAR(20)  NOT NULL UNIQUE,
    classe  VARCHAR(50)  NOT NULL,
    CONSTRAINT uq_eleve UNIQUE (nom, prenom, code)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- Table des professeurs
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS professeurs (
    id      INT AUTO_INCREMENT PRIMARY KEY,
    nom     VARCHAR(100) NOT NULL,
    prenom  VARCHAR(100) NOT NULL,
    code    VARCHAR(20)  NOT NULL UNIQUE,
    matiere VARCHAR(100) NOT NULL,
    CONSTRAINT uq_professeur UNIQUE (nom, prenom, code)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- Table des cours
-- ---------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cours (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    titre           VARCHAR(255) NOT NULL,
    matiere         VARCHAR(100) NOT NULL,
    niveau          VARCHAR(50)  NOT NULL DEFAULT '—',
    nom_professeur  VARCHAR(200) NOT NULL,
    chemin_pdf      VARCHAR(500),
    date_ajout      DATETIME DEFAULT NOW()
) ENGINE=InnoDB;

-- ---------------------------------------------------------------
-- Données de test — Professeurs
-- ---------------------------------------------------------------
INSERT INTO professeurs (nom, prenom, code, matiere) VALUES
    ('Diallo',   'Mamadou', 'PROF001', 'Mathématiques'),
    ('Ndiaye',   'Fatou',   'PROF002', 'Histoire-Géographie'),
    ('Sow',      'Oumar',   'PROF003', 'Physique-Chimie');

-- ---------------------------------------------------------------
-- Données de test — Élèves
-- ---------------------------------------------------------------
INSERT INTO eleves (nom, prenom, code, classe) VALUES
    ('Sarr',    'Ibrahima', 'ELV001', 'Terminale S'),
    ('Faye',    'Aminata',  'ELV002', '1ère L'),
    ('Camara',  'Moussa',   'ELV003', '2nde'),
    ('Ba',      'Rokhaya',  'ELV004', '3ème');

-- ---------------------------------------------------------------
-- Données de test — Cours
-- ---------------------------------------------------------------
INSERT INTO cours (titre, matiere, niveau, nom_professeur, chemin_pdf) VALUES
    ('Introduction à l''algèbre linéaire', 'Mathématiques',        'Terminale',  'Mamadou Diallo', NULL),
    ('La Révolution française',            'Histoire',              '1ère',       'Fatou Ndiaye',   NULL),
    ('Les fonctions trigonométriques',     'Mathématiques',        '2nde',       'Mamadou Diallo', NULL),
    ('Grammaire avancée — Le subjonctif',  'Français',             '3ème',       'Fatou Ndiaye',   NULL),
    ('Les lois de Newton',                 'Physique-Chimie',      'Terminale',  'Oumar Sow',      NULL);
