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

public class PetFilterTest {

    private static final String TEST_DIR = "test_output";
    private static final String TEST_CSV_PATH = TEST_DIR + "/pet_test.csv";

    private List<Pet> testPets;
    private PetFilter petFilter;

    @BeforeEach
    public void setUp() throws IOException {
        // Create test directory if it doesn't exist
        new File(TEST_DIR).mkdirs();

        // Set up test data
        setupTestData();

        // Initialize PetFilter
        petFilter = new PetFilter(TEST_CSV_PATH);
    }

    @AfterEach
    public void tearDown() {
        // Delete test CSV and directory
        new File(TEST_CSV_PATH).delete();
        new File(TEST_DIR).delete();
    }

    @Test
    public void testFilterPetsAbove80Percent() throws IOException {
        // Create CSV with mixed compatibility scores
        createTestCsv(new int[]{95, 85, 75, 65, 50});

        // Act
        List<Pet> filteredPets = petFilter.filterPetsByCompatibility(testPets);

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertEquals(2, filteredPets.size(), "Should have 2 pets with scores above 80%");
        assertEquals("Bella", filteredPets.get(0).getName());
        assertEquals("Max", filteredPets.get(1).getName());
    }

    @Test
    public void testFilterNoCompatiblePets() throws IOException {
        // Create CSV with all scores below 80%
        createTestCsv(new int[]{79, 75, 70, 65, 50});

        // Act
        List<Pet> filteredPets = petFilter.filterPetsByCompatibility(testPets);

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertTrue(filteredPets.isEmpty(), "Should have no pets when all scores are below 80%");
    }

    @Test
    public void testFilterAllCompatiblePets() throws IOException {
        // Create CSV with all scores above 80%
        createTestCsv(new int[]{95, 90, 85, 82, 81});

        // Act
        List<Pet> filteredPets = petFilter.filterPetsByCompatibility(testPets);

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertEquals(5, filteredPets.size(), "Should have all pets when all scores are above 80%");
    }

    @Test
    public void testFilterWithEdgeCase80Percent() throws IOException {
        // Create CSV with a score exactly at 80%
        createTestCsv(new int[]{95, 80, 70});

        // Act
        List<Pet> filteredPets = petFilter.filterPetsByCompatibility(testPets);

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertEquals(1, filteredPets.size(), "Should have 1 pet when one score is above 80%");
        assertEquals("Bella", filteredPets.get(0).getName());
    }

    @Test
    public void testFilterEmptyCsv() throws IOException {
        // Create empty CSV (just header)
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("Name,Breed,Type,Score,ImagePath\n");
        }

        // Act
        List<Pet> filteredPets = petFilter.filterPetsByCompatibility(testPets);

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertTrue(filteredPets.isEmpty(), "Should have no pets for empty CSV");
    }

    @Test
    public void testFilterNonExistentCsv() {
        // Create filter with non-existent CSV
        PetFilter badFilter = new PetFilter("non_existent_file.csv");

        // Act
        List<Pet> filteredPets = badFilter.filterPetsByCompatibility(testPets);

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertTrue(filteredPets.isEmpty(), "Should have no pets for non-existent CSV");
    }

    @Test
    public void testFilterInvalidCsvFormat() throws IOException {
        // Create CSV with invalid format
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("InvalidHeader\n");
            writer.write("InvalidData\n");
        }

        // Act
        List<Pet> filteredPets = petFilter.filterPetsByCompatibility(testPets);

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertTrue(filteredPets.isEmpty(), "Should have no pets for invalid CSV format");
    }

    @Test
    public void testFilterWithEmptyPetList() throws IOException {
        // Create valid CSV
        createTestCsv(new int[]{95, 85, 75});

        // Act with empty pet list
        List<Pet> filteredPets = petFilter.filterPetsByCompatibility(new ArrayList<>());

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertTrue(filteredPets.isEmpty(), "Should have no pets when input list is empty");
    }

    @Test
    public void testFilterWithNullPetList() throws IOException {
        // Create valid CSV
        createTestCsv(new int[]{95, 85, 75});

        // Act with null pet list
        List<Pet> filteredPets = petFilter.filterPetsByCompatibility(null);

        // Assert
        assertNotNull(filteredPets, "Filtered pets should not be null");
        assertTrue(filteredPets.isEmpty(), "Should have no pets when input list is null");
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

        testPets.add(new Pet("Rocky", "Hamster", "Dwarf", "Male", "ESFP",
                3, 50, 30, true, false, 1.0, "images/rocky.jpg"));

        testPets.add(new Pet("Daisy", "Dog", "Bulldog", "Female", "ESTP",
                6, 400, 100, false, true, 2.5, "images/daisy.jpg"));
    }

    private void createTestCsv(int[] scores) throws IOException {
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("Name,Breed,Type,Score,ImagePath\n");

            String[] names = {"Bella", "Max", "Luna", "Rocky", "Daisy"};
            String[] breeds = {"Golden Retriever", "German Shepherd", "Siamese", "Dwarf", "Bulldog"};
            String[] types = {"Dog", "Dog", "Cat", "Hamster", "Dog"};

            for (int i = 0; i < Math.min(scores.length, names.length); i++) {
                writer.write(String.format("%s,%s,%s,%d%%,images/%s.jpg\n",
                        names[i], breeds[i], types[i], scores[i], names[i].toLowerCase()));
            }
        }
    }
}