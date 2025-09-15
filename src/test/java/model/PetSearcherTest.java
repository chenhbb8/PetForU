package model;

import controller.ConsoleController;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PetSearcherTest {

    private static final String TEST_DIR = "test_output";
    private static final String TEST_CSV_PATH = TEST_DIR + "/pet_test.csv";

    @Mock
    private ConsoleController mockConsoleController;

    private PetSearcher petSearcher;
    private List<Pet> testPets;
    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() throws IOException {
        // Initialize mocks
        closeable = MockitoAnnotations.openMocks(this);

        // Create test directory
        new File(TEST_DIR).mkdirs();

        // Create a simple test dataset - only 5 pets with clear types
        createTestDatabase();

        // Create CSV file using the same dataset
        createTestCsvFromDatabase();

        // Initialize PetSearcher with the same dataset
        petSearcher = new PetSearcher(mockConsoleController, testPets);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
        new File(TEST_CSV_PATH).delete();
        new File(TEST_DIR).delete();
    }

    @Test
    public void testSearchByTypeOnly() {
        // Perform search - looking for Dog type
        petSearcher.searchAndDisplay("Dog", "", TEST_CSV_PATH);

        // Capture the argument passed to displaySearchResult
        ArgumentCaptor<List<PetWithScore>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockConsoleController).displaySearchResult(captor.capture());

        // Get actual results
        List<PetWithScore> result = captor.getValue();

        // Verify results - should find 3 dogs
        assertEquals(3, result.size(), "Should find 3 dogs");

        // Verify all results are dogs
        for (PetWithScore petWithScore : result) {
            assertEquals("Dog", petWithScore.getPet().getType(), "All results should be dogs");
        }
    }

    @Test
    public void testSearchByTypeAndBreed() {
        // Perform search - looking for Golden Retriever dog
        petSearcher.searchAndDisplay("Dog", "Golden Retriever", TEST_CSV_PATH);

        // Capture the argument passed to displaySearchResult
        ArgumentCaptor<List<PetWithScore>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockConsoleController).displaySearchResult(captor.capture());

        // Get actual results
        List<PetWithScore> result = captor.getValue();

        // Verify results - should find only 1 Golden Retriever
        assertEquals(1, result.size(), "Should find only 1 Golden Retriever");
        assertEquals("Bella", result.get(0).getPet().getName(), "Should find a dog named Bella");
        assertEquals("Golden Retriever", result.get(0).getPet().getBreed(), "Should be a Golden Retriever");
    }

    @Test
    public void testSearchNoMatches() {
        // Perform search - looking for a non-existent type
        petSearcher.searchAndDisplay("Bird", "", TEST_CSV_PATH);

        // Capture the argument passed to displaySearchResult
        ArgumentCaptor<List<PetWithScore>> captor = ArgumentCaptor.forClass(List.class);
        verify(mockConsoleController).displaySearchResult(captor.capture());

        // Verify results - should find no matches
        assertTrue(captor.getValue().isEmpty(), "Should not find any pets");
    }

    @Test
    public void testSearchCaseInsensitive() {
        // Act - using lowercase
        petSearcher.searchAndDisplay("dog", "golden retriever", TEST_CSV_PATH);

        // Assert
        verify(mockConsoleController).displaySearchResult(argThat(list ->
                list.size() == 1 &&
                        list.get(0).getPet().getType().equalsIgnoreCase("Dog") &&
                        list.get(0).getPet().getBreed().equalsIgnoreCase("Golden Retriever")
        ));
    }

    @Test
    public void testSearchExistingTypeNonExistingBreed() {
        // Act
        petSearcher.searchAndDisplay("Dog", "NonExistentBreed", TEST_CSV_PATH);

        // Assert
        verify(mockConsoleController).displaySearchResult(argThat(List::isEmpty));
    }

    @Test
    public void testSearchWithNullBreed() {
        // Act
        petSearcher.searchAndDisplay("Dog", null, TEST_CSV_PATH);

        // Assert
        verify(mockConsoleController).displaySearchResult(argThat(list ->
                list.size() == 3 &&
                        list.stream().allMatch(petWithScore ->
                                petWithScore.getPet().getType().equalsIgnoreCase("Dog"))
        ));
    }

    @Test
    public void testSearchNonExistentFile() {
        // Perform search - using a non-existent file
        petSearcher.searchAndDisplay("Dog", "", "non_existent_file.csv");

        // Verify results - should be called with empty list
        verify(mockConsoleController).displaySearchResult(argThat(List::isEmpty));
    }

    // Create a fixed test database
    private void createTestDatabase() {
        testPets = new ArrayList<>();

        // Add 3 dogs
        testPets.add(new Pet("Bella", "Dog", "Golden Retriever", "Female", "ENFP",
                7, 500, 120, false, true, 3.5, "images/bella.jpg"));

        testPets.add(new Pet("Max", "Dog", "German Shepherd", "Male", "ISTJ",
                9, 700, 150, false, true, 4.0, "images/max.jpg"));

        testPets.add(new Pet("Charlie", "Dog", "Poodle", "Male", "ENFJ",
                6, 450, 130, false, false, 2.5, "images/charlie.jpg"));

        // Add 2 cats
        testPets.add(new Pet("Luna", "Cat", "Siamese", "Female", "INTJ",
                5, 200, 80, true, false, 1.5, "images/luna.jpg"));

        testPets.add(new Pet("Misty", "Cat", "Bengal", "Female", "INFJ",
                6, 250, 85, false, false, 1.8, "images/misty.jpg"));
    }

    // Create a consistent CSV file using the database
    private void createTestCsvFromDatabase() throws IOException {
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            // CSV header row
            writer.write("Name,Breed,Type,Score,ImagePath\n");

            // Add a row for each pet
            for (Pet pet : testPets) {
                // Mock scores - simply set based on type, dogs 90%, cats 80%
                String score = pet.getType().equals("Dog") ? "90%" : "80%";

                writer.write(String.format("%s,%s,%s,%s,%s\n",
                        pet.getName(),
                        pet.getBreed(),
                        pet.getType(),
                        score,
                        pet.getImagePath()
                ));
            }
        }
    }
}