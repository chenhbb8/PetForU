package model;

public interface IPetSearcher {

    /**
     * Searches for pets by type and breed from a CSV file and displays the result using ConsoleController.
     * If no breed is provided, it will search for pets of the specified type.
     * If a breed is provided, it will search for that breed within the specified type.
     *
     * @param petType  the type of pet to search for (e.g., Dog, Cat, Hamster, etc.)
     * @param petBreed the breed of the pet to search for (can be empty to ignore)
     * @param csvPath  the path to the CSV file containing pet data and compatibility scores
     */
    void searchAndDisplay(String petType, String petBreed, String csvPath);
}
