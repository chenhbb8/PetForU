package model;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PetSorterTest {

    private static final String TEST_DIR = "test_output";
    private static final String TEST_CSV_PATH = TEST_DIR + "/pet_test.csv";

    private List<Pet> testPets;
    private User testUser;
    private ICompatibilityCalculator calculator;
    private PetSorter petSorter;

    @BeforeEach
    public void setUp() {
        // Create test directory if it doesn't exist
        new File(TEST_DIR).mkdirs();

        // Setup test data
        setupTestData();

        // Initialize PetSorter
        calculator = new CompatibilityCalculator();
        petSorter = new PetSorter(testUser, calculator);
    }

    @AfterEach
    public void tearDown() {
        // Delete test CSV and directory
        new File(TEST_CSV_PATH).delete();
        new File(TEST_DIR).delete();
    }

    @Test
    public void testSortAndExportToCSV() throws IOException {
        // Act - sort pets and export to CSV
        petSorter.sortAndExportToCSV(testPets, TEST_CSV_PATH);

        // Assert - check if CSV was created
        File csvFile = new File(TEST_CSV_PATH);
        assertTrue(csvFile.exists(), "CSV file should be created");

        // Read the CSV and check its content
        List<String[]> csvLines = readCSV(TEST_CSV_PATH);

        // Check header
        assertNotNull(csvLines, "CSV data should not be null");
        assertTrue(csvLines.size() > 0, "CSV should have at least a header row");
        assertEquals(5, csvLines.get(0).length, "CSV header should have 5 columns");
        assertEquals("Name", csvLines.get(0)[0], "First header should be Name");
        assertEquals("Breed", csvLines.get(0)[1], "Second header should be Breed");

        // Check content - should have all test pets
        assertEquals(testPets.size() + 1, csvLines.size(), "CSV should contain all test pets plus header");

        // Check sorting - pets should be sorted by score (first pet has highest score)
        // This assumes pet names are unique in the test data
        String highestScoringPetName = findPetWithHighestScore();
        assertEquals(highestScoringPetName, csvLines.get(1)[0],
                "First pet in CSV should be the one with highest score");
    }

    @Test
    public void testTieBreakingLogic() throws IOException {
        // Create pets with same compatibility score but different attributes
        Pet pet1 = new Pet("Pet1", "Dog", "Breed1", testUser.getPreferredPetGender(), testUser.getMbti(),
                testUser.getEnergyLevel(), testUser.getSpace() * 0.5, testUser.getBudget() * 0.5,
                false, testUser.hasYard(), testUser.getTimePerDay() * 0.5, "images/pet1.jpg");

        Pet pet2 = new Pet("Pet2", "Dog", "Breed2", "Opposite Gender", testUser.getMbti(),
                testUser.getEnergyLevel(), testUser.getSpace() * 0.5, testUser.getBudget() * 0.5,
                false, testUser.hasYard(), testUser.getTimePerDay() * 0.5, "images/pet2.jpg");

        List<Pet> tieBreakPets = new ArrayList<>();
        tieBreakPets.add(pet2); // Add opposite gender pet first
        tieBreakPets.add(pet1); // Add preferred gender pet second

        // Act - sort pets and export to CSV
        petSorter.sortAndExportToCSV(tieBreakPets, TEST_CSV_PATH);

        // Assert - check that preferred gender pet comes first
        List<String[]> csvLines = readCSV(TEST_CSV_PATH);
        assertEquals("Pet1", csvLines.get(1)[0], "Pet with preferred gender should be first");
    }

    @Test
    public void testEmptyPetList() {
        // Act & Assert - should not throw exception with empty list
        assertDoesNotThrow(() -> petSorter.sortAndExportToCSV(new ArrayList<>(), TEST_CSV_PATH));

        // Check that CSV was created but only has header
        try {
            List<String[]> csvLines = readCSV(TEST_CSV_PATH);
            assertEquals(1, csvLines.size(), "CSV should only have header row");
        } catch (IOException e) {
            fail("Failed to read CSV: " + e.getMessage());
        }
    }

    @Test
    public void testAllergicUserWithAllergenicPet() throws IOException {
        // Create allergic user
        User allergicUser = new User("Male", "Any", "ENFJ", 7, 500, 200, true, true, 4.0);
        PetSorter allergySorter = new PetSorter(allergicUser, calculator);

        // Add allergenic and non-allergenic pets
        Pet allergenicPet = new Pet("Sneeze", "Cat", "Allergenic", "Male", "ENFJ",
                7, 400, 150, true, false, 2.0, "images/sneeze.jpg");
        Pet nonAllergenicPet = new Pet("Safe", "Dog", "Hypoallergenic", "Male", "ENFJ",
                7, 400, 150, false, false, 2.0, "images/safe.jpg");

        List<Pet> allergyTestPets = new ArrayList<>();
        allergyTestPets.add(allergenicPet);
        allergyTestPets.add(nonAllergenicPet);

        // Act
        allergySorter.sortAndExportToCSV(allergyTestPets, TEST_CSV_PATH);

        // Assert - allergenic pet should have score 0 and be after non-allergenic
        List<String[]> csvLines = readCSV(TEST_CSV_PATH);
        assertEquals("Safe", csvLines.get(1)[0], "Non-allergenic pet should be first");
        assertEquals("Sneeze", csvLines.get(2)[0], "Allergenic pet should be last");

        // Check score - should be 0%
        assertTrue(csvLines.get(2)[3].contains("0%"), "Allergenic pet should have 0% score");
    }

    // Helper methods

    private void setupTestData() {
        testPets = new ArrayList<>();

        // Add test pets
        testPets.add(new Pet("Bella", "Dog", "Beagle", "Female", "ISFJ",
                7, 50.0, 30.0, false, true, 2.0, "images/bella.jpg"));

        testPets.add(new Pet("Charlie", "Dog", "Golden Retriever", "Male", "ENTJ",
                9, 75.0, 50.0, false, true, 2.5, "images/charlie.jpg"));

        testPets.add(new Pet("Milo", "Cat", "Siamese", "Male", "INFP",
                5, 25.0, 20.0, false, false, 1.5, "images/milo.jpg"));

        // Create test user - good match for Bella
        testUser = new User("Male", "Female", "ISFJ", 7, 500, 200, false, true, 4.0);
    }

    private List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.split(","));
            }
        }

        return lines;
    }

    private String findPetWithHighestScore() {
        double highestScore = -1;
        String highestScoringPetName = null;

        for (Pet pet : testPets) {
            double score = calculator.calculate(testUser, pet);
            if (score > highestScore) {
                highestScore = score;
                highestScoringPetName = pet.getName();
            }
        }

        return highestScoringPetName;
    }
}