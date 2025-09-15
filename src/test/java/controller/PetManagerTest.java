package controller;

import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class PetManagerTest {

    private PetManager manager;

    @BeforeEach
    void setUp() {
        manager = new PetManager(
                "Female",              // gender
                "Male",                // preferredPetGender
                1, 1, 1, 1,            // MBTI: ISTJ
                5,                    // energy
                30.0,                 // space
                20.0,                 // budget
                false,                // allergy
                true,                 // hasYard
                2.0                   // time
        );
    }

    @AfterEach
    void tearDown() {
        // Clean up generated CSV after test
        File output = new File(manager.getCsvPath());
        if (output.exists()) {
            output.delete();
        }
    }

    @Test
    void testUserInitialization() {
        User user = manager.getUser();
        assertEquals("Female", user.getGender());
        assertEquals("Male", user.getPreferredPetGender());
        assertEquals("ISTJ", user.getMbti());
        assertEquals(5, user.getEnergyLevel());
        assertEquals(30.0, user.getSpace());
        assertEquals(20.0, user.getBudget());
        assertFalse(user.isAllergic()); // fixed
        assertTrue(user.hasYard());
        assertEquals(2.0, user.getTimePerDay()); // fixed
    }


    @Test
    void testCsvPath() {
        assertEquals("output/pet_compatibility.csv", manager.getCsvPath());
    }

    @Test
    void testOutputCsvIsCreated() {
        File outputFile = new File(manager.getCsvPath());
        assertTrue(outputFile.exists(), "Expected CSV file to be created.");
    }
}
