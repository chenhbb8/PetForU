package model;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CompatibilityCalculatorTest {

    private ICompatibilityCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new CompatibilityCalculator();
    }

    @Test
    public void testPerfectCompatibility() {
        // Create user
        User user = new User("Male", "Female", "ENFJ", 7, 500, 200, false, true, 5.0);

        // Create pet with perfect match for this user
        Pet pet = new Pet("Bella", "Dog", "Golden Retriever", "Female", "ENFJ",
                7, 400, 150, false, true, 4.0, "images/bella.jpg");

        // Act
        double score = calculator.calculate(user, pet);

        // Assert
        assertEquals(1.0, score, 0.01, "Perfect match should score 1.0");
    }

    @Test
    public void testAllergyHardFail() {
        // Create allergic user
        User user = new User("Male", "Any", "ENFJ", 7, 500, 200, true, true, 5.0);

        // Create allergenic pet
        Pet pet = new Pet("Fluffy", "Cat", "Persian", "Female", "ENFJ",
                7, 400, 150, true, false, 4.0, "images/fluffy.jpg");

        // Act
        double score = calculator.calculate(user, pet);

        // Assert
        assertEquals(0.0, score, 0.01, "Allergenic pet with allergic user should score 0.0");
    }

    @Test
    public void testSpaceLimitation() {
        // Create user with limited space
        User user = new User("Male", "Any", "ENFJ", 7, 300, 200, false, true, 5.0);

        // Create pet requiring more space
        Pet pet = new Pet("Rex", "Dog", "Great Dane", "Male", "ENFJ",
                7, 600, 150, false, true, 4.0, "images/rex.jpg");

        // Act
        double score = calculator.calculate(user, pet);

        // Space score should be 300/600 = 0.5, which will reduce overall score
        double expectedSpaceContribution = 0.15 * 0.5; // 15% weight * 0.5 score

        // Assert
        assertTrue(score < 1.0, "Score should be reduced due to space limitations");
        assertTrue(score > 0.0, "Score should still be above 0.0");
    }

    @Test
    public void testBudgetLimitation() {
        // Create user with limited budget
        User user = new User("Male", "Any", "ENFJ", 7, 500, 100, false, true, 5.0);

        // Create expensive pet
        Pet pet = new Pet("Diamond", "Dog", "Tibetan Mastiff", "Female", "ENFJ",
                7, 400, 200, false, true, 4.0, "images/diamond.jpg");

        // Act
        double score = calculator.calculate(user, pet);

        // Budget score should be 100/200 = 0.5, which will reduce overall score
        double expectedBudgetContribution = 0.15 * 0.5; // 15% weight * 0.5 score

        // Assert
        assertTrue(score < 1.0, "Score should be reduced due to budget limitations");
        assertTrue(score > 0.0, "Score should still be above 0.0");
    }

    @Test
    public void testYardRequirement() {
        // Create user without yard
        User user = new User("Male", "Any", "ENFJ", 7, 500, 200, false, false, 5.0);

        // Create pet that requires yard
        Pet pet = new Pet("Rover", "Dog", "Border Collie", "Male", "ENFJ",
                7, 400, 150, false, true, 4.0, "images/rover.jpg");

        // Act
        double score = calculator.calculate(user, pet);

        // Assert
        assertTrue(score < 1.0, "Score should be reduced due to yard requirement");
    }

    @Test
    public void testMBTICompatibility() {
        // Create base user
        User user = new User("Male", "Any", "ENFJ", 7, 500, 200, false, true, 5.0);

        // Create pets with varying MBTI compatibility
        Pet perfectMatch = new Pet("Perfect", "Dog", "Breed1", "Male", "ENFJ",
                7, 400, 150, false, false, 4.0, "images/perfect.jpg");

        Pet threeLetterMatch = new Pet("ThreeMatch", "Dog", "Breed2", "Male", "ENFP",
                7, 400, 150, false, false, 4.0, "images/threematch.jpg");

        Pet twoLetterMatch = new Pet("TwoMatch", "Dog", "Breed3", "Male", "INFJ",
                7, 400, 150, false, false, 4.0, "images/twomatch.jpg");

        Pet oneLetterMatch = new Pet("OneMatch", "Dog", "Breed4", "Male", "ISTP",
                7, 400, 150, false, false, 4.0, "images/onematch.jpg");

        // Act
        double perfectScore = calculator.calculate(user, perfectMatch);
        double threeMatchScore = calculator.calculate(user, threeLetterMatch);
        double twoMatchScore = calculator.calculate(user, twoLetterMatch);
        double oneMatchScore = calculator.calculate(user, oneLetterMatch);

//        // Debug
//        System.out.println("Perfect MBTI Score: " + perfectScore);
//        System.out.println("Three Match MBTI Score: " + threeMatchScore);
//        System.out.println("Two Match MBTI Score: " + twoMatchScore);
//        System.out.println("One Match MBTI Score: " + oneMatchScore);

        // Assert
        assertTrue(perfectScore >= threeMatchScore,
                "Perfect MBTI match should score higher than partial matches");
        assertTrue(threeMatchScore >= twoMatchScore,
                "Three letter match should score higher than two letter match");
        assertTrue(twoMatchScore >= oneMatchScore,
                "Two letter match should score higher than one letter match");
    }

    @Test
    public void testEnergyLevelCompatibility() {
        // Create base user with energy level 7
        User user = new User("Male", "Any", "ENFJ", 7, 500, 200, false, true, 5.0);

        // Create pets with different energy levels
        Pet perfectEnergy = new Pet("Perfect", "Dog", "Breed1", "Male", "ENFJ",
                7, 400, 150, false, false, 4.0, "images/perfect.jpg");

        Pet closeEnergy = new Pet("Close", "Dog", "Breed2", "Male", "ENFJ",
                6, 400, 150, false, false, 4.0, "images/close.jpg");

        Pet farEnergy = new Pet("Far", "Dog", "Breed3", "Male", "ENFJ",
                2, 400, 150, false, false, 4.0, "images/far.jpg");

        // Act
        double perfectScore = calculator.calculate(user, perfectEnergy);
        double closeScore = calculator.calculate(user, closeEnergy);
        double farScore = calculator.calculate(user, farEnergy);

        // Assert
        assertTrue(perfectScore > closeScore,
                "Perfect energy match should score higher than close energy match");
        assertTrue(closeScore > farScore,
                "Close energy match should score higher than far energy match");
    }

    @Test
    public void testGenderPreference() {
        // Create user with female gender preference
        User user = new User("Male", "Female", "ENFJ", 7, 500, 200, false, true, 5.0);

        // Create pets with different genders
        Pet femalePet = new Pet("Female", "Dog", "Breed1", "Female", "ENFJ",
                7, 400, 150, false, false, 4.0, "images/female.jpg");

        Pet malePet = new Pet("Male", "Dog", "Breed2", "Male", "ENFJ",
                7, 400, 150, false, false, 4.0, "images/male.jpg");

        // Act
        double femaleScore = calculator.calculate(user, femalePet);
        double maleScore = calculator.calculate(user, malePet);

        // Assert
        assertTrue(femaleScore > maleScore,
                "Female pet should score higher due to gender preference");
    }

    @Test
    public void testAnyGenderPreference() {
        // Create user with any gender preference
        User user = new User("Male", "Any", "ENFJ", 7, 500, 200, false, true, 5.0);

        // Create pets with different genders
        Pet femalePet = new Pet("Female", "Dog", "Breed1", "Female", "ENFJ",
                7, 400, 150, false, false, 4.0, "images/female.jpg");

        Pet malePet = new Pet("Male", "Dog", "Breed2", "Male", "ENFJ",
                7, 400, 150, false, false, 4.0, "images/male.jpg");

        // Act
        double femaleScore = calculator.calculate(user, femalePet);
        double maleScore = calculator.calculate(user, malePet);

        // Assert
        assertEquals(femaleScore, maleScore, 0.01,
                "Both genders should score the same with 'Any' preference");
    }

    @Test
    public void testLimitedTimeAvailability() {
        // Create user with limited time
        User user = new User("Male", "Any", "ENFJ", 7, 500, 200, false, true, 2.0);

        // Create pet requiring more time
        Pet highMaintenancePet = new Pet("HighMaintenance", "Dog", "Breed1", "Male", "ENFJ",
                7, 400, 150, false, false, 4.0, "images/highmaintenance.jpg");

        // Create pet requiring less time
        Pet lowMaintenancePet = new Pet("LowMaintenance", "Cat", "Breed2", "Male", "ENFJ",
                7, 400, 150, false, false, 1.0, "images/lowmaintenance.jpg");

        // Act
        double highMaintenanceScore = calculator.calculate(user, highMaintenancePet);
        double lowMaintenanceScore = calculator.calculate(user, lowMaintenancePet);

        // Assert
        assertTrue(lowMaintenanceScore > highMaintenanceScore,
                "Low maintenance pet should score higher with limited time");
    }
}