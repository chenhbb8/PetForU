package model;
import Database.PetDatabase;

public class Pet {
    private final String name;
    private final String type;               // e.g. Dog, Cat, Hamster
    private final String breed;
    private final String gender;             // Male/Female
    private final String mbti;               // 4-letter MBTI
    private final int energyLevel;           // 1–10
    private final double requiredSpace;      // in square feet
    private final double monthlyCost;        // USD
    private final boolean allergenic;        // true if causes allergy
    private final boolean requiresYard;
    private final double timeNeededPerDay;   // in hours
    private final String imagePath;          // for console/GUI

    public Pet(String name, String type, String breed, String gender, String mbti,
               int energyLevel, double requiredSpace, double monthlyCost,
               boolean allergenic, boolean requiresYard, double timeNeededPerDay,
               String imagePath) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.gender = gender;
        this.mbti = mbti;
        this.energyLevel = energyLevel;
        this.requiredSpace = requiredSpace;
        this.monthlyCost = monthlyCost;
        this.allergenic = allergenic;
        this.requiresYard = requiresYard;
        this.timeNeededPerDay = timeNeededPerDay;
        this.imagePath = imagePath;
    }

    // Getters

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getBreed() {
        return breed;
    }

    public String getGender() {
        return gender;
    }

    public String getMbti() {
        return mbti;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public double getRequiredSpace() {
        return requiredSpace;
    }

    public double getMonthlyCost() {
        return monthlyCost;
    }

    public boolean isAllergenic() {
        return allergenic;
    }

    public boolean requiresYard() {
        return requiresYard;
    }

    public double getTimeNeededPerDay() {
        return timeNeededPerDay;
    }

    public String getImagePath() {
        return imagePath;
    }
}
