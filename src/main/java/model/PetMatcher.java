package model;

import controller.ConsoleController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * PetMatcher is responsible for matching a user to the best pet based on the compatibility score
 * calculated by PetSorter and written to a CSV file.
 * The class follows these steps:
 * 1. Read the CSV file containing pet data and their compatibility scores.
 * 2. Return the first pet from the sorted list (the best match).
 * 3. If the first pet's compatibility score is below 80%, suggest that it might not be the best time for the user to get a pet.
 * 4. After identifying the best match, display the pet's info and open the image on the console.
 */
public class PetMatcher implements IPetMatcher {

//    private final ConsoleController consoleController;
    private final List<Pet> petDatabase;

    public PetMatcher(List<Pet> petDatabase) {
//        this.consoleController = consoleController;
        this.petDatabase = petDatabase;
    }

    /**
     * This method reads the pets' data from a CSV file, sorts them by compatibility score, and returns the best match.
     * If the first pet's compatibility score is below 80%, a message will be printed to suggest that it might not be
     * the best time for the user to get a pet.
     *
     * @param user The user for whom the best match pet is being sought.
     * @param csvFilePath The path to the CSV file containing the pet data and their compatibility scores.
     * @return The pet with the highest compatibility score or null if no match is found.
     */
    @Override
    public Pet sortAndEvaluateBestMatch(User user, String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip header line
            String header = reader.readLine();

            // Read the first line (best match)
            String line = reader.readLine();

            // If no lines were found, return null
            if (line == null) {
                return null;
            }

            // Parse the line
            String[] parts = line.split(",");
            if (parts.length < 5) {
                System.err.println("Invalid CSV format");
                return null;
            }

            String name = parts[0];
            String breed = parts[1];
            String scoreStr = parts[3].replace("%", ""); // Remove % sign

            // Parse score
            double score = Double.parseDouble(scoreStr) / 100.0; // Convert to decimal

            // Find the corresponding Pet object from the database
            Pet bestMatchPet = null;
            for (Pet pet : petDatabase) {
                if (pet.getName().equals(name) && pet.getBreed().equals(breed)) {
                    bestMatchPet = pet;
                    break;
                }
            }

            // If no matching pet was found or  if score is below threshold
            if (bestMatchPet == null || score < 0.80) {
                return null;
            }

            // Display the best match
//            consoleController.displayBestMatch(new PetWithScore(bestMatchPet, score));

            return bestMatchPet;

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            return null;
        }
    }
}