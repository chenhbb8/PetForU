package model;

import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PetMatcherTest {

    private static final String TEST_DIR = "test_output";
    private static final String TEST_CSV_PATH = TEST_DIR + "/pet_test.csv";

    private List<Pet> testPets;
    private User testUser;
    private PetMatcher petMatcher;

    @BeforeEach
    public void setUp() throws IOException {
        // Create test directory if it doesn't exist
        new File(TEST_DIR).mkdirs();

        // Set up test data
        setupTestData();

        // Initialize PetMatcher
        petMatcher = new PetMatcher(testPets);
    }

    @AfterEach
    public void tearDown() {
        // Delete test CSV and directory
        new File(TEST_CSV_PATH).delete();
        new File(TEST_DIR).delete();
    }

    @Test
    public void testFindBestMatch() throws IOException {
        // Create CSV with high compatibility scores
        createTestCsv(90, 80, 70);

        // Act
        Pet bestMatch = petMatcher.sortAndEvaluateBestMatch(testUser, TEST_CSV_PATH);

        // Assert
        assertNotNull(bestMatch, "Best match should not be null");
        assertEquals("Bella", bestMatch.getName());
        assertEquals("Dog", bestMatch.getType());
        assertEquals("Golden Retriever", bestMatch.getBreed());
    }

    @Test
    public void testLowCompatibilityReturnsNull() throws IOException {
        // Create CSV with low compatibility scores
        createTestCsv(79, 75, 70);

        // Act
        Pet bestMatch = petMatcher.sortAndEvaluateBestMatch(testUser, TEST_CSV_PATH);

        // Assert
        assertNull(bestMatch, "Best match should be null when score is below 80%");
    }

    @Test
    public void testEmptyCsvReturnsNull() throws IOException {
        // Create empty CSV (just header)
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("Name,Breed,Type,Score,ImagePath\n");
        }

        // Act
        Pet bestMatch = petMatcher.sortAndEvaluateBestMatch(testUser, TEST_CSV_PATH);

        // Assert
        assertNull(bestMatch, "Best match should be null for empty CSV");
    }

    @Test
    public void testNonExistentCsvReturnsNull() {
        // Act
        Pet bestMatch = petMatcher.sortAndEvaluateBestMatch(testUser, "non_existent_file.csv");

        // Assert
        assertNull(bestMatch, "Best match should be null for non-existent CSV");
    }

    @Test
    public void testPetNotInDatabaseReturnsNull() throws IOException {
        // Create CSV with a pet not in the database
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("Name,Breed,Type,Score,ImagePath\n");
            writer.write("Unknown,Unknown Breed,Unknown Type,95%,images/unknown.jpg\n");
        }

        // Act
        Pet bestMatch = petMatcher.sortAndEvaluateBestMatch(testUser, TEST_CSV_PATH);

        // Assert
        assertNull(bestMatch, "Best match should be null if pet not in database");
    }

    @Test
    public void testInvalidCsvFormatReturnsNull() throws IOException {
        // Create CSV with invalid format
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("InvalidHeader\n");
            writer.write("InvalidData\n");
        }

        // Act
        Pet bestMatch = petMatcher.sortAndEvaluateBestMatch(testUser, TEST_CSV_PATH);

        // Assert
        assertNull(bestMatch, "Best match should be null for invalid CSV format");
    }

    // Helper methods

    private void setupTestData() {
        testPets = new ArrayList<>();

        // Add test pets
        testPets.add(new Pet("Bella", "Dog", "Golden Retriever", "Female", "ENFP",
                7, 500, 120, false, true, 3.5, "images/bella.jpg"));

        testPets.add(new Pet("Max", "Dog", "German Shepherd", "Male", "ISTJ",
                9, 700, 150, false, true, 4.0, "images/max.jpg"));

        testPets.add(new Pet("Luna", "Cat", "Siamese", "Female", "INTJ",
                5, 200, 80, true, false, 1.5, "images/luna.jpg"));

        // Create test user
        testUser = new User("Male", "Any", "ENFJ", 6, 1000, 200, false, true, 4.0);
    }

    private void createTestCsv(int score1, int score2, int score3) throws IOException {
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("Name,Breed,Type,Score,ImagePath\n");
            writer.write("Bella,Golden Retriever,Dog," + score1 + "%,images/bella.jpg\n");
            writer.write("Max,German Shepherd,Dog," + score2 + "%,images/max.jpg\n");
            writer.write("Luna,Siamese,Cat," + score3 + "%,images/luna.jpg\n");
        }
    }
}