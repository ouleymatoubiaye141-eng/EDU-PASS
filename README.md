# EduPortail — Application JavaFX de gestion scolaire

Architecture **3 couches** : UI (JavaFX) → Service → DAO  
Build : **Maven** (pom.xml inclus — JavaFX géré automatiquement)

---

## Structure du projet

```
EduPortail/
├── pom.xml                            ← Maven : dépendances + plugins
├── src/main/java/edu/
│   ├── Main.java                      ← extends Application (JavaFX)
│   ├── model/
│   │   ├── Utilisateur.java
│   │   ├── Eleve.java
│   │   ├── Professeur.java
│   │   └── Cours.java
│   ├── dao/                           ← Version mémoire (sans MySQL)
│   │   ├── EleveDAO.java
│   │   ├── ProfesseurDAO.java
│   │   └── CoursDAO.java
│   ├── service/
│   │   ├── AuthService.java
│   │   └── CoursService.java
│   └── ui/                            ← JavaFX
│       ├── LoginView.java
│       ├── EleveView.java
│       └── ProfesseurView.java
└── src/main/resources/
    └── schema.sql                     ← Script MySQL (pour plus tard)
```

---

## Prérequis

- **Java 17+** (JDK standard, pas besoin de Liberica ni de JavaFX séparé)
- **Maven 3.8+** — vérifiez avec `mvn -version`

Maven télécharge JavaFX automatiquement depuis Maven Central.

---

## Commandes Maven

```bash
# Compiler le projet
mvn compile

# Lancer l'application directement
mvn javafx:run

# Compiler + créer un JAR exécutable dans target/
mvn package
java -jar target/eduportail-1.0.0.jar
```

---

## Lancer dans VSCode

1. Installez l'extension **Extension Pack for Java**
2. Ouvrez le dossier `EduPortail/` dans VSCode
3. VSCode détecte `pom.xml` automatiquement et télécharge les dépendances
4. Ouvrez `Main.java` → cliquez sur ▶ **Run** en haut à droite
   — ou dans le terminal : `mvn javafx:run`

---

## Comptes de test (version mémoire — aucune BD requise)

| Rôle        | Prénom   | Nom    | Code    |
|-------------|----------|--------|---------|
| Professeur  | Mamadou  | Diallo | PROF001 |
| Professeur  | Fatou    | Ndiaye | PROF002 |
| Élève       | Ibrahima | Sarr   | ELV001  |
| Élève       | Aminata  | Faye   | ELV002  |

---

## Activer MySQL (quand vous serez prêt)

### 1. Décommentez la dépendance dans pom.xml

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
```

### 2. Créez la base de données

```bash
mysql -u root -p < src/main/resources/schema.sql
```

### 3. Modifiez DatabaseConnection.java

```java
private static final String URL      = "jdbc:mysql://localhost:3306/eduportail?...";
private static final String USER     = "root";
private static final String PASSWORD = "votre_mot_de_passe";
```

### 4. Remplacez les DAO mémoire par les versions SQL

Les couches `service/` et `ui/` ne changent pas du tout.
