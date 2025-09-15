package controller;

import model.*;

import java.io.File;
import java.util.List;

/**
 * PetManager handles all coordination between components like PetSorter, PetMatcher,
 * PetFilter, and PetSearcher. It abstracts the logic from PetForUApp to keep it clean.
 */
public class PetManager {

    private final User user;
    private final String csvPath;
    private final ICompatibilityCalculator calculator;
    private final List<Pet> allPets;

    public PetManager(String gender, String preferredPetGender, int iScore, int sScore,
                      int tScore, int jScore, int energy, double space, double budget,
                      boolean allergy, boolean hasYard, double time) {

        this.user = new User(gender, preferredPetGender,
                (iScore == 1 ? "I" : "E")
                        + (sScore == 1 ? "S" : "N")
                        + (tScore == 1 ? "T" : "F")
                        + (jScore == 1 ? "J" : "P"),
                energy, space, budget, allergy, hasYard, time);

        this.calculator = new CompatibilityCalculator();
        this.allPets = Database.PetDatabase.getAllPets();
        this.csvPath = "output/pet_compatibility.csv";

        // Create output directory if it doesn't exist
        new File("output").mkdirs();

        // Precompute CSV
        new PetSorter(user, calculator).sortAndExportToCSV(allPets, csvPath);
    }

    public User getUser() {
        return user;
    }

    public String getCsvPath() {
        return csvPath;
    }
}
