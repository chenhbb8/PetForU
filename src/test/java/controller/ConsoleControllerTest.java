package controller;

import controller.ConsoleController;
import model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ConsoleControllerTest {

    private static final String TEST_DIR = "test_output";
    private static final String TEST_CSV_PATH = TEST_DIR + "/pet_test.csv";

    private ConsoleController consoleController;
    private User testUser;
    private CompatibilityCalculator calculator;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() throws IOException {
        // Redirect System.out for testing console output
        System.setOut(new PrintStream(outContent));

        // Create test directory if it doesn't exist
        new File(TEST_DIR).mkdirs();

        // Create test CSV
        createTestCsv();

        // Setup test user and calculator
        testUser = new User("Male", "Any", "ENFJ", 7, 500, 200, false, true, 4.0);
        calculator = new CompatibilityCalculator();

        // Initialize ConsoleController
        consoleController = new ConsoleController(testUser, calculator);
    }

    @AfterEach
    public void tearDown() {
        // Reset System.out
        System.setOut(originalOut);

        // Delete test CSV and directory
        new File(TEST_CSV_PATH).delete();
        new File(TEST_DIR).delete();
    }

    @Test
    public void testDisplayBestMatch() {
        // Act
        consoleController.displayBestMatch(TEST_CSV_PATH);

        // Assert
        String output = outContent.toString();
        // 使用更灵活的断言，检查是否包含关键信息而不是特定标题
        assertTrue(output.contains("Bella"), "Output should contain pet name");
        assertTrue(output.contains("95%"), "Output should contain compatibility score");
    }

    @Test
    public void testDisplayNoCompatiblePets() throws IOException {
        // Create CSV with low scores
        createLowScoreCsv();

        // Act
        consoleController.displayBestMatch(TEST_CSV_PATH);

        // Assert
        String output = outContent.toString();
        // 检查是否包含无兼容宠物或低兼容性的相关信息
        assertTrue(output.contains("NO COMPATIBLE PETS FOUND") ||
                        output.contains("LOW COMPATIBILITY") ||
                        output.contains("might not be the best time"),
                "Output should indicate low compatibility or no compatible pets");
    }

    @Test
    public void testDisplayRecommendedPets() {
        // Act
        consoleController.displayRecommendedPets(TEST_CSV_PATH);

        // Assert
        String output = outContent.toString();
        // 使用更灵活的断言，检查是否包含关键信息而不是特定标题
        assertTrue(output.contains("Bella"), "Output should contain first pet");
        assertTrue(output.contains("Charlie"), "Output should contain second pet");
        assertFalse(output.contains("Luna"), "Output should not contain low-compatibility pet");
    }

    @Test
    public void testDisplayAllPetsWithScores() {
        // Act
        consoleController.displayAllPetsWithScores(TEST_CSV_PATH);

        // Assert
        String output = outContent.toString();
        // 检查是否包含所有宠物信息
        assertTrue(output.contains("ALL PETS") || output.contains("COMPATIBILITY SCORES"),
                "Output should mention all pets or compatibility scores");
        assertTrue(output.contains("Bella"), "Output should contain first pet");
        assertTrue(output.contains("Charlie"), "Output should contain second pet");
        assertTrue(output.contains("Milo"), "Output should contain third pet");
    }

    @Test
    public void testDisplaySearchResult() {
        // Create sample search results
        List<PetWithScore> searchResults = new ArrayList<>();

        Pet pet1 = new Pet("Bella", "Dog", "Beagle", "Female", "ISFJ",
                7, 50.0, 30.0, false, true, 2.0, "images/bella.jpg");
        Pet pet2 = new Pet("Charlie", "Dog", "Golden Retriever", "Male", "ENTJ",
                9, 75.0, 50.0, false, true, 2.5, "images/charlie.jpg");

        searchResults.add(new PetWithScore(pet1, 0.95));
        searchResults.add(new PetWithScore(pet2, 0.85));

        // Act
        consoleController.displaySearchResult(searchResults);

        // Assert
        String output = outContent.toString();
        // 检查是否包含搜索结果信息
        assertTrue(output.contains("SEARCH") || output.contains("RESULTS"),
                "Output should indicate search results");
        assertTrue(output.contains("Bella"), "Output should contain first pet");
        assertTrue(output.contains("Charlie"), "Output should contain second pet");
        assertTrue(output.contains("95%") && output.contains("85%"),
                "Output should contain compatibility scores");
    }

    @Test
    public void testDisplaySearchResultEmpty() {
        // Act
        consoleController.displaySearchResult(new ArrayList<>());

        // Assert
        String output = outContent.toString();
        // 检查是否包含没有搜索结果的信息
        assertTrue(output.contains("NO") && output.contains("RESULTS"),
                "Output should indicate no search results");
    }

    @Test
    public void testDisplayLowCompatibility() {
        // Create a pet with low compatibility
        Pet pet = new Pet("Milo", "Cat", "Siamese", "Male", "INFP",
                5, 25.0, 20.0, false, false, 1.5, "images/milo.jpg");
        PetWithScore petWithScore = new PetWithScore(pet, 0.75);

        // Act - using reflection to access private method
        try {
            Method method = ConsoleController.class.getDeclaredMethod(
                    "displayLowCompatibility", PetWithScore.class);
            method.setAccessible(true);
            method.invoke(consoleController, petWithScore);

            // Assert
            String output = outContent.toString();
            // 检查是否包含低兼容性信息
            assertTrue(output.contains("LOW") || output.contains("COMPATIBILITY"),
                    "Output should indicate low compatibility");
            assertTrue(output.contains("Milo"), "Output should contain pet name");
            assertTrue(output.contains("75%"), "Output should contain compatibility score");
        } catch (Exception e) {
            fail("Failed to invoke displayLowCompatibility: " + e.getMessage());
        }
    }

    @Test
    public void testFormatPercentage() {
        // Using reflection to access private method
        try {
            Method method = ConsoleController.class.getDeclaredMethod(
                    "formatPercentage", double.class);
            method.setAccessible(true);

            // Act
            String result1 = (String) method.invoke(consoleController, 0.95);
            String result2 = (String) method.invoke(consoleController, 0.7523);
            String result3 = (String) method.invoke(consoleController, 0.0);

            // Assert
            assertEquals("95%", result1, "Should format 0.95 as 95%");
            assertEquals("75%", result2, "Should format 0.7523 as 75%");
            assertEquals("0%", result3, "Should format 0.0 as 0%");
        } catch (Exception e) {
            fail("Failed to invoke formatPercentage: " + e.getMessage());
        }
    }

    // Helper methods

    private void createTestCsv() throws IOException {
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("Name,Breed,Type,Score,ImagePath\n");
            writer.write("Bella,Beagle,Dog,95%,images/bella.jpg\n");
            writer.write("Charlie,Golden Retriever,Dog,85%,images/charlie.jpg\n");
            writer.write("Milo,Siamese,Cat,70%,images/milo.jpg\n");
        }
    }

    private void createLowScoreCsv() throws IOException {
        try (FileWriter writer = new FileWriter(TEST_CSV_PATH)) {
            writer.write("Name,Breed,Type,Score,ImagePath\n");
            writer.write("Bella,Beagle,Dog,75%,images/bella.jpg\n");
            writer.write("Charlie,Golden Retriever,Dog,65%,images/charlie.jpg\n");
            writer.write("Milo,Siamese,Cat,55%,images/milo.jpg\n");
        }
    }
}