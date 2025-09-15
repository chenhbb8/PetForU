package model;

public class CompatibilityCalculator implements ICompatibilityCalculator {

    @Override
    public double calculate(User user, Pet pet) {
        // Allergy = hard fail
        if (user.isAllergic() && pet.isAllergenic()) {
            return 0.0;
        }

        double finalScore =
                0.15 * getSpaceScore(user.getSpace(), pet.getRequiredSpace()) +
                        0.15 * getTimeScore(user.getTimePerDay(), pet.getTimeNeededPerDay()) +
                        0.15 * getBudgetScore(user.getBudget(), pet.getMonthlyCost()) +
                        0.10 * getYardScore(user.hasYard(), pet.requiresYard()) +
                        0.15 * getEnergyLevelScore(user.getEnergyLevel(), pet.getEnergyLevel()) +
                        0.25 * getMBTIScore(user.getMbti(), pet.getMbti()) +
                        0.05 * getGenderScore(user.getPreferredPetGender(), pet.getGender());

        return finalScore;
    }

    private double getSpaceScore(double userSpace, double petSpace) {
        return userSpace >= petSpace ? 1.0 : userSpace / petSpace;
    }

    private double getTimeScore(double userTime, double petTime) {
        return userTime >= petTime ? 1.0 : userTime / petTime;
    }

    private double getBudgetScore(double userBudget, double petCost) {
        return userBudget >= petCost ? 1.0 : userBudget / petCost;
    }

    private double getYardScore(boolean userHasYard, boolean petNeedsYard) {
        return !petNeedsYard || userHasYard ? 1.0 : 0.0;
    }

    private double getEnergyLevelScore(int userEnergy, int petEnergy) {
        int diff = Math.abs(userEnergy - petEnergy);
        return 1.0 - (diff / 10.0); // score drops as difference increases
    }

    private double getMBTIScore(String userMBTI, String petMBTI) {
        if (userMBTI.equalsIgnoreCase(petMBTI)) {
            return 1.0;
        }

        int matchCount = 0;
        for (int i = 0; i < 4; i++) {
            if (i < userMBTI.length() && i < petMBTI.length() &&
                    Character.toUpperCase(userMBTI.charAt(i)) == Character.toUpperCase(petMBTI.charAt(i))) {
                matchCount++;
            }
        }

        return switch (matchCount) {
            case 4 -> 1.0;
            case 3 -> 0.75;
            case 2 -> 0.5;
            case 1 -> 0.25;
            default -> 0.0;  // Strict: no match = no MBTI compatibility
        };
    }

    private double getGenderScore(String preferredGender, String petGender) {
        if (preferredGender.equalsIgnoreCase("Any")) return 1.0;
        return preferredGender.equalsIgnoreCase(petGender) ? 1.0 : 0.0;
    }
}
