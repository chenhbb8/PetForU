package model;

import Database.PetDatabase;
import java.util.List;

/**
 * Interface for matching a user to the best pet based on compatibility.
 * This interface provides a method to sort pets based on compatibility score
 * and then take action based on the score (such as suggesting the user may not
 * be ready for a pet if the score is below a threshold).
 */
public interface IPetMatcher {

    /**
     * Sorts pets by compatibility score and returns the top pet from the sorted list.
     * If the first pet's compatibility score is below 80%, a message will be printed
     * suggesting that it might not be the best time for the user to have a pet.
     *
     * @param user the user for whom the best match pet is being sought
     * @param csvFilePath the path to the CSV file containing the pet data and compatibility scores
     * @return the pet with the highest compatibility score or null if no pets are found
     */
    Pet sortAndEvaluateBestMatch(User user, String csvFilePath);
}
