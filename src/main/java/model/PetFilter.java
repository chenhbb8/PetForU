package model;

import Database.PetDatabase;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * PetFilter is responsible for filtering pets based on their compatibility score.
 * This class filters the pets and delegates the printing of the filtered pets' details to the ConsoleController.
 * The pets are already sorted by compatibility score with tie-breaking logic applied (in descending order).
 */
public class PetFilter implements IPetFilter {

    private final String csvPath;

    public PetFilter(String csvPath) {
        this.csvPath = csvPath;
    }

    @Override
    public List<Pet> filterPetsByCompatibility(List<Pet> pets) {
        // Add the null check
        if (pets == null) {
            return new ArrayList<>();
        }

        List<PetWithScore> filteredPets = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            // Skip header line
            String line = reader.readLine();

            // Read each pet from the CSV
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Skip invalid rows

                String name = parts[0];
                String breed = parts[1];
                String type = parts[2];
                String scoreStr = parts[3].replace("%", ""); // Remove % sign
                String imagePath = parts[4];

                // Parse score and check if it's > 80%
                double score = Double.parseDouble(scoreStr) / 100.0; // Convert to decimal

                if (score > 0.80) {
                    // Find the corresponding Pet object from the list
                    for (Pet pet : pets) {
                        if (pet.getName().equals(name) && pet.getBreed().equals(breed)) {
                            filteredPets.add(new PetWithScore(pet, score));
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        // Extract and return just the Pet objects
        List<Pet> result = new ArrayList<>();
        for (PetWithScore petWithScore : filteredPets) {
            result.add(petWithScore.getPet());
        }

        return result;
    }
}