package model;

public class User {
    private final String gender;                // Male/Female/Other
    private final String preferredPetGender;    // Male/Female/Any
    private final String mbti;                  // 4-letter MBTI
    private final int energyLevel;              // 1–10
    private final double space;                 // square feet
    private final double budget;                // USD
    private final boolean allergic;             // true if user is allergic
    private final boolean hasYard;              // user has a yard or not
    private final double timePerDay;            // hours/day

    public User(String gender, String preferredPetGender, String mbti,
                int energyLevel, double space, double budget,
                boolean allergic, boolean hasYard, double timePerDay) {
        this.gender = gender;
        this.preferredPetGender = preferredPetGender;
        this.mbti = mbti;
        this.energyLevel = energyLevel;
        this.space = space;
        this.budget = budget;
        this.allergic = allergic;
        this.hasYard = hasYard;
        this.timePerDay = timePerDay;
    }

    // Getters

    public String getGender() {
        return gender;
    }

    public String getPreferredPetGender() {
        return preferredPetGender;
    }

    public String getMbti() {
        return mbti;
    }

    public int getEnergyLevel() {
        return energyLevel;
    }

    public double getSpace() {
        return space;
    }

    public double getBudget() {
        return budget;
    }

    public boolean isAllergic() {
        return allergic;
    }

    public boolean hasYard() {
        return hasYard;
    }

    public double getTimePerDay() {
        return timePerDay;
    }
}
