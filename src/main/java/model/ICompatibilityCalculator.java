package model;

public interface ICompatibilityCalculator {
    /**
     * Calculates a compatibility score between the given user and pet.
     * Score is in the range 0.0 (worst match) to 1.0 (best match).
     *
     * @param user the user to match
     * @param pet the pet being evaluated
     * @return the compatibility score (0.0 to 1.0)
     */
    double calculate(User user, Pet pet);
}
