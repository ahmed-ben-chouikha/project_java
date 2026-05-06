package edu.connexion3a36.tools;

public class ValidationUtil {

    private static final String LETTERS_PATTERN = "^[\\p{L}][\\p{L}\\s'’-]*$";

    // Validation for Budget
    public static boolean validateBudget(float montantAlloue, int teamId, String statut) {
        if (montantAlloue <= 0) {
            System.err.println("Erreur: Le montant alloué doit être positif");
            return false;
        }
        if (teamId <= 0) {
            System.err.println("Erreur: L'ID de l'équipe est invalide");
            return false;
        }
        if (!isValidStatut(statut)) {
            System.err.println("Erreur: Le statut est invalide. Statuts valides: en attente, approuvé, refusé, épuisé");
            return false;
        }
        return true;
    }

    // Validation for Depense
    public static boolean validateDepense(String titre, float montant, String statut, String categorie) {
        if (titre == null || titre.trim().isEmpty()) {
            System.err.println("Erreur: Le titre est obligatoire");
            return false;
        }
        if (titre.length() > 255) {
            System.err.println("Erreur: Le titre ne doit pas dépasser 255 caractères");
            return false;
        }
        if (montant <= 0) {
            System.err.println("Erreur: Le montant doit être positif");
            return false;
        }
        if (!isValidDepenseStatut(statut)) {
            System.err.println("Erreur: Le statut est invalide. Statuts valides: en attente, approuvé, refusé, payée");
            return false;
        }
        if (!isValidCategorie(categorie)) {
            System.err.println("Erreur: La catégorie est invalide. Catégories valides: salaire, équipement, voyage, autre");
            return false;
        }
        return true;
    }

    // Check if valid budget status
    public static boolean isValidStatut(String statut) {
        return statut != null && (statut.equals("en attente") || statut.equals("approuvé") || 
                                   statut.equals("refusé") || statut.equals("épuisé"));
    }

    // Check if valid depense status
    public static boolean isValidDepenseStatut(String statut) {
        return statut != null && (statut.equals("en attente") || statut.equals("approuvé") || 
                                   statut.equals("refusé") || statut.equals("payée"));
    }

    // Check if valid categorie
    public static boolean isValidCategorie(String categorie) {
        return categorie != null && (categorie.equals("salaire") || categorie.equals("équipement") || 
                                      categorie.equals("voyage") || categorie.equals("autre"));
    }

    // Validate montant utilise (not greater than montant alloue)
    public static boolean validateMontantUtilise(float montantUtilise, float montantAlloue) {
        if (montantUtilise < 0) {
            System.err.println("Erreur: Le montant utilisé ne peut pas être négatif");
            return false;
        }
        if (montantUtilise > montantAlloue) {
            System.err.println("Erreur: Le montant utilisé ne peut pas dépasser le montant alloué");
            return false;
        }
        return true;
    }

    // Validate amount is not too large
    public static boolean validateAmount(float amount) {
        if (amount <= 0 || amount > 999999.99f) {
            System.err.println("Erreur: Le montant doit être entre 0.01 et 999999.99");
            return false;
        }
        return true;
    }

    // Validate description length
    public static boolean validateDescription(String description) {
        if (description != null && description.length() > 5000) {
            System.err.println("Erreur: La description ne doit pas dépasser 5000 caractères");
            return false;
        }
        return true;
    }

    // Check if amount exceeds budget
    public static float checkBudgetExcess(float montantAlloue, float montantUtilise) {
        if (montantUtilise > montantAlloue) {
            return montantUtilise - montantAlloue;
        }
        return 0;
    }

    // Validate that a text contains letters (and optional spaces/apostrophes/hyphens) only.
    public static boolean isLettersOnly(String value) {
        if (value == null) {
            return false;
        }
        String trimmed = value.trim();
        return !trimmed.isEmpty() && trimmed.matches(LETTERS_PATTERN);
    }

    // Validate team form fields and return a user-friendly message when invalid.
    public static String validateTeam(String name,
                                      String country,
                                      String jeu,
                                      String niveau,
                                      String description,
                                      String detailedDescription,
                                      int score) {
        if (name == null || name.trim().isEmpty()) {
            return "Team name is required.";
        }
        if (name.trim().length() < 2 || name.trim().length() > 100) {
            return "Team name must be between 2 and 100 characters.";
        }
        if (country == null || country.trim().isEmpty()) {
            return "Country is required.";
        }
        if (country.trim().length() < 2 || country.trim().length() > 100) {
            return "Country must be between 2 and 100 characters.";
        }
        if (jeu == null || jeu.trim().isEmpty()) {
            return "Game is required.";
        }
        if (niveau == null || niveau.trim().isEmpty()) {
            return "Level is required.";
        }
        if (description != null && description.length() > 500) {
            return "Description must be 500 characters or less.";
        }
        if (detailedDescription != null && detailedDescription.length() > 5000) {
            return "Detailed description must be 5000 characters or less.";
        }
        if (score < 0 || score > 1000000) {
            return "Score must be between 0 and 1000000.";
        }
        return "";
    }

    // Validate tournament form fields and return a user-friendly error message.
    public static String validateTournament(String name,
                                            String gameType,
                                            java.time.LocalDate startDate,
                                            java.time.LocalDate endDate,
                                            String status,
                                            int maxTeams,
                                            String description,
                                            String location,
                                            String rules,
                                            double prizePool) {
        if (name == null || name.trim().isEmpty()) {
            return "Tournament name is required.";
        }
        if (name.trim().length() < 2 || name.trim().length() > 255) {
            return "Tournament name must be between 2 and 255 characters.";
        }
        if (gameType == null || gameType.trim().isEmpty()) {
            return "Game type is required.";
        }
        if (startDate == null) {
            return "Start date is required.";
        }
        if (endDate == null) {
            return "End date is required.";
        }
        if (endDate.isBefore(startDate)) {
            return "End date must be on or after start date.";
        }
        if (status == null || status.trim().isEmpty()) {
            return "Status is required.";
        }
        String normalized = status.trim().toLowerCase();
        if (!("open".equals(normalized) || "closed".equals(normalized) || "finished".equals(normalized)
                || "pending".equals(normalized) || "ongoing".equals(normalized))) {
            return "Status must be open, closed, finished, pending, or ongoing.";
        }
        if (maxTeams <= 0) {
            return "Max teams must be greater than 0.";
        }
        if (description != null && description.length() > 5000) {
            return "Description must be 5000 characters or less.";
        }
        if (rules == null || rules.trim().isEmpty()) {
            return "Rules are required.";
        }
        if (location != null && location.length() > 255) {
            return "Location must be 255 characters or less.";
        }
        if (rules != null && rules.length() > 5000) {
            return "Rules must be 5000 characters or less.";
        }
        if (prizePool < 0) {
            return "Prize pool cannot be negative.";
        }
        return "";
    }
}

