package model;

import controller.ConsoleController;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PetSearcher is responsible for searching pets by type and optional breed
 * from the sorted compatibility CSV file written by PetSorter.
 * This class reads the CSV, filters pets by user input, and passes results to ConsoleController.
 */
public class PetSearcher implements IPetSearcher {

    private final ConsoleController consoleController;
    private final List<Pet> petDatabase;

    /**
     * Default constructor that uses the system pet database.
     *
     * @param consoleController the console controller for displaying results
     */
    public PetSearcher(ConsoleController consoleController) {
        this.consoleController = consoleController;
        this.petDatabase = Database.PetDatabase.getAllPets();
    }

    /**
     * Constructor with injectable pet database for testing.
     *
     * @param consoleController the console controller for displaying results
     * @param petDatabase a list of pets to use instead of the system database
     */
    public PetSearcher(ConsoleController consoleController, List<Pet> petDatabase) {
        this.consoleController = consoleController;
        this.petDatabase = petDatabase;
    }

    /**
     * Searches for pets by type and optional breed and sends results to the console.
     *
     * @param petType  the type of pet to search for (e.g., Dog, Cat)
     * @param petBreed optional breed (can be empty to match all breeds of that type)
     * @param csvPath  the CSV file written by PetSorter
     */
    @Override
    public void searchAndDisplay(String petType, String petBreed, String csvPath) {
        List<PetWithScore> matchingPets = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            // Skip header line
            String header = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Skip invalid rows

                String name = parts[0];
                String breed = parts[1];
                String type = parts[2];
                String scoreStr = parts[3].replace("%", ""); // Remove % sign
                String imagePath = parts[4];

                // Case-insensitive comparison for type
                if (!type.equalsIgnoreCase(petType)) {
                    continue;
                }

                // Check breed if provided
                if (petBreed != null && !petBreed.isEmpty() && !breed.equalsIgnoreCase(petBreed)) {
                    continue;
                }

                try {
                    // Parse score
                    double score = Double.parseDouble(scoreStr) / 100.0; // Convert to decimal

                    // Find the corresponding Pet object from the database
                    for (Pet pet : petDatabase) {
                        if (pet.getName().equals(name) && pet.getBreed().equals(breed)) {
                            matchingPets.add(new PetWithScore(pet, score));
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid score format: " + scoreStr);
                    // Continue processing other entries
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        // Display search results
        consoleController.displaySearchResult(matchingPets);
    }
}