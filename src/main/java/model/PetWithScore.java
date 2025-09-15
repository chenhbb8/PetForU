package model;

import Database.PetDatabase;

public class PetWithScore {
    private final Pet pet;      // The original pet object
    private final double score; // Compatibility score for this user-pet pair

    public PetWithScore(Pet pet, double score) {
        this.pet = pet;
        this.score = score;
    }

    // Getter for Pet
    public Pet getPet() {
        return pet;
    }

    // Getter for Score
    public double getScore() {
        return score;
    }
}
