package controller;

import model.*;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ConsoleController is responsible for handling user interaction with the console.
 * This class manages the display of pet information based on the user's choice.
 *
 * Notes on image display:
 * - For methods like displayBestMatch(...) and displayRecommendedPets(...), the pet image will be opened
 *   using the system's default image viewer (cross-platform supported).
 * - displayAllPetsWithScores(...) will NOT display any images, only text data.
 */
public class ConsoleController {

    private final User user;
    private final ICompatibilityCalculator calculator;
    private static final String LINE_SEPARATOR = "============================================================";
    private static final String SECTION_SEPARATOR = "------------------------------------------------------------";

    // Constructor to initialize with user and calculator
    public ConsoleController(User user, ICompatibilityCalculator calculator) {
        this.user = user;
        this.calculator = calculator;
    }

    /**
     * Displays the best match pet to the user.
     * This method will read the CSV containing sorted pets and display the best matched pet's details.
     * The pet image will also be opened using the system default image viewer.
     *
     * @param csvFilePath The path to the CSV file containing the sorted pet information.
     */
    public void displayBestMatch(String csvFilePath) {
        List<Pet> allPets = Database.PetDatabase.getAllPets();
        PetMatcher matcher = new PetMatcher(allPets);
        Pet bestMatch = matcher.sortAndEvaluateBestMatch(user, csvFilePath);

        if (bestMatch == null) {
            displayNoCompatiblePets();
            return;
        }

        // Get the score from CSV
        double score = getScoreFromCSV(bestMatch.getName(), bestMatch.getBreed(), csvFilePath);

        if (score < 0.80) {
            displayLowCompatibility(new PetWithScore(bestMatch, score));
        } else {
            displayBestMatchDetails(new PetWithScore(bestMatch, score));
        }
    }

    /**
     * Displays recommended pets to the user.
     * This method will filter pets with compatibility scores above a certain threshold (e.g., 80%)
     * and display their details.
     * Each matching pet's image will also be opened using the system default image viewer.
     *
     * @param csvFilePath The path to the CSV file containing pet information.
     */
    public void displayRecommendedPets(String csvFilePath) {
        List<Pet> allPets = Database.PetDatabase.getAllPets();
        PetFilter filter = new PetFilter(csvFilePath);
        List<Pet> filteredPets = filter.filterPetsByCompatibility(allPets);

        if (filteredPets.isEmpty()) {
            displayNoCompatiblePets();
            return;
        }

        // Convert to PetWithScore for display
        List<PetWithScore> petsWithScores = new ArrayList<>();
        for (Pet pet : filteredPets) {
            double score = getScoreFromCSV(pet.getName(), pet.getBreed(), csvFilePath);
            petsWithScores.add(new PetWithScore(pet, score));
        }

        displayFilteredPets(petsWithScores);
    }

    /**
     * Displays all pets with their compatibility scores.
     * This method will read the CSV file containing all pets and display their details (name, breed, score).
     * NOTE: This method does NOT open or display any pet images.
     *
     * @param csvFilePath The path to the CSV file containing all pet data.
     */
    public void displayAllPetsWithScores(String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            System.out.println("\n" + LINE_SEPARATOR);
            System.out.println("ALL PETS WITH COMPATIBILITY SCORES");
            System.out.println(LINE_SEPARATOR);

            // Skip header line
            String header = reader.readLine();

            String line;
            int count = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Skip invalid rows

                String name = parts[0];
                String breed = parts[1];
                String type = parts[2];
                String score = parts[3];
                String imagePath = parts[4];

                System.out.println(count + ". " + name + " (" + type + " - " + breed + ")");
                System.out.println("   Compatibility: " + score);
                System.out.println(SECTION_SEPARATOR);
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    /**
     * Displays search results for a pet by type and/or breed.
     * This method will receive a list of matched pets and display their details.
     * Each matching pet's image will also be opened using the system default image viewer.
     *
     * @param results The list of matching pets with scores.
     */
    public void displaySearchResult(List<PetWithScore> results) {
        if (results.isEmpty()) {
            System.out.println("\n" + LINE_SEPARATOR);
            System.out.println("❌ NO SEARCH RESULTS ❌");
            System.out.println(LINE_SEPARATOR);
            System.out.println("No pets found matching your search criteria.");
            System.out.println("Try searching for a different type or breed.");
            System.out.println(LINE_SEPARATOR);
            return;
        }

        System.out.println("\n" + LINE_SEPARATOR);
        System.out.println("🔍 SEARCH RESULTS 🔍");
        System.out.println(LINE_SEPARATOR);
        System.out.println("Found " + results.size() + " matching pets:");
        System.out.println(SECTION_SEPARATOR);

        int count = 1;
        for (PetWithScore petWithScore : results) {
            Pet pet = petWithScore.getPet();
            System.out.println(count + ". " + pet.getName() + " (" + pet.getType() + " - " + pet.getBreed() + ")");
            System.out.println("   Compatibility: " + formatPercentage(petWithScore.getScore()));
            displayPetDetails(pet);
            System.out.println(SECTION_SEPARATOR);
            count++;

            // Open the image
            openImage(pet.getImagePath());
        }
    }

    /**
     * Displays a detailed view of the best matching pet.
     *
     * @param petWithScore the best matching pet with its compatibility score
     */
    private void displayBestMatchDetails(PetWithScore petWithScore) {
        Pet pet = petWithScore.getPet();
        System.out.println("\n" + LINE_SEPARATOR);
        System.out.println("🎉 YOUR BEST PET MATCH 🎉");
        System.out.println(LINE_SEPARATOR);
        System.out.println("Compatibility Score: " + formatPercentage(petWithScore.getScore()));
        System.out.println(SECTION_SEPARATOR);
        displayPetDetails(pet);
        System.out.println(LINE_SEPARATOR);

        // Open the image
        openImage(pet.getImagePath());
    }

    /**
     * Displays a message when the best match has a low compatibility score (below 80%).
     *
     * @param petWithScore the best matching pet with its low compatibility score
     */
    public void displayLowCompatibility(PetWithScore petWithScore) {
        Pet pet = petWithScore.getPet();
        System.out.println("\n" + LINE_SEPARATOR);
        System.out.println("⚠️ LOW COMPATIBILITY MATCH ⚠️");
        System.out.println(LINE_SEPARATOR);
        System.out.println("Your highest compatibility score is only " + formatPercentage(petWithScore.getScore()));
        System.out.println("Based on your current lifestyle and preferences, it might not be the best time for a pet.");
        System.out.println(SECTION_SEPARATOR);
        System.out.println("If you still want to consider a pet, here's your best match:");
        displayPetDetails(pet);
        System.out.println(LINE_SEPARATOR);

        // Open the image
        openImage(pet.getImagePath());
    }

    /**
     * Displays a message when no compatible pets are found.
     */
    public void displayNoCompatiblePets() {
        System.out.println("\n" + LINE_SEPARATOR);
        System.out.println("❌ NO COMPATIBLE PETS FOUND ❌");
        System.out.println(LINE_SEPARATOR);
        System.out.println("Based on your current lifestyle and preferences, we couldn't find a suitable pet match.");
        System.out.println("Consider adjusting your criteria or waiting until your circumstances change.");
        System.out.println(LINE_SEPARATOR);
    }

    /**
     * Displays a list of filtered pets with compatibility scores above 80%.
     *
     * @param filteredPets the list of pets with compatibility scores above 80%
     */
    public void displayFilteredPets(List<PetWithScore> filteredPets) {
        System.out.println("\n" + LINE_SEPARATOR);
        System.out.println("🔍 HIGHLY COMPATIBLE PETS (80%+ MATCH) 🔍");
        System.out.println(LINE_SEPARATOR);
        System.out.println("Found " + filteredPets.size() + " pets with compatibility scores above 80%:");
        System.out.println(SECTION_SEPARATOR);

        int count = 1;
        for (PetWithScore petWithScore : filteredPets) {
            Pet pet = petWithScore.getPet();
            System.out.println(count + ". " + pet.getName() + " (" + pet.getType() + " - " + pet.getBreed() + ")");
            System.out.println("   Compatibility: " + formatPercentage(petWithScore.getScore()));
            System.out.println(SECTION_SEPARATOR);
            displayPetDetails(pet);
            System.out.println(LINE_SEPARATOR);
            count++;

            // Open the image
            openImage(pet.getImagePath());
        }
    }

    /**
     * Opens an image file using the system's default image viewer.
     *
     * @param imagePath the path to the image file
     */
    private void openImage(String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                System.err.println("Image file not found: " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Could not open image: " + e.getMessage());
        }
    }

    /**
     * Displays detailed information about a pet.
     *
     * @param pet the pet to display
     */
    private void displayPetDetails(Pet pet) {
        System.out.println("Name: " + pet.getName());
        System.out.println("Type: " + pet.getType());
        System.out.println("Breed: " + pet.getBreed());
        System.out.println("Gender: " + pet.getGender());
        System.out.println("MBTI: " + pet.getMbti());
        System.out.println("Energy Level: " + pet.getEnergyLevel() + "/10");
        System.out.println("Space Needed: " + pet.getRequiredSpace() + " sq ft");
        System.out.println("Monthly Cost: $" + pet.getMonthlyCost());
        System.out.println("Time Needed: " + pet.getTimeNeededPerDay() + " hours/day");
        System.out.println("Allergenic: " + (pet.isAllergenic() ? "Yes" : "No"));
        System.out.println("Requires Yard: " + (pet.requiresYard() ? "Yes" : "No"));
    }

    /**
     * Formats a score as a percentage.
     *
     * @param score the score to format (0.0 to 1.0)
     * @return formatted percentage string
     */
    private String formatPercentage(double score) {
        return String.format("%.0f%%", score * 100);
    }

    /**
     * Gets the compatibility score for a pet from the CSV file.
     *
     * @param petName the name of the pet
     * @param petBreed the breed of the pet
     * @param csvFilePath the path to the CSV file
     * @return the compatibility score (0.0 to 1.0) or 0.0 if not found
     */
    private double getScoreFromCSV(String petName, String petBreed, String csvFilePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFilePath))) {
            // Skip header line
            String header = reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Skip invalid rows

                String name = parts[0];
                String breed = parts[1];
                String scoreStr = parts[3].replace("%", ""); // Remove % sign

                if (petName.equals(name) && petBreed.equals(breed)) {
                    return Double.parseDouble(scoreStr) / 100.0; // Convert to decimal
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }

        return 0.0; // Default if not found
    }
}